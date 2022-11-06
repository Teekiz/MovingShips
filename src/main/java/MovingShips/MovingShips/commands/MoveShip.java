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

public class MoveShip implements CommandExecutor {

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
            commandSender.sendMessage("Only players can use this command.");
        }

        //todo handle exception for invalid args2

        Player player = (Player) commandSender;

        if (args.length == 3){
            shipName = args[0];
            direction = args[1];
            directionAmount = Integer.parseInt(args[2]);
            selectedShip = shipAccess.getShipByName(shipName);

            if (selectedShip == null) {
                player.sendMessage("Cannot find ship.");
            } else {
                if (!direction.equalsIgnoreCase("forward")  && !direction.equalsIgnoreCase("back") &&
                        !direction.equalsIgnoreCase("left") && !direction.equalsIgnoreCase("right")){
                    moveShip(selectedShip, direction, directionAmount, player);
                } else {
                    player.sendMessage("Invalid direction.");
                }
            }
        } else {
            player.sendMessage("Invalid use of command");
        }
        return true;

    }
    //moves the ship in one direction by specified amount
    public void moveShip(Ship ship, String directionCommand, int directionAmount, Player player) {

        direction = ship.getFrontDirection();
        directionValue = ship.getFrontDirectionValue();

        //alters the direction of forward and back if necessary
        if (directionCommand.equalsIgnoreCase("forward") && directionValue.equalsIgnoreCase("-")){
                directionAmount = -directionAmount;
        } else if (directionCommand.equalsIgnoreCase("back") && directionValue.equalsIgnoreCase("-")){
            directionAmount = Math.abs(directionAmount);
        }

        //this method could be simplified - but i think its a bit easier to read atm
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

        if (!isClearToMove(ship, direction, directionAmount)) {
            player.sendMessage("Target location would place one or blocks inside another block.");
            ship.setSpeed(0);
        } else {
            shipBlocksNew = new HashMap<>();
            movePlayer(direction, directionAmount, getPlayersOnShip(ship));

            if (getPlayersOnShip(ship).size() == 0){
                ship.setSpeed(0);
                player.sendMessage(ship.getShipName() + " speed set to 0 because no players are onboard.");
                return;
            }

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

    //used to find all the players on the ship
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

    public void movePlayer(String direction, int directionAmount, ArrayList<Player> playersOnShip){
        for (Player player : playersOnShip){
            Location teleportLocation = player.getLocation();
            if (direction.equalsIgnoreCase("X")){
                teleportLocation.setX(teleportLocation.getX() + directionAmount);
            } else if (direction.equalsIgnoreCase("Z")){
                teleportLocation.setZ(teleportLocation.getZ() + directionAmount);
            }
            player.teleport(teleportLocation);
        }
    }
}
