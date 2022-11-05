package tekizship.tekizship;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tekizship.tekizship.commands.*;
import tekizship.tekizship.ships.ShipAccess;


public final class TekizShip extends JavaPlugin {

    private static TekizShip plugin;

    //todo create a save and load system
    //todo create the tick system
    //todo add command listeners
    //todo add crew system
    //todo add a system to prevent creating or destroying blocks on a ship
    //todo move beds and chest contents each movement check
    //todo neaten up the commands
    //todo to check for special blocks (i.e. ladders) and set them correctly
    //todo setup config file, to set max speed, max amount of ships
    //todo create a delete command

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Bukkit.getLogger().info("Ship Plugin Enable");
        getCommand("createship").setExecutor(new CreateShip());
        getCommand("moveship").setExecutor(new MoveShip());
        getCommand("setshipcontrols").setExecutor(new SetShipControls());
        getCommand("rotateship").setExecutor(new RotateShip());
        getCommand("setshipforwarddirection").setExecutor(new SetShipForwardDirection());
        getCommand("setupShip").setExecutor(new SetupShip());
        getServer().getPluginManager().registerEvents(new CommandMovementListener(), this);
        ShipAccess access = ShipAccess.getInstance();
        access.loadShip();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TekizShip getPlugin(){
        return plugin;
    }
}
