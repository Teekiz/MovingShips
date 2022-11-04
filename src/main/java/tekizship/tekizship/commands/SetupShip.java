package tekizship.tekizship.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tekizship.tekizship.ships.ShipAccess;

import java.util.*;

public class SetupShip implements CommandExecutor {

    private static final HashMap<Player, Location> storedCommands = new HashMap<>();
    private static final HashMap<Player, String> storedDirection = new HashMap<>();
    private static final HashMap<Player, String> storedName = new HashMap<>();

    int maxSize= 5000;
    Set<Material> set = null;
    ShipAccess shipAccess = ShipAccess.getInstance();

    //creation
    CreateShip createShip = new CreateShip();
    SetShipControls setShipControls = new SetShipControls();
    SetShipForwardDirection setShipForwardDirection = new SetShipForwardDirection();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }

        Player player = (Player) commandSender;
        String shipName =  "";
        boolean isConfirmed = false;
        boolean isCanceled = false;
        boolean isSet = false;

        List<String> arguments = new ArrayList<>();
        for (String argument : args){
            arguments.add(argument);
        }

        //to add spaces between the names
        for (int i = 1; i < arguments.size(); i++){
            if (i == arguments.size()-1){
                shipName+=arguments.get(i);
            }  else { shipName+=arguments.get(i)+" ";}
        }

        if (args.length > 0){
            if (!args[0].equalsIgnoreCase("confirm") && !args[0].equalsIgnoreCase("cancel") && !args[0].equalsIgnoreCase("set")){
                player.sendMessage("<MovingShips> unrecognised command.");
            } else {
                if (args[0].equalsIgnoreCase("confirm")){
                    isConfirmed = true;
                } else if (args[0].equalsIgnoreCase("set")) {
                    isSet = true;
                } else if (args[0].equalsIgnoreCase("cancel")){
                    isCanceled = true;
                }
            }
        }

        //this means the player hasn't stored anything just yet.
        if (getPlayerFromStoredCommands(player).isEmpty()){
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (1/10) --- <MovingShips>");
            player.sendMessage("Please look at first location block and type '/ShipSetup set'. The coordinate will be used to define the ship boundaries.");
            player.sendMessage("Ensure that the target is large enough to cover the whole ship.");
        }

        /*
                PART ONE: DEFINING THE SHIP LOCATION
        */

        if (getPlayerFromStoredCommands(player).isEmpty() && isSet){
            Location firstLocation = player.getTargetBlock(set, 1).getLocation();
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (2/10) --- <MovingShips>");
            player.sendMessage("First location set at " + firstLocation.getBlockX() + " (X), " +
                    firstLocation.getBlockY() + " (Y), " + firstLocation.getBlockZ() + " (Z).");
            player.sendMessage("Please look at second location block and type '/ShipSetup set");
            storedCommands.put(player, firstLocation);
        }

        if (getPlayerFromStoredCommands(player).size() == 1 && isSet){
            Location firstLocation = getLocationFromStoredCommands(player).get(0);
            Location secondLocation = player.getTargetBlock(set, 1).getLocation();
            player.sendMessage("Second location set at " + secondLocation.getBlockX() + " (X), " +
                    secondLocation.getBlockY() + " (Y), " + secondLocation.getBlockZ() + " (Z).");
            if (blocksSelected(secondLocation, firstLocation, player.getWorld()).size() > maxSize){
                player.sendMessage("The selected size is too large. Please try again.");
                removePlayerFromStoredCommands(player);
            } else if (createShip.doBlocksOverlap(blocksSelected(secondLocation, firstLocation, player.getWorld()))) {
                player.sendMessage("The selected area overlaps with an existing ship. Please try again.");
                player.sendMessage("The selected size is too large. Please try again.");
            } else {
                player.sendMessage(blocksSelected(secondLocation, firstLocation, player.getWorld()) + " blocks selected.");
                player.sendMessage("Please use '/ShipSetup confirm' to continue or '/ShipSetup cancel' to restart.");
                storedCommands.put(player, secondLocation);
            }
        }

        /*
                PART TWO: DEFINING THE SHIP FORWARD DIRECTION
        */

        if (getPlayerFromStoredCommands(player).size() == 2 && isConfirmed){
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (3/10) --- <MovingShips>");
            player.sendMessage("Please look towards the front of the ship and type '/ShipSetup set'.");
        }

        if (getPlayerFromStoredCommands(player).size() == 2 && isSet){
            String direction = setShipForwardDirection.getShipDirection(player);
            player.sendMessage("Ship direction set to " + direction + ".");
            player.sendMessage("Please use '/ShipSetup confirm' to continue or '/ShipSetup cancel' to restart.");
            storedDirection.put(player, direction);
        }

        /*
             PART THREE: DEFINING THE SHIP CONTROLS
        */

        if (getPlayerFromStoredCommands(player).size() == 2 && !getDirectionFromPlayer(player).isEmpty() && isConfirmed){
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (4/10) --- <MovingShips>");
            player.sendMessage("Please look at the block used to set the ship forward and use '/ShipSetup set'.");
        }

        if (getPlayerFromStoredCommands(player).size() == 2 && !getDirectionFromPlayer(player).isEmpty() && isSet){
            Location forwardLocation = player.getTargetBlock(set, 5).getLocation();
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (5/10) --- <MovingShips>");
            player.sendMessage("Please look at the block used to set the ship backwards and use '/ShipSetup set'.");
            storedCommands.put(player, forwardLocation);
        }

        if (getPlayerFromStoredCommands(player).size() == 3 && !getDirectionFromPlayer(player).isEmpty() && isSet){
            Location backLocation = player.getTargetBlock(set, 5).getLocation();
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (6/10) --- <MovingShips>");
            player.sendMessage("Please look at the block used to set the ship left and use '/ShipSetup set'.");
            storedCommands.put(player, backLocation);
        }

        if (getPlayerFromStoredCommands(player).size() == 4 && !getDirectionFromPlayer(player).isEmpty() && isSet){
            Location leftLocation = player.getTargetBlock(set, 5).getLocation();
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (7/10) --- <MovingShips>");
            player.sendMessage("Please look at the block used to set the ship right and use '/ShipSetup set'.");
            storedCommands.put(player, leftLocation);
        }

        if (getPlayerFromStoredCommands(player).size() == 5 && !getDirectionFromPlayer(player).isEmpty() && isSet){
            Location rightLocation = player.getTargetBlock(set, 5).getLocation();
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (8/10) --- <MovingShips>");
            player.sendMessage("Please look at the block used to rotate the ship left and use '/ShipSetup set'.");
            storedCommands.put(player, rightLocation);
        }

        if (getPlayerFromStoredCommands(player).size() == 6 && !getDirectionFromPlayer(player).isEmpty() && isSet){
            Location rotateLeftLocation = player.getTargetBlock(set, 5).getLocation();
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (9/10) --- <MovingShips>");
            player.sendMessage("Please look at the block used to rotate the ship right and use '/ShipSetup set'.");
            storedCommands.put(player, rotateLeftLocation);
        }

        if (getPlayerFromStoredCommands(player).size() == 7 && !getDirectionFromPlayer(player).isEmpty() && isSet){
            Location forwardLocation = getLocationFromStoredCommands(player).get(2);
            Location backLocation = getLocationFromStoredCommands(player).get(3);
            Location leftLocation = getLocationFromStoredCommands(player).get(4);
            Location rightLocation = getLocationFromStoredCommands(player).get(5);
            Location rotateLeftLocation = getLocationFromStoredCommands(player).get(6);
            Location rotateRightLocation = player.getTargetBlock(set, 5).getLocation();

            player.sendMessage("Forward control location set at " + forwardLocation.getBlockX() + " (X), " +
                    forwardLocation.getBlockY() + " (Y), " + backLocation.getBlockZ() + " (Z).");
            player.sendMessage("Back control location set at " + backLocation.getBlockX() + " (X), " +
                    forwardLocation.getBlockY() + " (Y), " + backLocation.getBlockZ() + " (Z).");
            player.sendMessage("Left control location set at " + leftLocation.getBlockX() + " (X), " +
                    leftLocation.getBlockY() + " (Y), " + leftLocation.getBlockZ() + " (Z).");
            player.sendMessage("Right control location set at " + rightLocation.getBlockX() + " (X), " +
                    rightLocation.getBlockY() + " (Y), " + rightLocation.getBlockZ() + " (Z).");
            player.sendMessage("Rotate left control location set at " + rotateLeftLocation.getBlockX() + " (X), " +
                    rotateLeftLocation.getBlockY() + " (Y), " + rotateLeftLocation.getBlockZ() + " (Z).");
            player.sendMessage("Rotate right control location set at " + rotateRightLocation.getBlockX() + " (X), " +
                    rotateRightLocation.getBlockY() + " (Y), " + rotateRightLocation.getBlockZ() + " (Z).");

            //todo check if they are on the ship
            player.sendMessage("Please use '/ShipSetup confirm' to continue or '/ShipSetup cancel' to restart.");
            storedCommands.put(player, rotateRightLocation);
        }

        /*
             PART FOUR: NAMING THE SHIP
        */

        if (getPlayerFromStoredCommands(player).size() == 8 && !getDirectionFromPlayer(player).isEmpty() && isConfirmed){
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (10/10) --- <MovingShips>");
            player.sendMessage("Please set a ship name. Please use '/ShipSetup set <name of ship>' to continue.");
        }

        if (getPlayerFromStoredCommands(player).size() == 8 && !getDirectionFromPlayer(player).isEmpty() && isSet){
            if (shipAccess.getShipByName(shipName) != null){
                player.sendMessage("That name is taken. Please try again using '/ShipSetup set <name of ship>' to continue.");
                removePlayerFromStoredName(player);
            } else {
                player.sendMessage("Ship name is set to:" + shipName);
                player.sendMessage("Please use '/ShipSetup confirm' to continue, /ShipSetup cancel' to restart or '/ShipSetup set <name of ship>' to rename.");
                storedName.put(player, shipName);
            }
        }

        /*
             PART FIVE: CREATING THE SHIP
        */

        if (getPlayerFromStoredCommands(player).size() == 8 && !getDirectionFromPlayer(player).isEmpty() && !getNameFromPlayer(player).isEmpty() && isConfirmed){
            Location firstLocation = getLocationFromStoredCommands(player).get(0);
            Location secondLocation = getLocationFromStoredCommands(player).get(1);

            Location forwardLocation = getLocationFromStoredCommands(player).get(2);
            Location backLocation = getLocationFromStoredCommands(player).get(3);
            Location leftLocation = getLocationFromStoredCommands(player).get(4);
            Location rightLocation = getLocationFromStoredCommands(player).get(5);
            Location rotateLeftLocation = getLocationFromStoredCommands(player).get(6);
            Location rotateRightLocation = getLocationFromStoredCommands(player).get(7);

            String nameOfShip = getNameFromPlayer(player);
            String direction = getDirectionFromPlayer(player);

            createShip.createShip(firstLocation, secondLocation, nameOfShip, player, player.getWorld());
            setShipForwardDirection.setShipDirection(shipAccess.getShipByName(nameOfShip), player, direction);

            setShipControls.setControl(nameOfShip, "forward", forwardLocation, player);
            setShipControls.setControl(nameOfShip, "back", backLocation, player);
            setShipControls.setControl(nameOfShip, "left", leftLocation, player);
            setShipControls.setControl(nameOfShip, "right", rightLocation, player);
            setShipControls.setControl(nameOfShip, "rotateLeft", rotateLeftLocation, player);
            setShipControls.setControl(nameOfShip, "rotateRight", rotateRightLocation, player);

            player.sendMessage("§b§ <MovingShips> --- Ship Setup Complete --- <MovingShips>");

            removePlayerFromStoredCommands(player);
            removePlayerFromStoredDirection(player);
            removePlayerFromStoredName(player);
        }

        //once complete, delete and created ship

        else if (isCanceled){
            removePlayerFromStoredCommands(player);
            removePlayerFromStoredDirection(player);
            removePlayerFromStoredName(player);
            player.sendMessage("<MovingShips> Setup cancelled.");
        }

        return true;
    }

    public HashMap<Player, Location> getPlayerFromStoredCommands(Player player){
        HashMap<Player, Location> playerStoredCommands = new HashMap<>();
        for (Map.Entry<Player, Location> pair : storedCommands.entrySet()){
            if (pair.getKey().getName().equals(player.getName())){
                playerStoredCommands.put(pair.getKey(), pair.getValue());
            }
        }
        return playerStoredCommands;
    }

    public String getDirectionFromPlayer(Player player){
        for (Map.Entry<Player, String> pair : storedDirection.entrySet()){
            if (pair.getKey().getName().equals(player.getName())){
               return pair.getValue();
            }
        }
        return "";
    }

    public String getNameFromPlayer(Player player){
        for (Map.Entry<Player, String> pair : storedName.entrySet()){
            if (pair.getKey().getName().equals(player.getName())){
                return pair.getValue();
            }
        }
        return "";
    }

    public void removePlayerFromStoredCommands(Player player){
        for (Map.Entry<Player, Location> pair : storedCommands.entrySet()){
            if (pair.getKey().getName().equals(player.getName())){
                storedCommands.remove(pair.getKey(), pair.getValue());
            }
        }
    }

    public void removePlayerFromStoredDirection(Player player){
        for (Map.Entry<Player, String> pair : storedDirection.entrySet()){
            if (pair.getKey().getName().equals(player.getName())){
                storedDirection.remove(pair.getKey(), pair.getValue());
            }
        }
    }

    public void removePlayerFromStoredName(Player player){
        for (Map.Entry<Player, String> pair : storedName.entrySet()){
            if (pair.getKey().getName().equals(player.getName())){
                storedName.remove(pair.getKey(), pair.getValue());
            }
        }
    }

    public ArrayList<Location> getLocationFromStoredCommands(Player player){
        ArrayList<Location> locations = new ArrayList<>();
        player.sendMessage("up to here");
        for (Location location : getPlayerFromStoredCommands(player).values()){
            location.add(location);
        }
        return locations;
    }

    public HashMap<Location, Material> blocksSelected (Location startLocation, Location endLocation, World world){
        HashMap<Location, Material> LocationsInArea = new HashMap<>();

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
                    if (!currentLocation.getBlock().getType().toString().equals("AIR") || !currentLocation.getBlock().getType().toString().equals("Water")) {
                        if (LocationsInArea.size() > maxSize){
                            LocationsInArea.put(currentLocation, currentLocation.getBlock().getType());
                            return LocationsInArea;
                        } else {
                            LocationsInArea.put(currentLocation, currentLocation.getBlock().getType());
                        }
                    }
                }
            }
        }
        return LocationsInArea;
    }


}
