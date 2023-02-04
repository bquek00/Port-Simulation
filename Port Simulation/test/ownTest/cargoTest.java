package ownTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import portsim.cargo.*;
import portsim.movement.CargoMovement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.port.ContainerQuay;
import portsim.ship.ContainerShip;
import portsim.ship.NauticalFlag;
import portsim.util.BadEncodingException;
import portsim.util.NoSuchCargoException;

import java.util.ArrayList;
import java.util.List;

public class cargoTest {
    Container mainTest;

    @Before
    public void setUp() {
    }

    @Test (expected = IllegalArgumentException.class)
    public void constructorTest() {
        mainTest = new Container(4, "Australia", ContainerType.OPEN_TOP);
        Container test1 = new Container(4, "Singapore", ContainerType.OTHER);
    }

    @Test
    public void test1() throws NoSuchCargoException, BadEncodingException {
        /*mainTest = new Container(3, "Australia", ContainerType.OPEN_TOP);
        Assert.assertEquals(2, Cargo.getCargoRegistry().size());
        Assert.assertEquals(mainTest, Cargo.getCargoRegistry().get(3));
        Cargo.getCargoRegistry().remove(1);
        Assert.assertEquals(2, Cargo.getCargoRegistry().size());

        Assert.assertTrue(Cargo.cargoExists(3));
        Assert.assertFalse(Cargo.cargoExists(55));

        Assert.assertEquals(mainTest, Cargo.getCargoById(3));
        Assert.assertTrue(mainTest.equals(mainTest));

        Container toStringTest = new Container(42, "Brazil", ContainerType.OTHER);
        Assert.assertEquals("Container 42 to Brazil [OTHER]", toStringTest.toString());
        Assert.assertEquals("Container:3:Australia:OPEN_TOP", mainTest.encode());

        BulkCargo bulkTest = new BulkCargo(21, "Germany", 50 ,BulkCargoType.GRAIN);
        Assert.assertTrue(bulkTest.equals(bulkTest));
        Assert.assertEquals("BulkCargo:21:Germany:GRAIN:50", bulkTest.encode());


        String s = "Container:80:Australia:OPEN_TOP";
        Container test1 = (Container) Cargo.fromString(s);
        Assert.assertEquals(test1.getType(), ContainerType.OPEN_TOP);
        Assert.assertEquals(test1.getId(), 80);
        Assert.assertEquals(test1.getDestination(), "Australia");

        s = "BulkCargo:2:Germany:GRAIN:50";
        BulkCargo test2 = (BulkCargo) Cargo.fromString(s);
        Assert.assertEquals(test2.getType(), BulkCargoType.GRAIN);
        Assert.assertEquals(test2.getTonnage(), 50);
        Assert.assertEquals(test2.getId(), 2);
        Assert.assertEquals(test2.getDestination(), "Germany"); */
    }

    @Test (expected = BadEncodingException.class)
    public void badString() throws BadEncodingException {
        Cargo.fromString("BulkCargo:12:Germany:GRAIN:50:");
    }

    @Test (expected = BadEncodingException.class)
    public void badString2() throws BadEncodingException {
        Cargo.fromString("BulkCargo:12:Ger:many:GRAIN:50:");
    }

    @Test (expected = BadEncodingException.class)
    public void badString3() throws BadEncodingException {
        Cargo.fromString("some:12:Germany:GRAIN:50");
    }

    @Test (expected = BadEncodingException.class)
    public void badString4() throws BadEncodingException {
        Cargo.fromString("some:ab:Germany:GRAIN:50");
    }

    @Test (expected = BadEncodingException.class)
    public void badString5() throws BadEncodingException {
        Cargo.fromString("BulkCargo:-200:Germany:GRAIN:50");
    }

   /* @Test (expected = BadEncodingException.class)
    public void badString6() throws BadEncodingException {
        Cargo.fromString("BulkCargo:3:Germany:GRAIN:50");
    } */

    @Test (expected = BadEncodingException.class)
    public void badString7() throws BadEncodingException {
        Cargo.fromString("BulkCargo:12:Germany:trash:50");
    }

    @Test (expected = BadEncodingException.class)
    public void badString8() throws BadEncodingException {
        Cargo.fromString("BulkCargo:12:Germany:GRAIN:ab");
    }

    @Test (expected = BadEncodingException.class)
    public void badString9() throws BadEncodingException {
        Cargo.fromString("BulkCargo:12:Germany:GRAIN:-10");
    }

    @Test
    public void movementTest() throws NoSuchCargoException, BadEncodingException {
        List<Cargo> cargos = new ArrayList<>();
        cargos.add(new BulkCargo(177, "Singapore", 100, BulkCargoType.OTHER));
        cargos.add(new Container(178, "Aus", ContainerType.OTHER));
        CargoMovement movement1 = new CargoMovement(120, MovementDirection.INBOUND, cargos);
        Assert.assertEquals("CargoMovement:120:INBOUND:2:177,178", movement1.encode());

        String s = "CargoMovement:120:INBOUND:2:177,178";
        CargoMovement movementTest = CargoMovement.fromString(s);
        Assert.assertEquals(movementTest.getCargo().size(), 2);
        Assert.assertEquals(movementTest.getDirection(), MovementDirection.INBOUND);
        Assert.assertEquals(movementTest.getTime(), 120);

        String sa = "ShipMovement:120:INBOUND:1258691";
        new ContainerShip(1258691, "hi", "s", NauticalFlag.BRAVO, 30);
        ShipMovement movementTest2 = ShipMovement.fromString(sa);
        Assert.assertEquals(movementTest2.getShip().getImoNumber(), 1258691);
        Assert.assertEquals(movementTest2.getTime(), 120);
        Assert.assertEquals(movementTest2.getDirection(), MovementDirection.INBOUND);
    }


    @Test (expected = NoSuchCargoException.class)
    public void badId() throws NoSuchCargoException {
        Cargo.getCargoById(1002);
    }

}
