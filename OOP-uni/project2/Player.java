import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;

public class Player extends Character {

    private final static String PLAYER_NAME = "Fae";
    private final Image FAE_LEFT = new Image("res/fae/faeLeft.png");
    private final Image FAE_RIGHT = new Image("res/fae/faeRight.png");
    private final Image FAE_ATTACK_LEFT = new Image("res/fae/faeAttackLeft.png");
    private final Image FAE_ATTACK_RIGHT = new Image("res/fae/faeAttackRight.png");

    private boolean attackState;
    private final static Double ATTACK_DURATION = 1.0;
    private final static Double ATTACK_COOLDOWN = 2.0;
    private final static Double INVINCIBILITY_DURATION = 3.0;
    private final static int MAX_HEALTH = 100;
    private final static Double PLAYER_SPEED = 2.0;
    private final static int DAMAGE = 30;


    public Player(Image image, Point position) {
        super(image, position);
        this.setSpeed(this.PLAYER_SPEED);
        this.setHealth(this.MAX_HEALTH);
        this.setDamage(this.DAMAGE);
        this.attackState = false;
        this.setMaxHealth(MAX_HEALTH);
        this.setInvincibilityDuration(INVINCIBILITY_DURATION);
        this.setName(PLAYER_NAME);
    }

    /**
     * Move player based on input keys if no collision
     */
    public void move(String direction, ArrayList<CollisionBody> collisionBodies, Point topLeft, Point bottomRight) {
        // Create "destination point" and update player position if collision hasn't occurred

        Double xCoord = this.getPosition().x;
        Double yCoord = this.getPosition().y;

        if (direction == "left") {
            xCoord -= this.getSpeed();
            if (this.attackState) {this.setImage(FAE_ATTACK_LEFT);}
            else {this.setImage(FAE_LEFT);}
        }
        if (direction == "right") {
            xCoord += this.getSpeed();
            if (this.attackState) {this.setImage(FAE_ATTACK_RIGHT);}
            else {this.setImage(FAE_RIGHT);}
        }
        if (direction == "down") {yCoord += this.getSpeed();}
        if (direction == "up") {yCoord -= this.getSpeed();}


        Point destinationPoint = new Point(xCoord, yCoord);

        if (this.boundaryCollisionCheck(destinationPoint, topLeft, bottomRight)) {return;}
        else {
            // check collision with another object
            for (int i=0; i< collisionBodies.size(); i++) {
                if (this.collisionCheck(destinationPoint, collisionBodies.get(i))) {
                    if (collisionBodies.get(i) instanceof Sinkhole) {
                        ((Sinkhole) collisionBodies.get(i)).damage(this);
                        collisionBodies.remove(i);
                    }
                    return;
                }
            }
        }

        this.setPosition(new Point(xCoord, yCoord));
        this.setHitbox(new Rectangle((this.getPosition().x),
                                    (this.getPosition().y),
                                    (this.getImage().getWidth()),
                                    (this.getImage().getHeight())));


    }

    /**
     * Sets player in attack mode and creates a timer
     */
    public void attack() {
        this.attackState = true;
        this.setImage(FAE_ATTACK_RIGHT);
        this.getTimer().set(ATTACK_DURATION);
    }

    /**
     * Exits player from attack mode
     */
    public void attackReset() {
        this.attackState = false;
        this.setImage(FAE_RIGHT);
        this.getTimer().set(ATTACK_COOLDOWN);
    }

    /* Getters and Setters */
    public boolean getAttackState() {return this.attackState;}
    public void setAttackState(boolean attackState) {
        this.attackState = attackState;
        this.setImage(FAE_RIGHT);
    }
}

