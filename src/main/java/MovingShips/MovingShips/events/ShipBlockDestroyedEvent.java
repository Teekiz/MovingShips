package MovingShips.MovingShips.events;

import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

public class ShipBlockDestroyedEvent implements Listener {
    ShipAccess shipAccess = ShipAccess.getInstance();

    @EventHandler
    public void destroyBlockCheck(BlockDamageEvent event) {
        try {
            Player player = event.getPlayer();
            if (isBlockOnShip(event.getBlock().getLocation())){
                event.setCancelled(true);
                player.sendMessage("§4§ <MovingShips> You cannot destroy blocks on a ship.");
            }
        } catch (Exception e){}

    }

    public boolean isBlockOnShip(Location blockDestroyedLocation){
        for (Ship ship : shipAccess.getShips()){
            for (Location location : ship.getShipBlocks().keySet()){
                if (blockDestroyedLocation.equals(location)){
                    return true;
                }
            }
        }
        return false;
    }
}
