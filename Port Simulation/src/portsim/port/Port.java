package portsim.port;

import portsim.cargo.Cargo;
import portsim.evaluators.*;
import portsim.movement.CargoMovement;
import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;
import portsim.ship.Ship;
import portsim.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * A place where ships can come and dock with Quays to load / unload their
 * cargo.
 * <p>
 * Ships can enter a port through its queue. Cargo is stored within the port at warehouses.
 *
 * @ass1_partial
 */
public class Port implements Tickable, Encodable {

    /**
     * The name of this port used for identification
     */
    private String name;
    /**
     * The quays associated with this port
     */
    private List<Quay> quays;
    /**
     * The cargo currently stored at the port at warehouses.
     * Cargo unloaded from trucks / ships
     */
    private List<Cargo> storedCargo;
    /**
     * The time since simulation started.
     */
    private long time;
    /**
     *  The list of evaluators at the port.
     */
    private List<StatisticsEvaluator> evaluators;
    /**
     *  The queue of ships waiting to be docked at this port.
     */
    private ShipQueue queue;
    /**
     *  The queue of movements waiting to be processed.
     */
    private PriorityQueue<Movement> movements;

    /**
     * Creates a new port with the given name.
     * <p>
     * The time since the simulation was started should be initialised as 0.
     * <p>
     * The list of quays in the port, stored cargo (warehouses) and statistics evaluators should be
     * initialised as empty lists.
     * <p>
     * An empty ShipQueue should be initialised, and a PriorityQueue should be initialised
     * to store movements ordered by the time of the movement (see {@link Movement#getTime()}).
     *
     * @param name name of the port
     * @ass1_partial
     */
    public Port(String name) {
        this(name, 0, new ShipQueue(), new ArrayList<Quay>(),
                new ArrayList<Cargo>());

    }

    /**
     * Creates a new port with the given name, time elapsed, ship queue, quays and stored cargo.
     *
     * @param name - name of the port
     * @param time - number of minutes since simulation started
     * @param shipQueue - ships waiting to enter the port
     * @param quays - the port's quays
     * @param storedCargo - the cargo stored at the port
     *
     * @throws IllegalArgumentException if time < 0
     *
     * @ass2
     */
    public Port(String name,
                 long time,
                 ShipQueue shipQueue,
                 List<Quay> quays,
                 List<Cargo> storedCargo)
            throws IllegalArgumentException {
        if (time < 0) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.queue = shipQueue;
        this.quays = quays;
        this.storedCargo = storedCargo;
        this.evaluators = new ArrayList<StatisticsEvaluator>();
        this.time = time;
        this.movements = new PriorityQueue<Movement>(new Comparator<Movement>() {
            public int compare(Movement m1, Movement m2) {
                return (int) (m1.getTime() - m2.getTime());
            }
        });
    }

    /**
     * Adds a movement to the PriorityQueue of movements.
     *
     * @param movement - movement to add
     *
     * @throws IllegalArgumentException if given movement's action time is
     * less than the current number of minutes elapsed
     *
     */
    public void addMovement(Movement movement)
            throws IllegalArgumentException {
        if (movement.getTime() < this.time) {
            throw new IllegalArgumentException();
        }

        this.movements.add(movement);
    }

    /**
     * Processes a movement.
     *
     * @param movement - movement to execute
     *
     */
    public void processMovement(Movement movement) {
        if (movement instanceof ShipMovement) {
            Ship ship = ((ShipMovement) movement).getShip();
            if (movement.getDirection().equals(MovementDirection.INBOUND)) {
                queue.add(ship);
            } else { // outbound
                for (Cargo cargo : getCargo()) {
                    if (ship.canLoad(cargo)) {
                        ship.loadCargo(cargo);
                    }
                    for (Quay quay : getQuays()) {
                        if (quay.getShip() == ship) {
                            quay.shipDeparts();
                        }
                    }
                }
            }
        } else { // Move is instance of CargoMovement
            List<Cargo> cargos = ((CargoMovement) movement).getCargo();
            if (movement.getDirection().equals(MovementDirection.INBOUND)) {
                this.storedCargo.addAll(cargos);
            } else { // OutBound
                for (Cargo cargo : cargos) {
                    storedCargo.removeIf(n -> n.getId() == cargo.getId());
                }
            }
        }

        for (StatisticsEvaluator evaluator : evaluators) {
            evaluator.onProcessMovement(movement);
        }
    }

