package MovingShips.MovingShips.scheduletasks;

import MovingShips.MovingShips.commands.MoveShip;
import org.bukkit.Bukkit;
import MovingShips.MovingShips.commands.RotateShip;
import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;

public class ShipMover  {
    ShipAccess shipAccess = ShipAccess.getInstance();
    MoveShip moveShip = new MoveShip();
    RotateShip rotateShip = new RotateShip();

    public void moveShips(){
        for (Ship ship : shipAccess.getShips()){
            if (ship.getSpeed() != 0){
                if (ship.getSpeed() > 0){
                    if (ship.getQueuedCommand() != null){ queuedCommand(ship);}
                    moveShip.moveShip(ship, "forward", ship.getSpeed(), Bukkit.getPlayer(ship.getOwnerName()), false);
                } else if (ship.getSpeed() < 0){
                    if (ship.getQueuedCommand() != null){ queuedCommand(ship);}
                    moveShip.moveShip(ship, "back", ship.getSpeed(), Bukkit.getPlayer(ship.getOwnerName()), false);
                }
            } else if (ship.getQueuedCommand() != null){
                queuedCommand(ship);
            }
        }
    }
    public void queuedCommand(Ship ship){
        if (ship.getQueuedCommand().equalsIgnoreCase("left")) {
            moveShip.moveShip(ship, "left", 1, Bukkit.getPlayer(ship.getOwnerName()), false);
            ship.setQueuedCommand(null);
        } else if (ship.getQueuedCommand().equalsIgnoreCase("right")){
            moveShip.moveShip(ship, "right", 1, Bukkit.getPlayer(ship.getOwnerName()), false);
            ship.setQueuedCommand(null);
        } else if (ship.getQueuedCommand().equalsIgnoreCase("rotateLeft")){
            rotateShip.rotateShip(ship, "left", Bukkit.getPlayer(ship.getOwnerName()));
            ship.setQueuedCommand(null);
        } else if (ship.getQueuedCommand().equalsIgnoreCase("rotateRight")){
            rotateShip.rotateShip(ship, "right", Bukkit.getPlayer(ship.getOwnerName()));
            ship.setQueuedCommand(null);
        }
    }

}
