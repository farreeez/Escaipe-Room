package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nz.Objectives;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GameTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.controllers.roomclasses.Player;
import nz.ac.auckland.se206.controllers.roomclasses.Sprite;
import nz.ac.auckland.se206.controllers.roomclasses.World;

/** Controller class for the room view. */
public class RoomController {

  // Flag for moving the player forward
  public static boolean forward = false;

  // Flag for moving the player backward
  public static boolean backward = false;

  // Flag for moving the player left
  public static boolean left = false;

  // Flag for moving the player right
  public static boolean right = false;

  // Flag for rotating the player right
  public static boolean rotateRight = false;

  // Flag for rotating the player left
  public static boolean rotateLeft = false;

  /**
   * Calculate the distance between two points in 2D space.
   *
   * @param x1 The x-coordinate of the first point.
   * @param y1 The y-coordinate of the first point.
   * @param x2 The x-coordinate of the second point.
   * @param y2 The y-coordinate of the second point.
   * @return The Euclidean distance between the two points.
   */
  public static double distBetweenPoints(double x1, double y1, double x2, double y2) {
    return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
  }

  /**
   * Adjust the brightness of a color by a specified factor.
   *
   * @param color The original color.
   * @param factor The brightness adjustment factor.
   * @return The adjusted color.
   */
  private static Color adjustColorBrightness(Color color, double factor) {
    double red = (color.getRed() * factor);
    double green = (color.getGreen() * factor);
    double blue = (color.getBlue() * factor);

    // Ensure that the values are within the valid range (0-255)
    red = Math.min(1, Math.max(0, red));
    green = Math.min(1, Math.max(0, green));
    blue = Math.min(1, Math.max(0, blue));

    return new Color(red, green, blue, color.getOpacity());
  }

  // Object representing the player in the room
  @FXML private Pane room;
  @FXML private Text timerText;
  @FXML private Canvas canvas;
  @FXML private Text interact;
  @FXML private AnchorPane objectiveTab;
  @FXML private Text objOne;
  @FXML private Text objTwo;
  @FXML private Text objThree;
  @FXML private Text objFour;
  @FXML private ImageView forwardKey;
  @FXML private ImageView leftSideKey;
  @FXML private ImageView backwardsKey;
  @FXML private ImageView rightSideKey;

  @FXML private ImageView upKey;
  @FXML private ImageView downKey;
  @FXML private ImageView rightKey;
  @FXML private ImageView leftKey;
  @FXML private ImageView spaceKey;

  // Resolution for the room, calculated as 720 divided by 5
  private int res;

  private int resFactor = 6;

  private Player player;

  private double[] glowDoor = new double[2];

  // Array to store room image data
  private double[][] imageArray;

  // Array to store distances to walls from the player's perspective
  // Flag for allowing player forward movement
  private boolean mvFor = true;

  // Flag for allowing player backward movement
  private boolean mvBack = true;

  // Flag for allowing player right movement
  private boolean mvRight = true;

  // Flag for allowing player left movement
  private boolean mvLeft = true;

  // Flag for allowing player right rotation
  private boolean rtRight = true;

  // Flag for allowing player left rotation
  private boolean rtLeft = true;

  // Graphics context for drawing on the canvas
  private GraphicsContext gc1;

  // Array to store angles of sprites
  private double[][] spriteAngles;

  // List of sprites in the room
  private List<Sprite> sprites;

  private AnimationTimer animationTimer =
      new AnimationTimer() {
        @Override
        public void handle(long now) {
          startAnimation();
        }
      };

  private Timeline timeline0 =
      new Timeline(
          new KeyFrame(
              Duration.millis(50),
              e -> {
                if (GameState.room == true) {
                  // start the room
                  room.requestFocus();
                  animationTimer.start();
                } else {
                  // stop the room
                  spaceKey.setOpacity(0.1);
                  forwardKey.setOpacity(0.1);
                  backwardsKey.setOpacity(0.1);
                  rightSideKey.setOpacity(0.1);
                  leftSideKey.setOpacity(0.1);
                  upKey.setOpacity(0.1);
                  downKey.setOpacity(0.1);
                  rightKey.setOpacity(0.1);
                  leftKey.setOpacity(0.1);
                  animationTimer.stop();
                  forward = false;
                  backward = false;
                  left = false;
                  right = false;
                  rotateRight = false;
                  rotateLeft = false;
                }
              }));

