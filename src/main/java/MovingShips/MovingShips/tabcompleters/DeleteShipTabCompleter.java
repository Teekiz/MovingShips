package MovingShips.MovingShips.tabcompleters;

import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import MovingShips.MovingShips.utility.PermissionCheck;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeleteShipTabCompleter implements TabCompleter {
    List<String> suggested;
    ShipAccess shipAccess = ShipAccess.getInstance();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        suggested = new ArrayList<>();
        if (args.length == 1) {
            if (PermissionCheck.hasPermissionAdmin(sender)) {
                for (Ship ship : shipAccess.getShips()) {
                    suggested.add(ship.getShipName());
                }
            } else {
                for (Ship ship : shipAccess.getShips()) {
                    if (ship.getOwnerName().equalsIgnoreCase(sender.getName())) {
                        suggested.add(ship.getShipName());
                        continue;
                    }
                    for (String crew : ship.getCrew()) {
                        if (crew.equalsIgnoreCase(sender.getName())) {
                            suggested.add(ship.getShipName());
                        }
                    }
                }
            }
        }
        return suggested;
    }


}
