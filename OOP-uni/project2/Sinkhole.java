import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.Image;
public class Sinkhole extends CollisionBody implements DamageDealing {

    // Class variables and constants
    private final static String NAME = "Sinkhole";
    private final static int DAMAGE = 30;
    private final static String DAMAGE_MSG =
            "Sinkhole inflicts 30 damage points on Fae. Fae's current health:  ";

    /* Constructors */
    public Sinkhole(Image image, Point position) {
        super(image, position);
        this.setName(NAME);
    }

    public void damage(Character player) {
        // inflict damage to player
        int newHealth = player.getHealth() - this.DAMAGE;
        if (newHealth < 0) {
            player.setHealth(0);
        }
        else {
            player.setHealth(player.getHealth() - this.DAMAGE);
        }
        printDmgMsg(this, player);
    }

    public void printDmgMsg(CollisionBody sinkhole, Character character) {
        System.out.println(DAMAGE_MSG + character.getHealth() + "/" + character.getMaxHealth());
    }
}