package MovingShips.MovingShips.utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ShipName {
    private ShipName(){}

    public static String filterShipName(String[] args, int totalCommandArgs){
        String shipName = "";
        //for the ship name
        List<String> arguments = new ArrayList<>();
        arguments.addAll(Arrays.asList(args));


        //to add spaces between the names
        for (int i = totalCommandArgs; i < arguments.size(); i++) {
            if (i == arguments.size() - 1) {
                shipName += arguments.get(i);
            } else {
                shipName += arguments.get(i) + " ";
            }
        }
        return shipName;
    }
}
