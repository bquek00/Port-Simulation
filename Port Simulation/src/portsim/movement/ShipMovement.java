package portsim.movement;

import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;

import java.util.List;
import java.util.Map;

/**
 * The movement of a ship coming into or out of the port.
 *
 * @ass1_partial
 */
public class ShipMovement extends Movement implements Encodable {

    /**
     * The ship entering of leaving the Port
     */
    private Ship ship;

    /**
     * Creates a new ship movement with the given action time and direction
     * to be undertaken with the given ship.
     *
     * @param time      the time the movement should occur
     * @param direction the direction of the movement
     * @param ship      the ship which that is waiting to move
     * @throws IllegalArgumentException if time &lt; 0
     * @ass1
     */
    public ShipMovement(long time, MovementDirection direction, Ship ship)
        throws IllegalArgumentException {
        super(time, direction);
        this.ship = ship;
    }

    /**
     * Returns the ship undertaking the movement.
     *
     * @return movements ship
     * @ass1
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns the human-readable string representation of this ShipMovement.
     * <p>
     * The format of the string to return is
     * <pre>
     * DIRECTION ShipMovement to occur at time involving the ship name </pre>
     * Where:
     * <ul>
     *   <li>{@code DIRECTION} is the direction of the movement </li>
     *   <li>{@code time} is the time the movement is meant to occur </li>
     *   <li>{@code name} is the name of the ship that is being moved</li>
     * </ul>
     * For example:
     * <pre>
     * OUTBOUND ShipMovement to occur at 135 involving the ship Voyager </pre>
     *
     * @return string representation of this ShipMovement
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s involving the ship %s",
            super.toString(),
            this.ship.getName());
    }

    @Override
    public String encode() {
        return super.encode() + ":" + ship.getImoNumber();
    }

    /**
     * Creates a ship movement from a string encoding.
     *
     * @param string - string containing the encoded ShipMovement
     *
     * @return decoded ShipMovement instance
     *
     * @throws BadEncodingException - if the format of the given string is
     * invalid according to the rules above
     * @ass1
     */
    public static ShipMovement fromString(String string)
            throws BadEncodingException {
        String [] result = string.split(":");
        Map<Long, Ship> ships = Ship.getShipRegistry();

        if (result.length != 4
                || !result[0].equals("ShipMovement")
                || stringToLong(result[1]) < 0
                || !isDirection(result[2])
                || stringToLong(result[3]) < 0
                || !Ship.shipExists(stringToLong(result[3]))) {
            throw new BadEncodingException();
        }

        return new ShipMovement(stringToLong(result[1]),
                MovementDirection.valueOf(result[2]), ships.get(stringToLong(result[3])));
    }

}
