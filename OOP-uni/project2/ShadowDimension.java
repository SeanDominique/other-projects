import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.Image;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 2, 2022
 *
 * Please enter your name below
 * Sean DOMINIQUE
 */

public class ShadowDimension extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static String GAME_TITLE = "SHADOW DIMENSION";

    /* Game Images */
    private final Image[] backgroundImages = {new Image("res/background0.png"),
            new Image("res/background1.png")};

    private final Image TREE = new Image("res/tree.png");
    private final Image WALL = new Image("res/wall.png");
    private final Image SINKHOLE = new Image("res/sinkhole.png");
    private final Image FAE_RIGHT = new Image("res/fae/faeRight.png");
    private final Image FAE_ATTACK_RIGHT = new Image("res/fae/faeAttackRight.png");
    private final Image NAVEC_RIGHT = new Image("res/navec/navecRight.png");
    private final Image DEMON_RIGHT = new Image("res/demon/demonRight.png");

    // Message constants
    private final static int TITLE_FONT_SIZE = 75;
    private final static int TITLE_X = 260;
    private final static int TITLE_Y = 250;
    private final static int INSTRUCTION_FONT_SIZE = 40;
    private final static int INS_0_X_OFFSET = 90;
    private final static int INS_0_Y_OFFSET = 190;
    private final static int INS_1_X = 350;
    private final static int INS_1_Y = 350;
    private final static String FONT_FILE = "res/frostbite.ttf";
    private final Font TITLE_FONT = new Font(FONT_FILE, TITLE_FONT_SIZE);
    private final Font INSTRUCTION_FONT = new Font(FONT_FILE, INSTRUCTION_FONT_SIZE);
    private final static String INS_MSG_0 = "PRESS SPACE TO START\nUSE ARROW KEYS TO FIND GATE";
    private final static String INS_MSG_1 = "PRESS SPACE TO START\nPRESS A TO ATTACK\nDEFEAT NAVEC TO WIN";
    private final static String LVL_COMPLETE_MSG = "LEVEL COMPLETE!";
    private final static String LOSE_MSG = "GAME OVER!";
    private final static String WIN_MSG = "CONGRATULATIONS!";

    /* Health bar variables */
    private final static Point HEALTH_BAR_POSITION = new Point(20,25);
    private final static int HEALTH_SIZE = 30;
    private final static int HEALTH_SIZE_ENEMY = 15;
    private final static int HEALTH_OFFSET = 6;
    private final static Colour GREEN = new Colour(0,0.8,0.2);
    private final static Colour ORANGE = new Colour(0.9,0.6,0);
    private final static Colour RED = new Colour(1,0,0);
    private final static double THRESHOLD_ORANGE = 65;
    private final static double THRESHOLD_RED = 35;


    /* Game variables and constants */
    private final static int START = 0;
    private final static int PLAY = 1;
    private final static int WIN = 2;
    private final static int LOSE = 3;
    private int gameState;
    private int currLevel;
    private int timeScale;
    private final static boolean INCREASE = true;
    private final static Double TIMESCALE_INCREASE = 1.5;
    private final static Double TIMESCALE_DECREASE = 0.5;
    private Timer gameTimer;
    private Timer timeScaleTimer;
    private final static Double TIMESCALE_COOLDOWN = 0.2;
    private final static Double LVL_COMPLETE_SCRN_DURATION = 3.0;
    private final static Point WIN_POINT = new Point(950,650);
    private final static int LVL_0_MAX_ASSETS = 60;
    private final static int LVL_1_MAX_ASSETS = 29;


    /* Boundary constants, not set to final because they are determined after readCSV */
    private Point topLeft;
    private Point bottomRight;

    // Game objects
    private ArrayList<CollisionBody> collisionBodies = new ArrayList<CollisionBody>();
    private Player player;


    public ShadowDimension(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        this.currLevel = 0;
        this.timeScale = 0;
        this.gameTimer = new Timer();
        this.timeScaleTimer = new Timer();
        readCSV("res/level0.csv", LVL_0_MAX_ASSETS, collisionBodies);
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDimension game = new ShadowDimension();
        game.run();
    }

    /**
     * Method used to read file and create objects (You can change this
     * method as you wish).
     */

    private void readCSV(String filename, int maxAssets, ArrayList<CollisionBody> collisionBodies) {

        int num_assets = 0;
        // Read through file until no more inputs
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while (((line = br.readLine()) != null) && (num_assets < maxAssets)) {
                // Separate each category and create according image and collision body
                String[] words = line.split(",");
                Point position = new Point(Double.parseDouble(words[1]), Double.parseDouble(words[2]));

                // create player object or change position if new level
                if (words[0].equals("Fae")) {
                    if (this.currLevel == 0) {
                    player = new Player(FAE_RIGHT, position);
                    }
                    else {
                        player.setPosition(position);
                    }
                }

                else {
                    // Set perimeter values
                    if (words[0].equals("BottomRight")) {
                        this.bottomRight = new Point(position.x, position.y);
                    }
                    else if (words[0].equals("TopLeft")) {
                        this.topLeft = new Point(position.x, position.y);
                    }
                    else {
                        // create and store game assets
                        num_assets++;
                        if (words[0].equals("Sinkhole")) { collisionBodies.add(new Sinkhole(SINKHOLE, position)); }
                        else if (words[0].equals("Wall")) { collisionBodies.add(new CollisionBody(WALL, position)); }
                        else if (words[0].equals("Tree")) { collisionBodies.add(new CollisionBody(TREE, position)); }
                        else if (words[0].equals("Demon")) { collisionBodies.add(new Demon(DEMON_RIGHT, position)); }
                        else if (words[0].equals("Navec")) { collisionBodies.add(new Navec(NAVEC_RIGHT, position)); }

                        else {
                            System.out.println("Not a valid game object");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    protected void update(Input input) {
        // Quit game
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        // Display start screen messages
        if (gameState == START) {

            if (currLevel == 0) {
                drawStartScreen();
                if (input.wasPressed(Keys.SPACE)) {
                    gameState = PLAY;
                }
                return;
            }

            // draw level start screen until timer is over
            if ((currLevel > 0) && (gameTimer.done() == false)) {
                drawLevelCompleteScreen();
            }
            else {
                drawLevelStartScreen();
                if (input.wasPressed(Keys.SPACE)) {
                    gameState = PLAY;
                }
            }
        }

        // Gameplay
        if (gameState == PLAY) {
            // Draw in game assets
            draw();
            // Draw player and enemy health bars
            printHealth(player, HEALTH_BAR_POSITION.x, HEALTH_BAR_POSITION.y, HEALTH_SIZE);
            for (int i=0; i < collisionBodies.size(); i++) {
                if (collisionBodies.get(i) instanceof Enemy) {
                    printHealth((Enemy) collisionBodies.get(i), ((Enemy) collisionBodies.get(i)).getPosition().x,
                            (((Enemy) collisionBodies.get(i)).getPosition().y - HEALTH_OFFSET), HEALTH_SIZE_ENEMY);
                }
            }

            // Player movement
            if (input.isDown(Keys.LEFT)) {
                player.move("left", collisionBodies, topLeft, bottomRight);
            }
            if (input.isDown(Keys.RIGHT)) {
                player.move("right", collisionBodies, topLeft, bottomRight);
            }
            if (input.isDown(Keys.DOWN)) {
                player.move("down", collisionBodies, topLeft, bottomRight);
            }
            if (input.isDown(Keys.UP)) {
                player.move("up", collisionBodies, topLeft, bottomRight);
            }

            // ATTACK
            if (input.isDown(Keys.A) && (player.getTimer().done())) {
                player.attack();
            }
            if (player.getAttackState()) {
                // check if timer is done to reset player to passive state
                if (player.getTimer().done()) {
                    player.attackReset();
                }
                checkHits();
            }

            resetInvincibility();

            // Level 1 gameplay
            if ((gameState == PLAY) && (currLevel == 1)) {

                moveEnemies(collisionBodies);

                // Timescale
                if (timeScaleTimer.done()) {
                    if ((input.isDown(Keys.L)) && (timeScale <= 2)) {
                        timeScale++;
                        timeScale(collisionBodies, INCREASE);
                    }
                    if ((input.isDown(Keys.K)) && (timeScale >= -2)) {
                        timeScale--;
                        timeScale(collisionBodies, !INCREASE);
                    }
                }
            }
        }

        // check loss
        if (player.getHealth() <= 0) {
            // exit game loop
            gameState = LOSE;
            endScreen(gameState);
        }

        // check level0 complete or skip to level 1 if 'w' is pressed
        if ((((player.getPosition().x > WIN_POINT.x) && (player.getPosition().y > WIN_POINT.y)) ||
                (input.wasPressed(Keys.W))) && (currLevel == 0)) {
            changeLevel("res/level1.csv", LVL_1_MAX_ASSETS);
        }

        // Victory!
        if (gameState == WIN) {
            endScreen(gameState);
        }

        this.tock();
    }

    /**
     * Draw game objects and background screen
    */
    public void draw() {
        backgroundImages[currLevel].draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
        player.getImage().drawFromTopLeft(player.getPosition().x, player.getPosition().y);

        for (int i=0; i < collisionBodies.size(); i++) {
            collisionBodies.get(i).getImage().drawFromTopLeft(collisionBodies.get(i).getPosition().x,
                                                              collisionBodies.get(i).getPosition().y);
        }
    }


    /**
     * Increments any active timers
     */
    public void tock() {
        gameTimer.tick();
        timeScaleTimer.tick();
        player.getTimer().tick();
        // Enemy timers
        for (int i=0; i<collisionBodies.size(); i++) {
            if (collisionBodies.get(i) instanceof Enemy) {
                ((Enemy) collisionBodies.get(i)).getTimer().tick();
            }
        }
    }

    /**
     * Move aggressive enemies across the screen
     */
    public void moveEnemies(ArrayList<CollisionBody> collisionBodies) {
        for (int i=0; i<collisionBodies.size(); i++) {
            if (collisionBodies.get(i) instanceof Enemy) {
                if (((Enemy) collisionBodies.get(i)).getAggression()) {
                    ((Enemy) collisionBodies.get(i)).move(collisionBodies, topLeft, bottomRight);
                }
            }
        }
    }

    /**
     * Adjust speed of enemies
     */
    public void timeScale(ArrayList<CollisionBody> collisionBodies, boolean increase) {
        timeScaleTimer.set(TIMESCALE_COOLDOWN);
        for (int i=0; i<collisionBodies.size(); i++) {

            if (collisionBodies.get(i) instanceof Enemy) {

                Double newSpeed = ((Enemy) collisionBodies.get(i)).getSpeed();

                // Only change speed of aggressive enemies
                if (((Enemy) collisionBodies.get(i)).getAggression()) {
                    if (timeScale > 0) {
                        newSpeed = newSpeed * (Math.pow(TIMESCALE_INCREASE, timeScale));
                    }
                    else {
                        newSpeed = newSpeed * (Math.pow(TIMESCALE_DECREASE, (-1)*timeScale));
                    }
                ((Enemy) collisionBodies.get(i)).setCurrSpeed(newSpeed);
                }
            }
        }
        if (increase) {
            System.out.println("Sped up, Speed:  " + timeScale);
        }
        else {
            System.out.println("Slowed down, Speed:  " + timeScale);
        }
    }

    /**
     * Check character overlap while player is in attack mode and applies damage
     * Also removes enemies if they die and checks win condition
     */
    public void checkHits() {

        for (int i = 0; i < collisionBodies.size(); i++) {

            // check if player collides with non-invincible enemy
            if (collisionBodies.get(i) instanceof Enemy) {
                if (((((Enemy) collisionBodies.get(i)).getInvincible()) == false) &&
                        ((Enemy) collisionBodies.get(i)).overlapCheck(player)) {

                    // successful attack
                    ((Enemy) collisionBodies.get(i)).hit(player.getDamage(), player);

                    // remove enemy if dead
                    if (((Enemy) collisionBodies.get(i)).getHealth() == 0) {

                        // win game if Navec is killed
                        if (collisionBodies.get(i) instanceof Navec) {
                            // exit game loop
                            gameState = WIN;
                        }
                        collisionBodies.remove(i);
                    }
                }
            }
        }
    }

    /**
     * Reset invincibility of characters if their timers have expired
     */
    public void resetInvincibility() {
        if ((player.getInvincible()) && (player.getTimer().done())) {
            player.setInvincible(false);
        }

        for (int i=0; i<collisionBodies.size(); i++) {
            if (collisionBodies.get(i) instanceof Enemy) {
                if ((((Enemy) collisionBodies.get(i)).getInvincible()) &&
                       ((Enemy) collisionBodies.get(i)).getTimer().done()) {
                    ((Enemy) collisionBodies.get(i)).setInvincible(false);
                }
            }
        }
    }

    /**
     * Change to next level
     */
    private void changeLevel(String levelFilename, int levelMaxAssets) {
        currLevel += 1;
        collisionBodies.clear();
        readCSV(levelFilename, levelMaxAssets, collisionBodies);
        gameTimer.set(LVL_COMPLETE_SCRN_DURATION);
        gameState = START;
    }

    /**
     * Print health bars of in game characters
     */
    public void printHealth(Character character, Double xPos, Double yPos, int fontSize) {
        // Display health bar
        Colour colour;
        int healthPercentage = (int) Math.round(character.getHealth() * 100/character.getMaxHealth());

        // Color changes
        if (healthPercentage > THRESHOLD_ORANGE) {
            colour = GREEN;
        }
        else if (healthPercentage > THRESHOLD_RED) {
            colour = ORANGE;
        }
        else {
            colour = RED;
        }
        Font healthBar = new Font(FONT_FILE, fontSize);
        String healthBarMsg = Integer.toString(healthPercentage) + "%";
        DrawOptions drawOptions = new DrawOptions();
        healthBar.drawString(healthBarMsg, xPos, yPos, drawOptions.setBlendColour(colour));
    }

    /**
     * Draw level complete message
     */
    private void drawLevelCompleteScreen() {
        TITLE_FONT.drawString(LVL_COMPLETE_MSG, ((WINDOW_WIDTH / 2) - (TITLE_FONT.getWidth(WIN_MSG) / 2)),
                WINDOW_HEIGHT / 2);
    }

    /**
     * Draw the level 1 instructions
     */
    private void drawLevelStartScreen(){
        INSTRUCTION_FONT.drawString(INS_MSG_1,INS_1_X, INS_1_Y);
    }

    /**
     * Display message on screen at end of game
     */
    private void endScreen(int gameState) {
        if (gameState == WIN) {
            TITLE_FONT.drawString(WIN_MSG, ((WINDOW_WIDTH / 2) - (TITLE_FONT.getWidth(WIN_MSG) / 2)),
                    WINDOW_HEIGHT / 2);
        }
        else if (gameState == LOSE) {
            TITLE_FONT.drawString(LOSE_MSG, ((WINDOW_WIDTH / 2) - (TITLE_FONT.getWidth(LOSE_MSG) / 2)),
                    WINDOW_HEIGHT / 2);
        }
    }

    // Methods from Project 1 solution
    /**
     * Draw the start screen title and instructions
     */
    private void drawStartScreen() {
        TITLE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
        INSTRUCTION_FONT.drawString(INS_MSG_0,TITLE_X + INS_0_X_OFFSET, TITLE_Y + INS_0_Y_OFFSET);
    }
}
