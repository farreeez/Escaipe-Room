package nz.ac.auckland.se206.controllers.roomclasses;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;

/**
 * The World class represents the game world in the escape room scenario. It contains the game
 * elements, such as walls, sprites, and other interactive objects, and provides methods for
 * initializing and managing the game world.
 */
public class World {
  // map array
  private static int[][] worldMap = {
    // 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18
    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 0
    {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, // 1
    {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, // 2
    {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, // 3
    {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, // 4
    {1, 1, 1, -1, 1, 1, 1, 1, 1, -1, 1, 1, 1, 1, 1, -1, 1, 1, 1}, // 5
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 6
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2}, // 7
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 8
    {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, // 9
    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, // 10
  };

  public static int[][] floorTexture =
      new int[worldMap.length + 5][worldMap[0].length + 5]; // floor array

  private static List<Image> wallTextures = new ArrayList<>();
  private static Image door;
  private static List<Image> floorCeilingTextures = new ArrayList<>();
  private static boolean unloaded =
      true; // shows if the textures have been loaded or not so that they are not
  // needlessly
  // reloaded
  private static List<Sprite> sprites = new ArrayList<>();

  /*Loads the textures for the game.*/
  private static void loadTextures() {
    if (unloaded) {
      try {
        // adds all the textures needed for the game
        wallTextures.add(
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/wallTextures/wallex2.png"));
        wallTextures.add(
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/wallTextures/wallexright.png"));
        wallTextures.add(
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/wallTextures/5.jpg"));
        wallTextures.add(
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/sprites/desk.png"));
        // Texture for the door which is 1:1
        door =
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/wallTextures/newDoor1.png");
        // adds all the floor and ceiling textures
        floorCeilingTextures.add(
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/floorAndCeilingTextures/floorTile.png"));
        // Add snake texture
        Image brainWorm =
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/sprites/brainWorm.png");
        // Added f1 image for sprite
        Image f1 =
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/sprites/brainTime.png");
        // Added brain image for sprite
        Image brain =
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/sprites/brain.png");
        // Added gameMaster image for sprite
        Image gameMasterImg =
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/sprites/nasserShort.png");
        Image bulb =
            new Image(
                "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                    + "/textures/sprites/bulb.png");
        Sprite gameMaster = new Sprite("gameMaster", 16.5, 9, gameMasterImg, true);
        Sprite gameMaster1 = new Sprite("gameMaster", 17.5, 4, gameMasterImg, true);
        Sprite gameMaster2 = new Sprite("gameMaster", 11.5, 4, gameMasterImg, true);
        Sprite gameMaster3 = new Sprite("gameMaster", 5.5, 4, gameMasterImg, true);
        Sprite brainWormGame = new Sprite("brainWorm", 15.5, 2, brainWorm, true);
        Sprite memory = new Sprite("memory", 9.5, 2, brain, true);
        Sprite reaction = new Sprite("reaction", 3.5, 2, f1, true);
        Sprite bulb1 = new Sprite("bulb", 3.5, 2.5, bulb, false);
        Sprite bulb5 = new Sprite("bulb", 9.5, 2.5, bulb, false);
        Sprite bulb6 = new Sprite("bulb", 15.5, 2.5, bulb, false);
        Sprite bulb2 = new Sprite("bulb", 8.5, 8, bulb, false);
        Sprite bulb3 = new Sprite("bulb", 13.5, 8, bulb, false);
        Sprite bulb4 = new Sprite("bulb", 3.5, 8, bulb, false);

        // adds sprites to the list

        sprites.add(gameMaster);
        sprites.add(gameMaster1);
        sprites.add(gameMaster2);
        sprites.add(gameMaster3);
        sprites.add(gameMaster);
        sprites.add(brainWormGame);
        sprites.add(memory);
        sprites.add(reaction);
        sprites.add(bulb1);
        sprites.add(bulb2);
        sprites.add(bulb3);
        sprites.add(bulb4);
        sprites.add(bulb5);
        sprites.add(bulb6);

        // prints out error if there is one
      } catch (Exception e) {
        System.out.println(e);
        System.out.println("--------------------------------------------------");
        String currentDirectory = System.getProperty("user.dir");

        System.out.println("Current Working Directory: " + currentDirectory);
      }
      unloaded = false;
    }

    for (int i = 0; i < worldMap.length + 5; i++) {
      for (int j = 0; j < worldMap[0].length + 5; j++) {
        floorTexture[i][j] = 1;
      }
    }
  }

  /**
   * Retrieves the image of the door used in the game.
   *
   * @return The Image object representing the door used in the game.
   */
  public static Image getDoor() {
    return door;
  }

  /**
   * Retrieves a list of sprites after loading textures.
   *
   * @return A List of Sprite objects.
   */
  public static List<Sprite> getsprites() {
    loadTextures();
    return sprites;
  }

  /**
   * Retrieves the world map, which is a 2D array representing the game map.
   *
   * @return The 2D array representing the game map.
   */
  public static int[][] getMap() {
    return worldMap;
  }

  /**
   * Retrieves a list of wall textures after loading textures.
   *
   * @return A List of Image objects representing wall textures.
   */
  public static List<Image> getWallTextures() {
    loadTextures();
    return wallTextures;
  }

  /**
   * Retrieves a list of floor and ceiling textures after loading textures.
   *
   * @return A List of Image objects representing floor and ceiling textures.
   */
  public static List<Image> getFloorCeilingTextures() {
    loadTextures();
    return floorCeilingTextures;
  }

  /**
   * Sets the world map to the provided 2D array.
   *
   * @param worldMap2 The 2D array to set as the game map.
   */
  public static void setMap(int[][] worldMap2) {
    worldMap = worldMap2;
  }
}
