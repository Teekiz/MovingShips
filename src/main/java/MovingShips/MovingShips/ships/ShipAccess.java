package MovingShips.MovingShips.ships;


import java.io.*;
import java.util.ArrayList;

import MovingShips.MovingShips.MovingShips;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

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

    public void removeShip(Ship ship){
        ships.remove(ship);
    }

    //TODO Saves the ship to an exteneral file.
    public void saveShip(){
        try {
            JsonArray jsonShopArray = new JsonArray();
            if (ships.size() > 0) {
                for (Ship ship : ships) {
                    JsonObject JsonShipObject = new JsonObject();
                    JsonShipObject.addProperty("shipName", ship.getShipName());
                    JsonShipObject.addProperty("ownerName", ship.getOwnerName());
                    JsonShipObject.addProperty("frontDirection", ship.getFrontDirection());
                    JsonShipObject.addProperty("frontDirectionValue", ship.getFrontDirectionValue());
                    JsonArray jsonShipBlocks = new JsonArray();
                    for (Map.Entry<Location, Material> pair : ship.getShipBlocks().entrySet()){
                        JsonObject jsonBlock = new JsonObject();
                        jsonBlock.addProperty("World", pair.getKey().getBlock().getLocation().getWorld().getName());
                        jsonBlock.addProperty("X", pair.getKey().getBlock().getLocation().getBlockX());
                        jsonBlock.addProperty("Y", pair.getKey().getBlock().getLocation().getBlockY());
                        jsonBlock.addProperty("Z", pair.getKey().getBlock().getLocation().getBlockZ());
                        jsonBlock.addProperty("Material", pair.getValue().toString());
                        jsonShipBlocks.add(jsonBlock);
                    }
                    JsonArray jsonShipControls = new JsonArray();
                    for (Map.Entry<String, Location> pair : ship.getShipControlLocations().entrySet()){
                        JsonObject jsonControl = new JsonObject();
                        jsonControl.addProperty("World", pair.getValue().getBlock().getLocation().getWorld().getName());
                        jsonControl.addProperty("X", pair.getValue().getBlock().getLocation().getBlockX());
                        jsonControl.addProperty("Y", pair.getValue().getBlock().getLocation().getBlockY());
                        jsonControl.addProperty("Z", pair.getValue().getBlock().getLocation().getBlockZ());
                        jsonControl.addProperty("controlName", pair.getKey());
                        jsonShipControls.add(jsonControl);
                    }

                    JsonArray jsonShipCrew = new JsonArray();
                    for (String crewName : ship.getCrew()){;
                        jsonShipCrew.add(crewName);
                    }

                    JsonShipObject.add("shipBlocks", jsonShipBlocks);
                    JsonShipObject.add("controlLocations", jsonShipControls);
                    JsonShipObject.add("crew", jsonShipCrew);
                    jsonShopArray.add(JsonShipObject);
                }
            }

            File file = new File(MovingShips.getPlugin().getDataFolder().getAbsolutePath() + "/ShipData.json");
            file.getParentFile().mkdir();
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            String jsonString = jsonShopArray.toString();
            writer.write(jsonString);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            Bukkit.getLogger().info("Cannot save ship data.");
        }
    }

    public void loadShip(){
        try {
            JsonParser jsonParser = new JsonParser();
            File file = new File(MovingShips.getPlugin().getDataFolder().getAbsolutePath() + "/ShipData.json");
            if (file.exists()) {
                Reader reader = new FileReader(file);
                JsonArray jsonShopArray = (JsonArray) jsonParser.parse(reader);
                for (int i = 0; i < jsonShopArray.size(); i++) {
                    JsonObject jsonShip = (JsonObject) jsonShopArray.get(i);

                    Ship ship = new Ship();

                    String shipName = jsonShip.get("shipName").toString().replace("\"", "");
                    String ownerName = jsonShip.get("ownerName").toString().replace("\"", "");
                    String frontDirection = jsonShip.get("frontDirection").toString().replace("\"", "");
                    String frontDirectionValue = jsonShip.get("frontDirectionValue").toString().replace("\"", "");

                    ship.setShipName(shipName);
                    ship.setOwnerName(ownerName);
                    ship.setFrontDirection(frontDirection);
                    ship.setFrontDirectionValue(frontDirectionValue);

                    HashMap<Location, Material> shipBlocks = new HashMap<>();
                    JsonArray jsonShipBlocks = jsonShip.getAsJsonArray("shipBlocks");
                    for (int j = 0; j < jsonShipBlocks.size(); j++) {
                        JsonObject jsonBlock = (JsonObject) jsonShipBlocks.get(j);
                        String jsonWorld = jsonBlock.get("World").toString().replace("\"", "");
                        World world = Bukkit.getWorld(jsonWorld);
                        int x = jsonBlock.get("X").getAsInt();
                        int y = jsonBlock.get("Y").getAsInt();
                        int z = jsonBlock.get("Z").getAsInt();
                        Location blockLocation = new Location(world, x, y, z);
                        Material blockMaterial = blockLocation.getBlock().getType();
                        shipBlocks.put(blockLocation, blockMaterial);
                    }

                    ship.setShipBlocks(shipBlocks);

                    HashMap<String, Location> shipControls = new HashMap<>();
                    JsonArray jsonShipControls = jsonShip.getAsJsonArray("controlLocations");
                    for (int k = 0; k < jsonShipControls.size(); k++) {
                        JsonObject jsonControl = (JsonObject) jsonShipControls.get(k);
                        String controlName = jsonControl.get("controlName").toString().replace("\"", "");
                        String jsonWorld = jsonControl.get("World").toString().replace("\"", "");
                        World world = Bukkit.getWorld(jsonWorld);
                        int x = jsonControl.get("X").getAsInt();
                        int y = jsonControl.get("Y").getAsInt();
                        int z = jsonControl.get("Z").getAsInt();
                        Location controlLocation = new Location(world, x, y, z);
                        shipControls.put(controlName, controlLocation);
                    }

                    ship.setShipControlLocations(shipControls);

                    ArrayList<String> crew = new ArrayList<>();
                    JsonArray jsonCrew = jsonShip.getAsJsonArray("crew");
                    for (int h = 0; h < jsonCrew.size(); h++) {
                        String crewName = jsonCrew.get(h).toString().replace("\"", "");
                        crew.add(crewName);
                    }
                    ship.setCrew(crew);

                    ships.add(ship);
                }

                Bukkit.getLogger().info("MovingShips plugin data has been loaded.");
            }
        } catch (IOException e) {
            Bukkit.getLogger().info("Cannot load shop data.");

        }
    }
}
