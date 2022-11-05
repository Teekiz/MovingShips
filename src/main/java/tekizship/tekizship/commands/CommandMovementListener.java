package tekizship.tekizship.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import tekizship.tekizship.ships.Ship;
import tekizship.tekizship.ships.ShipAccess;

import java.util.Map;

public class CommandMovementListener implements Listener {

    ShipAccess shipAccess;

    @EventHandler
    public void shipControlsClick(PlayerInteractEvent event) {
        shipAccess = ShipAccess.getInstance();
        Player player = event.getPlayer();
        String command = null;
        String frontDirection = null;
        Ship selectedShip = null;

        if (event.getClickedBlock() == null) {}

        for (Ship ship : shipAccess.getShipByOwnerName(player.getName())) {
            for (Map.Entry<String, Location> pair : ship.getShipControlLocations().entrySet()) {
                if (pair.getValue().getBlock().getLocation().equals(event.getClickedBlock().getLocation())) {
                    command = pair.getKey();
                    frontDirection = ship.getFrontDirection();
                    selectedShip = ship;
                }
            }
        }

        if (command != null && frontDirection != null && selectedShip != null){
            shipControlClicked(command, frontDirection, selectedShip, player);
        }
    }

    //todo - let players assign others to be able to use the ship
    public void shipControlClicked(String command, String frontDirection, Ship ship, Player player){
        MoveShip moveShip = new MoveShip();
        RotateShip rotateShip = new RotateShip();

        char[] frontDirectionSplit = frontDirection.toCharArray();
        //direction = X or Z, negativeOrPositive = + or -
        String direction = String.valueOf(frontDirectionSplit[0]);
        String negativeOrPositive = String.valueOf(frontDirectionSplit[1]);
        String directionOpposite = "";

        if (direction.equalsIgnoreCase("X")) {
            directionOpposite = "Z";
        } else if (direction.equalsIgnoreCase("Z")) {
            directionOpposite = "X";
        }

        //todo change to speed
        if (command.equalsIgnoreCase("forward")) {
            if (negativeOrPositive.equalsIgnoreCase("+")) {
                moveShip.moveShip(ship, direction, 1, player);
            } else {
                moveShip.moveShip(ship, direction, -1, player);
            }
        } else if (command.equalsIgnoreCase("back")) {
            if (negativeOrPositive.equalsIgnoreCase("+")) {
                moveShip.moveShip(ship, direction, -1, player);
            } else {
                moveShip.moveShip(ship, direction, 1, player);
            }
        } else if (command.equalsIgnoreCase("right")) {
            if (direction.equalsIgnoreCase("X")){
                if (negativeOrPositive.equalsIgnoreCase("+")) {
                    moveShip.moveShip(ship, directionOpposite, 1, player);
                } else {
                    moveShip.moveShip(ship, directionOpposite, -1, player);
                }
            } else {
                if (negativeOrPositive.equalsIgnoreCase("+")) {
                    moveShip.moveShip(ship, directionOpposite, -1, player);
                } else {
                    moveShip.moveShip(ship, directionOpposite, 1, player);
                }
            }
        } else if (command.equalsIgnoreCase("left")) {
            if (direction.equalsIgnoreCase("X")){
                if (negativeOrPositive.equalsIgnoreCase("+")) {
                    moveShip.moveShip(ship, directionOpposite, -1, player);
                } else {
                    moveShip.moveShip(ship, directionOpposite, 1, player);
                }
            } else {
                if (negativeOrPositive.equalsIgnoreCase("+")) {
                    moveShip.moveShip(ship, directionOpposite, 1, player);
                } else {
                    moveShip.moveShip(ship, directionOpposite, -1, player);
                }
            }
        } else if (command.equalsIgnoreCase("rotateRight")){
            rotateShip.rotateShip(ship, "right", player);
        } else if (command.equalsIgnoreCase("rotateLeft")) {
            rotateShip.rotateShip(ship, "left", player);
        }
    }
}
