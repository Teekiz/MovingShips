package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import MovingShips.MovingShips.utility.PermissionCheck;
import MovingShips.MovingShips.utility.ShipName;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
public class DeleteShip implements CommandExecutor {

    ShipAccess shipAccess;
    Ship ship;
    String shipName;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String [] args) {
        try {
            if (args.length == 0){
                commandSender.sendMessage("<MovingShips> DeleteShip arguments: <Name of Ship>.");
            } else {
                shipName = ShipName.filterShipName(args, 0);
                shipAccess = ShipAccess.getInstance();
                ship = shipAccess.getShipByName(shipName);
                if (ship == null){
                    commandSender.sendMessage("§4§ <MovingShips> Cannot find ship by that name.");
                } else {
                    if (PermissionCheck.hasPermissionOwner(ship, commandSender)){
                        shipAccess.removeShip(ship);
                        commandSender.sendMessage("§4§ <MovingShips> " + ship.getShipName() + " has been deleted.");
                        shipAccess.saveShip();
                    } else {
                        commandSender.sendMessage("§4§ <MovingShips> You do not have permission to move this ship.");
                    }
                }

            }
        } catch (Exception e){
            commandSender.sendMessage("§4§ <MovingShips> Invalid command usage. Proper command usage: /DeleteShip <Name of Ship>.");
        }

        return true;
    }
}
