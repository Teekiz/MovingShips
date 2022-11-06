package MovingShips.MovingShips.utility;
import MovingShips.MovingShips.ships.Ship;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public final class PermissionCheck {

    private PermissionCheck(){}

    //checks with crew
    public static boolean hasPermission(Ship ship, Player player){
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (permission.getPermission().equalsIgnoreCase("movingships.admin")){
                return true;
            }
        }

        if (player.isOp()){return true;}
        if (player.equals(ship.getOwnerName())){return true;}

        for (String crewName : ship.getCrew()){
            if (crewName.equalsIgnoreCase(player.getName())){
                return true;
            }
        }
        return false;
    }

    //checks without crew
    public static boolean hasPermissionOwner(Ship ship, Player player){
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (permission.getPermission().equalsIgnoreCase("movingships.admin")){
                return true;
            }
        }

        if (player.isOp()){return true;}
        if (player.equals(ship.getOwnerName())){return true;}
        return false;
    }

    //checks without owner name or crew
    public static boolean hasPermissionAdmin(Player player){
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (permission.getPermission().equalsIgnoreCase("movingships.admin")){
                return true;
            }
        }
        if (player.isOp()){return true;}

        return false;
    }
}
