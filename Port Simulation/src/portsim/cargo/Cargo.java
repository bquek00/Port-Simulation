package portsim.cargo;

import portsim.util.BadEncodingException;
import portsim.util.Encodable;
import portsim.util.NoSuchCargoException;

import java.util.HashMap;
import java.util.Map;

/**
 * Denotes a cargo whose function is to be transported via a Ship or land
 * transport.
 * <p>
 * Cargo is kept track of via its ID.
 *
 * @ass1_partial
 */
public abstract class Cargo implements Encodable {
    /**
     * The ID of the cargo instance
     */
    private int id;

    /**
     * Destination for this cargo
     */
    private String destination;

    /**
     * Database of all cargo currently active in the simulation
     */
    private static Map<Integer, Cargo> cargoRegistry = new HashMap<>();

    /**
     * Creates a new Cargo with the given ID and destination port.
     * <p>
     * When a new piece of cargo is created, it should be added to the cargo registry.
     * @param id          cargo ID
     * @param destination destination port
     * @throws IllegalArgumentException if a cargo already exists with the
     *                                  given ID or ID &lt; 0
     * @ass1_partial
     */
    public Cargo(int id, String destination) throws IllegalArgumentException {
        if (id < 0) {
            throw new IllegalArgumentException("Cargo ID must be greater than"
                + " or equal to 0: " + id);
        }

        if (cargoExists(id)) {
            throw new IllegalArgumentException();
        }


        this.id = id;
        this.destination = destination;
        cargoRegistry.put(id, this);
    }

    /**
     * Retrieve the ID of this piece of cargo.
     *
     * @return the cargo's ID
     * @ass1
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieve the destination of this piece of cargo.
     *
     * @return the cargo's destination
     * @ass1
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Retrieve the global registry of all pieces of cargo, as a mapping
     * cargo IDs to Cargo instances.
     *
     * @return the cargo registry
     * @ass2
     */
    public static Map<Integer, Cargo> getCargoRegistry() {
        Map<Integer, Cargo> result = new HashMap<>();
        for (Map.Entry<Integer, Cargo> entry : cargoRegistry.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Checks if a cargo exists in the simulation using its ID.
     *
     * @param id  unique key to identify cargo
     *
     * @return true if there is a cargo stored in the registry
     * with key id; false otherwise
     * @ass2
     */
    public static boolean cargoExists(int id) {
        for (Map.Entry<Integer, Cargo> entry : cargoRegistry.entrySet()) {
            if (entry.getKey().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the cargo specified by the given ID.
     *
     * @param id  unique key to identify cargo
     *
     * @return cargo specified by the id
     * @throws NoSuchCargoException if the cargo does not exist in the registry
     * @ass2
     */
    public static Cargo getCargoById(int id) throws NoSuchCargoException {
        for (Map.Entry<Integer, Cargo> entry : cargoRegistry.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue();
            }
        }
        throw new NoSuchCargoException();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cargo)) {
            return false;
        }

        if (this.id == ((Cargo) o).getId()
                && this.destination == ((Cargo) o).getDestination()) {
            return true;
        }
        return false;
    }

    /**
     * Returns the hash code of this cargo.
     *
     * @return hash code of this cargo
     * @ass2
     */
    public int hashCode() {
        return id + destination.hashCode();
    }

    /**
     * Returns the human-readable string representation of this cargo.
     * <p>
     * The format of the string to return is
     * <pre>CargoClass id to destination</pre>
     * Where:
     * <ul>
     *   <li>{@code CargoClass} is the cargo class name</li>
     *   <li>{@code id} is the id of this cargo </li>
     *   <li>{@code destination} is the destination of the cargo </li>
     * </ul>
     * <p>
     * For example: <pre>Container 55 to New Zealand</pre>
     *
     * @return string representation of this Cargo
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %d to %s",
            this.getClass().getSimpleName(),
            this.id,
            this.destination);
    }

    /**
     * Reads a piece of cargo from its encoded representation in
     * the given string.
     *
     * @return encoded string representation of this Cargo.
     * @ass2
     */
    public String encode() {
        return "" + this.getClass().getSimpleName() + ":" + id + ":"
                + destination;
    }

    /**
     * Returns the machine-readable string representation of
     * this Cargo.
     *
     * @param string - string containing the encoded cargo
     *
     * @return decoded cargo instance
     * @ass2
     */
    public static Cargo fromString(String string)
            throws BadEncodingException {
        String[] result = string.split(":", -1);
        Integer id;
        if (result.length >= 3) {
            id = stringToInt(result[1]);
        } else {
            throw new BadEncodingException();
        }

        if (result[0].equals("Container")) {
            if (result.length != 4
                    || stringToContainerType(result[3]) == null
                    || id < 0
                    || cargoExists(id)) {
                throw new BadEncodingException();
            }

            return new Container(id, result[2],
                    ContainerType.valueOf(result[3]));

        } else if (result[0].equals("BulkCargo")) {
            if (result.length != 5
                    || stringToBulkCargoType(result[3]) == null
                    || id < 0
                    || cargoExists(id)
                    || stringToInt(result[4]) < 0) {
                throw new BadEncodingException();
            }

            return new BulkCargo(id, result[2],
                    stringToInt(result[4]), BulkCargoType.valueOf(result[3]));
        }

        throw new BadEncodingException();

    }

    /**
     * Take a string and tries to parse it as an int.
     *
     * @param i a String to be parsed to an int
     *
     * @return the parsed integer if parsing was successful, -1 otherwise.
     * @ass2
     */
    private static int stringToInt(String i) {
        try {
            return Integer.parseInt(i);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Take a string and tries to parse it as a  bulkCargoType.
     *
     * @param type a String to be parsed to a bulkCargoType
     *
     * @return the parsed bulkCargoType if parsing was successful,
     * null otherwise.
     *
     * @ass2
     */
    private static BulkCargoType stringToBulkCargoType(String type) {
        try {
            return BulkCargoType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Take a string and tries to parse it as a container type.
     *
     * @param type a String to be parsed to a container type
     *
     * @return the parsed containerType if parsing was successful,
     * null otherwise.
     *
     * @ass2
     */
    private static ContainerType stringToContainerType(String type) {
        try {
            return ContainerType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Resets the global cargo registry.
     * This utility method is for the testing suite.
     *
     * @given
     */
    public static void resetCargoRegistry() {
        Cargo.cargoRegistry = new HashMap<>();
    }

}
