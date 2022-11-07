package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import MovingShips.MovingShips.utility.PermissionCheck;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeleteShip implements CommandExecutor {

    ShipAccess shipAccess;
    Ship ship;

    public boolean onCommand(CommandSender commandSender, Command command, String s, String [] args) {
        try {
            if (args.length == 0){
                commandSender.sendMessage("<MovingShips> DeleteShip arguments: <Name of Ship>.");
            } else {
                String shipName = "";
                //for the ship name
                List<String> arguments = new ArrayList<>();
                for (String argument : args) {
                    arguments.add(argument);
                }

                //to add spaces between the names
                for (int i = 0; i < arguments.size(); i++) {
                    if (i == arguments.size() - 1) {
                        shipName += arguments.get(i);
                    } else {
                        shipName += arguments.get(i) + " ";
                    }
                }

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
