package MovingShips.MovingShips.commands;

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
import java.util.Map;

public class RotateShip implements CommandExecutor {
    ShipAccess shipAccess = ShipAccess.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }

        Player player = (Player) commandSender;

        if (args.length == 0 || args.length < 2){
            player.sendMessage("Please provide a ship name and rotation direction.");
            return true;
        } else {
            Ship ship = shipAccess.getShipByName(args[0]);
            String direction = args[1];
            rotateShip(ship, direction,  player);
        }
        return true;
    }

    //todo update shipcontrol locations
    public void rotateShip(Ship ship, String directionRotate, Player player) {
        Location centerBlock = ship.getShipControlLocations().get("forward").getBlock().getLocation();
        HashMap<Location, Material> shipBlocksNew = new HashMap<>();

        if (!isClearToRotate(ship, directionRotate, centerBlock)) {
            player.sendMessage("Target location would place one or blocks inside another block.");
            ship.setSpeed(0);
            return;
        }

        movePlayer(directionRotate, centerBlock, getPlayersOnShip(ship));

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

    public ArrayList<Player> getPlayersOnShip(Ship ship){
        ArrayList<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()){
            for (Location location : ship.getShipBlocks().keySet()){
                if (player.getLocation().getWorld().equals(location.getWorld())){
                    int playerY = player.getLocation().getBlockY();
                    int playerX = player.getLocation().getBlockX();
                    int playerZ = player.getLocation().getBlockZ();

                    int locationY = location.getBlockY();
                    int locationX = location.getBlockX();
                    int locationZ = location.getBlockZ();

                    //first checks X and Z, then Y. Y accounts for either standing or jumping.
                    if (Integer.compare(playerX, locationX) == 0  && Integer.compare(playerZ, locationZ) == 0){
                        if (Integer.compare(playerY - 1, locationY) == 0 || Integer.compare(playerY - 2, locationY) == 0){
                            if (!players.contains(player)){players.add(player);}
                        }
                    }
                }
            }
        }
        return players;
    }

    //todo - fix yaw
    public void movePlayer(String directionRotate, Location centerBlock, ArrayList<Player> playersOnShip){

        //rotation double based on code provided by worthless_hobo
        //https://www.spigotmc.org/threads/how-do-i-get-the-direction-the-player-is-facing.433419/

        for (Player player : playersOnShip){
            Location location = player.getLocation();
            float differenceZ = centerBlock.getBlock().getZ() - location.getBlock().getZ();
            float differenceX = centerBlock.getBlock().getX() - location.getBlock().getX();
            float rotation = 0;
            if (directionRotate.equalsIgnoreCase("left")){
                location.setX(centerBlock.getBlock().getX() - differenceZ);
                location.setZ(centerBlock.getBlock().getZ() + differenceX);
                rotation = player.getLocation().getYaw() - 90;
            } else if (directionRotate.equalsIgnoreCase("right")){
                location.setX(centerBlock.getBlock().getX() + differenceZ);
                location.setZ(centerBlock.getBlock().getZ() - differenceX);
                rotation = player.getLocation().getYaw() + 90;
            }
            if (rotation > 180) {
                float over = rotation - 180;
                rotation = -180 + over;
            }
            else if (rotation < -180) {
                float over = rotation + 180;
                rotation = 180 - over;
            }

            Location teleportLocation = new Location(centerBlock.getWorld(), location.getBlockX(),
                    location.getBlockY(), location.getBlockZ(), rotation, location.getPitch());
            player.teleport(teleportLocation);
        }
    }
}
