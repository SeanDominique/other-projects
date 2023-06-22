import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;

public class Navec extends Enemy {

    private final static String ENEMY_NAME = "Navec";
    private final Image NAVEC_LEFT = new Image("res/navec/navecLeft.png");
    private final Image NAVEC_RIGHT = new Image("res/navec/navecRight.png");
    private final Image NAVEC_INVINCIBLE_LEFT = new Image("res/navec/navecInvincibleLeft.PNG");
    private final Image NAVEC_INVINCIBLE_RIGHT= new Image("res/navec/navecInvincibleRight.PNG");
    private final static int LEFT = 0;
    private final static int RIGHT = 1;
    private final static int UP = 2;
    private final static int DOWN = 3;

    private final static int MAX_HEALTH = 80;
    private final static int DAMAGE = 20;

    public Navec(Image image, Point position) {
        super(image, position);
        this.setName(ENEMY_NAME);
        this.setAggression(true);
        this.setDamage(DAMAGE);
        this.setHealth(MAX_HEALTH);
        this.setMaxHealth(MAX_HEALTH);
    }

    /**
     * Move Navec based on current direction until collision occurs
     */
    @Override
    public void move(ArrayList<CollisionBody> collisionBodies, Point topLeft, Point bottomRight) {
        // Create "destination point" and update player position if collision hasn't occurred

        Double xCoord = this.getPosition().x;
        Double yCoord = this.getPosition().y;

        if (this.getDirection() == LEFT) {
            xCoord -= this.getCurrSpeed();

            if (this.getInvincible()) {this.setImage(NAVEC_INVINCIBLE_LEFT);}
            else {this.setImage(NAVEC_LEFT);}
        }
        else if (this.getDirection() == RIGHT) {
            xCoord += this.getCurrSpeed();

            if (this.getInvincible()) {this.setImage(NAVEC_INVINCIBLE_RIGHT);}
            else {this.setImage(NAVEC_RIGHT);}
        }
        else if (this.getDirection() == UP) {yCoord -= this.getCurrSpeed();}
        else if (this.getDirection() == DOWN) {yCoord += this.getCurrSpeed();}

        Point destinationPoint = new Point(xCoord, yCoord);

        if (this.boundaryCollisionCheck(destinationPoint, topLeft, bottomRight)) {
            changeDirection();
            return;
        }
        else {
            for (int i=0; i< collisionBodies.size(); i++) {
                if (this.collisionCheck(destinationPoint, collisionBodies.get(i))) {
                    changeDirection();
                    return;
                }
            }
        }

        this.setPosition(destinationPoint);
        this.setHitbox(new Rectangle((this.getPosition().x),
                (this.getPosition().y),
                (this.getImage().getWidth()),
                (this.getImage().getHeight())));
    }

    /**
     * Changes the direction Navec is moving in
     */
    public void changeDirection() {
        if (this.getDirection() == LEFT) {
            this.setDirection(RIGHT);
        }
        else if (this.getDirection() == RIGHT) {
            this.setDirection(LEFT);
        }
        else if (this.getDirection() == UP) {
            this.setDirection(DOWN);
        }
        else if (this.getDirection() == DOWN) {
            this.setDirection(UP);
        }
    }

    @Override
    public void setInvincible(boolean invincible) {
        super.setInvincible(invincible);
        if (invincible) {
            this.setImage(NAVEC_INVINCIBLE_RIGHT);
        }
        else {
            this.setImage(NAVEC_RIGHT);
        }
    }
}