    /**
     * Adds the given statistics evaluator to the port's list of evaluators.
     *
     * @param eval - statistics evaluator to add to the port
     *
     */
    public void addStatisticsEvaluator(StatisticsEvaluator eval) {
        String type = eval.getClass().getSimpleName();
        for (StatisticsEvaluator evaluator : evaluators) {
            if (evaluator.getClass().getSimpleName().equals(type)) {
                return;
            }
        }

        this.evaluators.add(eval);
    }

    /**
     * Returns the time since simulation started
     *
     * @return time in minutes
     *
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the queue of ships waiting to be docked at this port.
     *
     * @return port's queue of ships.
     *
     */
    public ShipQueue getShipQueue() {
        return this.queue;
    }

    /**
     * Returns the queue of movements waiting to be processed.
     *
     * @return movements queue.
     *
     */
    public PriorityQueue<Movement> getMovements() {
        return movements;
    }

    /**
     * Returns the list of evaluators at the port.
     *
     * @return the ports evaluators.
     *
     */
    public List<StatisticsEvaluator> getEvaluators() {
        return evaluators;
    }


    /**
     * Returns the name of this port.
     *
     * @return port's name
     * @ass1
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a list of all quays associated with this port.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     * <p>
     * The order in which quays appear in this list should be the same as
     * the order in which they were added by calling {@link #addQuay(Quay)}.
     *
     * @return all quays
     * @ass1
     */
    public List<Quay> getQuays() {
        return new ArrayList<>(this.quays);
    }

    /**
     * Returns the cargo stored in warehouses at this port.
     * <p>
     * Adding or removing elements from the returned list should not affect the original list.
     *
     * @return port cargo
     * @ass1
     */
    public List<Cargo> getCargo() {
        return new ArrayList<>(this.storedCargo);
    }

    /**
     * Adds a quay to the ports control.
     *
     * @param quay the quay to add
     * @ass1
     */
    public void addQuay(Quay quay) {
        this.quays.add(quay);
    }

    @Override
    public String encode() {
        String sep = System.getProperty("line.separator");
        StringBuilder result = new StringBuilder();
        final Map<Integer, Cargo> cargoRegistry = Cargo.getCargoRegistry();
        final Map<Long, Ship> shipRegistry = Ship.getShipRegistry();
        final List<Ship> shipQueue = queue.getShipQueue();

        result.append(name);
        result.append(sep);
        result.append(time);
        result.append(sep);
        result.append(cargoRegistry.size());
        result.append(sep);

        for (Map.Entry<Integer, Cargo> entry : cargoRegistry.entrySet()) {
            result.append(entry.getValue().encode());
            result.append(sep);
        }

        result.append(shipRegistry.size());
        result.append(sep);

        for (Map.Entry<Long, Ship> entry : shipRegistry.entrySet()) {
            result.append(entry.getValue().encode());
            result.append(sep);
        }

        result.append(quays.size());
        result.append(sep);

        for (Quay quay : quays) {
            result.append(quay.encode());
            result.append(sep);
        }

        result.append("ShipQueue:").append(shipQueue.size()).append(":");
        for (int i = 0; i < shipQueue.size(); i++) {
            result.append(shipQueue.get(i).getImoNumber());

            if (i < shipQueue.size() - 1) {
                result.append(",");
            }

        }
        result.append(sep);

        result.append("StoredCargo:").append(storedCargo.size()).append(":");
        for (int i = 0; i < storedCargo.size(); i++) {
            result.append(storedCargo.get(i).getId());

            if (i < storedCargo.size() - 1) {
                result.append(",");
            }
        }
        result.append(sep);

        result.append("Movements:").append(movements.size());
        result.append(sep);

        for (Movement movement : movements) {
            result.append(movement.encode());
            result.append(sep);
        }

        result.append("Evaluators:").append(evaluators.size()).append(":");

        for (int i = 0; i < evaluators.size(); i++) {
            result.append(evaluators.get(i).getClass().getSimpleName());

            if (i < evaluators.size() - 1) {
                result.append(",");
            }
        }


        return result.toString();
    }

