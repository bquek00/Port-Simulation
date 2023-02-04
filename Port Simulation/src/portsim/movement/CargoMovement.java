package portsim.movement;

import portsim.cargo.Cargo;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The movement of cargo coming into or out of the port.
 *
 * @ass1_partial
 */
public class CargoMovement extends Movement implements Encodable {

    /**
     * The cargo that will be involved in the movement
     */
    private List<Cargo> cargo;

    /**
     * Creates a new cargo movement with the given action time and direction
     * to be undertaken with the given cargo.
     *
     * @param time      the time the movement should occur
     * @param direction the direction of the movement
     * @param cargo     the cargo to be moved
     * @throws IllegalArgumentException if time &lt; 0
     * @ass1
     */
    public CargoMovement(long time, MovementDirection direction,
                         List<Cargo> cargo) throws IllegalArgumentException {
        super(time, direction);
        this.cargo = cargo;
    }

    /**
     * Returns the cargo that will be moved.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return all cargo in the movement
     * @ass1
     */
    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    /**
     * Returns the human-readable string representation of this CargoMovement.
     * <p>
     * The format of the string to return is
     * <pre>
     * DIRECTION CargoMovement to occur at time involving num piece(s) of cargo </pre>
     * Where:
     * <ul>
     *   <li>{@code DIRECTION} is the direction of the movement </li>
     *   <li>{@code time} is the time the movement is meant to occur </li>
     *   <li>{@code num} is the number of cargo pieces that are being moved</li>
     * </ul>
     * <p>
     * For example: <pre>
     * OUTBOUND CargoMovement to occur at 135 involving 5 piece(s) of cargo </pre>
     *
     * @return string representation of this CargoMovement
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s involving %d piece(s) of cargo",
            super.toString(),
            this.cargo.size());
    }

    @Override
    public String encode() {
        String result = super.encode() + ":" + this.cargo.size() + ":";

        for (int i = 0; i < cargo.size(); i++) {
            result += cargo.get(i).getId();

            if (i != cargo.size() - 1) {
                result += ",";
            }
        }

        return result;
    }

    /**
     * Creates a cargo movement from a string encoding.
     *
     * @param string - string containing the encoded CargoMovement
     *
     * @return decoded CargoMovement instance
     * @ass2
     */
    public static CargoMovement fromString(String string)
            throws BadEncodingException {
        String[] result = string.split(":");
        ArrayList<Cargo> storedCargo = new ArrayList<>();
        Map<Integer, Cargo> cargoRegistry = Cargo.getCargoRegistry();

        if (result.length < 5
                || !result[0].equals("CargoMovement")
                || stringToLong(result[1]) < 0
                || !isDirection(result[2])
                || stringToInt(result[3]) < 1) {
            throw new BadEncodingException();
        }

        String[] listedCargo = result[4].split(",");

        for (String cargo : listedCargo) {
            if (stringToInt(cargo) < 0
                    || !Cargo.cargoExists(stringToInt(cargo))) {
                throw new BadEncodingException();
            }
            storedCargo.add(cargoRegistry.get(stringToInt(cargo)));
        }
        if (storedCargo.size() != stringToInt(result[3])) {
            throw new BadEncodingException();
        }

        return new CargoMovement(stringToLong(result[1]),
                MovementDirection.valueOf(result[2]), storedCargo);
    }


    /**
     * Check if a string can be parsed as an int
     *
     * @param i String to be parsed as an int
     *
     * @return parsed int if successful -1 otherwise.
     * @ass2
     */
    private static int stringToInt(String i) {
        try {
            return Integer.parseInt(i);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
