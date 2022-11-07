package MovingShips.MovingShips;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public final class MovingShipsConfiguration {
    private static int maxSpeed = 3;
    private static int protectionRadius = 5;
    private static int maxShips = 100;

    private static int maxShipSize = 10000;

    private MovingShipsConfiguration(){}

    public static int getMaxSpeed() {
        return maxSpeed;
    }

    public static void setMaxSpeed(int maxSpeed) {
        MovingShipsConfiguration.maxSpeed = maxSpeed;
    }

    public static int getProtectionRadius() {
        return protectionRadius;
    }

    public static void setProtectionRadius(int protectionRadius) {
        MovingShipsConfiguration.protectionRadius = protectionRadius;
    }

    public static int getMaxShips() {
        return maxShips;
    }

    public static void setMaxShips(int maxShips) {
        MovingShipsConfiguration.maxShips = maxShips;
    }

    public static int getMaxShipSize() {
        return maxShipSize;
    }

    public static void setMaxShipSize(int maxShipSize) {
        MovingShipsConfiguration.maxShipSize = maxShipSize;
    }

    //just incase it is needed
    public static void saveConfig(){
        FileConfiguration configuration = MovingShips.getPlugin().getConfig();
        configuration.set("maxSpeed", getMaxSpeed());
        configuration.set("protectionRadius", getProtectionRadius());
        configuration.set("maxShips", getMaxShips());
        configuration.set("maxShipSize", getMaxShipSize());
        MovingShips.getPlugin().saveConfig();
    }

    public static void loadConfig(){
        FileConfiguration configuration = MovingShips.getPlugin().getConfig();
        setMaxSpeed(configuration.getInt("maxSpeed"));
        setProtectionRadius(configuration.getInt("protectionRadius"));
        setMaxShips(configuration.getInt("maxShips"));
        setMaxShipSize(configuration.getInt("maxShipSize"));
    }
}