  /**
   * Initializes the game or UI element.
   *
   * <p>This method is called to set up the initial state and configuration for the game or UI
   * element. It includes tasks such as setting objectives, configuring the game timer, initializing
   * graphics context, creating player and sprite objects, and starting any necessary animations.
   *
   * @see Objectives#setObj(Object, int)
   * @see GameTimer#addText(Text)
   * @see World#getSprites()
   */
  public void initialize() {

    Objectives.setObj(objOne, 0);
    Objectives.setObj(objTwo, 1);
    Objectives.setObj(objThree, 2);
    Objectives.setObj(objFour, 3);

    GameTimer.addText(timerText);

    res = Math.round(960 / resFactor);
    // res = 20;
    gc1 = canvas.getGraphicsContext2D();
    // Initialization code goes here
    timeline0.setCycleCount(Animation.INDEFINITE);
    timeline0.play();

    player = new Player(res);
    imageArray = player.ddaCaster();
    sprites = World.getsprites();
    arrangeSprites(sprites, player.getPosition());
    spriteAngles = new double[sprites.size()][5];
  }

  /**
   * Handles the click event within the game. Determines if there's a clickable sprite in the way
   * and takes appropriate action based on the clicked object. If a sprite is in the way, and it is
   * positioned in front of the player, and it's clickable, it performs actions related to that
   * sprite. Otherwise, it checks for doors or other clickable objects in the middle of the screen
   * and performs actions accordingly.
   *
   * @see Sprite
   */
  private void clicked() {
    boolean spriteInWay = false;
    int ray = 0;
    for (int i = spriteAngles.length - 1; i >= 0; i--) {
      if ((res - 1) / 2 >= spriteAngles[i][0]
          && (res - 1) / 2 <= spriteAngles[i][1]
          && sprites.get(i).isClickable()) {
        spriteInWay = true;
        ray = i;
        break;
      }
    }
    if (spriteInWay
        && isBehind((res - 1) / 2, spriteAngles[ray][2])
        && spriteAngles[ray][2] >= 0.85) {
      // code for clicking on player or object
      Sprite sprite = sprites.get(ray);
      if (sprite.isName("brainWorm")) {
        GameState.room = false;
        try {
          App.setRoot(SceneManager.getUiRoot(AppUi.PlantOpen));
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      } else if (sprite.isName("memory")) {
        GameState.room = false;
        try {
          App.setRoot(SceneManager.getUiRoot(AppUi.Memory));
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      } else if (sprite.isName("reaction")) {
        GameState.room = false;
        try {
          App.setRoot(SceneManager.getUiRoot(AppUi.Reaction));
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      } else if (sprite.isName("gameMaster")) {
        GameState.aiChat = true;
        GameState.room = false;
        try {
          App.setRoot(SceneManager.getUiRoot(AppUi.Chat));
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    } else {
      boolean open = false;
      // checks if the middle of the screen is hovering over a door
      if (imageArray[(res - 1) / 2][1] == -1) {
        open = true;
      } else if (imageArray[(res - 1) / 2][1] == -2) {
        GameState.room = false;
        if (GameState.snakeDead
            && GameState.isMemoryGameCompleted
            && GameState.isMemoryGameCompleted
            && GameState.isRiddleResolved) {
          GameState.won = true;
          GameState.ended = true;
          // switch to won screen
          try {
            App.setRoot(SceneManager.getUiRoot(AppUi.Won));
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        } else {
          GameState.riddleChat = true;
          try {
            App.setRoot(SceneManager.getUiRoot(AppUi.Chat));
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }
      }
      // if the middle is hovering over a door when the button is clicked then the door is
      // removed
      if (open) {
        int[][] worldMap = World.getMap();
        worldMap[(int) imageArray[(res - 1) / 2][5]][(int) imageArray[(res - 1) / 2][6]] = 0;
        World.setMap(worldMap);
      }
    }
  }

  /**
   * Starts the animation and renders the game scene on the canvas. This method initializes the
   * canvas, draws the floor and ceiling, walls, and sprites on the screen. It also handles player
   * interaction with objects and doors, and provides visual cues.
   *
   * @see Player
   * @see Sprite
   */
  private void startAnimation() {
    // Initialize the canvas
    int screenWidth = 965;
    List<Image> wallTextures = World.getWallTextures();
    List<Image> floorCeilingTextures = World.getFloorCeilingTextures();
    int heightOfFloor = (720) / 2;
    Image image;
    Image floor = floorCeilingTextures.get(0);
    PixelReader floorPixel = floor.getPixelReader();
    PixelReader imagePixel;
    imageArray = player.ddaCaster();

    // drawing floor and ceiling
    int textureHeight = 720;

    gc1.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

    // drawing floor and ceiling
    for (int i = 0; i < (int) Math.ceil(res / 2); i++) {
      int floorScreenPosY = heightOfFloor + i * resFactor;
      int ceilingScreenPosY = heightOfFloor - i * resFactor;
      // Steps is the width of the screen divided by the number of rays.
      double steps = (screenWidth) / (double) imageArray.length;
      Color floorColor = Color.YELLOW;

      // Iterate through each ray.
      for (int j = 0; j < imageArray.length; j++) {
        double currentDistance =
            textureHeight / (Math.cos(imageArray[j][2]) * resFactor * i * 2 * 0.8);
        double posX =
            imageArray[0][8] + currentDistance * Math.cos(Math.toRadians(imageArray[j][7]));
        double posY =
            imageArray[0][9] - currentDistance * Math.sin(Math.toRadians(imageArray[j][7]));

        // Check if the position is within the bounds of the floor texture.
        if (posY >= 0
            && posY < World.floorTexture.length
            && posX >= 0
            && posX < World.floorTexture[(int) posY].length) {
          if (World.floorTexture[(int) Math.floor(posY)][(int) Math.floor(posX)] == 1) {
            double ty = posY - Math.floor(posY);
            double tx = posX - Math.floor(posX);
            tx *= floor.getWidth();
            ty *= floor.getHeight();
            floorColor = floorPixel.getColor((int) Math.floor(tx), (int) Math.floor(ty));
            floorColor = changeOpacity(floorColor, 1);
          }
        } else {
          floorColor = Color.BLACK;
        }
        double factor = 1 / Math.pow(currentDistance, 0.9);
        gc1.setFill(adjustColorBrightness(floorColor, factor));
        // gc1.setFill(floorColor);
        gc1.fillRect(
            (int) Math.floor(j * steps), floorScreenPosY, (int) Math.ceil(steps), resFactor);
        gc1.fillRect(
            (int) Math.floor(j * steps), ceilingScreenPosY, (int) Math.ceil(steps), resFactor);
      }
    }

    // drawing walls
    for (int i = 0; i < imageArray.length; i++) {
      double originalDistance = imageArray[i][0];
      double factor = 0;
      if (imageArray[i][1] != 0) {
        factor = imageArray[i][3] / Math.pow(originalDistance, 0.9);
      }

      factor = Math.min(factor, 1);

      double distanceFactor = Math.cos(imageArray[i][2]) * originalDistance;

      distanceFactor *= 0.8;

      int height = (int) Math.round(720 / distanceFactor);

      int currentWallVal = (int) imageArray[i][1];
      if (currentWallVal < 0) {
        image = World.getDoor();
        imagePixel = image.getPixelReader();
        if (imageArray[i][5] == glowDoor[0] && imageArray[i][6] == glowDoor[1]) {
          factor = 1.3;
        }
      } else {
        if (currentWallVal == 0) {
          currentWallVal = 1;
        }
        image = wallTextures.get(currentWallVal - 1);
        imagePixel = image.getPixelReader();
      }

      // the width of the texture
      textureHeight = (int) image.getHeight();
      int textureWidth = (int) image.getWidth();
      double width = ((double) screenWidth) / imageArray.length;
      double xdirectionTexture = imageArray[i][4];
      if (imageArray[i][3] == 1) {
        if (imageArray[i][7] > 180) {
          xdirectionTexture = 1 - xdirectionTexture;
        }
      } else {
        if (imageArray[i][7] < 90 || imageArray[i][7] > 270) {
          xdirectionTexture = 1 - xdirectionTexture;
        }
      }

      // the factor to scale the texture by
      double textureFactorFraction = 0;
      if (height != 0) {
        textureFactorFraction = ((double) textureHeight) / height;
      }
      double startingHeight = (720 - height) / 2.0;
      double start = 0;
      // System.out.println(start);
      if (startingHeight < 0) {
        start = startingHeight * -1;
      }

      double heightScalingFactor = ((double) textureHeight) / (720.0 / resFactor);
      height = (int) Math.ceil(height / heightScalingFactor);
      start = (int) Math.floor(start / heightScalingFactor);

      for (int j = (int) Math.floor(start); j < height; j++) {
        try {
          Color color =
              imagePixel.getColor(
                  (int) Math.floor(xdirectionTexture * textureWidth),
                  (int) (j * textureFactorFraction * heightScalingFactor));
          // color = changeOpacity(color, 1);
          if (imageArray[i][1] != 0) {
            gc1.setFill(adjustColorBrightness(color, factor));
          } else {
            gc1.setFill(color);
          }
          gc1.fillRect(
              (int) Math.round(width * i),
              startingHeight + j * heightScalingFactor,
              (int) Math.ceil(width),
              heightScalingFactor + 1);
        } catch (Exception e) {
          System.out.println(e);
          System.out.println("---------------");
          System.out.println(xdirectionTexture * textureWidth);
          System.out.println(textureFactorFraction);
          System.out.println(heightScalingFactor);
          System.out.println();
        }
        if ((startingHeight + j * heightScalingFactor) > 720) {
          break;
        }
      }
    }

    // drawing sprites
    // List<Sprite> sprites = World.getsprites();
    arrangeSprites(sprites, player.getPosition());
    double currentPosX = imageArray[0][8];
    double currentPosY = imageArray[0][9];
    double playerAngle = Math.toRadians(imageArray[(res - 1) / 2][7]);
    for (int i = 0; i < sprites.size(); i++) {
      Sprite currentSprite = sprites.get(i);
      double[] spritePos = currentSprite.getpos();

      double spriteDirY = -(spritePos[0] - currentPosY);
      double spriteDirX = (spritePos[1] - currentPosX);
      double spriteAngle = findAngle(spriteDirY, spriteDirX, playerAngle);

      double angleDiff = fixAngle(Math.PI / 2 - spriteAngle);

      double orgDistanceFromPlayer =
          distBetweenPoints(currentPosX, currentPosY, spritePos[1], spritePos[0]);
      double factor = 1 / Math.pow(orgDistanceFromPlayer, 0.9);
      if (currentSprite.getGlow()) {
        factor = 1;
      }
      spriteAngles[i][2] = orgDistanceFromPlayer;
      double savedAngleDiff = angleDiff;

      if (isBiggerThan(spriteAngle, Math.PI / 2)) {
        angleDiff += Math.PI / 4;
      } else {
        angleDiff = Math.PI / 4 - angleDiff;
      }
      double middlePos = angleDiff * screenWidth / (Math.PI / 2);
      double distanceFromPlayer = orgDistanceFromPlayer;
      if (middlePos > 0 && middlePos < 960) {
        distanceFromPlayer =
            orgDistanceFromPlayer
                * Math.cos(imageArray[(int) Math.floor(middlePos / resFactor)][2]);
      }

      Image img = currentSprite.getTexture();
      PixelReader imgPixel = img.getPixelReader();
      double spriteHeight = (720 / (distanceFromPlayer * 0.8));
      double spriteProportion = ((double) img.getWidth()) / img.getHeight();
      double spriteWidth = (spriteProportion * spriteHeight);

      double adjustedSpriteWidth = spriteWidth / resFactor;
      double widthFactor = img.getWidth() / adjustedSpriteWidth;
      double heightFactor = img.getHeight() / spriteHeight;

      double spritePixel = middlePos - spriteWidth / 2;
      spritePixel = (int) Math.floor((((double) spritePixel) / screenWidth) * imageArray.length);
      double rightAngle = 0;
      double leftAngle = 0;

      if (spritePixel >= 0 && spritePixel <= screenWidth) {
        leftAngle = (int) spritePixel;
      }
      spritePixel = middlePos + adjustedSpriteWidth * resFactor - spriteWidth / 2;
      spritePixel = (int) Math.floor((((double) spritePixel) / screenWidth) * imageArray.length);
      if (spritePixel >= 0 && spritePixel <= imageArray.length) {
        rightAngle = (int) spritePixel;
      }
      double[] arr = {leftAngle, rightAngle, orgDistanceFromPlayer, spritePos[0], spritePos[1]};
      spriteAngles[i] = arr;
      double adjustedSpriteHeight = spriteHeight / resFactor;

      for (int j = 0; j < adjustedSpriteWidth; j++) {
        int screenX = (int) (middlePos + j * resFactor - spriteWidth / 2);
        int currentRay = (int) Math.floor((((double) screenX) / screenWidth) * imageArray.length);
        if ((savedAngleDiff <= Math.toRadians(45)) && (orgDistanceFromPlayer >= 0.85)) {
          if (isBehind(currentRay, orgDistanceFromPlayer)) {
            for (int k = 0; k < adjustedSpriteHeight; k++) {
              int tx = (int) Math.floor(j * widthFactor);
              int ty = (int) Math.floor(k * heightFactor * resFactor);
              Color color = imgPixel.getColor(tx, ty);
              color = adjustColorBrightness(color, factor);
              int screenY = (int) Math.ceil(((720 - spriteHeight) / 2) + k * resFactor);
              gc1.setFill(color);
              gc1.fillRect(screenX, screenY, resFactor, resFactor);
            }
          }
        }
      }
    }

    int[][] worldMap = World.getMap();
    int width = (int) Math.floor((double) screenWidth / 6);
    int height = (int) (((double) worldMap.length / worldMap[0].length) * width);
    int smallWidth = (int) Math.floor((double) width / worldMap[0].length);
    int smallHeight = (int) Math.floor((double) height / worldMap.length);

    for (int i = 0; i < worldMap.length; i++) {
      for (int j = 0; j < worldMap[i].length; j++) {
        Color color = Color.BLACK;
        if (worldMap[i][j] > 0) {
          color = Color.GRAY;
        } else if (worldMap[i][j] < 0) {
          color = Color.YELLOW;
        }
        gc1.setFill(color);
        gc1.fillRect(
            screenWidth - width + 1 + smallWidth * j, smallHeight * i, smallWidth, smallHeight);
      }
    }

    double[] position = player.getPosition();
    gc1.setFill(Color.WHITE);
    int radius = 16;
    int playerPosX = screenWidth - width + 1 + (int) Math.round(smallWidth * position[1]);
    int playerPosY = (int) Math.round(smallHeight * position[0]);
    gc1.fillOval(playerPosX, playerPosY, radius, radius);
    double angle = player.getAngle() + Math.PI / 2;
    int playerPeekX = playerPosX + (int) (radius / 2 * Math.sin(angle)) + radius / 4;
    int playerPeekY = playerPosY + (int) (radius / 2 * Math.cos(angle)) + radius / 4;
    gc1.setFill(Color.BLUE);
    gc1.fillOval(playerPeekX, playerPeekY, radius / 2, radius / 2);
    gc1.setFill(Color.BLUE);

    int crosairSize = 12;
    gc1.fillRect(
        screenWidth / 2 - crosairSize / 2, (720) / 2 - crosairSize / 2, crosairSize, crosairSize);
    boolean spriteInWay = false;
    int ray = 0;
    for (int i = spriteAngles.length - 1; i >= 0; i--) {
      if ((res - 1) / 2 >= spriteAngles[i][0]
          && (res - 1) / 2 <= spriteAngles[i][1]
          && sprites.get(i).isClickable()) {
        spriteInWay = true;
        ray = i;
        break;
      } else {
        sprites.get(i).setGlow(false);
      }
    }

    if (spriteInWay
        && isBehind((res - 1) / 2, spriteAngles[ray][2])
        && spriteAngles[ray][2] >= 0.85
        && sprites.get(ray).isClickable()) {
      interact.setText("Click space to interact with character.");
      sprites.get(ray).setGlow(true);
      glowDoor[0] = -1;
      glowDoor[1] = -1;
    } else if (imageArray[(res - 1) / 2][1] < 0) {
      interact.setText("Click space to open the door.");
      glowDoor[0] = imageArray[(res - 1) / 2][5];
      glowDoor[1] = imageArray[(res - 1) / 2][6];
    } else {
      interact.setText("");
      glowDoor[0] = -1;
      glowDoor[1] = -1;
    }
  }

  /**
   * Arranges the provided list of sprites based on their distances from a given position. The
   * method performs a bubble sort to order the sprites by distance, placing the closest sprites at
   * the beginning of the list.
   *
   * @param sprites The list of sprites to be arranged.
   * @param position The position relative to which the sprites' distances are calculated.
   * @see Sprite
   */
  private void arrangeSprites(List<Sprite> sprites, double[] position) {
    // Iterate through each sprite in the list.
    for (int i = 0; i < sprites.size(); i++) {
      // Store the current index in 'current'.
      int current = i;

      // Use a while loop to compare the distances of the current sprite and the previous sprite.
      // Swap sprites in the list if the current sprite is closer to the given position.
      while (current > 0
          && sprites.get(current).getDist(position) > sprites.get(current - 1).getDist(position)) {
        // Store the current sprite in 'saved'.
        Sprite saved = sprites.get(current);

        // Swap the current sprite with the previous sprite in the list.
        sprites.set(current, sprites.get(current - 1));
        sprites.set(current - 1, saved);

        // Move to the previous position in the list for further comparison.
        current--;
      }
    }
  }

  /**
   * Checks if the object at the specified index within the 'imageArray' is behind a certain
   * distance.
   *
   * @param index The index of the object within the 'imageArray.'
   * @param distance The distance to compare with the object's distance.
   * @return 'true' if the object is behind the specified distance, 'false' otherwise.
   */
  private boolean isBehind(int index, double distance) {
    if (index < 0 || index >= imageArray.length) {
      return false;
    }

    if (imageArray[index][0] < distance) {
      return false;
    }

    return true;
  }

  /**
   * Adjusts an angle to ensure it falls within the range [0, 2π] (0 to 360 degrees).
   *
   * @param angle The input angle to be adjusted.
   * @return The adjusted angle within the range [0, 2π].
   */
  private double fixAngle(double angle) {
    if (angle < 0) {
      angle += Math.PI * 2;
    }

    if (angle > Math.PI) {
      angle = Math.PI * 2 - angle;
    }

    return angle;
  }

  /**
   * Compares two angles to determine if one angle is greater than the other, considering the full
   * circle (2π).
   *
   * @param spriteAngle The angle to be compared (in radians).
   * @param currentAngle The reference angle for comparison (in radians).
   * @return 'true' if spriteAngle is greater than currentAngle, 'false' otherwise.
   */
  private boolean isBiggerThan(double spriteAngle, double currentAngle) {
    // Initialize a boolean variable 'bigger' as false.
    boolean bigger = false;

    // Ensure that both angles are within the range of 0 to 2π (full circle).
    spriteAngle = spriteAngle % (Math.PI * 2);
    currentAngle = currentAngle % (Math.PI * 2);

    // Calculate the quadrant (multiple of 90 degrees) for both angles.
    int multiple90Sprite = (int) (spriteAngle / (Math.PI * 0.5));
    int multiple90Current = (int) (currentAngle / (Math.PI * 0.5));

    // Check if both angles are in the same quadrant and spriteAngle is less than currentAngle.
    if (multiple90Sprite == multiple90Current && spriteAngle < currentAngle) {
      // If the conditions are met, set 'bigger' to true.
      bigger = true;
    } else {
      // If the conditions are not met in the same quadrant, check the following conditions:
      if (multiple90Current > 0 && multiple90Sprite < multiple90Current) {
        // If spriteAngle is in a previous quadrant and currentAngle is not in the first quadrant,
        // set 'bigger' to true.
        bigger = true;
      } else if (multiple90Sprite == 3) {
        // If spriteAngle is in the fourth quadrant (3 * 90 degrees), set 'bigger' to true.
        bigger = true;
      }
    }

    // Return the 'bigger' boolean indicating whether spriteAngle is greater than currentAngle.
    return bigger;
  }

  /**
   * Calculates the angle between a sprite's direction (given by spriteDirY and spriteDirX) and a
   * reference angle.
   *
   * @param spriteDirY The vertical component of the sprite's direction vector.
   * @param spriteDirX The horizontal component of the sprite's direction vector.
   * @param currentAngle The reference angle (in radians) for comparison.
   * @return The angle (in radians) between the sprite's direction and the reference angle.
   */
  private double findAngle(double spriteDirY, double spriteDirX, double currentAngle) {
    double fakeSpriteDirX = Math.abs(spriteDirX);
    double fakeSpriteDirY = Math.abs(spriteDirY);
    double angle = Math.atan(fakeSpriteDirY / fakeSpriteDirX);

    if (spriteDirX < 0 && spriteDirY > 0) {
      angle = Math.PI - angle;
    } else if (spriteDirX < 0 && spriteDirY < 0) {
      angle += Math.PI;
    } else if (spriteDirX > 0 && spriteDirY < 0) {
      angle = 2 * Math.PI - angle;
    }
    // angle %= Math.PI*2;
    angle += (Math.PI / 2 - currentAngle);

    if (angle < 0) {
      angle += Math.PI * 2;
    }

    return angle;
  }

  /**
   * Changes the opacity (alpha) of a given color and returns a new color with the modified opacity.
   *
   * @param color The original color to modify.
   * @param opacity The new opacity value, ranging from 0.0 (completely transparent) to 1.0 (fully
   *     opaque).
   * @return A new color with the modified opacity.
   */
  private Color changeOpacity(Color color, double opacity) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    KeyCode keyCode = event.getCode();
    if (keyCode == KeyCode.RIGHT) {
      rotateRight = true;
      rightKey.setOpacity(1);
      if (rtRight) {
        player.rotateRight();
        rtRight = false;
      }
    } else if (keyCode == KeyCode.LEFT) {
      rotateLeft = true;
      leftKey.setOpacity(1);
      if (rtLeft) {
        player.rotateLeft();
        rtLeft = false;
      }
    } else if (keyCode == KeyCode.W || keyCode == KeyCode.UP) {
      forward = true;
      if (keyCode == KeyCode.W) {
        forwardKey.setOpacity(1);
      } else if (keyCode == KeyCode.UP) {
        upKey.setOpacity(1);
      }
      if (mvFor) {
        player.moveForward();
        mvFor = false;
      }
    } else if (keyCode == KeyCode.S || keyCode == KeyCode.DOWN) {
      backward = true;
      if (keyCode == KeyCode.S) {
        backwardsKey.setOpacity(1);
      } else if (keyCode == KeyCode.DOWN) {
        downKey.setOpacity(1);
      }
      if (mvBack) {
        player.moveBack();
        mvBack = false;
      }
    } else if (keyCode == KeyCode.A) {
      left = true;
      leftSideKey.setOpacity(1);
      if (mvLeft) {
        player.moveLeft();
        mvLeft = false;
      }
    } else if (keyCode == KeyCode.D) {
      right = true;
      rightSideKey.setOpacity(1);
      if (mvRight) {
        player.moveRight();
        mvRight = false;
      }
    } else if (keyCode == KeyCode.SPACE) {
      clicked();
      spaceKey.setOpacity(1);
    }
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    KeyCode keyCode = event.getCode();
    if (keyCode == KeyCode.RIGHT) {
      rotateRight = false;
      rtRight = true;
      rightKey.setOpacity(0.2);
    } else if (keyCode == KeyCode.LEFT) {
      rotateLeft = false;
      rtLeft = true;
      leftKey.setOpacity(0.2);
    } else if (keyCode == KeyCode.W || keyCode == KeyCode.UP) {
      forward = false;
      mvFor = true;
      if (keyCode == KeyCode.W) {
        forwardKey.setOpacity(0.2);
      } else if (keyCode == KeyCode.UP) {
        upKey.setOpacity(0.2);
      }
    } else if (keyCode == KeyCode.S || keyCode == KeyCode.DOWN) {
      backward = false;
      mvBack = true;
      if (keyCode == KeyCode.S) {
        backwardsKey.setOpacity(0.2);
      } else if (keyCode == KeyCode.DOWN) {
        downKey.setOpacity(0.2);
      }
    } else if (keyCode == KeyCode.A) {
      left = false;
      mvLeft = true;
      leftSideKey.setOpacity(0.2);
    } else if (keyCode == KeyCode.D) {
      right = false;
      mvRight = true;
      rightSideKey.setOpacity(0.2);
    } else if (keyCode == KeyCode.SPACE) {
      spaceKey.setOpacity(0.2);
    }
  }
}
