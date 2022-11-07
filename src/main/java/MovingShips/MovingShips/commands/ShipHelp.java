package MovingShips.MovingShips.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShipHelp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        commandSender.sendMessage("<MovingShips> --- Commands  --- <MovingShips>");
        commandSender.sendMessage("§b§ /createship");
        commandSender.sendMessage("description: - Creates a ship using the two locations provided.");
        commandSender.sendMessage("usage: - /CreateShip <X1> Y1> <Z1> <X2> <Y2> <Z2> <Name of Ship>");

        commandSender.sendMessage("§b§ /shipsetup");
        commandSender.sendMessage("description: - A setup guide to creating a ship.");
        commandSender.sendMessage("usage: - /ShipSetup");

        commandSender.sendMessage("§b§ /moveship");
        commandSender.sendMessage("description: - Moves the ship in the specified direction and amount.");
        commandSender.sendMessage("usage: - /MoveShip <Forward/Back/Left/Right> <Distance> <Name of Ship>.");

        commandSender.sendMessage("§b§ /rotateship");
        commandSender.sendMessage("description: - Rotates the ship in the specified direction.");
        commandSender.sendMessage("usage: - /RotateShip <Left/Right> <Name of Ship>.");

        commandSender.sendMessage("§b§ /setshipcontrols");
        commandSender.sendMessage("description: - Sets the specified control at the target location.");
        commandSender.sendMessage("usage: - /SetShipControls <Forward/Back/Left/Right/RotateLeft/RotateRight> <Name of Ship>");

        commandSender.sendMessage("§b§ /setforwarddirection");
        commandSender.sendMessage("description: - Sets the forward direction of the ship.");
        commandSender.sendMessage("usage: - /setforwarddirection <Name of Ship>");

        commandSender.sendMessage("§b§ /shiplist");
        commandSender.sendMessage("description: - Shows a list of all the ships in the server.");
        commandSender.sendMessage("usage: - /ShipList ");

        commandSender.sendMessage("§b§ /deleteship");
        commandSender.sendMessage("description: - Deletes the specified ship.");
        commandSender.sendMessage("usage: - /deleteship <Name of Ship>");

        commandSender.sendMessage("§b§ /shipinfo");
        commandSender.sendMessage("description: - Shows information about the specified ship.");
        commandSender.sendMessage("usage: - /shipinfo <Name of Ship>");

        return true;
    }
}