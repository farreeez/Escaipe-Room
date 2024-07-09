package nz.ac.auckland.se206.controllers.roomclasses;

import java.util.Timer;
import java.util.TimerTask;
import nz.ac.auckland.se206.controllers.RoomController;

/**
 * The Player class represents a player character in the game. It handles player-related
 * functionality such as movement, interaction, and viewpoint.
 */
public class Player {
  private double posY = 8.5;
  private double posX = 5.23; // x and y start position
  private double angle = Math.toRadians(0.112); // the starting angle that the player is facing
  private int res; // number of rays the player projects
  private double fov = 90;
  private double inc; // the angle increment for each ray
  private double[][] viewPlane; // array holding values regarding each ray projected.

  /**
   * Constructs a Player object with the specified resolution and field of view (FOV).
   *
   * <p>This constructor initializes a Player object with the given resolution and FOV. It
   * calculates the view plane by dividing the FOV into equal angles and creating direction values
   * for each ray based on these angles.
   *
   * @param res The resolution, specifying the number of rays or view angles.
   * @see #changeDirection()
   */
  public Player(int res) {
    int pos = 0;
    this.res = res;
    inc = Math.toRadians(fov / (this.res));
    viewPlane = new double[this.res][3];
    // loads all the angles before the middle ray and makes them bigger than the angle of the middle
    // ray
    for (int i = (this.res - 1) / 2; i >= 1; i--) {
      viewPlane[pos][0] = angle + inc * i;
      pos++;
    }
    // sets the angle of the middle ray
    viewPlane[(this.res - 1) / 2][0] = angle;
    for (int i = 0; i < (this.res - 1) / 2; i++) {
      viewPlane[i + 1 + (this.res - 1) / 2][0] = angle - inc * (i + 1);
    }
    // creates direction values for each ray from their angles
    changeDirection();
  }

  /** Changes the direction vector according to the angle. */
  private void changeDirection() {
    // changeDirection(Math.PI / 4 + Math.PI);
    for (int i = 0; i < viewPlane.length; i++) {
      viewPlane[i][0] =
          viewPlane[i][0] - Math.floor(viewPlane[i][0] / (2 * Math.PI)) * (2 * Math.PI);
      if (viewPlane[i][0] > Math.PI || viewPlane[i][0] < 0) {
        viewPlane[i][1] = -1;
      } else {
        viewPlane[i][1] = 1;
      }
      viewPlane[i][2] = viewPlane[i][1] / Math.tan(viewPlane[i][0]);
    }
  }

  /**
   * Gets the current position of the player as an array of doubles.
   *
   * @return An array containing the Y-coordinate and X-coordinate of the player, in that order.
   */
  public double[] getPosition() {
    double[] position = {posY, posX};
    return position;
  }

  /**
   * Gets the angle that the player is currently facing.
   *
   * @return The angle in radians.
   */
  public double getAngle() {
    return viewPlane[(res - 1) / 2][0];
  }

