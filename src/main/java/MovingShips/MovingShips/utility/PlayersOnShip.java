package MovingShips.MovingShips.utility;

import MovingShips.MovingShips.ships.Ship;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public final class PlayersOnShip {
    private PlayersOnShip(){}

    public static void movePlayer(String direction, int directionAmount, ArrayList<Player> playersOnShip){
        for (Player player : playersOnShip){
            Location teleportLocation = player.getLocation();
            if (direction.equalsIgnoreCase("X")){
                teleportLocation.setX(teleportLocation.getX() + directionAmount);
            } else if (direction.equalsIgnoreCase("Z")){
                teleportLocation.setZ(teleportLocation.getZ() + directionAmount);
            }
            player.teleport(teleportLocation);
        }
    }

    public static void movePlayerRotate(String directionRotate, Location centerBlock, ArrayList<Player> playersOnShip){

        //rotation double based on code provided by worthless_hobo
        //https://www.spigotmc.org/threads/how-do-i-get-the-direction-the-player-is-facing.433419/

        for (Player player : playersOnShip){
            Location location = player.getLocation();
            float differenceZ = centerBlock.getBlock().getZ() - location.getBlock().getZ();
            float differenceX = centerBlock.getBlock().getX() - location.getBlock().getX();
            float rotation = 0;
            if (directionRotate.equalsIgnoreCase("left")){
                location.setX(centerBlock.getBlock().getX() - differenceZ);
                location.setZ(centerBlock.getBlock().getZ() + differenceX);
                rotation = player.getLocation().getYaw() - 90;
            } else if (directionRotate.equalsIgnoreCase("right")){
                location.setX(centerBlock.getBlock().getX() + differenceZ);
                location.setZ(centerBlock.getBlock().getZ() - differenceX);
                rotation = player.getLocation().getYaw() + 90;
            }
            if (rotation > 180) {
                float over = rotation - 180;
                rotation = -180 + over;
            }
            else if (rotation < -180) {
                float over = rotation + 180;
                rotation = 180 - over;
            }

            Location teleportLocation = new Location(centerBlock.getWorld(), location.getBlockX(),
                    location.getBlockY(), location.getBlockZ(), rotation, location.getPitch());
            player.teleport(teleportLocation);
        }
    }

    public static ArrayList<Player> getPlayersOnShip(Ship ship){
        ArrayList<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()){
            for (Location location : ship.getShipBlocks().keySet()){
                if (player.getLocation().getWorld().equals(location.getWorld())){
                    int playerY = player.getLocation().getBlockY();
                    int playerX = player.getLocation().getBlockX();
                    int playerZ = player.getLocation().getBlockZ();

                    int locationY = location.getBlockY();
                    int locationX = location.getBlockX();
                    int locationZ = location.getBlockZ();

                    //first checks X and Z, then Y. Y accounts for either standing or jumping.
                    if (Integer.compare(playerX, locationX) == 0  && Integer.compare(playerZ, locationZ) == 0){
                        if (Integer.compare(playerY - 1, locationY) == 0 || Integer.compare(playerY - 2, locationY) == 0){
                            if (!players.contains(player)){players.add(player);}
                        }
                    }
                }
            }
        }
        return players;
    }
}
