import bagel.Image;
import bagel.util.Point;
public class Wall extends CollisionBody{

    /* Constructors */
    public Wall(Image image, Point position) {
        super(image, position);
        // automatically creates hitbox
    }
}
