package tekizship.tekizship.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tekizship.tekizship.ships.Ship;
import tekizship.tekizship.ships.ShipAccess;
import java.util.HashMap;
import java.util.Map;

public class MoveShip implements CommandExecutor {

    ShipAccess shipAccess = ShipAccess.getInstance();
    Ship selectedShip;
    String shipName;
    String direction;
    int directionAmount;
    HashMap<Location, Material> shipBlocksNew;
    Location destinationBlock;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }

        Player player = (Player) commandSender;
        shipName = args[0];
        direction = args[1];
        directionAmount = Integer.valueOf(args[2]);
        selectedShip = shipAccess.getShipByName(shipName);

        if (selectedShip == null) {
            player.sendMessage("Cannot find ship.");
        }
        moveShip(selectedShip, direction, directionAmount, player);

        return true;

    }
    //moves the ship in one direction by specified amount
    public void moveShip(Ship ship, String direction, int directionAmount, Player player) {

        if (!isClearToMove(ship, direction, directionAmount)) {
            player.sendMessage("Target location would place one or blocks inside another block.");
        } else {
            shipBlocksNew = new HashMap<>();

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
                } else {
                    newLocation.setZ(pair.getValue().getZ() + directionAmount);
                }
                newShipControlLocation.put(pair.getKey(), newLocation);
            }
            ship.setShipControlLocations(newShipControlLocation);
            ship.setShipBlocks(shipBlocksNew);
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
