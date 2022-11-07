package MovingShips.MovingShips.tabcompleters;

import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ShipsTabCompleter implements TabCompleter {

    List<String> suggested;
    ShipAccess shipAccess = ShipAccess.getInstance();
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        suggested = new ArrayList<>();
        for (Ship ship : shipAccess.getShips()){
            suggested.add(ship.getShipName());
        }
        return suggested;
    }
}
