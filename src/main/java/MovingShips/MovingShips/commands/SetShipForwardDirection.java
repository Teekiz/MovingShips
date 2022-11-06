package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.utility.PermissionCheck;
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
    Ship selectedShip;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }

        Player player = (Player) commandSender;
        try {
            if (args.length < 1){
                player.sendMessage("<MovingShips> SetShipForwardDirection arguments: <Name of Ship>.");
            } else {
                String shipName = "";
                //for the ship name
                List<String> arguments = new ArrayList<>();
                for (String argument : args) {
                    arguments.add(argument);
                }

                //to add spaces between the names
                for (int i = 0; i < arguments.size(); i++) {
                    if (i == arguments.size() - 1) {
                        shipName += arguments.get(i);
                    } else {
                        shipName += arguments.get(i) + " ";
                    }
                }

                selectedShip = shipAccess.getShipByName(shipName);
                if (selectedShip == null) {
                    player.sendMessage("§4§ <MovingShips> Cannot find ship by that name.");
                } else {
                    if (PermissionCheck.hasPermissionOwner(selectedShip, player)){
                        String direction = getShipDirection(player).get(0);
                        String directionValue = getShipDirection(player).get(1);
                        setShipDirection(selectedShip, player, direction, directionValue);
                    } else {
                        player.sendMessage("§4§ <MovingShips> You do not have to set the forward direction of this ship.");
                    }
                }

            }
        } catch (Exception e){
            player.sendMessage("§4§ <MovingShips> Invalid command usage. Proper command usage: /SetShipForwardDirection <Name of Ship>.");
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
        player.sendMessage("<MovingShips> Set ship forward direction to " + direction + directionValue + ".");
        shipAccess.saveShip();
    }
}
