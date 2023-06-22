import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.Image;
public abstract class Character extends CollisionBody {

    private int health;
    private int maxHealth;
    private int damage;
    private boolean invincible;
    private Double invincibilityDuration;
    private Double speed;
    private Timer timer;

    public Character (Image image, Point position) {
        super(image, position);
        timer = new Timer();
        this.invincible = false;
    }


    /* Methods */
    /**
     * Checks if a character collided with a CollisionBody or world boundaries
     */
    public boolean collisionCheck(Point destinationPoint, CollisionBody collisionBody) {

        Rectangle newHitbox = new Rectangle(destinationPoint.x, destinationPoint.y,
                this.getImage().getWidth(), this.getImage().getHeight());

        if (!(collisionBody instanceof Character) && (collisionBody.getHitbox().intersects(newHitbox))) { return true; }
        return false;
    }

    /**
     * Checks if a character overlaps with another character
     */
    public boolean overlapCheck(Character other) {return other.getHitbox().intersects(this.getHitbox());}


    /**
     * Checks if a character collided with world boundaries
     */
    public boolean boundaryCollisionCheck(Point destinationPoint, Point topLeft, Point bottomRight) {
        if ((destinationPoint.x > topLeft.x) && (destinationPoint.x < bottomRight.x) &&
                (destinationPoint.y > topLeft.y) && (destinationPoint.y < bottomRight.y)) {return false;}

        return true;
    }

    /**
     * Logic for when enemy gets hit in game
     * Starts temporary invincibility and reduces health
     * Prints damage dealt
     */
    public void hit(int damage, CollisionBody damager) {
        if ((this.getHealth() - damage) < 0) {
            this.setHealth(0);
        }
        else {
            this.setHealth((this.getHealth() - damage));
            this.getTimer().set(this.invincibilityDuration);
            this.setInvincible(true);
            System.out.println(damager.getName()+" inflicts "+damage+" points on "+
                    this.getName()+".  "+this.getName()+"'s current health:  "+
                    this.getHealth()+"/"+this.getMaxHealth());
        }
    }

    /* Getters and setters */
    public Double getSpeed() { return this.speed;}
    public void setSpeed(Double speed) { this.speed = speed; }
    public int getHealth() { return this.health; }
    public void setHealth(int health) {this.health = health;}
    public int getDamage() {return this.damage;}
    public void setDamage(int damage) {this.damage = damage;}
    public boolean getInvincible() {return this.invincible;}
    public void setInvincible(boolean invincible) {this.invincible = invincible;}
    public Timer getTimer() {return this.timer;}
    public int getMaxHealth() {return this.maxHealth;}
    public void setMaxHealth(int maxHealth) {this.maxHealth = maxHealth;}

    public void setInvincibilityDuration(Double invincibilityDuration) {
        this.invincibilityDuration = invincibilityDuration;
    }
}
