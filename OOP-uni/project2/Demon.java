import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Demon extends Enemy {

    private final static String ENEMY_NAME = "Demon";
    private final Image DEMON_LEFT = new Image("res/demon/demonLeft.png");
    private final Image DEMON_RIGHT = new Image("res/demon/demonRight.png");
    private final Image DEMON_INVINCIBLE_LEFT = new Image("res/demon/demonInvincibleLeft.PNG");
    private final Image DEMON_INVINCIBLE_RIGHT = new Image("res/demon/demonInvincibleRight.PNG");
    private final static int LEFT = 0;
    private final static int RIGHT = 1;
    private final static int UP = 2;
    private final static int DOWN = 3;

    private final static int MAX_HEALTH = 40;
    private final static int DAMAGE = 10;

    public Demon(Image image, Point position) {
        super(image, position);

        this.setName(ENEMY_NAME);
        this.setHealth(MAX_HEALTH);
        this.setMaxHealth(MAX_HEALTH);

        Random random = new Random();
        this.setAggression(random.nextBoolean());

        this.setDamage(DAMAGE);

        // set left or right image randomly
        if (random.nextBoolean()) {
            this.setImage(DEMON_LEFT);
        }
    }

    /**
     * Move Demon based on current direction until collision occurs
     */
    @Override
    public void move(ArrayList<CollisionBody> collisionBodies, Point topLeft, Point bottomRight) {
        // Create "destination point" and update player position if collision hasn't occurred

        Double xCoord = this.getPosition().x;
        Double yCoord = this.getPosition().y;

        if (this.getDirection() == LEFT) {
            xCoord -= this.getCurrSpeed();

            if (this.getInvincible()) {
                this.setImage(DEMON_INVINCIBLE_LEFT);
            } else {
                this.setImage(DEMON_LEFT);
            }
        } else if (this.getDirection() == RIGHT) {
            xCoord += this.getCurrSpeed();

            if (this.getInvincible()) {
                this.setImage(DEMON_INVINCIBLE_RIGHT);
            } else {
                this.setImage(DEMON_RIGHT);
            }
        } else if (this.getDirection() == UP) {
            yCoord -= this.getCurrSpeed();
        } else if (this.getDirection() == DOWN) {
            yCoord += this.getCurrSpeed();
        }

        Point destinationPoint = new Point(xCoord, yCoord);

        if (this.boundaryCollisionCheck(destinationPoint, topLeft, bottomRight)) {
            changeDirection();
            return;
        } else {
            for (int i = 0; i < collisionBodies.size(); i++) {
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
     * Changes the direction the demon is moving in
     */
    public void changeDirection() {
        if (this.getDirection() == LEFT) {
            this.setDirection(RIGHT);
        } else if (this.getDirection() == RIGHT) {
            this.setDirection(LEFT);
        } else if (this.getDirection() == UP) {
            this.setDirection(DOWN);
        } else if (this.getDirection() == DOWN) {
            this.setDirection(UP);
        }
    }

    @Override
    public void setInvincible(boolean invincible) {
        super.setInvincible(invincible);
        if (invincible) {
            this.setImage(DEMON_INVINCIBLE_RIGHT);
        }
        else {
            this.setImage(DEMON_RIGHT);
        }
    }
}
