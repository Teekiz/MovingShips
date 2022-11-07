package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import MovingShips.MovingShips.utility.ShipName;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
public class ShipInfo implements CommandExecutor {
    ShipAccess shipAccess = ShipAccess.getInstance();
    String shipName;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String [] args) {
        try {
            if (args.length == 0){
                commandSender.sendMessage("<MovingShips> ShipInfo arguments: <Name of Ship>.");
            }
            else {
                String shipName = ShipName.filterShipName(args, 0);
                Ship ship = shipAccess.getShipByName(shipName);
                if (ship == null){
                    commandSender.sendMessage("§4§ <MovingShips> Cannot find ship by that name.");
                } else {
                    commandSender.sendMessage("§b§ <MovingShips> --- " + ship.getShipName() + " details --- <MovingShips>");
                    commandSender.sendMessage("Owner Name: " + ship.getOwnerName());
                    commandSender.sendMessage("Size of crew: " + ship.getCrew().size());
                    commandSender.sendMessage("Size of ship (in blocks): " + ship.getShipBlocks().size());

                    if (ship.getCrew().size() > 0){
                        int size = ship.getCrew().size();
                        String crewList = "Crew Names: ";
                        for (String crewName : ship.getCrew()){
                            if (crewName.equalsIgnoreCase(ship.getCrew().get(size-1))){
                                crewList+=crewName+".";
                            } else {
                                crewList+=crewName+", ";
                            }
                        }
                        commandSender.sendMessage(crewList);
                    }
                }
            }
        } catch (Exception e){
            commandSender.sendMessage("§4§ <MovingShips> Invalid command usage. Proper command usage: /ShipInfo <Name of Ship>.");
        }

        return true;
    }
}