  /**
   * Casts a ray using the Digital Differential Analyzer (DDA) algorithm.
   *
   * @param i The index of the ray.
   * @param orgPosX The original X-coordinate position for the ray.
   * @param orgPosY The original Y-coordinate position for the ray.
   * @return An array of raycasting information including distances, angles, and positions.
   */
  private double[] ddaCaster(int i, double orgPosX, double orgPosY) {
    double[] imageArray = new double[11];
    double hypX = 1000000000; // the hypotenuse for the xSteps
    double hypY = 1000000000; // the hypotenuse for the ySteps
    double verticalStep = 0;
    double horizontalStep = 0;
    double horizontalValue = 0;
    double verticalValue = 0;
    int roundingValue = 32;
    double posX = orgPosX * roundingValue;
    double posY = orgPosY * roundingValue;
    boolean noBoundary = true;
    int count = 0;
    int iteration = 0;
    int y = 0;
    int x = 0;
    int[][] worldMap = World.getMap();
    // viewPlane[i][1] is dirY
    // viewPlane[i][2] is dirX
    while (noBoundary && count < 2000) {
      // code for the ray to calculate the x boundaries
      boolean allowed = true;
      if (viewPlane[i][1] != 0) {
        if (count == 0) {
          hypX = 0;
        }
        if (hypX <= hypY) {
          // if the position of the player is not on the edges
          if (count == 0) {
            // calculates the length of the step that is needed to reach the closest edge.
            double posYfloor = Math.floor(posY);
            if (viewPlane[i][1] > 0) {
              verticalStep = -(posY - posYfloor);
            } else {
              verticalStep = (1 - (posY - posYfloor));
            }
          } else {
            // adds or subtracts one from verticalStep depending on the direction of
            // viewPlane[i][1] to reach
            // the next edge
            verticalStep += getSign(viewPlane[i][1]) * -1;
          }
          // calculates the hypotinuse and the x factor that the line moves by.
          hypX =
              Math.abs(verticalStep)
                  * Math.sqrt(1 + Math.pow(viewPlane[i][2] / viewPlane[i][1], 2))
                  / roundingValue;
          allowed =
              false; // this is so that if verticalStep is calculated causing hypX to be bigger
          // horizontalStep
          // does
          // not get erroneously calculated
        }
      }

      if (viewPlane[i][2] != 0) {
        if (count == 0) {
          hypY = 0; // makes hypY 0  so that the step is calculated
        }
        if ((hypY <= hypX && allowed) || iteration == 0) {
          if (iteration == 0) {
            // calculates the step needed to reach the closest edge
            double posXfloor = Math.floor(posX);
            if (viewPlane[i][2] < 0) {
              horizontalStep = -(posX - posXfloor);
            } else {
              horizontalStep = 1 - (posX - posXfloor);
            }
          } else {
            // adds or subtracts one from horizontalStep depending on the direction of
            // viewPlane[i][2] to reach
            // the next edge
            horizontalStep += getSign(viewPlane[i][2]);
          }
          hypY =
              Math.abs(horizontalStep)
                  * Math.sqrt(1 + Math.pow(viewPlane[i][1] / viewPlane[i][2], 2))
                  / roundingValue;
          iteration++;
        }
      }

      if (hypX < hypY) {
        verticalValue = verticalStep;
        imageArray[3] = 1;
      } else if (hypY < hypX) {
        horizontalValue = horizontalStep;
        imageArray[3] = 0.5;
      }

      // gets the position of x and y
      y = (int) Math.floor((verticalValue + posY) / roundingValue);
      x = (int) Math.floor((horizontalValue + posX) / roundingValue);

      // if the square is not empty (doesn't equal 0) then the loop is stopped
      if (x >= 0 && y >= 0 && y < worldMap.length && x < worldMap[y].length) {
        if (worldMap[y][x] != 0) {
          noBoundary = false;
        }
      }
      count++;
    }

    imageArray[0] = Math.min(hypX, hypY);

    // gets the exact fraction that the ray has hit
    if (imageArray[3] == 0.5) {
      // y
      double opposite = Math.sin(viewPlane[i][0]) * imageArray[0];
      imageArray[4] = (opposite - orgPosY) - Math.floor(opposite - orgPosY);
    } else {
      // x
      double adjacent = Math.cos(viewPlane[i][0]) * imageArray[0];
      imageArray[4] = (adjacent + orgPosX) - Math.floor(adjacent + orgPosX);
    }

    imageArray[7] = (viewPlane[i][0] * 180) / Math.PI; // angle in degrees
    imageArray[8] = orgPosX; // exact x position
    imageArray[9] = orgPosY; // exact y position
    imageArray[5] = y; // rounded y position
    imageArray[6] = x; // rounded y position

    if (x >= 0 && y >= 0 && y < worldMap.length && x < worldMap[y].length) {
      imageArray[1] = worldMap[y][x]; // colour value
    }

    // angle difference for fixing the fisheye effect
    imageArray[2] = viewPlane[(res - 1) / 2][0] - viewPlane[i][0];

    // corrects the angle so that it is always positive
    if (imageArray[2] < 0) {
      imageArray[2] += 2 * Math.PI;
    } else if (imageArray[2] > 2 * Math.PI) {
      imageArray[2] -= 2 * Math.PI;
    }

    return imageArray;
  }

  /**
   * Casts rays using the Digital Differential Analyzer (DDA) algorithm for each angle in the view
   * plane.
   *
   * @return A 2D array containing raycasting information for each ray projected by the player.
   */
  public double[][] ddaCaster() {
    double[][] imageArray = new double[res][5];

    for (int i = 0; i < viewPlane.length; i++) {
      imageArray[i] = ddaCaster(i, this.posX, this.posY);
    }
    return imageArray;
  }

  /**
   * Changes the angle of the rays depending on the mouse movement.
   *
   * @param movement The mouse movement factor to adjust the angle.
   */
  public void mouseAim(double movement) {
    for (int i = 0; i < viewPlane.length; i++) {
      viewPlane[i][0] += (Math.PI / 100) * movement * 0.8;
    }
    changeDirection();
  }

  /**
   * Gets the sign of a number.
   *
   * @param num The number to determine the sign of.
   * @return 1 if the number is positive, -1 if the number is negative.
   */
  private int getSign(double num) {
    // Gets the sign of a number if it is positive or negative

    if (num < 0) {
      return -1;
    } else {
      return 1;
    }
  }

  /** Rotates the angle to the right by continuously updating the angle. */
  public void rotateRight() {
    // Rotates the angle to the right
    Timer timer = new Timer();
    // New timer object
    TimerTask task =
        new TimerTask() {
          @Override
          // Runs the code every 5 milliseconds
          public void run() {
            // If the rotateRight boolean is true, then the code runs
            if (RoomController.rotateRight) {
              for (int i = 0; i < viewPlane.length; i++) {
                viewPlane[i][0] -= 0.01;
              }
              // Changes the direction of the rays
              changeDirection();
            } else {
              timer.cancel();
            }
          }
        };
    timer.scheduleAtFixedRate(task, 0, 5);
  }

