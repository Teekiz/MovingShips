package MovingShips.MovingShips.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class BlankArgsTabCompleter implements TabCompleter {
    List<String> suggested;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        suggested = new ArrayList<>();
        return suggested;
    }
}
