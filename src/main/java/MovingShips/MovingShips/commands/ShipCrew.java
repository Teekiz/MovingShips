package MovingShips.MovingShips.commands;

import MovingShips.MovingShips.ships.Ship;
import MovingShips.MovingShips.ships.ShipAccess;
import MovingShips.MovingShips.utility.PermissionCheck;
import MovingShips.MovingShips.utility.ShipName;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShipCrew implements CommandExecutor {

    ShipAccess shipAccess = ShipAccess.getInstance();
    String shipName;
    Ship ship;
    Player player;
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String [] args) {
        try {
            if (args.length == 0){
                commandSender.sendMessage("<MovingShips> ShipCrew arguments: <Add/Remove/Accept/Reject> <Name of Player> <Name of Ship>.");
            } else {
                if (args[0].equalsIgnoreCase("Add") || args[0].equalsIgnoreCase("Remove")){
                    player = Bukkit.getPlayer(args[1]);
                    if (player != null){
                        shipName = ShipName.filterShipName(args, 2);
                        if (shipAccess.getShipByName(shipName) != null){
                            ship = shipAccess.getShipByName(shipName);
                            if (PermissionCheck.hasPermissionOwner(ship, commandSender)){
                                AddRemove(ship, args[0], player, commandSender);
                            } else {
                                commandSender.sendMessage("§4§ <MovingShips> You do not have permission to modify the ships crew.");
                            }
                        } else {
                            commandSender.sendMessage("§4§ <MovingShips> Cannot find ship by that name.");
                        }
                    } else {
                        commandSender.sendMessage("§4§ <MovingShips> Cannot find player by that name.");
                    }
                } else if (args[0].equalsIgnoreCase("Accept") || args[0].equalsIgnoreCase("Reject")) {
                    shipName = ShipName.filterShipName(args, 1);
                    if (shipAccess.getShipByName(shipName) != null){
                        ship = shipAccess.getShipByName(shipName);
                        AcceptReject(ship, args[0], commandSender);
                    } else {
                        commandSender.sendMessage("§4§ <MovingShips> Cannot find ship by that name.");
                    }
                } else {
                    commandSender.sendMessage("<MovingShips> Unrecognised command.");
                }
            }
        } catch (Exception e){
            commandSender.sendMessage("§4§ <MovingShips> Invalid command usage. Proper command usage: /ShipCrew <Add/Remove/Accept/Reject> <Name of Player> <Name of Ship>.");
        }

        return true;
    }

    public void AddRemove(Ship ship, String addOrRemove, Player player, CommandSender commandSender){
        boolean isCrewMember = false;
        for (String crew : ship.getCrew()){
            if (crew.equalsIgnoreCase(player.getName())){
                isCrewMember = true;
            }
        }
        if (addOrRemove.equalsIgnoreCase("Add")){
            if (isCrewMember){
                commandSender.sendMessage("§4§ <MovingShips> " + player.getName() + " is already a member of the crew for " + ship.getShipName() + ".");
            } else if (player.getName().equalsIgnoreCase(ship.getOwnerName())){
                commandSender.sendMessage("§4§ <MovingShips> " + player.getName() + " is already the captain of " + ship.getShipName() + ".");
            } else {
                ship.inviteCrew(player.getName());
                shipAccess.saveShip();
                commandSender.sendMessage("<MovingShips> " + player.getName() + " has been invited to the crew of " + ship.getShipName() + ".");
                player.sendMessage("§b§ <MovingShips> You have been invited to the crew of " + ship.getShipName() + "!");
                player.sendMessage("Please use /ShipCrew <Accept/Reject> <Name of Ship> to accept or reject this offer.");
            }

        } else if (addOrRemove.equalsIgnoreCase("Remove")) {
            if (!isCrewMember){
                commandSender.sendMessage("§4§ <MovingShips> + " + player.getName() + "is not a member of the crew for " + ship.getShipName() + ".");
            } else {
                ship.deleteCrew(player.getName());
                shipAccess.saveShip();
                commandSender.sendMessage("§4§ <MovingShips> " + player.getName() + " has been removed from the crew of " + ship.getShipName() + ".");
                player.sendMessage("§4§ <MovingShips> You has been removed from the crew of " + ship.getShipName() + ".");
            }
        }
    }

    public void AcceptReject(Ship ship, String acceptOrReject, CommandSender commandSender){
        boolean hasInvite = false;
        for (String playerName : ship.getInvitedCrew()){
            if (playerName.equalsIgnoreCase(commandSender.getName())){
                hasInvite = true;
            }
        }
        if (!hasInvite) {
            commandSender.sendMessage("§4§ <MovingShips> You do not current have an invite to " + ship.getShipName() + ".");
        } else {
            if (acceptOrReject.equalsIgnoreCase("Accept")){
                ship.addCrew(commandSender.getName());
                ship.removeInvitedCrew(commandSender.getName());
                shipAccess.saveShip();
                Bukkit.getPlayer(ship.getOwnerName()).sendMessage("§b§ <MovingShips> " + commandSender.getName() + " has been added to the crew of " + ship.getShipName() + ".");
                commandSender.sendMessage("<MovingShips> You has been added from the crew of " + ship.getShipName() + ".");
            } else if (acceptOrReject.equalsIgnoreCase("Reject")) {
                ship.removeInvitedCrew(commandSender.getName());
                shipAccess.saveShip();
                Bukkit.getPlayer(ship.getOwnerName()).sendMessage("§4§ <MovingShips> " + commandSender.getName() + " has rejected the offer to join the crew of " + ship.getShipName() + ".");
                commandSender.sendMessage("§4§ <MovingShips> You have rejected the invite to join the crew of " + ship.getShipName() + ".");
            }
        }
    }

}
