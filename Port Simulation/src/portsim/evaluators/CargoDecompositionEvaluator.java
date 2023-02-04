package portsim.evaluators;

import portsim.cargo.*;
import portsim.movement.CargoMovement;
import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.BulkCarrier;
import portsim.ship.ContainerShip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects data on what types of cargo are passing through the port.
 * Gathers data on all derivatives of the cargo class.
 *
 */
public class CargoDecompositionEvaluator extends StatisticsEvaluator {
    /**
     *  The distribution of bulk cargo types that have entered the port.
     */
    private Map<BulkCargoType, Integer> bulkCargoDistribution;
    /**
     *  The distribution of which cargo types that have entered the port.
     */
    private Map<String, Integer> cargoDistribution;
    /**
     *  Returns the distribution of container cargo types that have entered
     *  the port.
     */
    private Map<ContainerType, Integer> containerDistribution;


    /**
     * Creates a statistics evaluator and initialises the time since the
     * evaluator was created to zero.
     *
     */
    public CargoDecompositionEvaluator() {
        super();
        this.bulkCargoDistribution = new HashMap<>();
        this.cargoDistribution = new HashMap<>();
        this.containerDistribution = new HashMap<>();
    }

    /**
     * Returns the distribution of which cargo types that have entered the port.
     *
     * @return cargo distribution map
     *
     */
    public Map<String, Integer> getCargoDistribution() {
        return cargoDistribution;
    }

    /**
     * Returns the distribution of bulk cargo types that have entered the port.
     *
     * @return bulk cargo distribution map
     *
     */
    public Map<BulkCargoType, Integer> getBulkCargoDistribution() {
        return bulkCargoDistribution;
    }

    /**
     * Returns the distribution of container cargo types that have entered
     * the port.
     *
     * @return container distribution map.
     *
     */
    public Map<ContainerType, Integer> getContainerDistribution() {
        return containerDistribution;
    }

    /**
     * Updates the Cargo Distribution hashmap, and CargoType, or
     * ContainerType distribution. maps given the cargo.
     *
     * Increments the number if the class has seen before. Sets the number to 1
     * if the class has never been seen before.
     *
     * @param cargo the cargo to be counted.
     *
     *
     */
    private void processCargo(Cargo cargo) {
        // Update the Cargo hashmap
        String simpleName = cargo.getClass().getSimpleName();
        Integer count = cargoDistribution.get(simpleName);
        if (count != null) {
            cargoDistribution.put(simpleName, count + 1);
        } else {
            cargoDistribution.put(simpleName, 1);
        }

        // Update the type hashmaps
        if (cargo instanceof BulkCargo) {
            BulkCargoType type = ((BulkCargo) cargo).getType();
            Integer typeCount = bulkCargoDistribution.get(type);

            if (typeCount != null) {
                bulkCargoDistribution.put(type, typeCount + 1);
            } else {
                bulkCargoDistribution.put(type, 1);
            }
        } else { // cargo is a container
            ContainerType type = ((Container) cargo).getType();
            Integer typeCount = containerDistribution.get(type);

            if (typeCount != null) {
                containerDistribution.put(type, typeCount + 1);
            } else {
                containerDistribution.put(type, 1);
            }
        }
    }

    @Override
    public void onProcessMovement(Movement movement) {
        if (movement.getDirection().equals(MovementDirection.INBOUND)) {

            if (movement instanceof ShipMovement) {
                ShipMovement shipMovement = (ShipMovement) movement;
                if (shipMovement.getShip() instanceof BulkCarrier) {
                    BulkCarrier ship = (BulkCarrier) shipMovement.getShip();
                    BulkCargo cargo = ship.getCargo();

                    processCargo(cargo);

                } else { // ship is instance of ContainerShip
                    ContainerShip ship = (ContainerShip) shipMovement.getShip();
                    List<Container> containers = ship.getCargo();
                    for (Container cargo : containers) {
                        processCargo(cargo);
                    }

                }
            } else { // Movement is cargo movement
                List<Cargo> cargos = ((CargoMovement) movement).getCargo();
                for (Cargo cargo : cargos) {
                    processCargo(cargo);
                }
            }

        }
    }
}
