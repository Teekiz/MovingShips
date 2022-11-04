package tekizship.tekizship.ships;


import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ShipAccess {

    private static ShipAccess instance;
    private ShipAccess(){}
    private ArrayList<Ship> ships = new ArrayList<>();
    private Ship selectedShip;


    //theory - on enable, a list of ship objects will be created.
    public static synchronized ShipAccess getInstance(){
        if(instance == null){
            instance = new ShipAccess();
        }
        return instance;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public Ship getShipByName(String shipName){
        for (Ship ship : getShips()) {
            if (ship.getShipName().equalsIgnoreCase(shipName)){
                selectedShip = ship;
                break;
            } else {
                selectedShip  = null;
            }
        }
        return selectedShip;
    }

    public ArrayList<Ship> getShipByOwnerName(String ownerName){
        ArrayList<Ship> shipsOwnedByPlayer = new ArrayList<>();
        for (Ship ship : getShips()) {
            if (ship.getOwnerName().equalsIgnoreCase(ownerName)){
                shipsOwnedByPlayer.add(ship);}
        }
        return shipsOwnedByPlayer;
    }

    public void createShip(String shipName, String ownerName, HashMap<Location, Material> shipBlocks){
        Ship newShip = new Ship(shipName, ownerName, shipBlocks);
        ships.add(newShip);
    }

    public void saveShip(String shipName){
        //TODO Saves the ship to an exteneral file.
    }
}
