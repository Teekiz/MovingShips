package MovingShips.MovingShips.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;

import java.util.HashMap;
import java.util.Set;

public class SetShipControls implements CommandExecutor {

    ShipAccess shipAccess = ShipAccess.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }
        Player player = (Player) commandSender;

        if (args.length==0 || args.length < 2){
            player.sendMessage("Please provide a ship name and control.");
            return true;
        } else {
            String shipName = args[0];
            String control = args[1];

            if (control.equalsIgnoreCase("forward") || control.equalsIgnoreCase("back")
                    || control.equalsIgnoreCase("left") || control.equalsIgnoreCase("right")
                    || control.equalsIgnoreCase("rotateLeft") || control.equalsIgnoreCase("rotateRight")){
                //Not 100% sure why I cannot set this directly, but it works
                Set<Material> set = null;
                Location targetLocation = player.getTargetBlock(set, 5).getLocation();
                if (checkIsControlOnShip(targetLocation, shipAccess.getShipByName(shipName).getShipBlocks())){
                    setControl(shipName, control, targetLocation, player);
                } else {
                    player.sendMessage("The location must be on the ship.");
                }

            } else {
                //todo - check to see if the ship is within the area of the ship (otherwise will be the controls)
                player.sendMessage("Invalid control, must be either forward, back, right, left, rotateRight or rotateLeft");
            }
        }
        return true;
    }

    public void setControl(String shipName, String control, Location controlLocation, Player player){
        Ship ship = shipAccess.getShipByName(shipName);
        if (ship != null){
            ship.setShipControlLocation(control, controlLocation, player);
            shipAccess.saveShip();
        } else {
            player.sendMessage("Ship cannot be found.");
        }
    }

    //this could use ship but its used in ship setup as well/
    public Boolean checkIsControlOnShip(Location location, HashMap<Location, Material> LocationsInArea){
        for (Location checkLocation : LocationsInArea.keySet()){
            if (location.getBlock().equals(checkLocation.getBlock())){
                if (checkLocation.getBlock().getType() != Material.AIR || checkLocation.getBlock().getType() != Material.WATER){
                    return true;
                }
            }
        }
        return false;
    }

}
