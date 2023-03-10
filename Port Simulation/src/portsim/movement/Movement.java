package portsim.movement;

import portsim.util.Encodable;

/**
 * The movement of ships or cargo coming into or out of the port from land or
 * sea.
 *
 * @ass1_partial
 */
public abstract class Movement implements Encodable {

    /**
     * The time in minutes that the movement should be completed
     */
    private long time;

    /**
     * The direction of the movement in relation to the port
     */
    private MovementDirection direction;

    /**
     * Creates a new movement with the given action time and direction.
     *
     * @param time      the time the movement should occur
     * @param direction the direction of the movement
     * @throws IllegalArgumentException if time &lt; 0
     * @ass1
     */
    public Movement(long time, MovementDirection direction) throws IllegalArgumentException {
        if (time < 0) {
            throw new IllegalArgumentException("Time must be greater than"
                + " or equal to 0: " + time);
        }
        this.time = time;
        this.direction = direction;
    }

    /**
     * Returns the time the movement should be actioned.
     *
     * @return movement time
     * @ass1
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the direction of the movement.
     *
     * @return movement direction
     * @ass1
     */
    public MovementDirection getDirection() {
        return direction;
    }

    /**
     * Returns the human-readable string representation of this Movement.
     * <p>
     * The format of the string to return is
     * <pre>DIRECTION MovementClass to occur at time</pre>
     * Where:
     * <ul>
     *   <li>{@code DIRECTION} is the direction of the movement </li>
     *   <li>{@code MovementClass} is the Movement class name</li>
     *   <li>{@code time} is the time the movement is meant to occur </li>
     * </ul>
     * For example: <pre>INBOUND Movement to occur at 120</pre>
     *
     * @return string representation of this Movement
     * @ass1
     */
    @Override
    public String toString() {
        return String.format("%s %s to occur at %d",
            this.direction,
            this.getClass().getSimpleName(),
            this.time);
    }

    /**
     * Returns the machine-readable string representation of this movement.
     *
     * @return encoded string representation of this movement
     * @ass2
     */
    public String encode() {
        return "" + this.getClass().getSimpleName() + ":" + this.time + ":"
                + this.direction;
    }

    /**
     * A helper method to check if a given string can be converted to a
     * movement direction
     *
     * @param s a given string to be converted
     *
     * @return true  if the string is a valid direction false otherwise.
     * @ass2
     */
    protected static boolean isDirection(String s) {
        try {
            MovementDirection.valueOf(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Check if a string can be parsed as a long
     *
     * @param s String to be parsed
     *
     * @return parsed long if succesfull, -1 otherwise.
     * @ass2
     */
    protected static long stringToLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
