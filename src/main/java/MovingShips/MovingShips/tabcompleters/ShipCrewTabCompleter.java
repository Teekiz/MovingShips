package MovingShips.MovingShips.tabcompleters;

import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShipCrewTabCompleter implements TabCompleter {
    List<String> suggested;
    ShipAccess shipAccess = ShipAccess.getInstance();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        suggested = new ArrayList<>();
        if (args.length == 1) {
            suggested.add("add");
            suggested.add("remove");
            suggested.add("accept");
            suggested.add("reject");
        }

        if (args.length == 2) {
            suggested = new ArrayList<>();
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    suggested.add(player.getName());
                }
            } else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("reject")) {
                for (Ship ship : shipAccess.getShips()) {
                    for (String crew : ship.getInvitedCrew()) {
                        if (sender.getName().equalsIgnoreCase(crew)) {
                            suggested.add(ship.getShipName());
                        }
                    }
                }
            }
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            if (args[0].equalsIgnoreCase("add")) {
                for (Ship ship : shipAccess.getShips()) {
                    if (!ship.isCrewMember(args[1])) {
                        suggested.add(ship.getShipName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                for (Ship ship : shipAccess.getShips()) {
                    if (ship.isCrewMember(args[1])) {
                        suggested.add(ship.getShipName());
                    }
                }
            }
        }
        return suggested;
    }
}
