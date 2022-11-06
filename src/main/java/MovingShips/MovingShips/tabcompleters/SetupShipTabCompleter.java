package MovingShips.MovingShips.tabcompleters;

import MovingShips.MovingShips.commands.SetupShip;
import MovingShips.MovingShips.ships.ShipSetup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetupShipTabCompleter implements TabCompleter {
    List<String> suggested;
    SetupShip setupShip;
    ShipSetup shipSetup;
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            suggested = new ArrayList<>();
            setupShip = new SetupShip();
            Player player = (Player) sender;

            if (args.length == 1){
                suggested.add("set");
                suggested.add("confirm");
                suggested.add("undo");
                suggested.add("cancel");
            }

            if (setupShip.getShipSetup(player) != null){
                shipSetup = setupShip.getShipSetup(player);
                if (args.length > 1 && shipSetup.isDirectionAndFirstAndSecondLocationSet() && shipSetup.isControlsSet()
                        && shipSetup.getShipName() == null && args[0].equalsIgnoreCase("set")){
                    suggested.add(player.getName() + "'s Ship");
                }
            }


        }

        return suggested;
    }
}
