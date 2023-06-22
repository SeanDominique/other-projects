import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;
public class Sinkhole extends CollisionBody {

    // Class variables and constants
    private int damage = 30;
    private final static String DAMAGE_MSG =
                "Sinkhole inflicts 30 damage points on Fae. Fae's current health:  ";

    private final static int FAR_AWAY = 10000;

    /* Constructors */
    public Sinkhole(Image image, Point position) {
        super(image, position);
        // automatically creates hitbox
    }

    @Override
    public void damage(KinematicBody player) {
        // inflict damage to player
        player.setHealth(player.getHealth() - 30);
        System.out.println(DAMAGE_MSG + player.getHealth() + "/" + player.getMaxHealth());

        // disappear from screen
        this.setPosition(new Point(FAR_AWAY, FAR_AWAY));
        this.setHitbox(new Rectangle(FAR_AWAY, FAR_AWAY,
                       getImage().getWidth(), getImage().getHeight()));
    }

}
