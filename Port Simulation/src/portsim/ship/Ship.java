package portsim.ship;

import portsim.cargo.Cargo;
import portsim.port.Quay;
import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchShipException;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a ship whose movement is managed by the system.
 * <p>
 * Ships store various types of cargo which can be loaded and unloaded at a port.
 *
 * @ass1_partial
 */
public abstract class Ship implements Encodable {
    /**
     * Name of the ship
     */
    private String name;

    /**
     * Unique 7 digit identifier to identify this ship (no leading zero's [0])
     */
    private long imoNumber;

    /**
     * Port of origin of ship
     */
    private String originFlag;

    /**
     * Maritime flag designated for use on this ship
     */
    private NauticalFlag flag;

    /**
     * Database of all ships currently active in the simulation
     */
    private static Map<Long, Ship> shipRegistry = new HashMap<>();

    /**
     * Creates a new ship with the given
     * <a href="https://en.wikipedia.org/wiki/IMO_number">IMO number</a>,
     * name, origin port flag and nautical flag.
     * <p>
     * Finally, the ship should be added to the ship registry with the
     * IMO number as the key.
     *
     * @param imoNumber  unique identifier
     * @param name       name of the ship
     * @param originFlag port of origin
     * @param flag       the nautical flag this ship is flying
     * @throws IllegalArgumentException if a ship already exists with the given
     *                                  imoNumber, imoNumber &lt; 0 or imoNumber is not 7 digits
     *                                  long (no leading zero's [0])
     * @ass1_partial
     */
    public Ship(long imoNumber, String name, String originFlag,
                NauticalFlag flag) throws IllegalArgumentException {
        if (imoNumber < 0) {
            throw new IllegalArgumentException("The imoNumber of the ship "
                + "must be positive: " + imoNumber);
        }
        if (String.valueOf(imoNumber).length() != 7
                || String.valueOf(imoNumber).startsWith("0")) {
            throw new IllegalArgumentException("The imoNumber of the ship "
                + "must have 7 digits (no leading zero's [0]): " + imoNumber);
        }
        if (shipExists(imoNumber)) {
            throw new IllegalArgumentException("IMO Number already exists");
        }

        this.imoNumber = imoNumber;
        this.name = name;
        this.originFlag = originFlag;
        this.flag = flag;

        shipRegistry.put(imoNumber, this);
    }

