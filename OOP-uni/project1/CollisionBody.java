import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

public abstract class CollisionBody {
    private Image image;
    private Rectangle hitbox;
    private Point position;     // top-left corer of collision box

    // Constructors
    public CollisionBody(Image image, Point position) {
        // Constructor for in-game assets (ie. Sinkholes & Walls)
        this.position = position;
        this.image = image;
        this.hitbox = new Rectangle((position.x),
                                    (position.y),
                                    (image.getWidth()),
                                    (image.getHeight()));
    }

    /* Methods */
    public Point getPosition() {
        return this.position;
    }
    public void setPosition(Point newPosition){
        this.position = newPosition;
    }
    public Rectangle getHitbox() {
        return this.hitbox;
    }
    public void setHitbox(Rectangle newHitbox) {
        this.hitbox = newHitbox;
    }

    public Image getImage() {
        return this.image;
    }
    public void setImage(Image image) {
        this.image = image;
    }

    public void damage(KinematicBody player) {
        return;
    }
}
