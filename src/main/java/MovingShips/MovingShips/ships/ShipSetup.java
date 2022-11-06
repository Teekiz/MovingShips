package MovingShips.MovingShips.ships;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ShipSetup {
    /*
        This class here is used to store the relevant information regarding the ship until all the information is complete.
     */

    Player player;

    Location firstLocation;
    Location secondLocation;

    String shipDirection;
    String shipDirectionValue;

    String shipName;

    Location forward;
    Location back;
    Location left;
    Location right;
    Location rotateLeft;
    Location rotateRight;

    public ShipSetup(Player player){
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getFirstLocation() {
        return firstLocation;
    }

    public void setFirstLocation(Location firstLocation) {
        this.firstLocation = firstLocation;
    }

    public Location getSecondLocation() {
        return secondLocation;
    }

    public void setSecondLocation(Location secondLocation) {
        this.secondLocation = secondLocation;
    }

    public String getShipDirection() {
        return shipDirection;
    }

    public void setShipDirection(String shipDirection) {
        this.shipDirection = shipDirection;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public Location getForward() {
        return forward;
    }

    public void setForward(Location forward) {
        this.forward = forward;
    }

    public Location getBack() {
        return back;
    }

    public void setBack(Location back) {
        this.back = back;
    }

    public Location getLeft() {
        return left;
    }

    public void setLeft(Location left) {
        this.left = left;
    }

    public Location getRight() {
        return right;
    }

    public void setRight(Location right) {
        this.right = right;
    }

    public Location getRotateLeft() {
        return rotateLeft;
    }

    public void setRotateLeft(Location rotateLeft) {
        this.rotateLeft = rotateLeft;
    }

    public Location getRotateRight() {
        return rotateRight;
    }

    public void setRotateRight(Location rotateRight) {
        this.rotateRight = rotateRight;
    }

    public String getShipDirectionValue() {
        return shipDirectionValue;
    }

    public void setShipDirectionValue(String shipDirectionValue) {
        this.shipDirectionValue = shipDirectionValue;
    }

    //these are just to save writing it so much in the if statements for setup ship.
    public boolean isFirstAndSecondLocationSet(){
        if (getFirstLocation() != null && getSecondLocation() != null){
            return true;
        }
        return false;
    }

    public boolean isDirectionAndFirstAndSecondLocationSet(){
        if (getFirstLocation() != null && getSecondLocation() != null && getShipDirection() != null && getShipDirectionValue() != null){
            return true;
        }
        return false;
    }

    public boolean isControlsSet(){
        if (getForward() != null && getBack() != null &&  getLeft() != null && getRight() !=  null && getRotateLeft() != null && getRotateRight() != null){
            return true;
        }
        return false;
    }


}
