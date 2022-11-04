package tekizship.tekizship.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tekizship.tekizship.ships.Ship;
import tekizship.tekizship.ships.ShipAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CreateShip implements CommandExecutor {

    HashMap<Location, Material> shipBlocksCurrent;
    ShipAccess shipAccess = ShipAccess.getInstance();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String [] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }

        Player player = (Player) commandSender;

        if (command.getName().equalsIgnoreCase("createship")){
            try {
                //todo handle exception for missing values
                String shipName = args[6];
                if (shipName.equals("") || shipName.isEmpty() || shipName == null){
                    player.sendMessage("createship arguments: <x> <y> <z> <x> <y> <z> <ship name>.");
                }

                Location startLocation = new Location(player.getWorld(), Integer.valueOf(args[0]), Integer.valueOf(args[1]), Integer.valueOf(args[2]));
                Location endLocation = new Location(player.getWorld(), Integer.valueOf(args[3]), Integer.valueOf(args[4]), Integer.valueOf(args[5]));
                createShip(startLocation, endLocation, shipName, player, player.getWorld());

            } catch (Exception e){
                player.sendMessage("Invalid command usage. Proper command usage: /createship <x> <y> <z> <x> <y> <z> <name of ship>");
            }
        }
        return true;
    }

    //decided to split the methods up, this focuses on the creation of the ship
    public void createShip(Location startLocation, Location endLocation, String shipName, Player player, World targetWorld){
        shipBlocksCurrent = new HashMap<>();

        for (Location block : getLocationsInArea(startLocation, endLocation, targetWorld)) {
            Material blockDetailMaterial = block.getBlock().getType();
            shipBlocksCurrent.put(block, blockDetailMaterial);
        }

        //checks to see if the current place is empty
        if (shipBlocksCurrent.isEmpty()){
            player.sendMessage("You have not selected any blocks.");
        } else {
            //returns null if there is no ship by that name
            if ((shipAccess.getShipByName(shipName) == null)) {
                //return false if no blocks overlap
                if (!doBlocksOverlap(shipBlocksCurrent)) {
                    shipAccess.createShip(shipName, player.getName(), shipBlocksCurrent);
                    player.sendMessage("Ship created: " + shipName + ".");
                } else {
                    player.sendMessage("Cannot create ship. Blocks overlap with another ship.");
                }
            } else {
                player.sendMessage("The ship name is taken. Please try another name.");
            }
        }
    }

    //checks area for existing ship
    public Boolean doBlocksOverlap(HashMap <Location, Material> checkBlocks){
        for (Ship ship : shipAccess.getShips()){
            for (Location existingBlock : ship.getShipBlocks().keySet()){
                for (Location destinationBlock : checkBlocks.keySet()){
                    if (Integer.valueOf(existingBlock.getBlock().getX()).equals(Integer.valueOf(destinationBlock.getBlock().getX())) &&
                            Integer.valueOf(existingBlock.getBlock().getY()).equals(Integer.valueOf(destinationBlock.getBlock().getY())) &&
                            Integer.valueOf(existingBlock.getBlock().getZ()).equals(Integer.valueOf(destinationBlock.getBlock().getZ()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<Location> getLocationsInArea (Location startLocation, Location endLocation, World world){
        ArrayList<Location> LocationsInArea = new ArrayList<>();

        int startingX = Math.min((int) startLocation.getX(), (int) endLocation.getX());
        int endingX = Math.max((int) startLocation.getX(), (int) endLocation.getX());

        int startingY = Math.min((int) startLocation.getY(), (int) endLocation.getY());
        int endingY = Math.max((int) startLocation.getY(), (int) endLocation.getY());

        int startingZ = Math.min((int) startLocation.getZ(), (int) endLocation.getZ());
        int endingZ = Math.max((int) startLocation.getZ(), (int) endLocation.getZ());

        for (int x = startingX; x <= endingX; x++) {
            for (int y = startingY; y <= endingY; y++) {
                for (int z = startingZ; z <= endingZ; z++) {
                    Location currentLocation = new Location(world, x, y, z);
                    if (!currentLocation.getBlock().getType().toString().equals("AIR")) {
                            LocationsInArea.add(currentLocation);
                    }
                }
            }
        }
        return LocationsInArea;
    }
}
