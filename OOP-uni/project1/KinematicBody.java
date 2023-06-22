import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

public class KinematicBody extends CollisionBody{

    private final static int MAX_HEALTH = 100;
    private int health;

    /* Constructors */
    public KinematicBody(Image image, Point position) {
        super(image, position);
        // automatically creates hitbox
        this.health = this.MAX_HEALTH;
    }


    /* Methods */
    public boolean collisionCheck(KinematicBody player, Point destinationPoint, CollisionBody[] collisionBodies, int num_assets) {
        // Method responsible for checking collisions; returns true if collision occured
        /**
         * This is javadoc
         * test
         */
        Rectangle newHitbox = new Rectangle(destinationPoint.x, destinationPoint.y,
                                            this.getImage().getWidth(), this.getImage().getHeight());
        for (int i=0; i<num_assets; i++) {
            if (collisionBodies[i].getHitbox().intersects(newHitbox)) {

                // check if player collided with Sinkhole
                if (collisionBodies[i] instanceof Sinkhole) {
                    collisionBodies[i].damage(player);
                }
                return true;
            }
        }
        return false;
    }

    // Getters & setters
    public int getHealth() {
        return this.health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public int getMaxHealth() {
        return this.MAX_HEALTH;
    }
}
