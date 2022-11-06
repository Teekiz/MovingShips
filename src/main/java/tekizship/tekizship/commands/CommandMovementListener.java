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

        if (command != null && frontDirection != null){
            shipControlClicked(command, selectedShip, player);
        }
    }

    //todo - let players assign others to be able to use the ship
    public void shipControlClicked(String command, Ship ship, Player player){
        MoveShip moveShip = new MoveShip();
        RotateShip rotateShip = new RotateShip();

        //todo change to speed
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
            moveShip.moveShip(ship, "right", 1, player);
        } else if (command.equalsIgnoreCase("left")) {
            moveShip.moveShip(ship, "left", 1, player);
        } else if (command.equalsIgnoreCase("rotateRight")){
            rotateShip.rotateShip(ship, "right", player);
        } else if (command.equalsIgnoreCase("rotateLeft")) {
            rotateShip.rotateShip(ship, "left", player);
        }
    }
}
