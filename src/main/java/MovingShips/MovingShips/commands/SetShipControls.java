package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.utility.PermissionCheck;
import MovingShips.MovingShips.utility.ShipName;
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
    String shipName;
    String control;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
        }
        Player player = (Player) commandSender;

        try {
            if (args.length < 2){
                player.sendMessage("<MovingShips> SetShipControls arguments: <Forward/Back/Left/Right/RotateLeft/RotateRight> <Name of Ship>.");
            } else {
                shipName = ShipName.filterShipName(args, 1);
                control = args[0];

                if (control.equalsIgnoreCase("forward") || control.equalsIgnoreCase("back")
                        || control.equalsIgnoreCase("left") || control.equalsIgnoreCase("right")
                        || control.equalsIgnoreCase("rotateLeft") || control.equalsIgnoreCase("rotateRight")){
                    //Not 100% sure why I cannot set this directly, but it works
                    Set<Material> set = null;
                    Location targetLocation = player.getTargetBlock(set, 5).getLocation();
                    Ship ship = shipAccess.getShipByName(shipName);
                    if (ship != null){
                        if (checkIsControlOnShip(targetLocation, ship.getShipBlocks())){
                            if (PermissionCheck.hasPermissionOwner(ship, player)){
                                setControl(shipName, control, targetLocation, player);
                            } else {
                                player.sendMessage("§4§ <MovingShips> You do not have to set controls on this ship.");
                            }
                        } else {
                            player.sendMessage("§4§ <MovingShips> The location must be on the ship.");
                        }
                    } else {
                        player.sendMessage("§4§ <MovingShips> Cannot find ship by that name.");
                    }
                } else {
                    player.sendMessage("§4§ <MovingShips> Invalid control. The control must be either forward, back, right, left, rotateRight or rotateLeft");
                }

            }
        } catch (Exception e){
            player.sendMessage("§4§ <MovingShips> Invalid command usage. Proper command usage: /SetShipControls <Forward/Back/Left/Right/RotateLeft/RotateRight> <Name of Ship>.");
        }
        return true;
    }

    public void setControl(String shipName, String control, Location controlLocation, Player player){
        Ship ship = shipAccess.getShipByName(shipName);
        if (ship != null){
            ship.setShipControlLocation(control, controlLocation, player);
            shipAccess.saveShip();
        } else {
            player.sendMessage("§4§ <MovingShips> Cannot find ship by that name.");
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
