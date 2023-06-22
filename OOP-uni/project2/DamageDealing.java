public interface DamageDealing {

    void damage(Character character);
    void printDmgMsg(CollisionBody attacker, Character damaged);
}
