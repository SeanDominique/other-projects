import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 2, 2022
 *
 * Please enter your name below
 * Sean Dominique
 */


public class ShadowDimension extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;

    /* In game text variables */
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final static String FONT_FILENAME = "res/frostbite.ttf";
    private final static int GAME_TITLE_x = 260;
    private final static int GAME_TITLE_y = 250;
    private final static int DEFAULT_SIZE = 75;
    private final static String INSTRUCTION1 = "PRESS SPACE TO START";
    private final static String INSTRUCTION2 = "USE ARROW KEYS TO FIND GATE";
    private final static int INSTRUCTION1_y = GAME_TITLE_y + 190;
    private final static int INSTRUCTION2_y = WINDOW_HEIGHT/2;
    private final static int INSTRUCTION_SIZE = 40;

    private final static String WINNING_MSG = "CONGRATULATIONS!";
    private final static String LOSING_MSG = "GAME OVER!";

    /* Health bar variables */
    private final static Point HEALTH_BAR_POSITION = new Point(20,25);
    private final static int HEALTH_SIZE = 30;
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
    private final static Point WIN_POINT = new Point(950,650);

    /* Player constants */
    private final static int SPEED = 2;

    /* Images */
    private final Image BACKGROUND_IMAGE = new Image("res/background0.png");
    private final Image PLAYER_LEFT = new Image("res/faeLeft.png");
    private final Image PLAYER_RIGHT = new Image("res/faeRIGHT.png");

    private final Image SINKHOLE = new Image("res/sinkhole.png");
    private final Image WALL = new Image("res/wall.png");

    /* Array and constants used to store game's collision bodies */
    private final static String ASSETS_FILENAME = "res/level0.csv";
    private final static int MAX_ASSETS = 60;
    private int num_assets = 0;
    private CollisionBody[] collisionBodies = new CollisionBody[MAX_ASSETS];
    private KinematicBody player;

    /* Boundary constants, not set to final because they are determined after readCSV */
    private double top;
    private double bot;
    private double right;
    private double left;

    public ShadowDimension(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
        gameState = START;
        readCSV(collisionBodies, ASSETS_FILENAME);
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
    private void readCSV(CollisionBody[] collisionBodies, String filename) {
        // Read through file until no more inputs
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while (((line = br.readLine()) != null) && (num_assets < MAX_ASSETS)) {
                // Separate each category and create according image and collision body
                String[] words = line.split(",");
                Point position = new Point(Double.parseDouble(words[1]), Double.parseDouble(words[2]));

                if (words[0].equals("Player")) {
                    player = new KinematicBody(PLAYER_RIGHT, position);
                }

                else {
                    if (words[0].equals("Sinkhole")) {
                        collisionBodies[num_assets] = new Sinkhole(SINKHOLE,position);
                        num_assets++;
                    }

                    else if (words[0].equals("Wall")) {
                        collisionBodies[num_assets] = new Wall(WALL,position);
                        num_assets++;
                    }

                    // Set perimeter values
                    else if (words[0].equals("BottomRight")) {
                        this.bot = position.y;
                        this.right = position.x;
                    }
                    else if (words[0].equals("TopLeft")) {
                        this.top = position.y;
                        this.left = position.x;
                    }
                    else {
                        System.out.println("Not a valid game object");
                    }
                }
            }
        } catch (Exception e) {
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
        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        // Display start screen messages
        if (gameState == START) {
            Font gameTitle = new Font(FONT_FILENAME, DEFAULT_SIZE);
            gameTitle.drawString(GAME_TITLE, GAME_TITLE_x, GAME_TITLE_y);

            Font instruction1 = new Font(FONT_FILENAME, INSTRUCTION_SIZE);
            instruction1.drawString(INSTRUCTION1, ((WINDOW_WIDTH/2) - (instruction1.getWidth(INSTRUCTION1)/2)),
                                    INSTRUCTION1_y);
            Font instruction2 = new Font(FONT_FILENAME, INSTRUCTION_SIZE);
            instruction2.drawString(INSTRUCTION2, ((WINDOW_WIDTH/2) - (instruction2.getWidth(INSTRUCTION2)/2)),
                                    INSTRUCTION2_y);
        }

        // Enter GAME state once space bar is pressed
        if (input.wasPressed(Keys.SPACE)) {
            gameState = PLAY;
        }

        /* GAMEPLAY */
        if (gameState == PLAY) {
            // Check player movement
            // Create "destination point" and update player position if collision hasn't occurred
            Double xCoord = player.getPosition().x;
            Double yCoord = player.getPosition().y;


            if (input.isDown(Keys.LEFT)) {
                xCoord -= SPEED;
                player.setImage(PLAYER_LEFT);
            }
            if (input.isDown(Keys.RIGHT)) {
                xCoord += SPEED;
                player.setImage(PLAYER_RIGHT);
            }
            if (input.isDown(Keys.DOWN)) {
                yCoord += SPEED;
            }
            if (input.isDown(Keys.UP)) {
                yCoord -= SPEED;
            }

            Point destinationPoint = new Point(xCoord, yCoord);
            // Update player's position if player has NOT
            // collided with an object and is in bounds
            if (((destinationPoint.x > left) && (destinationPoint.x < right) &&
                 (destinationPoint.y > top) && (destinationPoint.y < bot)) &&
                 (!player.collisionCheck(player, destinationPoint, collisionBodies, num_assets))) {
                player.setPosition(destinationPoint);
            }

            // check loss
            if (player.getHealth() <= 0) {
                // exit game loop
                gameState = LOSE;
            }
            // check win
            if ((destinationPoint.x > WIN_POINT.x) && (destinationPoint.y > WIN_POINT.y)) {
                // exit game loop
                gameState = WIN;
            }

            // Display images
            BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
            player.getImage().drawFromTopLeft(player.getPosition().x, player.getPosition().y);
            for (int i=0; i<num_assets;i++) {
                collisionBodies[i].getImage().drawFromTopLeft(collisionBodies[i].getPosition().x,
                                                              collisionBodies[i].getPosition().y);
            }
            // display health bar
            this.printHealth(player);

        }

        // Loss message
        if (gameState == LOSE) {
            this.endScreen(gameState);
        }

        // Victory message
        if (gameState == WIN) {
            this.endScreen(gameState);
        }
    }

    /* Display methods */
    public void printHealth(KinematicBody player) {
        // Display health bar
        Colour colour;
        int healthPercentage = (int) Math.round(player.getHealth() * 100/player.getMaxHealth());

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
        Font healthBar = new Font(FONT_FILENAME, HEALTH_SIZE);
        String healthBarMsg = Integer.toString(healthPercentage) + "%";
        DrawOptions drawOptions = new DrawOptions();
        healthBar.drawString(healthBarMsg,
                HEALTH_BAR_POSITION.x,
                HEALTH_BAR_POSITION.y,
                drawOptions.setBlendColour(colour));
    }

    public void endScreen(int gameState) {
        // Display message on screen at end of game
        Font endMsg = new Font(FONT_FILENAME, DEFAULT_SIZE);

        if (gameState == WIN) {
            endMsg.drawString(WINNING_MSG, ((WINDOW_WIDTH / 2) - (endMsg.getWidth(WINNING_MSG) / 2)),
                    WINDOW_HEIGHT / 2);
        }
        else if (gameState == LOSE) {
            endMsg.drawString(LOSING_MSG, ((WINDOW_WIDTH / 2) - (endMsg.getWidth(LOSING_MSG) / 2)),
                    WINDOW_HEIGHT / 2);
        }
    }
}