  /** Rotates the angle to the left by continuously updating the angle. */
  public void rotateLeft() {
    // Rotates the angle to the left
    Timer timer = new Timer();
    // New timer object
    TimerTask task =
        new TimerTask() {
          @Override
          // Runs the code every 5 milliseconds
          public void run() {
            if (RoomController.rotateLeft) {
              for (int i = 0; i < viewPlane.length; i++) {
                viewPlane[i][0] += 0.01;
              }
              // Changes the direction of the rays
              changeDirection();
            } else {
              timer.cancel();
            }
          }
        };
    timer.scheduleAtFixedRate(task, 0, 5);
  }

  /** Moves the player forward by continuously updating the player's position. */
  public void moveForward() {
    // Moves the player forward
    Timer timer = new Timer();
    // New timer object
    TimerTask task =
        new TimerTask() {
          @Override
          // Runs the code every 5 milliseconds
          public void run() {
            if (RoomController.forward) {
              double angle = viewPlane[(res - 1) / 2][0];
              move(angle);
            } else {
              // If the forward boolean is false, then the timer is canceled
              timer.cancel();
            }
          }
        };
    timer.scheduleAtFixedRate(task, 0, 5);
  }

  /** Moves the player backward by continuously updating the player's position. */
  public void moveBack() {
    Timer timer = new Timer();
    TimerTask task =
        new TimerTask() {
          @Override
          public void run() {
            if (RoomController.backward) {
              // Reverts the angle of the middle angle
              double angle = viewPlane[(res - 1) / 2][0] + Math.PI;
              move(angle);
            } else {
              timer.cancel();
            }
          }
        };
    timer.scheduleAtFixedRate(task, 0, 5);
  }

  /** Moves the player to the right by continuously updating the player's position. */
  public void moveRight() {
    Timer timer = new Timer();
    TimerTask task =
        new TimerTask() {
          @Override
          public void run() {
            if (RoomController.right) {
              // Minuses 90 degrees from the middle angle
              double angle = viewPlane[(res - 1) / 2][0] - Math.PI / 2;
              move(angle);
            } else {
              timer.cancel();
            }
          }
        };
    timer.scheduleAtFixedRate(task, 0, 5);
  }

  /**
   * Initiates a leftward movement by periodically updating the player's view angle. The method
   * creates a Timer and TimerTask to repeatedly adjust the view angle to simulate a leftward
   * movement.
   */
  public void moveLeft() {
    Timer timer = new Timer();
    TimerTask task =
        new TimerTask() {
          @Override
          public void run() {
            if (RoomController.left) {
              // adds 90 degrees to the middle angle
              double angle = viewPlane[(res - 1) / 2][0] + Math.PI / 2;
              move(angle);
            } else {
              timer.cancel();
            }
          }
        };
    timer.scheduleAtFixedRate(task, 0, 5);
  }

  /**
   * Moves the player's position based on the specified angle.
   *
   * @param angle The angle that determines the direction of movement.
   */
  private void move(double angle) {
    int[][] worldMap = World.getMap();
    double speed = 0.02;
    angle = angle - Math.floor(angle / (2 * Math.PI)) * (2 * Math.PI);
    double posX = this.posX;
    double posY = this.posY;
    if (angle >= 0 && angle <= Math.PI / 2) {
      posY -= Math.abs(Math.sin(angle) * speed);
      posX += Math.abs(Math.cos(angle) * speed);
    } else if (angle > Math.PI / 2 && angle <= Math.PI) {
      posY -= Math.abs(Math.sin(Math.PI - angle) * speed);
      posX -= Math.abs(Math.cos(Math.PI - angle) * speed);
    } else if (angle > Math.PI && angle <= (3 * Math.PI) / 2) {
      posY += Math.abs(Math.sin(angle - Math.PI) * speed);
      posX -= Math.abs(Math.cos(angle - Math.PI) * speed);
    } else {
      posY += Math.abs(Math.sin(2 * Math.PI - angle) * speed);
      posX += Math.abs(Math.cos(2 * Math.PI - angle) * speed);
    }

    int y = (int) Math.floor(posY);
    int x = (int) Math.floor(posX);

    int currentX = (int) Math.floor(this.posX);
    // if the position of the player is not on the edges
    if (posY >= 0) {
      if (y < worldMap.length && worldMap[y][currentX] == 0) {
        this.posY = posY;
      }
    }
    // if the position of the player is not on the edges
    if (posX >= 0
        && x < worldMap[(int) Math.floor(this.posY)].length
        && worldMap[(int) Math.floor(this.posY)][x] == 0) {
      this.posX = posX;
    }
  }
}
