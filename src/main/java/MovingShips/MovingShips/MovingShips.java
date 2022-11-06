package MovingShips.MovingShips;

import MovingShips.MovingShips.commands.*;
import MovingShips.MovingShips.events.CommandMovementListener;
import MovingShips.MovingShips.tabcompleters.CreateShipTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import MovingShips.MovingShips.scheduletasks.ShipMover;
import MovingShips.MovingShips.ships.ShipAccess;


public final class MovingShips extends JavaPlugin {

    private static MovingShips plugin;

    //todo add command listeners
    //todo add crew system
    //todo add a system to prevent creating or destroying blocks on a ship
    //todo move beds and chest contents each movement check
    //todo neaten up the commands
    //todo to check for special blocks (i.e. ladders) and set them correctly
    //todo setup config file, to set max speed, max amount of ships
    //todo create a delete command
    //todo check if the ship is actually in water
    //todo add ship permissions

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Bukkit.getLogger().info("MovingShips Plugin Enabled");
        getCommand("createship").setExecutor(new CreateShip());
        getCommand("createship").setTabCompleter(new CreateShipTabCompleter());
        getCommand("moveship").setExecutor(new MoveShip());
        getCommand("setshipcontrols").setExecutor(new SetShipControls());
        getCommand("rotateship").setExecutor(new RotateShip());
        getCommand("setshipforwarddirection").setExecutor(new SetShipForwardDirection());
        getCommand("setupShip").setExecutor(new SetupShip());

        getServer().getPluginManager().registerEvents(new CommandMovementListener(), this);

        ShipAccess access = ShipAccess.getInstance();
        ShipMover shipMover = new ShipMover();
        access.loadShip();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                shipMover.moveShips();
            }
        }, 0, 60);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MovingShips getPlugin(){
        return plugin;
    }
}
