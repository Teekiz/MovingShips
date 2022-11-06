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

public class RotateShipTabCompleter implements TabCompleter {

    List<String> suggested;
    ShipAccess shipAccess = ShipAccess.getInstance();
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            suggested = new ArrayList<>();
            Player player = (Player) sender;

            if (args.length == 1){
                suggested.add("left");
                suggested.add("right");
            } else if (args.length == 2){
                if (PermissionCheck.hasPermissionAdmin(player)){
                    for (Ship ship : shipAccess.getShips()){
                        suggested.add(ship.getShipName());
                    }
                } else {
                    for (Ship ship : shipAccess.getShips()){
                        if (ship.getOwnerName().equalsIgnoreCase(player.getName())){
                            suggested.add(ship.getShipName());
                            continue;
                        }
                        for (String crew : ship.getCrew()){
                            if (crew.equalsIgnoreCase(player.getName())){
                                suggested.add(ship.getShipName());
                            }
                        }
                    }
                }
            }
        }

        return suggested;
    }

}
