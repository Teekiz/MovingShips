package MovingShips.MovingShips.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CreateShipTabCompleter implements TabCompleter {
    List<String> suggested;
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            suggested = new ArrayList<>();
            Player player = (Player) sender;
            if (args.length == 1){
                suggested.add(String.valueOf(player.getLocation().getBlockX()));
            } else if (args.length == 2) {
                suggested.add(String.valueOf(player.getLocation().getBlockY()));
            } else if (args.length == 3) {
                suggested.add(String.valueOf(player.getLocation().getBlockZ()));
            } else if (args.length == 4) {
                suggested.add("<X>");
            } else if (args.length == 5) {
                suggested.add("<Y>");
            } else if (args.length == 6) {
                suggested.add("<Z>");
            } else if (args.length == 7) {
                suggested.add(player.getName() + "'s Ship");
            }
        }
        return suggested;
    }
}