    /**
     * Creates a port instance by reading various ship, quay, cargo, movement a
     * nd evaluator entities from the given reader.
     *
     * @param reader - reader from which to load all info
     *
     * @return port created by reading from given reader
     *
     * @throws IOException - if an IOException
     *                      is encountered when reading from the reader
     * @ass2
     */
    public static Port initialisePort(Reader reader)
            throws IOException, BadEncodingException {
        BufferedReader bufferedReader = new BufferedReader(reader);

        final String name = bufferedReader.readLine();
        final long time = checkLong(bufferedReader.readLine());

        int numCargo = stringToInt(bufferedReader.readLine());
        for (int i = 0; i < numCargo; i++) {
            String encodedCargo = bufferedReader.readLine();
            Cargo.fromString(encodedCargo);
        }
        if (Cargo.getCargoRegistry().size() != numCargo) {
            throw new BadEncodingException();
        }

        int numShips = stringToInt(bufferedReader.readLine());
        for (int i = 0; i < numShips; i++) {
            String encodedShip = bufferedReader.readLine();
            Ship.fromString(encodedShip);
        }
        if (Ship.getShipRegistry().size() != numShips) {
            throw new BadEncodingException();
        }

        int numQuays = stringToInt(bufferedReader.readLine());
        List<Quay> quays = new ArrayList<>();
        for (int i = 0; i < numQuays; i++) {
            String encodedQuay = bufferedReader.readLine();
            quays.add(Quay.fromString(encodedQuay));
        }
        if (quays.size() != numQuays) {
            throw new BadEncodingException();
        }

        String shipQueue = bufferedReader.readLine();
        String[] queueInfo = shipQueue.split(":", -1);
        ShipQueue queue = new ShipQueue();
        processQueue(queueInfo, queue);

        String storedCargo = bufferedReader.readLine();
        String[] storageInfo = storedCargo.split(":", -1);
        List<Cargo> cargos = new ArrayList<>();
        processCargo(storageInfo, cargos);

        String movements = bufferedReader.readLine();
        String[] movementInfo = movements.split(":");
        if (movementInfo.length != 2 || !movementInfo[0].equals("Movements")) {
            throw new BadEncodingException();
        }

        int numMovements = stringToInt(movementInfo[1]);
        List<Movement> storedMovements = new ArrayList<>();
        for (int i = 0; i < numMovements; i++) {
            String encodedMovement = bufferedReader.readLine();
            String [] encodedMovements = encodedMovement.split(":");
            if (encodedMovements[0].equals("ShipMovement")) {
                storedMovements.add(ShipMovement.fromString(encodedMovement));
            } else if (encodedMovements[0].equals("CargoMovement")) {
                storedMovements.add(CargoMovement.fromString(encodedMovement));
            } else {
                throw new BadEncodingException();
            }
        }

        if (storedMovements.size() != numMovements) {
            throw new BadEncodingException();
        }

        String evaluators = bufferedReader.readLine();
        String [] evaluatorInfo = evaluators.split(":", -1);
        if (evaluatorInfo.length != 3 || !evaluatorInfo[0].equals("Evaluators")) {
            throw new BadEncodingException();
        }
        int numEvaluators = stringToInt(evaluatorInfo[1]);

        String [] encodedEvaluators;
        if (evaluatorInfo[2].equals("")) {
            encodedEvaluators = ",".split(",");
        } else {
            encodedEvaluators = evaluatorInfo[2].split(",");
        }

        if (encodedEvaluators.length != numEvaluators) {
            throw new BadEncodingException();
        }

        Port result = new Port(name, time, queue, quays, cargos);
        for (Movement movement : storedMovements) {
            result.addMovement(movement);
        }

        List<String> evaluatorClasses
                = new ArrayList<>(Arrays.asList("CargoDecompositionEvaluator",
                "QuayOccupancyEvaluator",
                "ShipFlagEvaluator",
                "ShipThroughputEvaluator"));
        for (String encodedEvaluator : encodedEvaluators) {
            if (encodedEvaluator.equals(evaluatorClasses.get(0))) {
                result.addStatisticsEvaluator(new CargoDecompositionEvaluator());
            } else if (encodedEvaluator.equals(evaluatorClasses.get(1))) {
                result.addStatisticsEvaluator(new QuayOccupancyEvaluator(result));
            } else if (encodedEvaluator.equals(evaluatorClasses.get(2))) {
                result.addStatisticsEvaluator(new ShipFlagEvaluator());
            } else if (encodedEvaluator.equals(evaluatorClasses.get(3))) {
                result.addStatisticsEvaluator(new ShipThroughputEvaluator());
            } else {
                throw new BadEncodingException();
            }
        }

        return result;
    }

