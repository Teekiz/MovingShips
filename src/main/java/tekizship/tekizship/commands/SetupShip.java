package tekizship.tekizship.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tekizship.tekizship.ships.ShipAccess;
import tekizship.tekizship.ships.ShipSetup;

import java.util.*;

public class SetupShip implements CommandExecutor {

    private static final ArrayList<ShipSetup> storedShipSetup = new ArrayList<>();

    int maxSize = 5000;
    Set<Material> set = null;
    ShipAccess shipAccess;
    ShipSetup shipSetup;

    //creation
    CreateShip createShip;
    SetShipControls setShipControls;
    SetShipForwardDirection setShipForwardDirection;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }

        shipAccess = ShipAccess.getInstance();
        createShip = new CreateShip();
        setShipControls = new SetShipControls();
        setShipForwardDirection = new SetShipForwardDirection();


        Player player = (Player) commandSender;
        String shipName = "";
        boolean isConfirmed = false;
        boolean isCanceled = false;
        boolean isSet = false;
        boolean isUndone = false;

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

        if (args.length > 0) {
            if (!args[0].equalsIgnoreCase("confirm") && !args[0].equalsIgnoreCase("cancel") &&
                    !args[0].equalsIgnoreCase("set") && !args[0].equalsIgnoreCase("undo")) {
                player.sendMessage("<MovingShips> Unrecognised command.");
            } else {
                if (args[0].equalsIgnoreCase("confirm")) {
                    isConfirmed = true;
                } else if (args[0].equalsIgnoreCase("set")) {
                    isSet = true;
                } else if (args[0].equalsIgnoreCase("cancel")) {
                    isCanceled = true;
                } else if (args[0].equalsIgnoreCase("undo")) {
                    isUndone = true;
                }
            }
        }

        //this means the player hasn't stored anything just yet.
        if (getShipSetup(player) == null) {
            player.sendMessage("§b§ <MovingShips> --- Ship Setup (1/10) --- <MovingShips>");
            player.sendMessage("Please look at first location block and type '/ShipSetup set'. The coordinate will be used to define the ship boundaries.");
            player.sendMessage("Ensure that the target is large enough to cover the whole ship.");
            shipSetup = new ShipSetup(player);
            storedShipSetup.add(shipSetup);
        } else {
            shipSetup = getShipSetup(player);



        /*
             PART FIVE: CREATING THE SHIP
        */

            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.isControlsSet() && shipSetup.getShipName() != null && isConfirmed) {
                Location firstLocation = shipSetup.getFirstLocation();
                Location secondLocation = shipSetup.getSecondLocation();

                Location forwardLocation = shipSetup.getForward();
                Location backLocation = shipSetup.getBack();
                Location leftLocation = shipSetup.getLeft();
                Location rightLocation = shipSetup.getRight();
                Location rotateLeftLocation = shipSetup.getRotateLeft();
                Location rotateRightLocation = shipSetup.getRotateRight();

                String nameOfShip = shipSetup.getShipName();
                String direction = shipSetup.getShipDirection();

                createShip.createShip(firstLocation, secondLocation, nameOfShip, player, player.getWorld());
                setShipForwardDirection.setShipDirection(shipAccess.getShipByName(nameOfShip), player, direction);

                setShipControls.setControl(nameOfShip, "forward", forwardLocation, player);
                setShipControls.setControl(nameOfShip, "back", backLocation, player);
                setShipControls.setControl(nameOfShip, "left", leftLocation, player);
                setShipControls.setControl(nameOfShip, "right", rightLocation, player);
                setShipControls.setControl(nameOfShip, "rotateLeft", rotateLeftLocation, player);
                setShipControls.setControl(nameOfShip, "rotateRight", rotateRightLocation, player);

                player.sendMessage("§b§ <MovingShips> --- Ship Setup Complete --- <MovingShips>");

                removeShipSetup(shipSetup);
                shipAccess.saveShip();
            }

        /*
             PART FOUR: NAMING THE SHIP
        */
            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.isControlsSet() && shipSetup.getShipName() == null && isConfirmed) {
                player.sendMessage("§b§ <MovingShips> --- Ship Setup (10/10) --- <MovingShips>");
                player.sendMessage("Please set a ship name. Please use '/ShipSetup set <name of ship>' to continue.");
            }

            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.isControlsSet() && shipSetup.getShipName() == null && isSet) {
                if (shipAccess.getShipByName(shipName) != null) {
                    player.sendMessage("That name is taken. Please try again using '/ShipSetup set <name of ship>' to continue.");
                } else {
                    player.sendMessage("Ship name is set to: " + shipName);
                    player.sendMessage("Please use '/ShipSetup confirm' to continue, /ShipSetup cancel' to restart or '/ShipSetup set <name of ship>' to rename.");
                    shipSetup.setShipName(shipName);
                }
            }

        /*
             PART THREE: DEFINING THE SHIP CONTROLS
        */

            //Part Three is back to front

            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.getForward() != null && shipSetup.getBack() != null
                    && shipSetup.getLeft() != null && shipSetup.getRight() != null && shipSetup.getRotateLeft() != null && shipSetup.getRotateRight()
                    == null && isSet) {
                Location forwardLocation = shipSetup.getForward();
                Location backLocation = shipSetup.getBack();
                Location leftLocation = shipSetup.getLeft();
                Location rightLocation = shipSetup.getRight();
                Location rotateLeftLocation = shipSetup.getRotateLeft();
                Location rotateRightLocation = player.getTargetBlock(set, 5).getLocation();

                if (isControlOnShip(rotateRightLocation, blocksSelected(shipSetup.getFirstLocation(), shipSetup.getSecondLocation(), player.getWorld()), player)) {
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
                    player.sendMessage("Please use '/ShipSetup confirm' to continue or '/ShipSetup cancel' to restart or '/ShipSetup undo' to set the controls again.");
                    shipSetup.setRotateRight(rotateRightLocation);
                }
            }

            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.getForward() != null && shipSetup.getBack() != null
                    && shipSetup.getLeft() != null && shipSetup.getRight() != null && shipSetup.getRotateLeft() == null && shipSetup.getRotateRight()
                    == null && isSet) {
                Location rotateLeftLocation = player.getTargetBlock(set, 5).getLocation();
                if (isControlOnShip(rotateLeftLocation, blocksSelected(shipSetup.getFirstLocation(), shipSetup.getSecondLocation(), player.getWorld()), player)){
                    player.sendMessage("§b§ <MovingShips> --- Ship Setup (9/10) --- <MovingShips>");
                    player.sendMessage("Please look at the block used to rotate the ship right and use '/ShipSetup set'.");
                    shipSetup.setRotateLeft(rotateLeftLocation);
                }
            }

            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.getForward() != null && shipSetup.getBack() != null
                    && shipSetup.getLeft() != null && shipSetup.getRight() == null && shipSetup.getRotateLeft() == null && shipSetup.getRotateRight()
                    == null && isSet) {
                Location rightLocation = player.getTargetBlock(set, 5).getLocation();
                if (isControlOnShip(rightLocation, blocksSelected(shipSetup.getFirstLocation(), shipSetup.getSecondLocation(), player.getWorld()), player)) {
                    player.sendMessage("§b§ <MovingShips> --- Ship Setup (8/10) --- <MovingShips>");
                    player.sendMessage("Please look at the block used to rotate the ship left and use '/ShipSetup set'.");
                    shipSetup.setRight(rightLocation);
                }
            }

            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.getForward() != null && shipSetup.getBack() != null
                    && shipSetup.getLeft() == null && shipSetup.getRight() == null && shipSetup.getRotateLeft() == null && shipSetup.getRotateRight()
                    == null && isSet) {
                Location leftLocation = player.getTargetBlock(set, 5).getLocation();
                if (isControlOnShip(leftLocation, blocksSelected(shipSetup.getFirstLocation(), shipSetup.getSecondLocation(), player.getWorld()), player)) {
                    player.sendMessage("§b§ <MovingShips> --- Ship Setup (7/10) --- <MovingShips>");
                    player.sendMessage("Please look at the block used to set the ship right and use '/ShipSetup set'.");
                    shipSetup.setLeft(leftLocation);
                }
            }

            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.getForward() != null && shipSetup.getBack() == null
                    && shipSetup.getLeft() == null && shipSetup.getRight() == null && shipSetup.getRotateLeft() == null && shipSetup.getRotateRight()
                    == null && isSet) {
                Location backLocation = player.getTargetBlock(set, 5).getLocation();
                if (isControlOnShip(backLocation, blocksSelected(shipSetup.getFirstLocation(), shipSetup.getSecondLocation(), player.getWorld()), player)) {
                    player.sendMessage("§b§ <MovingShips> --- Ship Setup (6/10) --- <MovingShips>");
                    player.sendMessage("Please look at the block used to set the ship left and use '/ShipSetup set'.");
                    shipSetup.setBack(backLocation);
                }
            }

            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.getForward() == null && shipSetup.getBack() == null
                    && shipSetup.getLeft() == null && shipSetup.getRight() == null && shipSetup.getRotateLeft() == null && shipSetup.getRotateRight()
                    == null && isSet) {
                Location forwardLocation = player.getTargetBlock(set, 5).getLocation();
                if (isControlOnShip(forwardLocation, blocksSelected(shipSetup.getFirstLocation(), shipSetup.getSecondLocation(), player.getWorld()), player)) {
                    player.sendMessage("§b§ <MovingShips> --- Ship Setup (5/10) --- <MovingShips>");
                    player.sendMessage("Please look at the block used to set the ship backwards and use '/ShipSetup set'.");
                    shipSetup.setForward(forwardLocation);
                }
            }

            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.getForward() == null && shipSetup.getBack() == null
                    && shipSetup.getLeft() == null && shipSetup.getRight() == null && shipSetup.getRotateLeft() == null && shipSetup.getRotateRight()
                    == null && isConfirmed) {
                player.sendMessage("§b§ <MovingShips> --- Ship Setup (4/10) --- <MovingShips>");
                player.sendMessage("Please look at the block used to set the ship forward and use '/ShipSetup set'.");
            }

            //this repeats part three.
            if (shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.getForward() != null && shipSetup.getBack() != null
                    && shipSetup.getLeft() != null && shipSetup.getRight() != null && shipSetup.getRotateLeft() != null && shipSetup.getRotateRight()
                    != null && isUndone) {
                shipSetup.setForward(null);
                shipSetup.setBack(null);
                shipSetup.setLeft(null);
                shipSetup.setRight(null);
                shipSetup.setRotateLeft(null);
                shipSetup.setRotateRight(null);

                player.sendMessage("§b§ <MovingShips> --- Ship Setup (4/10) --- <MovingShips>");
                player.sendMessage("Please look at the block used to set the ship forward and use '/ShipSetup set'.");
            }

        /*
             PART TWO: DEFINING THE SHIP FORWARD DIRECTION
        */

            if (shipSetup.isFirstAndSecondLocationSet() && shipSetup.getShipDirection() == null && isConfirmed) {
                player.sendMessage("§b§ <MovingShips> --- Ship Setup (3/10) --- <MovingShips>");
                player.sendMessage("Please look towards the front of the ship and type '/ShipSetup set'.");
            }

            if (shipSetup.isFirstAndSecondLocationSet() && shipSetup.getShipDirection() == null && isSet) {
                String direction = setShipForwardDirection.getShipDirection(player);
                player.sendMessage("Ship direction set to " + direction + ".");
                player.sendMessage("Please use '/ShipSetup confirm' to continue or '/ShipSetup cancel' to restart.");
                shipSetup.setShipDirection(direction);

            }

        /*
            PART ONE: DEFINING THE SHIP LOCATION
        */
            //Part One is back to front

            if (shipSetup.getFirstLocation() != null && shipSetup.getSecondLocation() == null && isSet) {
                Location firstLocation = shipSetup.getFirstLocation();
                Location secondLocation = player.getTargetBlock(set, 5).getLocation();
                player.sendMessage("Second location set at " + secondLocation.getBlockX() + " (X), " +
                        secondLocation.getBlockY() + " (Y), " + secondLocation.getBlockZ() + " (Z).");
                if (firstLocation.getBlock().equals(secondLocation.getBlock())) {
                    player.sendMessage("please select a different block.");
                } else if (blocksSelected(firstLocation, secondLocation, player.getWorld()).size() > maxSize) {
                    player.sendMessage("The selected size is too large. Please try again.");
                    shipSetup.setFirstLocation(null);
                } else if (createShip.doBlocksOverlap(blocksSelected(firstLocation, secondLocation, player.getWorld()))) {
                    player.sendMessage("The selected area overlaps with an existing ship. Please try again.");
                    player.sendMessage("The selected size is too large. Please try again.");
                } else {
                    player.sendMessage(blocksSelected(firstLocation, secondLocation, player.getWorld()).size() + " blocks selected.");
                    player.sendMessage("Please use '/ShipSetup confirm' to continue or '/ShipSetup cancel' to restart.");
                    shipSetup.setSecondLocation(secondLocation);
                }
            }

            if (shipSetup.getFirstLocation() == null && shipSetup.getSecondLocation() == null && isSet) {
                Location firstLocation = player.getTargetBlock(set, 5).getLocation();
                player.sendMessage("§b§ <MovingShips> --- Ship Setup (2/10) --- <MovingShips>");
                player.sendMessage("First location set at " + firstLocation.getBlockX() + " (X), " +
                        firstLocation.getBlockY() + " (Y), " + firstLocation.getBlockZ() + " (Z).");
                player.sendMessage("Please look at second location block and type '/ShipSetup set'.");
                shipSetup.setFirstLocation(firstLocation);
            }

            //once complete, delete and created ship

            else if (isCanceled) {
                removeShipSetup(shipSetup);
                player.sendMessage("<MovingShips> Setup cancelled.");
            }
        }
        return true;
    }

    public boolean isControlOnShip(Location location, HashMap<Location, Material> blocks, Player player){
        if (setShipControls.checkIsControlOnShip(location,blocks)){
            return true;
        } else {
            player.sendMessage("This block is not within the ships area, please selected another block.");
            return false;
        }
    }

    public ShipSetup getShipSetup(Player player) {
        for (ShipSetup shipSetup : storedShipSetup) {
            if (player.equals(shipSetup.getPlayer())) {
                return shipSetup;
            }
        }
        return null;
    }

    public void removeShipSetup(ShipSetup shipSetup) {
        storedShipSetup.remove(shipSetup);
    }

    public HashMap<Location, Material> blocksSelected(Location startLocation, Location endLocation, World world) {
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
                        if (LocationsInArea.size() > maxSize) {
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
