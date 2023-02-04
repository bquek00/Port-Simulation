package portsim.evaluators;

import portsim.movement.Movement;
import portsim.movement.MovementDirection;
import portsim.movement.ShipMovement;
import portsim.ship.NauticalFlag;

import java.util.HashMap;
import java.util.Map;

/**
 * Gathers data on how many ships each country has sent to this port.
 *
 */
public class ShipFlagEvaluator extends StatisticsEvaluator {
    /**
     *  The flag distribution seen at this port.
     */
    private Map<String, Integer> flagDistribution;

    /**
     *  Constructs a new ShipFlagEvaluator.
     */
    public ShipFlagEvaluator() {
        super();
        this.flagDistribution = new HashMap<>();
    }

    /**
     *  Return the flag distribution seen at this port.
     *
     * @return flag distribution
     */
    public Map<String, Integer> getFlagDistribution() {
        return this.flagDistribution;
    }

    /**
     * Return the number of times the given flag has been seen at the port.
     *
     * @param flag - country flag to find in the mapping
     *
     * @return number of times flag seen or 0 if not seen
     */
    public int getFlagStatistics(String flag) {
        Integer count = flagDistribution.get(flag);
        if (count == null) {
            return 0;
        }
        return count;
    }


    @Override
    public void onProcessMovement(Movement movement) {
        if (movement.getDirection().equals(MovementDirection.INBOUND)
                && movement instanceof ShipMovement) {
            ShipMovement shipMovement = (ShipMovement) movement;
            String originFlag = shipMovement.getShip().getOriginFlag();
            int count = this.getFlagStatistics(originFlag);

            if (count == 0) {
                flagDistribution.put(originFlag, 1);
            } else {
                flagDistribution.put(originFlag, count + 1);
            }
        }
    }
}
