package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.MovingShipsConfiguration;
import MovingShips.MovingShips.utility.ShipName;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateShip implements CommandExecutor {

    HashMap<Location, Material> shipBlocksCurrent;
    ShipAccess shipAccess = ShipAccess.getInstance();
    String shipName;
    int maxSize = MovingShipsConfiguration.getMaxShipSize();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String [] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("<MovingShips> Only players can use this command.");
        }

        Player player = (Player) commandSender;
        if (command.getName().equalsIgnoreCase("createship")){
            try {
                if (args.length < 7){
                    player.sendMessage("<MovingShips> CreateShip arguments: <X1> Y1> <Z1> <X2> <Y2> <Z2> <Name of Ship>.");
                } else {
                    shipName = ShipName.filterShipName(args, 6);
                    Location startLocation = new Location(player.getWorld(), Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                    Location endLocation = new Location(player.getWorld(), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
                    createShip(startLocation, endLocation, shipName, player, player.getWorld());
                }
            } catch (Exception e){
                player.sendMessage("§4§ <MovingShips> Invalid command usage. Proper command usage: /CreateShip <X1> Y1> <Z1> <X2> <Y2> <Z2> <Name of Ship>.");
            }
        }
        return true;
    }

    //decided to split the methods up, this focuses on the creation of the ship
    public void createShip(Location startLocation, Location endLocation, String shipName, Player player, World targetWorld){
        shipBlocksCurrent = new HashMap<>();

        if (getLocationsInArea(startLocation, endLocation, targetWorld).size() > maxSize){
            player.sendMessage("§4§ <MovingShips> This ship is too big. Please select a smaller area.");
        }
        for (Location block : getLocationsInArea(startLocation, endLocation, targetWorld)) {
            Material blockDetailMaterial = block.getBlock().getType();
            shipBlocksCurrent.put(block, blockDetailMaterial);
        }

        //checks to see if the current place is empty
        if (shipBlocksCurrent.isEmpty()){
            player.sendMessage("§4§ <MovingShips> You have not selected any blocks.");
        } else {
            //returns null if there is no ship by that name
            if ((shipAccess.getShipByName(shipName) == null)) {
                //return false if no blocks overlap
                if (!doBlocksOverlap(shipBlocksCurrent)) {
                    shipAccess.createShip(shipName, player.getName(), shipBlocksCurrent);
                    player.sendMessage("<MovingShips> Ship created: " + shipName + ".");
                    shipAccess.saveShip();
                } else {
                    player.sendMessage("§4§ <MovingShips> Cannot create ship. One or more blocks overlap with an existing ship.");
                }
            } else {
                player.sendMessage("§4§ <MovingShips> That ship name is taken. Please try another name.");
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
