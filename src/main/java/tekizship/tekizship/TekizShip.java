package tekizship.tekizship;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tekizship.tekizship.commands.*;
import tekizship.tekizship.ships.ShipAccess;


public final class TekizShip extends JavaPlugin {


    /*
    *
    *   Steps to use:
    *   Step one - use /createship <x1> <y1> <z1> <x2> <y2> <z2> <name of ship>
    *   Step two - set the controls by aiming at the related control and typing /setshipcontrols <name of ship> <control>
    *   Step three - set the forward direction by using /setfowarddirection <name of ship>
    * */

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Ship Plugin Enable");
        getCommand("createship").setExecutor(new CreateShip());
        getCommand("moveship").setExecutor(new MoveShip());
        getCommand("setshipcontrols").setExecutor(new SetShipControls());
        getCommand("rotateship").setExecutor(new RotateShip());
        getCommand("setshipforwarddirection").setExecutor(new SetShipForwardDirection());
        getCommand("setupShip").setExecutor(new SetupShip());
        getServer().getPluginManager().registerEvents(new CommandMovementListener(), this);
        ShipAccess access = ShipAccess.getInstance();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
