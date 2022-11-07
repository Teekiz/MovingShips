package MovingShips.MovingShips.events;

import MovingShips.MovingShips.MovingShipsConfiguration;
import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.utility.PermissionCheck;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import MovingShips.MovingShips.ships.ShipAccess;

import java.util.Map;

public class ShipInteractionEvent implements Listener {

    ShipAccess shipAccess = ShipAccess.getInstance();

    @EventHandler
    public void shipControlsClick(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            String command = null;
            Ship selectedShip = null;

            if (event.getClickedBlock() == null) {
            }

            for (Ship ship : shipAccess.getShips()) {
                for (Map.Entry<String, Location> pair : ship.getShipControlLocations().entrySet()) {
                    if (pair.getValue().getBlock().getLocation().equals(event.getClickedBlock().getLocation())) {
                        command = pair.getKey();
                        selectedShip = ship;
                    }
                }
            }

            if (command != null) {
                shipControlClicked(command, selectedShip, player);
            }
        } catch (Exception e){}
    }


    //todo - let players assign others to be able to use the ship
    public void shipControlClicked(String command, Ship ship, Player player) {
        if (PermissionCheck.hasPermission(ship, player)) {
            int maxSpeed = MovingShipsConfiguration.getMaxSpeed();
            if (command.equalsIgnoreCase("forward")) {
                if (ship.getSpeed() >= maxSpeed) {
                    ship.setSpeed(maxSpeed);
                } else {
                    ship.setSpeed(ship.getSpeed() + 1);
                }
            } else if (command.equalsIgnoreCase("back")) {
                if (ship.getSpeed() <= -1) {
                    ship.setSpeed(-1);
                } else {
                    ship.setSpeed(ship.getSpeed() - 1);
                }
            } else if (command.equalsIgnoreCase("right")) {
                ship.setQueuedCommand("right");
            } else if (command.equalsIgnoreCase("left")) {
                ship.setQueuedCommand("left");
            } else if (command.equalsIgnoreCase("rotateRight")) {
                ship.setQueuedCommand("rotateRight");
            } else if (command.equalsIgnoreCase("rotateLeft")) {
                ship.setQueuedCommand("rotateLeft");
            }
        }
    }

}
