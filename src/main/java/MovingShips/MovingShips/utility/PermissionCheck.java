package MovingShips.MovingShips.utility;
import MovingShips.MovingShips.ships.Ship;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public final class PermissionCheck {

    private PermissionCheck(){}

    //checks with crew
    public static boolean hasPermission(Ship ship, CommandSender commandSender){
        for (PermissionAttachmentInfo permission : commandSender.getEffectivePermissions()) {
            if (permission.getPermission().equalsIgnoreCase("movingships.admin")){
                return true;
            }
        }

        if (commandSender.isOp()){return true;}
        if (commandSender.equals(ship.getOwnerName())){return true;}

        for (String crewName : ship.getCrew()){
            if (crewName.equalsIgnoreCase(commandSender.getName())){
                return true;
            }
        }
        return false;
    }

    //checks without crew
    public static boolean hasPermissionOwner(Ship ship, CommandSender commandSender){
        for (PermissionAttachmentInfo permission : commandSender.getEffectivePermissions()) {
            if (permission.getPermission().equalsIgnoreCase("movingships.admin")){
                return true;
            }
        }

        if (commandSender.isOp()){return true;}
        if (commandSender.equals(ship.getOwnerName())){return true;}
        return false;
    }

    //checks without owner name or crew
    public static boolean hasPermissionAdmin(CommandSender commandSender){
        for (PermissionAttachmentInfo permission : commandSender.getEffectivePermissions()) {
            if (permission.getPermission().equalsIgnoreCase("movingships.admin")){
                return true;
            }
        }
        if (commandSender.isOp()){return true;}

        return false;
    }
}
