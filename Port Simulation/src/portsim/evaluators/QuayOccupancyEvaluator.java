package portsim.evaluators;

import portsim.cargo.BulkCargoType;
import portsim.movement.Movement;
import portsim.port.Port;
import portsim.port.Quay;

import java.util.List;
import java.util.Map;

/**
 * Evaluator to monitor how many quays are currently occupied at the port.
 *
 */
public class QuayOccupancyEvaluator extends StatisticsEvaluator {
    /**
     *  Port to monitor quays
     */
    private Port port;

    /**
     * Constructs a new QuayOccupancyEvaluator.\
     *
     * @param port port to monitor quays.
     *
     */
    public QuayOccupancyEvaluator(Port port) {
        super();
        this.port = port;
    }

    /**
     * Return the number of quays that are currently occupied.
     *
     * @return number of quays
     */
    public int getQuaysOccupied() {
        int occupied = 0;
        List<Quay> quays = this.port.getQuays();
        for (Quay quay : quays) {
            if (!quay.isEmpty()) {
                occupied++;
            }
        }
        return occupied;
    }

    @Override
    public void onProcessMovement(Movement movement) {
        // DO NOTHING
        ;
    }
}
