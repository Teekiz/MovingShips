package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.utility.PermissionCheck;
import MovingShips.MovingShips.utility.PlayersOnShip;
import MovingShips.MovingShips.utility.ShipName;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;

import java.util.HashMap;
import java.util.Map;

public class MoveShip  implements CommandExecutor {

    ShipAccess shipAccess = ShipAccess.getInstance();
    Ship selectedShip;
    String shipName;
    String direction;
    String directionValue;
    int directionAmount;
    HashMap<Location, Material> shipBlocksNew;
    Location destinationBlock;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("<MovingShips> Only players can use this command.");
        }

        //todo handle exception for invalid args2

        Player player = (Player) commandSender;
        try {
            if (args.length < 3){
                player.sendMessage("<MovingShips> MoveShip arguments: <Forward/Back/Left/Right> <Distance> <Name of Ship>.");
            } else {
                direction = args[0];
                directionAmount = Integer.parseInt(args[1]);
                shipName = ShipName.filterShipName(args, 2);
                selectedShip = shipAccess.getShipByName(shipName);

                if (selectedShip == null) {
                    player.sendMessage("§4§ <MovingShips> Cannot find ship by that name.");
                } else {
                    if (direction.equalsIgnoreCase("forward")  || direction.equalsIgnoreCase("back") ||
                            direction.equalsIgnoreCase("left") || direction.equalsIgnoreCase("right")){
                        if (PermissionCheck.hasPermission(selectedShip, player)){
                            moveShip(selectedShip, direction, directionAmount, player, true);
                        } else {
                            player.sendMessage("§4§ <MovingShips> You do not have permission to move this ship.");
                        }
                    } else {
                        player.sendMessage("§4§ <MovingShips> Invalid direction. Please use either forward, back, left or right.");
                    }
                }
            }
        } catch (Exception e){
            player.sendMessage("§4§ <MovingShips> Invalid command usage. Proper command usage: /MoveShip <Forward/Back/Left/Right> <Distance> <Name of Ship>.");
        }

        return true;

    }

    //todo create a hashmap with location and face and then set it at the location if the special block equals it
    //moves the ship in one direction by specified amount
    public void moveShip(Ship ship, String directionCommand, int directionAmount, Player player, boolean isCommand) {

        direction = ship.getFrontDirection();
        directionValue = ship.getFrontDirectionValue();

        if (direction == null || direction.equalsIgnoreCase("null") ||
                directionValue == null || directionValue.equalsIgnoreCase("null")){
            player.sendMessage("§4§ <MovingShips> The ships forward direction has not been set. Please use /setshipforwarddirection before continuing.");
            ship.setSpeed(0);
            ship.setQueuedCommand(null);
            return;
        }

        //alters the direction of forward and back if necessary
        if (directionCommand.equalsIgnoreCase("forward") && directionValue.equalsIgnoreCase("-")){
                directionAmount = -directionAmount;
        } else if (directionCommand.equalsIgnoreCase("back") && directionValue.equalsIgnoreCase("-")){
            directionAmount = Math.abs(directionAmount);
        }

        //this method could be simplified - but I think it's a bit easier to read atm
        if (directionCommand.equalsIgnoreCase("left")){
            if (direction.equalsIgnoreCase("X") && directionValue.equalsIgnoreCase("-")){
                direction = "Z";
            } else if (direction.equalsIgnoreCase("Z") && directionValue.equalsIgnoreCase("+")){
                direction = "X";
            } else if (direction.equalsIgnoreCase("X") && directionValue.equalsIgnoreCase("+")){
                direction = "Z";
                directionAmount = -directionAmount;
            } else if (direction.equalsIgnoreCase("Z") && directionValue.equalsIgnoreCase("-")){
                direction = "X";
                directionAmount = -directionAmount;
            }
        } else if (directionCommand.equalsIgnoreCase("right")){
            if (direction.equalsIgnoreCase("X") && directionValue.equalsIgnoreCase("-")){
                direction = "Z";
                directionAmount = -directionAmount;
            } else if (direction.equalsIgnoreCase("Z") && directionValue.equalsIgnoreCase("-")){
                direction = "X";
            } else if (direction.equalsIgnoreCase("X") && directionValue.equalsIgnoreCase("+")){
                direction = "Z";
            } else if (direction.equalsIgnoreCase("Z") && directionValue.equalsIgnoreCase("+")){
                direction = "X";
                directionAmount = -directionAmount;
            }
        }

        //handles the commands, as the value should really only be above 0, back should only ever be called.
        if (isCommand && direction.equalsIgnoreCase("forward") && directionAmount < 0){
            directionAmount = Math.abs(directionAmount);
        } else if (isCommand && direction.equalsIgnoreCase("back") && directionAmount > 0){
            directionAmount = -directionAmount;
        }

        if (!isClearToMove(ship, direction, directionAmount)) {
            player.sendMessage("§4§ <MovingShips> Target location would place one or more blocks inside another block.");
            ship.setSpeed(0);
            ship.setQueuedCommand(null);
        } else {
            shipBlocksNew = new HashMap<>();
            //if the player uses the command version, it skips this as the command is a one time thing.
            if (PlayersOnShip.getPlayersOnShip(ship).size() == 0 && !isCommand){
                ship.setSpeed(0);
                ship.setQueuedCommand(null);
                player.sendMessage("<MovingShips> " + ship.getShipName() + " speed set to 0 because no players are onboard.");
                return;
            }

            PlayersOnShip.movePlayer(direction, directionAmount, PlayersOnShip.getPlayersOnShip(ship));

            //todo handle special blocks (e.g. ladders), get the face and place them last

            //todo set if at a certain level to water
            //sets the current blocks to air
            for (Map.Entry<Location, Material> pair : ship.getShipBlocks().entrySet()) {
                shipBlocksNew.put(pair.getKey(), pair.getValue());
                pair.getKey().getBlock().setType(Material.AIR);
            }

            //sets the new blocks to material.
            for (Map.Entry<Location, Material> pair : shipBlocksNew.entrySet()) {
                if (direction.equalsIgnoreCase("X")) {
                    pair.getKey().setX(pair.getKey().getX() + directionAmount);
                } else if (direction.equalsIgnoreCase("Z")) {
                    pair.getKey().setZ(pair.getKey().getZ() + directionAmount);
                }
                pair.getKey().getBlock().setType(pair.getValue());
            }

            //updates the ship control locations
            HashMap<String, Location> newShipControlLocation = new HashMap<>();

            for (Map.Entry<String, Location> pair : ship.getShipControlLocations().entrySet()) {
                Location newLocation = pair.getValue();
                if (direction.equalsIgnoreCase("X")) {
                    newLocation.setX(pair.getValue().getX() + directionAmount);
                } else if (direction.equalsIgnoreCase("Z")) {
                    newLocation.setZ(pair.getValue().getZ() + directionAmount);
                }
                newShipControlLocation.put(pair.getKey(), newLocation);
            }
            ship.setShipControlLocations(newShipControlLocation);
            ship.setShipBlocks(shipBlocksNew);

            shipAccess.saveShip();
        }
    }

    //checks to see if the block it is moving to either contains water or air
    public boolean isClearToMove(Ship ship, String direction, int directionAmount) {
        for (Location destinationBlocks : ship.getShipBlocks().keySet()) {
            destinationBlock = destinationBlocks.getBlock().getLocation();
            if (direction.equalsIgnoreCase("X")) {
                destinationBlock.setX(destinationBlock.getX() + directionAmount);
                if (!destinationBlock.getBlock().getType().equals(Material.AIR)
                        && !destinationBlock.getBlock().getType().equals(Material.WATER)) {
                    if (!isBlockInCurrentShipArea(destinationBlock, ship)) {
                        return false;
                    }
                }
            } else if (direction.equalsIgnoreCase("Z")) {
                destinationBlock.setZ(destinationBlock.getZ() + directionAmount);
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

    //this is a last resort to check if the block already exists in the existing ship (try to use this last)
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
}
