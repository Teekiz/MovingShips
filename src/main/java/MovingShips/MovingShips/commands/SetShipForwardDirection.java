package MovingShips.MovingShips.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetShipForwardDirection implements CommandExecutor {

    ShipAccess shipAccess = ShipAccess.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }

        Player player = (Player) commandSender;

        if (args.length == 0) {
            player.sendMessage("Please provide a ship name.");
        } else {
            Ship ship = shipAccess.getShipByName(args[0]);
            Set<Material> set = null;
            String direction = getShipDirection(player).get(0);
            String directionValue = getShipDirection(player).get(1);
            setShipDirection(ship, player, direction, directionValue);
        }
        return true;
    }

    public List<String> getShipDirection(Player player){
        String direction;
        String directionValue;
        List<String> directions = new ArrayList<>();

        //rotation double based on code provided by worthless_hobo
        //https://www.spigotmc.org/threads/how-do-i-get-the-direction-the-player-is-facing.433419/

        double rotation = (player.getLocation().getYaw() - 180) % 360;
        if (rotation < 0) { rotation += 360.0;}

        if (rotation >= 45 && rotation < 135){ direction = "X"; directionValue ="+";}
        else if (rotation >= 135 && rotation < 225) { direction = "Z"; directionValue ="+";}
        else if (rotation >= 225 && rotation < 315) { direction = "X"; directionValue ="-";}
        else { direction = "Z"; directionValue ="-";}

        directions.add(direction);
        directions.add(directionValue);
        return directions;
    }

    public void setShipDirection(Ship ship, Player player, String direction, String directionValue){
        ship.setFrontDirection(direction);
        ship.setFrontDirectionValue(directionValue);
        player.sendMessage("Set ship forward direction to " + direction + directionValue + ".");
        shipAccess.saveShip();
    }
}
