package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.ships.Ship;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import MovingShips.MovingShips.ships.ShipAccess;

import java.util.Map;

public class CommandMovementListener implements Listener {

    ShipAccess shipAccess;

    @EventHandler
    public void shipControlsClick(PlayerInteractEvent event) {
        shipAccess = ShipAccess.getInstance();
        Player player = event.getPlayer();
        String command = null;
        Ship selectedShip = null;

        if (event.getClickedBlock() == null) {}

        for (Ship ship : shipAccess.getShipByOwnerName(player.getName())) {
            for (Map.Entry<String, Location> pair : ship.getShipControlLocations().entrySet()) {
                if (pair.getValue().getBlock().getLocation().equals(event.getClickedBlock().getLocation())) {
                    command = pair.getKey();
                    selectedShip = ship;
                }
            }
        }

        if (command != null){
            shipControlClicked(command, selectedShip, player);
        }
    }

    //todo - let players assign others to be able to use the ship
    public void shipControlClicked(String command, Ship ship, Player player){
        if (command.equalsIgnoreCase("forward")) {
            if (ship.getSpeed() >= 3){
                ship.setSpeed(3);
            } else {
                ship.setSpeed(ship.getSpeed()+1);
            }
        } else if (command.equalsIgnoreCase("back")) {
            if (ship.getSpeed() <= -1){
                ship.setSpeed(-1);
            } else {
                ship.setSpeed(ship.getSpeed()-1);
            }
        } else if (command.equalsIgnoreCase("right")) {
            ship.setQueuedCommand("right");
        } else if (command.equalsIgnoreCase("left")) {
            ship.setQueuedCommand("left");
        } else if (command.equalsIgnoreCase("rotateRight")){
            ship.setQueuedCommand("rotateRight");
        } else if (command.equalsIgnoreCase("rotateLeft")) {
            ship.setQueuedCommand("rotateLeft");
        }
    }
}
