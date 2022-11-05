package tekizship.tekizship.ships;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ship {

    //todo - future versions could use sails up as speed

    String shipName;
    String ownerName;
    HashMap<Location, Material> shipBlocks;
    HashMap<String, Location> shipControls;
    ArrayList<String> crewNames;

    //todo queue the commands
    List<String> queuedCommands;
    String frontDirection;

    //todo - record the current speed for forward
    int speed = 0;


    public Ship(String ShipName, String ownerName, HashMap<Location, Material> shipBlocks) {
        this.shipName = ShipName;
        this.ownerName = ownerName;
        this.shipBlocks = shipBlocks;
        shipControls = new HashMap<>();
        crewNames = new ArrayList<>();
    }

    public Ship() {
        //used when loading from the external file.
    }

    public String getShipName() {
        return shipName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getFrontDirection() {
        return frontDirection;
    }
    public HashMap<Location, Material> getShipBlocks() {
        return shipBlocks;
    }

    public HashMap<String, Location> getShipControlLocations() {
        return shipControls;
    }
    public ArrayList<String> getCrew() {return crewNames;}

    public void setShipName(String shipName) {this.shipName = shipName;}

    public void setOwnerName(String ownerName) {this.ownerName = ownerName;}

    public void setFrontDirection(String direction) {
        frontDirection = direction;
    }

    public void setShipBlocks(HashMap<Location, Material> newShipBlocks) {
        shipBlocks = newShipBlocks;
    }

    //updates all the ship control locations at once. - useful for when the ship moves
    public void setShipControlLocations(HashMap<String, Location> shipControlLocations) {
        //string will be the control (forward, back)
        shipControls = shipControlLocations;
    }

    //checks to see if the control has been placed, if it isn't add it, otherwise replace it.
    public void setShipControlLocation(String controlName, Location location, Player player) {
        //checks to see if a control already exists in the location
        for (Map.Entry<String, Location> pair : getShipControlLocations().entrySet()) {
            if (pair.getValue().equals(location) && !pair.getKey().equalsIgnoreCase(controlName)){
                player.sendMessage("This block already has control '" + pair.getKey() + "' set. Please move the previous control and try again.");
                return;
            }
        }

        //checks to see if a control is already exists but placed elsewhere
        for (String controlNameOld : getShipControlLocations().keySet()) {
            //if the control already exists
            if (controlNameOld.equalsIgnoreCase(controlName)) {
                shipControls.replace(controlName, location);
                player.sendMessage("Control '" + controlName+ "' has been moved.");
                return;
            }
        }

        //assumes there are no existing controls
        shipControls.put(controlName, location);
        player.sendMessage("Control " + controlName + " successfully set to ship: " + shipName + ".");
    }

    public void setCrew(ArrayList <String> crewNames){
        this.crewNames = crewNames;
    }

    public void addCrew(String crewName){ crewNames.add(crewName);}

    public void deleteCrew(String crewName){crewNames.remove(crewName);}
}