    private static void processCargo(String[] storageInfo, List<Cargo> cargos)
            throws BadEncodingException {
        if (!storageInfo[0].equals("StoredCargo") || storageInfo.length != 3) {
            throw new BadEncodingException();
        }

        int numCargoStored = stringToInt(storageInfo[1]);

        String[] cargoIds;
        if (storageInfo[2].equals("")) {
            cargoIds = ",".split(",");
        } else {
            cargoIds = storageInfo[2].split(",");
        }

        if (cargoIds.length != numCargoStored) {
            throw new BadEncodingException();
        }

        for (String cargoId : cargoIds) {
            int decodedCargoId = stringToInt(cargoId);
            try {
                cargos.add(Cargo.getCargoById(decodedCargoId));
            } catch (NoSuchCargoException e) {
                throw new BadEncodingException();
            }
        }
    }

    private static void processQueue(String[] queueInfo, ShipQueue queue)
            throws BadEncodingException {
        if (!queueInfo[0].equals("ShipQueue") || queueInfo.length != 3) {
            throw new BadEncodingException();
        }

        int shipsInQueue = stringToInt(queueInfo[1]);
        String[] imoInQueue;
        if (queueInfo[2].equals("")) {
            imoInQueue = ",".split(",");
        } else {
            imoInQueue = queueInfo[2].split(",");
        }

        if (imoInQueue.length != shipsInQueue) {
            throw new BadEncodingException();
        }

        for (String shipImo : imoInQueue) {
            long decodedImo = checkLong(shipImo);
            try {
                queue.add(Ship.getShipByImoNumber(decodedImo));
            } catch (NoSuchShipException e) {
                throw new BadEncodingException();
            }
        }
    }

    private static long checkLong(String s) throws BadEncodingException {
        try {
            return Long.parseLong(s);

        } catch (NumberFormatException e) {
            throw new BadEncodingException();
        }
    }

    private static int stringToInt(String i) throws BadEncodingException {
        try {
            return Integer.parseInt(i);
        } catch (NumberFormatException e) {
            throw new BadEncodingException();
        }
    }

    @Override
    public void elapseOneMinute() {
        time++;

        if (time % 10 == 0) {
            Ship ship = queue.poll();
            for (Quay quay : getQuays()) {
                if (ship.canDock(quay) && quay.isEmpty()) {
                    quay.shipArrives(ship);
                    break;
                }
            }
        }

        if (time % 5 == 0) {
            for (Quay quay : quays) {
                if (!quay.isEmpty()) {
                    Ship ship = quay.getShip();

                    if (ship instanceof BulkCarrier) {
                        BulkCarrier bulkCarrier = (BulkCarrier) ship;
                        try {
                            storedCargo.add(bulkCarrier.unloadCargo());
                        } catch (NoSuchCargoException e) {
                            // Do Nothing
                            ;
                        }
                    } else { // ship is a ContainerSHip
                        ContainerShip containerShip = (ContainerShip) ship;
                        try {
                            storedCargo.addAll(containerShip.unloadCargo());
                        } catch (NoSuchCargoException e) {
                            //Do Nothing
                            ;
                        }
                    }

                }
            }
        }

        PriorityQueue<Movement> movementsCopy
                = new PriorityQueue<>(movements);

        while (!movementsCopy.isEmpty()) {
            Movement movement = movementsCopy.poll();
            if (movement != null && movement.getTime() == this.time) {
                processMovement(movement);
            }
        }

        for (StatisticsEvaluator evaluator : evaluators) {
            evaluator.elapseOneMinute();
        }

    }
}
