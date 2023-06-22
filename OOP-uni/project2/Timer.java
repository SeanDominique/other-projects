public class Timer {

    private int frame;
    private Double endFrame;
    private boolean timerOn;
    private final static int REFRESH_RATE = 60;

    public Timer() {
        this.frame = 1;
        this.endFrame = 0.0;
        timerOn = false;
    }

    /**
     * Calculate number of frames to count for given an amount of seconds
     * @param n_seconds
     */
    public void set(Double n_seconds) {
        // make sure clock is not already ticking so you can't reset
        if (this.timerOn == false) {
            this.timerOn = true;
            this.endFrame = n_seconds * REFRESH_RATE;
        }
    }
    /**
     * Increments frame counter
     */
    public void tick() {if (timerOn) {this.frame++;}}

    /**
     * Returns true if timer has ended and resets it
     */
    public boolean done() {
        if (this.frame > this.endFrame) {
            this.timerOn = false;
            this.frame = 1;
            this.endFrame = 0.0;
            return true;
        }
        return false;
    }
}
