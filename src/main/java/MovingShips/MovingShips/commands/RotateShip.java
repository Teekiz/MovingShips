package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.utility.PermissionCheck;
import MovingShips.MovingShips.utility.PlayersOnShip;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RotateShip implements CommandExecutor {
    ShipAccess shipAccess = ShipAccess.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }

        Player player = (Player) commandSender;
        try {
            if (args.length < 2){
                player.sendMessage("<MovingShips> RotateShip arguments: <Left/Right> <Name of Ship>.");
            } else {

                String shipName = "";
                //for the ship name
                List<String> arguments = new ArrayList<>();
                for (String argument : args) {
                    arguments.add(argument);
                }

                //to add spaces between the names
                for (int i = 1; i < arguments.size(); i++) {
                    if (i == arguments.size() - 1) {
                        shipName += arguments.get(i);
                    } else {
                        shipName += arguments.get(i) + " ";
                    }
                }

                Ship ship = shipAccess.getShipByName(shipName);
                String direction = args[0];

                if (ship != null){
                    if (direction.equalsIgnoreCase("left") || direction.equalsIgnoreCase("right")){
                        if (PermissionCheck.hasPermission(ship, player)){
                            rotateShip(ship, direction,  player);
                        } else {
                            player.sendMessage("§4§ <MovingShips> You do not have permission to rotate this ship.");
                        }
                    } else {
                        player.sendMessage("§4§ <MovingShips> Invalid direction. Please use either left or right.");
                    }
                } else {
                    player.sendMessage("§4§ <MovingShips> Cannot find ship by that name.");
                }
            }
        } catch (Exception e){
            player.sendMessage("§4§ <MovingShips> Invalid command usage. Proper command usage: /RotateShip <Name of Ship> <Left/Right>.");
        }
        return true;
    }

    public void rotateShip(Ship ship, String directionRotate, Player player) {
        Location centerBlock = ship.getShipControlLocations().get("forward").getBlock().getLocation();
        HashMap<Location, Material> shipBlocksNew = new HashMap<>();

        if (!isClearToRotate(ship, directionRotate, centerBlock)) {
            player.sendMessage("§4§ <MovingShips> Target location would place one or more blocks inside another block.");
            ship.setSpeed(0);
            ship.setQueuedCommand(null);
            return;
        }

        PlayersOnShip.movePlayerRotate(directionRotate, centerBlock, PlayersOnShip.getPlayersOnShip(ship));

        for (Map.Entry<Location, Material> pair : ship.getShipBlocks().entrySet()) {
            shipBlocksNew.put(pair.getKey(), pair.getValue());
            pair.getKey().getBlock().setType(Material.AIR);
        }

        for (Map.Entry<Location, Material> pair : shipBlocksNew.entrySet()) {
            //finds the difference between the old block z and new block
            int differenceZ = centerBlock.getBlock().getZ() - pair.getKey().getBlock().getZ();
            int differenceX = centerBlock.getBlock().getX() - pair.getKey().getBlock().getX();

            if (directionRotate.equalsIgnoreCase("left")) {
                pair.getKey().setX(centerBlock.getBlock().getX() - differenceZ);
                pair.getKey().setZ(centerBlock.getBlock().getZ() + differenceX);
            } else if (directionRotate.equalsIgnoreCase("right")) {
                pair.getKey().setX(centerBlock.getBlock().getX() + differenceZ);
                pair.getKey().setZ(centerBlock.getBlock().getZ() - differenceX);
            }
            pair.getKey().getBlock().setType(pair.getValue());
        }

        HashMap<String, Location> newShipControlLocation = new HashMap<>();
        for (Map.Entry<String, Location> pair : ship.getShipControlLocations().entrySet()) {
            Location newLocation = pair.getValue();
            int differenceZ = centerBlock.getBlock().getZ() - pair.getValue().getBlock().getZ();
            int differenceX = centerBlock.getBlock().getX() - pair.getValue().getBlock().getX();

            if (directionRotate.equalsIgnoreCase("left")) {
                newLocation.setX(centerBlock.getBlock().getX() - differenceZ);
                newLocation.setZ(centerBlock.getBlock().getZ() + differenceX);
            } else {
                newLocation.setX(centerBlock.getBlock().getX() + differenceZ);
                newLocation.setZ(centerBlock.getBlock().getZ() - differenceX);
            }
            newShipControlLocation.put(pair.getKey(), newLocation);
        }
        updateDirection(ship, directionRotate);
        ship.setShipControlLocations(newShipControlLocation);
        ship.setShipBlocks(shipBlocksNew);

        shipAccess.saveShip();
    }

    public boolean isClearToRotate(Ship ship, String directionRotate, Location centerBlock){
        for (Location destinationBlocks : ship.getShipBlocks().keySet()){
            Location destinationBlock = destinationBlocks.getBlock().getLocation();
            int differenceZ = centerBlock.getBlock().getZ() - destinationBlock.getBlock().getZ();
            int differenceX = centerBlock.getBlock().getX() - destinationBlock.getBlock().getX();

            if (directionRotate.equalsIgnoreCase("left")){
                destinationBlock.setX(centerBlock.getBlock().getX() - differenceZ);
                destinationBlock.setZ(centerBlock.getBlock().getZ() + differenceX);
                if (!destinationBlock.getBlock().getType().equals(Material.AIR)
                        && !destinationBlock.getBlock().getType().equals(Material.WATER)) {
                    if (!isBlockInCurrentShipArea(destinationBlock, ship)) {
                        return false;
                    }
                }

            } else if (directionRotate.equalsIgnoreCase("right")){
                destinationBlock.setX(centerBlock.getBlock().getX() + differenceZ);
                destinationBlock.setZ(centerBlock.getBlock().getZ() - differenceX);
                if (!destinationBlock.getBlock().getType().equals(Material.AIR)
                        && !destinationBlock.getBlock().getType().equals(Material.WATER)) {
                    if (!isBlockInCurrentShipArea(destinationBlock, ship)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isBlockInCurrentShipArea(Location destinationBlock, Ship ship) {
        for (Location shipBlocks : ship.getShipBlocks().keySet()) {
            if (Integer.valueOf(destinationBlock.getBlock().getX()).equals(Integer.valueOf(shipBlocks.getBlock().getX())) &&
                    Integer.valueOf(destinationBlock.getBlock().getY()).equals(Integer.valueOf(shipBlocks.getBlock().getY())) &&
                    Integer.valueOf(destinationBlock.getBlock().getZ()).equals(Integer.valueOf(shipBlocks.getBlock().getZ()))) {
                return true;
            }
        }
        return false;
    }

    public void updateDirection(Ship ship, String directionRotate){
        String forwardDirection = ship.getFrontDirection();
        String fowardDirectionValue = ship.getFrontDirectionValue();
        if (directionRotate.equalsIgnoreCase("left")){
            if (forwardDirection.equalsIgnoreCase("X") && fowardDirectionValue.equalsIgnoreCase("-")){
                ship.setFrontDirection("Z"); ship.setFrontDirectionValue("+");
            } else if (forwardDirection.equalsIgnoreCase("Z") && fowardDirectionValue.equalsIgnoreCase("+")){
                ship.setFrontDirection("X"); ship.setFrontDirectionValue("+");
            } else if (forwardDirection.equalsIgnoreCase("X") && fowardDirectionValue.equalsIgnoreCase("+")){
                ship.setFrontDirection("Z"); ship.setFrontDirectionValue("-");
            } else if (forwardDirection.equalsIgnoreCase("Z") && fowardDirectionValue.equalsIgnoreCase("-")){
                ship.setFrontDirection("X"); ship.setFrontDirectionValue("-");
            }
        } else if (directionRotate.equalsIgnoreCase("right")){
            if (forwardDirection.equalsIgnoreCase("X") && fowardDirectionValue.equalsIgnoreCase("-")){
                ship.setFrontDirection("Z"); ship.setFrontDirectionValue("-");
            } else if (forwardDirection.equalsIgnoreCase("Z") && fowardDirectionValue.equalsIgnoreCase("+")){
                ship.setFrontDirection("X"); ship.setFrontDirectionValue("-");
            } else if (forwardDirection.equalsIgnoreCase("X") && fowardDirectionValue.equalsIgnoreCase("+")){
                ship.setFrontDirection("Z"); ship.setFrontDirectionValue("+");
            } else if (forwardDirection.equalsIgnoreCase("Z") && fowardDirectionValue.equalsIgnoreCase("-")){
                ship.setFrontDirection("X"); ship.setFrontDirectionValue("+");
            }
        }
    }
}
