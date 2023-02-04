package ownTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import portsim.cargo.*;
import portsim.evaluators.*;
import portsim.movement.CargoMovement;
import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.port.*;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;
import portsim.util.BadEncodingException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;


public class PortTest {

    private Port port1;
    private Port port2;

    private List<Quay> quays1;
    private List<Quay> quays2;

    private Quay quay1;
    private Quay quay2;
    private Quay quay3;

    private ShipQueue sq1;
    private ShipQueue sq2;

    private List<Cargo> sCargo1;
    private List<Cargo> sCargo2;

    private Ship ship1;
    private Ship ship2;
    private Ship ship3;
    private Ship ship4;

    private Cargo cargo1;
    private Cargo cargo2;
    private Cargo cargo3;
    private Cargo cargo4;
    private Cargo cargo5;

    private Movement movement1;
    private Movement movement2;
    private Movement movement3;
    private Movement movement4;

    private CargoDecompositionEvaluator cargoEval;
    private ShipFlagEvaluator shipFlagEval;

    private Map<Integer, Cargo> cargoMap;
    private Map<Long, Ship> shipMap;

    @Before
    public void setUp() throws Exception {
        shipMap = new HashMap<>();
        cargoMap = new HashMap<>();
        Ship.resetShipRegistry();
        ship1 = new BulkCarrier(3456789, "Glorious", "Switzerland",
                NauticalFlag.HOTEL, 120);
        ship2 = new BulkCarrier(4567890, "Panda", "Japan",
                NauticalFlag.NOVEMBER, 10);
        ship3 = new ContainerShip(1234567, "Polly", "USA",
                NauticalFlag.WHISKEY, 4);
        ship4 = new ContainerShip(9876543, "Samantha", "Australia",
                NauticalFlag.BRAVO, 3);
        shipMap.put(ship1.getImoNumber(), ship1);
        shipMap.put(ship2.getImoNumber(), ship2);
        shipMap.put(ship3.getImoNumber(), ship3);
        shipMap.put(ship4.getImoNumber(), ship4);

        Cargo.resetCargoRegistry();
        cargo1 = new Container(1, "Australia", ContainerType.OPEN_TOP);
        cargo2 = new Container(2, "New Zealand", ContainerType.OTHER);
        cargo3 = new BulkCargo(3, "Japan", 150, BulkCargoType.OIL);
        cargo4 = new BulkCargo(4, "Finland", 200, BulkCargoType.GRAIN);
        cargo5 = new BulkCargo(5, "Switzerland", 10, BulkCargoType.OTHER);
        cargoMap.put(cargo1.getId(), cargo1);
        cargoMap.put(cargo2.getId(), cargo2);
        cargoMap.put(cargo3.getId(), cargo3);
        cargoMap.put(cargo4.getId(), cargo4);
        cargoMap.put(cargo5.getId(), cargo5);

        ship1.loadCargo(cargo5);

        quay1 = new BulkQuay(1, 200);
        quay2 = new BulkQuay(2, 250);
        quay3 = new ContainerQuay(3, 100);

        quays1 = new ArrayList<>();
        quays2 = new ArrayList<>();

        quays1.add(quay1);

        quays2.add(quay1);
        quays2.add(quay3);

        sq1 = new ShipQueue();
        sq2 = new ShipQueue();

        sq1.add(ship1);
        sq2.add(ship1);
        sq2.add(ship2);
        sq2.add(ship3);

        sCargo1 = new ArrayList<>();
        sCargo2 = new ArrayList<>();
        sCargo1.add(cargo1);
        sCargo2.add(cargo1);
        sCargo2.add(cargo2);
        sCargo2.add(cargo3);
        List<Cargo> sCargo3 = new ArrayList<>();
        sCargo3.add(cargo2);
        sCargo3.add(cargo3);

        port1 = new Port("Danie", 9, sq1, quays1, new ArrayList<>(sCargo1));
        port2 = new Port("Ahmad", 29, sq2, quays2, new ArrayList<>(sCargo2));

        movement1 = new CargoMovement(10, MovementDirection.INBOUND,
                new ArrayList<>(sCargo3));
        movement2 = new CargoMovement(33, MovementDirection.OUTBOUND,
                new ArrayList<>(sCargo3));
        movement3 = new ShipMovement(10, MovementDirection.INBOUND, ship3);
        movement4 = new ShipMovement(34, MovementDirection.OUTBOUND, ship3);

        cargoEval = new CargoDecompositionEvaluator();
        shipFlagEval = new ShipFlagEvaluator();

    }

    @After
    public void tearDown() throws Exception {
        Cargo.resetCargoRegistry();
        Ship.resetShipRegistry();
    }

    @Rule
    public Timeout timeout = Timeout.seconds(1);

//
    @Test
    public void constructorIllegalArgumentException_Test() {
        try {
            new Port("", -1, sq1, quays1, sCargo1);
            fail("Constructor wth a negative time value should throw an " +
                    "IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

//
//
//
//
//
//
//
//
//
//
//
    @Test
    public void addMovement_IAE_Time() {
        try {
            port2.addMovement(movement1);
            fail("Movements in the past should throw an " +
                    "IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
    @Test
    public void initialisePort_Basic() {
        Cargo.resetCargoRegistry();
        Ship.resetShipRegistry();
        String encoding = "Port of Brisbane" + System.lineSeparator()
                + "0" + System.lineSeparator()
                + "0" + System.lineSeparator()
                + "0" + System.lineSeparator()
                + "0" + System.lineSeparator()
                + "ShipQueue:0:" + System.lineSeparator()
                + "StoredCargo:0:" + System.lineSeparator()
                + "Movements:0" + System.lineSeparator()
                + "Evaluators:0:";
        Port newPort = null;
        try {
            newPort = Port.initialisePort(new StringReader(encoding));
        } catch (IOException ignored) {
            // not possible
        } catch (BadEncodingException e) {
            fail("This should be a valid encoding");
        }
        List<?> emptyList = new ArrayList<>();
        assertEquals("The ports name was not read correctly", "Port of Brisbane",
                newPort.getName());
        assertEquals("The ports time was not read correctly", 0, newPort.getTime());
        assertEquals("The ports evaluators were not read correctly", emptyList,
                newPort.getEvaluators());
        assertEquals("The ports quays were not read correctly", emptyList, newPort.getQuays());
        assertEquals("The ports stored cargo were not read correctly", emptyList,
                newPort.getCargo());
        assertEquals("The ports movements were not read correctly", 0,
                newPort.getMovements().size());
        assertEquals("The ports shipQueue was not read correctly", new ShipQueue().getShipQueue(),
                newPort.getShipQueue().getShipQueue());
        assertEquals("The simulations cargo registry was not read correctly", new HashMap<>(),
                Cargo.getCargoRegistry());
        assertEquals("The simulations ship registry was not read correctly", new HashMap<>(),
                Ship.getShipRegistry());

    }
}