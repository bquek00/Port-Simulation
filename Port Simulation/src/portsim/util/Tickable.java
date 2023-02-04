package portsim.util;

/**
 * Gathers data on how many ships pass through the port over time.
 */
public interface Tickable {
    /**
     * Method to be called once on every simulation tick.
     */
    void elapseOneMinute();
}
