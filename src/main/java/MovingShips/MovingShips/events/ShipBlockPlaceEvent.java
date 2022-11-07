package MovingShips.MovingShips.events;

import MovingShips.MovingShips.MovingShipsConfiguration;
import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ShipBlockPlaceEvent implements Listener {
    ShipAccess shipAccess = ShipAccess.getInstance();
    @EventHandler
    public void placeBlockCheck(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        int protectionRadius = MovingShipsConfiguration.getProtectionRadius();
        if (isInShipProtectionRadius(protectionRadius, event.getBlockPlaced().getLocation())){
            player.sendMessage("§4§ <MovingShips> You cannot place blocks within " + protectionRadius + " blocks of a ship.");
            event.getBlockPlaced().getLocation().getBlock().setType(Material.AIR);
        }
    }

    public boolean isInShipProtectionRadius(int protectionRadius, Location blockPlacedLocation){
        for (Ship ship : shipAccess.getShips()){
            for (Location location : ship.getShipBlocks().keySet()){
                for (int i = 0; i < protectionRadius; i++){
                    Location checkLocationPOSX = location.clone().add(i, 0, 0);
                    Location checkLocationPOSY = location.clone().add(0, i, 0);
                    Location checkLocationPOSZ = location.clone().add(0, 0, i);

                    Location checkLocationNEGX = location.clone().subtract(i, 0, 0);
                    Location checkLocationNEGY = location.clone().subtract(0, i, 0);
                    Location checkLocationNEGZ = location.clone().subtract(0, 0, i);

                    if (blockPlacedLocation.equals(checkLocationPOSX) || blockPlacedLocation.equals(checkLocationPOSY)
                            || blockPlacedLocation.equals(checkLocationPOSZ) ||  blockPlacedLocation.equals(checkLocationNEGX)
                            || blockPlacedLocation.equals(checkLocationNEGY) || blockPlacedLocation.equals(checkLocationNEGZ)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