    /**
     * Checks if a ship exists in the simulation using its IMO number.
     *
     * @param imoNumber unique key to identify ship
     *
     * @return true if there is a ship with key imoNumber else false
     * @ass2
     */
    public static boolean shipExists(long imoNumber) {
        for (Map.Entry<Long, Ship> entry : shipRegistry.entrySet()) {
            if (entry.getKey() == imoNumber) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the ship specified by the IMO number.
     *
     * @param imoNumber unique key to identify ship
     *
     * @return Ship specified by the given IMO number
     * @ass2
     */
    public static Ship getShipByImoNumber(long imoNumber)
            throws NoSuchShipException {
        for (Map.Entry<Long, Ship> entry : shipRegistry.entrySet()) {
            if (entry.getKey() == imoNumber) {
                return entry.getValue();
            }
        }
        throw new NoSuchShipException();
    }

    /**
     * Check if this ship can dock with the specified quay according
     * to the conditions determined by the ships type.
     *
     * @param quay quay to be checked
     * @return true if the Quay satisfies the conditions else false
     * @ass1
     */
    public abstract boolean canDock(Quay quay);

    /**
     * Checks if the specified cargo can be loaded onto the ship according
     * to the conditions determined by the ships type and contents.
     *
     * @param cargo cargo to be loaded
     * @return true if the Cargo satisfies the conditions else false
     * @ass1
     */
    public abstract boolean canLoad(Cargo cargo);

    /**
     * Loads the specified cargo onto the ship.
     *
     * @param cargo cargo to be loaded
     * @require Cargo given is able to be loaded onto this ship according to
     * the implementation of {@link Ship#canLoad(Cargo)}
     * @ass1
     */
    public abstract void loadCargo(Cargo cargo);

    /**
     * Returns this ship's name.
     *
     * @return name
     * @ass1
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns this ship's IMO number.
     *
     * @return imoNumber
     * @ass1
     */
    public long getImoNumber() {
        return this.imoNumber;
    }

    /**
     * Returns this ship's flag denoting its origin.
     *
     * @return originFlag
     * @ass1
     */
    public String getOriginFlag() {
        return this.originFlag;
    }

    /**
     * Returns the nautical flag the ship is flying.
     *
     * @return flag
     * @ass1
     */
    public NauticalFlag getFlag() {
        return this.flag;
    }

    /**
     * Returns the database of ships currently active in the simulation as a
     * mapping from the ship's IMO number to its Ship instance.
     *
     * @return ship registry database
     * @ass2s
     */
    public static Map<Long, Ship> getShipRegistry() {
        Map<Long, Ship> result = new HashMap<>();
        for (Map.Entry<Long, Ship> entry : shipRegistry.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Ship)) {
            return false;
        }

        Ship givenShip = (Ship) o;

        if (this.getName() == givenShip.getName()
                && this.getFlag() == givenShip.getFlag()
                && this.getOriginFlag() == givenShip.getOriginFlag()
                && this.getImoNumber() == givenShip.getImoNumber()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode() + this.getOriginFlag().hashCode()
                + this.getFlag().hashCode() + (int) this.getImoNumber();
    }

    /**
     * Returns the human-readable string representation of this Ship.
     * <p>
     * The format of the string to return is
     * <pre>ShipClass name from origin [flag]</pre>
     * Where:
     * <ul>
     *   <li>{@code ShipClass} is the Ship class</li>
     *   <li>{@code name} is the name of this ship</li>
     *   <li>{@code origin} is the country of origin of this ship</li>
     *   <li>{@code flag} is the nautical flag of this ship</li>
     * </ul>
     * For example: <pre>BulkCarrier Evergreen from Australia [BRAVO]</pre>
     *
     * @return string representation of this Ship
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %s from %s [%s]",
            this.getClass().getSimpleName(),
            this.name,
            this.originFlag,
            this.flag);
    }

    /**
     * Resets the global ship registry.
     * This utility method is for the testing suite.
     *
     * @given
     */
    public static void resetShipRegistry() {
        Ship.shipRegistry = new HashMap<>();
    }

    /**
     * Returns the machine-readable string representation of this Ship.
     *
     * @return encoded string representation of this Ship
     * @ass2s
     */
    public String encode() {
        return "" + this.getClass().getSimpleName() + ":"
                + this.getImoNumber() + this.getName() + this.getOriginFlag()
                + this.getFlag();
    }

    /**
     * Reads a Ship from its encoded representation in the given string.
     *
     * @param string string containing the encoded Ship
     *
     * @return decoded ship instance
     * @ass2
     */
    public static Ship fromString(String string) throws BadEncodingException {
        String [] result = string.split(":", -1);
        long shipImo = checkImo(result);
        NauticalFlag flag = checkNautical(result);

        if (result.length < 7
                || shipImo < -1
                || flag == null) {
            throw new BadEncodingException();
        }

        int capacity = stringToInt(result[5]);

        if (result[0].equals("BulkCarrier")) {
            int id = stringToInt(result[6]);

            if (result.length != 7) {
                throw new BadEncodingException();
            }


            BulkCarrier decodedShip;
            try {
                decodedShip =  new BulkCarrier(shipImo, result[2],
                        result[3], flag, capacity);


            } catch (IllegalArgumentException e) {
                throw new BadEncodingException();
            }

            if (!result[6].equals("")) {
                if (Cargo.cargoExists(id)) {
                    Cargo toLoad = Cargo.getCargoRegistry().get(id);

                    if (!decodedShip.canLoad(toLoad)) {
                        throw new BadEncodingException();
                    }

                    decodedShip.loadCargo(toLoad);

                } else {
                    throw new BadEncodingException();
                }
            }

            return decodedShip;

        } else if (result[0].equals("ContainerShip")) {
            if (result.length != 8) {
                throw new BadEncodingException();
            }

            Ship decodedShip;

            try {
                decodedShip = new ContainerShip(shipImo, result[2],
                        result[3], flag, capacity);
            } catch (IllegalArgumentException e) {
                throw new BadEncodingException();
            }

            // Check each cargo is valid and loadable.
            // Then load it to the ship.
            String encodedCargo = result[7];

            if (!encodedCargo.equals("")) {
                String [] cargos =
                        encodedCargo.split(",");

                for (String cargo : cargos) {
                    int id = stringToInt(cargo);
                    if (!Cargo.cargoExists(id)) {
                        throw new BadEncodingException();
                    }

                    Cargo toLoad = Cargo.getCargoRegistry().get(id);

                    if (!decodedShip.canLoad(toLoad)) {
                        throw new BadEncodingException();
                    }

                    decodedShip.loadCargo(toLoad);
                }
            }

            return decodedShip;

        }

        // Else ship's type specified is not one of ContainerShip or BulkCarrier
        throw new BadEncodingException();

    }

    /**
     * Check for a valid imo given a string.
     *
     * @param s An array of strings after splitting an encoded ship
     *
     * @return parsed imo if String can be converted to imo. -1 otherwise.
     * @ass2
     */
    private static long checkImo(String[] s) {
        try {
            return Long.parseLong(s[1]);

        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return -1;
        }
    }

    /**
     * Check for a NauticalFlag.
     *
     * @param s An array of strings after splitting an encoded ship
     *
     * @return parsed NauticalFlag if String can be converted to imo.
     * null otherwise.
     * @ass2
     */
    private static NauticalFlag checkNautical(String [] s) {
        try {
            return NauticalFlag.valueOf(s[4]);

        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Convert a string to int
     *
     * @param i A string to be converted to an int
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
