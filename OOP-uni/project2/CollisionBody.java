
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.Image;

public class CollisionBody {

    private String name;
    private Point position;     // top-left corner of collision box
    private Image image;
    private Rectangle hitbox;

    // Constructors

    // For simple game assets
    public CollisionBody(Image image, Point position) {
        // Constructor for in-game assets (ie. Sinkholes, Walls, Demons...)
        this.position = position;
        this.image = image;
        this.hitbox = new Rectangle((position.x),
                (position.y),
                (image.getWidth()),
                (image.getHeight()));
    }



    /* Getters and setters */
    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}
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
}
