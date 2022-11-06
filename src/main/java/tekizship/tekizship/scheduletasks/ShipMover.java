package tekizship.tekizship.scheduletasks;

import org.bukkit.Bukkit;
import tekizship.tekizship.commands.MoveShip;
import tekizship.tekizship.ships.Ship;
import tekizship.tekizship.ships.ShipAccess;

public class ShipMover  {
    ShipAccess shipAccess = ShipAccess.getInstance();
    MoveShip moveShip = new MoveShip();

    public void moveShips(){
        for (Ship ship : shipAccess.getShips()){
            if (ship.getSpeed() != 0){
                if (ship.getSpeed() > 0){
                    moveShip.moveShip(ship, "forward", ship.getSpeed(), Bukkit.getPlayer(ship.getOwnerName()));
                } else if (ship.getSpeed() < 0){
                    moveShip.moveShip(ship, "back", ship.getSpeed(), Bukkit.getPlayer(ship.getOwnerName()));
                }
            }
        }
    }

}
