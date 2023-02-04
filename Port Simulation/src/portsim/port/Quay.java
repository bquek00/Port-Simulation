package portsim.port;

import portsim.ship.Ship;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchShipException;


/**
 * Quay is a platform lying alongside or projecting into the water where
 * ships are moored for loading or unloading.
 *
 * @ass1_partial
 */
public abstract class Quay implements Encodable {
    /**
     * The ID of the quay
     */
    private int id;

    /**
     * The ship currently in the Quay
     */
    private Ship ship;

    /**
     * Creates a new Quay with the given ID, with no ship docked at the quay.
     *
     * @param id quay ID
     * @throws IllegalArgumentException if ID &lt; 0
     * @ass1
     */
    public Quay(int id) throws IllegalArgumentException {
        if (id < 0) {
            throw new IllegalArgumentException("Quay ID must be greater than"
                + " or equal to 0: " + id);
        }
        this.id = id;
        this.ship = null;
    }

    /**
     * Get the id of this quay
     *
     * @return quay id
     * @ass1
     */
    public int getId() {
        return id;
    }

    /**
     * Docks the given ship at the Quay so that the quay becomes occupied.
     *
     * @param ship ship to dock to the quay
     * @ass1
     */
    public void shipArrives(Ship ship) {
        this.ship = ship;
    }

    /**
     * Removes the current ship docked at the quay.
     * The current ship should be set to {@code null}.
     *
     * @return the current ship or null if quay is empty.
     * @ass1
     */
    public Ship shipDeparts() {
        Ship current = this.ship;
        this.ship = null;
        return current;
    }

    /**
     * Returns whether a ship is currently docked at this quay.
     *
     * @return true if there is no ship docked else false
     * @ass1
     */
    public boolean isEmpty() {
        return this.ship == null;
    }

    /**
     * Returns the ship currently docked at the quay.
     *
     * @return ship at quay or null if no ship is docked
     * @ass1
     */
    public Ship getShip() {
        return ship;
    }

    @Override
    public boolean equals(Object o) {
        String compareClass = o.getClass().getSimpleName();
        String thisClass = this.getClass().getSimpleName();
        if (!compareClass.equals(thisClass)) {
            return false;
        }

        return this.id == ((Quay) o).getId()
                && this.isEmpty() == ((Quay) o).isEmpty();
    }

    @Override
    public int hashCode() {
        int result = this.id;

        if (this.isEmpty()) {
            result += 1;
        }

        return result;
    }

    /**
     * Returns the human-readable string representation of this quay.
     * <p>
     * The format of the string to return is
     * <pre>QuayClass id [Ship: imoNumber]</pre>
     * Where:
     * <ul>
     * <li>{@code id} is the ID of this quay</li>
     * <li>{@code imoNumber} is the IMO number of the ship docked at this
     * quay, or {@code None} if the quay is unoccupied.</li>
     * </ul>
     * <p>
     * For example: <pre>BulkQuay 1 [Ship: 2313212]</pre> or
     * <pre>ContainerQuay 3 [Ship: None]</pre>
     *
     * @return string representation of this quay
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %d [Ship: %s]",
            this.getClass().getSimpleName(),
            this.id,
            (this.ship != null ? this.ship.getImoNumber() : "None"));
    }

    /**
     * Returns the machine-readable string representation of this Quay.
     *
     * @return encoded string representation of this quay
     * @ass2
     */
    public String encode() {
        String result =  "" + this.getClass().getSimpleName() + ":"
                + this.id + ":";

        if (this.isEmpty()) {
            result += "None";
        } else {
            result += this.ship.getImoNumber();
        }

        return result;
    }

    /**
     * Reads a Quay from its encoded representation in the given string.
     *
     * @param string - string containing the encoded Quay
     *
     * @return decoded Quay instance
     * @ass2
     */
    public static Quay fromString(String string)
            throws BadEncodingException {
        String [] result = string.split(":");
        int id = stringToId(result);
        int capacity = stringToCapacity(result);
        Quay decodedQuay;

        if (result.length < 4
                || id < 0
                || capacity < 0) {
            throw new BadEncodingException();
        }

        if (result[0].equals("BulkQuay")) {
            try {
                decodedQuay = new BulkQuay(id, capacity);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException();
            }

        } else if (result[0].equals("ContainerQuay")) {
            try {
                decodedQuay = new ContainerQuay(id, capacity);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException();
            }
        } else {
            throw new BadEncodingException();
        }

        if (!result[2].equals("None")) {
            long imo = checkImo(result[2]);
            if (imo < 0) {
                throw new BadEncodingException();
            }

            try {
                decodedQuay.shipArrives(Ship.getShipByImoNumber(imo));
            } catch (NoSuchShipException e) {
                throw new BadEncodingException();
            }
        }

        return decodedQuay;

    }

    private static int stringToId(String [] i) {
        try {
            return Integer.parseInt(i[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }

    private static int stringToCapacity(String [] i) {
        try {
            return Integer.parseInt(i[3]);
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
