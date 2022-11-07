package MovingShips.MovingShips;

import MovingShips.MovingShips.commands.*;
import MovingShips.MovingShips.events.CommandMovementListener;
import MovingShips.MovingShips.tabcompleters.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import MovingShips.MovingShips.scheduletasks.ShipMover;
import MovingShips.MovingShips.ships.ShipAccess;


public final class MovingShips extends JavaPlugin {

    private static MovingShips plugin;

    //todo add crew system
    //todo add a system to prevent creating or destroying blocks on a ship
    //todo move beds and chest contents each movement check
    //todo to check for special blocks (i.e. ladders) and set them correctly
    //todo setup config file, to set max speed, max amount of ships
    //todo check if the ship is actually in water

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Bukkit.getLogger().info("MovingShips Plugin Enabled");
        getCommand("createship").setExecutor(new CreateShip());
        getCommand("createship").setTabCompleter(new CreateShipTabCompleter());

        getCommand("moveship").setExecutor(new MoveShip());
        getCommand("moveship").setTabCompleter(new MoveShipTabCompleter());

        getCommand("rotateship").setExecutor(new RotateShip());
        getCommand("rotateship").setTabCompleter(new RotateShipTabCompleter());

        getCommand("setshipcontrols").setExecutor(new SetShipControls());
        getCommand("setshipcontrols").setTabCompleter(new SetShipControlsTabCompleter());

        getCommand("setshipforwarddirection").setExecutor(new SetShipForwardDirection());
        getCommand("setshipforwarddirection").setTabCompleter(new SetShipForwardDirectionCompleter());

        getCommand("shipsetup").setExecutor(new SetupShip());
        getCommand("shipsetup").setTabCompleter(new SetupShipTabCompleter());

        getCommand("deleteship").setExecutor(new DeleteShip());
        getCommand("deleteship").setTabCompleter(new DeleteShipTabCompleter());

        getCommand("shiplist").setExecutor(new ShipList());
        getCommand("shiplist").setTabCompleter(new BlankArgsTabCompleter());

        getCommand("shiphelp").setExecutor(new ShipHelp());
        getCommand("shiphelp").setTabCompleter(new BlankArgsTabCompleter());

        getCommand("shipinfo").setExecutor(new ShipInfo());
        getCommand("shipinfo").setTabCompleter(new ShipsTabCompleter());

        getCommand("shipcrew").setExecutor(new ShipCrew());
        getCommand("shipcrew").setTabCompleter(new ShipCrewTabCompleter());

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
