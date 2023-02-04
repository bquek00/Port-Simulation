package portsim.evaluators;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import portsim.cargo.BulkCargo;
import portsim.cargo.BulkCargoType;
import portsim.cargo.Cargo;
import portsim.movement.CargoMovement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ShipThroughputEvaluatorTest {
    ShipThroughputEvaluator test;

    @Before
    public void setUp() throws Exception {
        test = new ShipThroughputEvaluator();
    }

    @Test
    public void initialThroughputTest() {
        assertEquals(0, test.getThroughputPerHour());
        assertEquals(0, test.getTime());
    }

    @Test
    public void processMovementTest() {
        CargoMovement badMovement1 =
                new CargoMovement(120, MovementDirection.INBOUND, new ArrayList<Cargo>());
        CargoMovement badMovement2 =
                new CargoMovement(124, MovementDirection.OUTBOUND, new ArrayList<Cargo>());
        test.onProcessMovement(badMovement1);
        ShipMovement badMovement3 =
                new ShipMovement(125, MovementDirection.INBOUND, new ContainerShip(1000008, "hi", "Singapore", NauticalFlag.BRAVO, 30));
        test.onProcessMovement(badMovement1);
        test.onProcessMovement(badMovement1);
        test.onProcessMovement(badMovement2);
        test.onProcessMovement(badMovement3);

        assertEquals(0, test.getThroughputPerHour());

        ShipMovement goodMovement1 =
                new ShipMovement(126, MovementDirection.OUTBOUND, new ContainerShip(1000009, "hi", "Singapore", NauticalFlag.BRAVO, 30));
        test.onProcessMovement(goodMovement1);
        assertEquals(1, test.getThroughputPerHour());

        test.elapseOneMinute();

        ShipMovement goodMovement2 =
                new ShipMovement(126, MovementDirection.OUTBOUND, new ContainerShip(1000001, "hi", "Singapore", NauticalFlag.BRAVO, 30));
        test.onProcessMovement(goodMovement2);
        assertEquals(2, test.getThroughputPerHour());

        for (int i = 0; i < 60; i ++) {
            test.elapseOneMinute();
        }

        assertEquals(61, test.getTime());
        assertEquals(1, test.getThroughputPerHour());
        test.elapseOneMinute();
        assertEquals(0, test.getThroughputPerHour());
    }

    @After
    public void tearDown() throws Exception {
    }
}