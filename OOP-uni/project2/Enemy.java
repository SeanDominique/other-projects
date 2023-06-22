import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public abstract class Enemy extends Character {

    private boolean aggression;
    private final static Double INVINCIBILITY_DURATION = 3.0; // in seconds
    private final static int SPEED_RANGE = 6; // 6 values between 2 and 7
    private final static int MIN_SPEED = 2;
    private Double currSpeed;

    private int direction;


    public Enemy(Image image, Point position) {
        super(image, position);

        Random random = new Random();
        this.direction = random.nextInt(4);

        Double randomSpeed = 0.1 * (random.nextInt(SPEED_RANGE) + MIN_SPEED);
        this.setSpeed(randomSpeed);

        this.currSpeed = this.getSpeed();

        this.setInvincibilityDuration(INVINCIBILITY_DURATION);
    }

    public void move(ArrayList<CollisionBody> collisionBodies, Point topLeft, Point bottomRight) {}



    /* Getters and setters */
    public boolean getAggression() {return this.aggression;}
    public void setAggression(boolean newAggression) {this.aggression = newAggression;}
    public int getDirection() {return this.direction;}
    public void setDirection(int newDirection) {this.direction = newDirection;}
    public Double getCurrSpeed() {return this.currSpeed;}
    public void setCurrSpeed(Double newCurrSpeed) {this.currSpeed = newCurrSpeed;}

}
