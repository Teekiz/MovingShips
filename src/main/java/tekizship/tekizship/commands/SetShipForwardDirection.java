package tekizship.tekizship.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tekizship.tekizship.ships.Ship;
import tekizship.tekizship.ships.ShipAccess;

import java.util.Set;

public class SetShipForwardDirection implements CommandExecutor {

    ShipAccess shipAccess;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }

        shipAccess = ShipAccess.getInstance();
        Player player = (Player) commandSender;

        if (args.length == 0) {
            player.sendMessage("Please provide a ship name.");
        } else {
            Ship ship = shipAccess.getShipByName(args[0]);
            Set<Material> set = null;
            String direction = getShipDirection(player);
            setShipDirection(ship, player, direction);
        }
        return true;
    }

    public String getShipDirection(Player player){
        String direction;
        //rotation double based on code provided by worthless_hobo
        //https://www.spigotmc.org/threads/how-do-i-get-the-direction-the-player-is-facing.433419/

        double rotation = (player.getLocation().getYaw() - 180) % 360;
        if (rotation < 0) { rotation += 360.0;}

        if (rotation >= 45 && rotation < 135){ direction = "X+";}
        else if (rotation >= 135 && rotation < 225) {direction = "Z+";}
        else if (rotation >= 225 && rotation < 315) {direction = "X-";}
        else {direction = "Z-";}

        return direction;
    }

    public void setShipDirection(Ship ship, Player player, String direction){
        ship.setFrontDirection(direction);
        player.sendMessage("Set ship forward direction to " + direction + ".");
    }
}
