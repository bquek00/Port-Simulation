package portsim.port;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.ship.Ship;
import portsim.util.BadEncodingException;

import java.util.List;
import java.util.SortedMap;

import static org.junit.Assert.*;

public class ShipQueueTest {
    ShipQueue queueTest;

    @Before
    public void setUp() throws Exception {
        queueTest = new ShipQueue();
    }

    @Test
    public void testEmpty() {
        List<Ship> queue = queueTest.getShipQueue();
        assertEquals(0, queue.size());
        assertNull(queueTest.poll());
        assertNull(queueTest.peek());
        queue.add(new ContainerShip(1000008, "dummy", "Singapore", NauticalFlag.BRAVO, 100));
        assertEquals(0, queueTest.getShipQueue().size());
        ShipQueue clone1 = new ShipQueue();
        assertEquals(queueTest.hashCode(), clone1.hashCode());
        assertTrue(queueTest.equals(clone1));
        assertTrue(clone1.equals(queueTest));
        assertTrue("ShipQueue:0:".equals(queueTest.encode()));
    }

    @Test
    public void testComplex() throws BadEncodingException {
        ContainerShip ship1 = new ContainerShip(1000009, "first", "Singapore", NauticalFlag.BRAVO, 100);
        BulkCarrier ship2 = new BulkCarrier(1000010, "second", "Singapore", NauticalFlag.BRAVO, 10);
        BulkCarrier ship3 = new BulkCarrier(1000011, "third", "Singapore", NauticalFlag.BRAVO, 10);

        ContainerShip ship4 = new ContainerShip(1000012, "fourth", "Singapore", NauticalFlag.WHISKEY, 100);
        BulkCarrier ship5 = new BulkCarrier(1000013, "fifth", "Singapore", NauticalFlag.WHISKEY, 10);
        BulkCarrier ship6 = new BulkCarrier(1000014, "sixth", "Singapore", NauticalFlag.WHISKEY, 10);

        ContainerShip ship7 = new ContainerShip(1000015, "seventh", "Singapore", NauticalFlag.HOTEL, 100);
        BulkCarrier ship8 = new BulkCarrier(1000016, "eight", "Singapore", NauticalFlag.HOTEL, 10);
        BulkCarrier ship9 = new BulkCarrier(1000017, "nine", "Singapore", NauticalFlag.HOTEL, 10);

        ContainerShip ship10 = new ContainerShip(1000018, "ten", "Singapore", NauticalFlag.NOVEMBER, 100);
        BulkCarrier ship11 = new BulkCarrier(1000019, "eleven", "Singapore", NauticalFlag.NOVEMBER, 10);
        BulkCarrier ship12 = new BulkCarrier(1000020, "twelve", "Singapore", NauticalFlag.NOVEMBER, 10);

        queueTest.add(ship11);
        queueTest.add(ship12);
        queueTest.add(ship10);
        queueTest.add(ship7);
        queueTest.add(ship8);
        queueTest.add(ship9);
        queueTest.add(ship4);
        queueTest.add(ship5);
        queueTest.add(ship6);
        queueTest.add(ship1);
        queueTest.add(ship2);
        queueTest.add(ship3);

        Ship [] ships = {ship1, ship2,ship3,ship4,ship5,ship6,ship7,ship8,ship9,ship10,ship11,ship12};


        List<Ship> tempQueue = queueTest.getShipQueue();
        String s = "";
        for (int i = 0; i < tempQueue.size(); i++) {
            s += tempQueue.get(i).getImoNumber();
            if (i < tempQueue.size() - 1) {
                s += ",";
            }
        }

        //FromString test
        ShipQueue fromString;
        fromString = ShipQueue.fromString("ShipQueue:12:"+ s);
        assertTrue(fromString.equals(queueTest));
        assertTrue(queueTest.equals(fromString));
        assertEquals(fromString.hashCode(), queueTest.hashCode());
        //Encode
        assertEquals("ShipQueue:12:" + s, queueTest.encode());

        //PeekPoll test
        for (int i = 0; i < ships.length; i++) {
            assertEquals(ships[i], queueTest.peek());
            assertEquals(ships[i], queueTest.poll());
        }

        //bad equals
        ShipQueue badEqual = ShipQueue.fromString("ShipQueue:12:1000020,1000019,1000018,1000017,1000016,1000015,1000014,1000013,1000012,1000011,1000010,1000009");
        assertFalse(queueTest.equals(badEqual));
        assertFalse(badEqual.equals(queueTest));
        assertFalse(badEqual.equals(null));

        ShipQueue.fromString("ShipQueue:0:");

    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest1() throws BadEncodingException {
        new ContainerShip(2000008, "dummy", "Singapore", NauticalFlag.BRAVO, 100);
        new ContainerShip(2000009, "dummy1", "Singapore", NauticalFlag.BRAVO, 100);
        ShipQueue.fromString("ShipQueue:0");
    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest2() throws BadEncodingException {
        ShipQueue.fromString("ShipQueue:21000020,2000009");
    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest3() throws BadEncodingException {
        ShipQueue.fromString("ShipQueue2:2000008,2000009");
    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest4() throws BadEncodingException {
        ShipQueue.fromString("ShipQueue:2:2000008,2000009:");
    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest5() throws BadEncodingException {
        ShipQueue.fromString("ShipQueue:2:2000008:2000009");
    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest6() throws BadEncodingException {
        ShipQueue.fromString("ShipQu3ue:2:2000008,2000009");
    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest7() throws BadEncodingException {
        ShipQueue.fromString("ShipQueue:2a:2000008,2000009");
    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest8() throws BadEncodingException {
        ShipQueue.fromString("ShipQueue:3:2000008,2000009");
    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest9() throws BadEncodingException {
        ShipQueue.fromString("ShipQueue:3:2000008ab,2000009");
    }

    @Test (expected = BadEncodingException.class)
    public void badFromStringTest10() throws BadEncodingException {
        ShipQueue.fromString("ShipQueue:3:2000010,2000009");
    }

    @After
    public void tearDown() throws Exception {
    }

}