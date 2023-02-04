package portsim.port;

import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchShipException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Queue of ships waiting to enter a Quay at the port. Ships are chosen based
 * on their priority.
 *
 * @ass2
 */
public class ShipQueue implements Encodable {
    /**
     * A list containing all the ships currently stored in this ShipQueue.
     */
    private ArrayList<Ship> queue;

    /**
     * Constructs a new ShipQueue with an initially empty queue of ships.
     *
     * @ass2
     */
    public ShipQueue() {
        this.queue = new ArrayList<>();
    }

    /**
     * Gets the next ship to enter the port and removes it from the queue.
     *
     * @return next ship in queue
     *
     * @ass2
     */
    public Ship poll() {
        if (this.queue.size() != 0) {
            Ship result =  queue.get(findNext());
            queue.remove(findNext());
            return result;
        }
        return null;
    }

    /**
     * Returns the next ship waiting to enter the port.
     * The queue should not change.
     *
     * @return next ship in queue
     *
     * @ass2
     */
    public Ship peek() {
        if (this.queue.size() != 0) {
            return queue.get(findNext());
        }
        return null;
    }

    /**
     * A helper method to return the index of the next ship waiting to enter
     * the port.
     *
     * @return the index of the ship waiting to enter the port.
     *
     * @ass2
     */
    private int findNext() {
        // A hashmap of possible indexes.
        Map<String, Integer> possibleIndex = new HashMap<>();

        // Iterate through the list of ships and record the first instance
        // of a ship of each criteria on the prioty list occuring.
        for (int i = 0; i < queue.size(); i++) {
            Ship ship = queue.get(i);
            NauticalFlag flag = ship.getFlag();

            if (flag.equals(NauticalFlag.BRAVO)) {
                checkFirst(possibleIndex, "dangerous", i);
            } else if (flag.equals(NauticalFlag.WHISKEY)) {
                checkFirst(possibleIndex, "medical", i);
            } else if (flag.equals(NauticalFlag.HOTEL)) {
                checkFirst(possibleIndex, "dockReady", i);
            } else if (ship instanceof ContainerShip
                    && possibleIndex.get("containers") == null) {
                possibleIndex.put("containers", i);
            }
            // Else none of the ships meet the priorty criteria
            // So do nothing.
        }

        // Check the map of possible indexes
        // Find which index should be returned
        if (possibleIndex.get("dangerous") != null) {
            return possibleIndex.get("dangerous");
        } else if (possibleIndex.get("medical") != null) {
            return possibleIndex.get("medical");
        } else if (possibleIndex.get("dockReady") != null) {
            return possibleIndex.get("dockReady");
        } else if (possibleIndex.get("containers") != null) {
            return possibleIndex.get("containers");
        } else  {
            // There are no ships  that meet the priority criteria
            // So return the index of the ship added to the queue first
            return 0;
        }
    }

    private void checkFirst(Map<String, Integer> possibleIndex,
                            String type,
                            int index) {
        if (possibleIndex.get(type) == null) {
            possibleIndex.put(type, index);
        } /*else if (!(queue.get(possibleIndex.get(type)) instanceof ContainerShip)
                && queue.get(index) instanceof ContainerShip) {
            possibleIndex.put(type, index);
        } */
    }

    /**
     * Adds the specified ship to the queue.
     *
     * @param ship to be added to queue
     *
     * @ass2
     */
    public void add(Ship ship) {
        this.queue.add(ship);
    }

    /**
     * Returns a list containing all the ships currently stored in this
     * ShipQueue.
     *
     * @return ships in queue
     *
     * @ass2
     */
    public List<Ship> getShipQueue() {
        return new ArrayList<Ship>(queue);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ShipQueue) {
            return queue.equals(((ShipQueue) o).getShipQueue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Ship ship : queue) {
            result += ship.hashCode();
        }
        return result;
    }

    /**
     * Returns the machine-readable string representation of this ShipQueue.
     *
     *
     * @return encoded string representation of this ShipQueue.
     *
     * @ass2
     */
    public String encode() {
        String result = "" + this.getClass().getSimpleName() + ":"
                + queue.size() + ":";

        for (int i = 0; i < queue.size(); i++) {
            result += queue.get(i).getImoNumber();

            if (i < queue.size() - 1) {
                result += ",";
            }
        }
        return result;
    }

    /**
     * Creates a ship queue from a string encoding.
     *
     * @param string - string containing the encoded ShipQueue
     *
     * @return decoded ship queue instance
     *
     * @ass2
     */
    public static ShipQueue fromString(String string)
            throws BadEncodingException {
        String [] result = string.split(":", -1);
        int numShipsInQueue = stringToInt(result);
        ShipQueue decodedShipQueue = new ShipQueue();

        if (result.length != 3
                || !result[0].equals("ShipQueue")
                || numShipsInQueue < 0) {
            throw new BadEncodingException();
        }

        String [] ships;
        if (result[2].equals("")) {
            ships = ",".split(",");
        } else {
            ships = result[2].split(",");
        }

        int shipCounter = 0;
        for (String ship : ships) {
            long shipImo = checkImo(ship);

            if (shipImo < 0) {
                throw new BadEncodingException();
            }

            try {
                decodedShipQueue.add(Ship.getShipByImoNumber(shipImo));
            } catch (NoSuchShipException e) {
                throw new BadEncodingException();
            }

            shipCounter++;

        }

        if (numShipsInQueue != shipCounter) {
            throw new BadEncodingException();
        }

        return decodedShipQueue;

    }

    private static int stringToInt(String [] i) {
        try {
            return Integer.parseInt(i[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }

    private static long checkImo(String s) {
        try {
            return Long.parseLong(s);

        } catch (NumberFormatException e) {
            return -1;
        }
    }



}
