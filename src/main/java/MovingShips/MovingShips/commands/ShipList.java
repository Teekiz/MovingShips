package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShipList implements CommandExecutor {
    ShipAccess shipAccess;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String [] args) {
        try {
            shipAccess = ShipAccess.getInstance();
            if (shipAccess.getShips().size() > 0){
                int size = shipAccess.getShips().size();
                String shipList = "<MovingShips> Ships in server: ";
                for (Ship ship : shipAccess.getShips()){
                    if (ship == shipAccess.getShips().get(size-1)){
                        shipList+=ship.getShipName()+".";
                    } else {
                        shipList+=ship.getShipName()+", ";
                    }
                }
                commandSender.sendMessage(shipList);
            } else {
                commandSender.sendMessage("<MovingShips> No ships have been created yet.");
            }
        } catch (Exception e){
            commandSender.sendMessage("§4§ <MovingShips> Invalid command usage. Proper command usage: /ShipList.");
        }

        return true;
    }
}
