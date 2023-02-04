package portsim.evaluators;


import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;

import java.util.ArrayList;

import java.util.List;


/**
 * Gathers data on how many ships pass through the port over time.
 */
public class ShipThroughputEvaluator extends StatisticsEvaluator {
    /**
     *  A map with ships as keys and the time they passed through the port
     *  as values.
     */
    private List<Long> movementLog;

    /**
     *  Constructs a new ShipThroughputEvaluator.
     */
    public ShipThroughputEvaluator() {
        super();
        this.movementLog = new ArrayList<>();
    }

    /**
     *  Return the number of ships that have passed through the port
     *  in the last 60 minutes.
     *
     * @return ships throughput
     */
    public int getThroughputPerHour() {
        return this.movementLog.size();
    }

    @Override
    public void onProcessMovement(Movement movement) {
        if (movement.getDirection().equals(MovementDirection.OUTBOUND)
                && movement instanceof ShipMovement) {
            this.movementLog.add(this.getTime());
        }

    }

    @Override
    public void elapseOneMinute() {
        super.elapseOneMinute();
        for (int i = 0; i < movementLog.size(); i++) {
            if (this.getTime() - movementLog.get(i) > 60) {
                movementLog.remove(i);
            }
        }
    }
}
