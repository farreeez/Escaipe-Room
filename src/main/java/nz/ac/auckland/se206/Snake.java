package nz.ac.auckland.se206;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * The Snake class represents a snake game character with methods to manage its movement and
 * actions.
 */
public class Snake {
  private int direction;
  private int[] applePosition = {13, 10};
  private ArrayList<Integer> positions = new ArrayList<>(Arrays.asList(-2, -2));
  private int applEaten = 0;

  /** Constructs a new Snake object and initializes its initial position and direction. */
  public Snake() {
    this.direction = 1;
    this.positions.addAll(Arrays.asList(4, 10, 3, 10));
  }

  /**
   * Generates a new random position for the apple to appear on the game board.
   *
   * @return An ArrayList of available positions for the apple.
   */
  private ArrayList<int[]> changeApplePosition() {
    ArrayList<int[]> availablePositions = new ArrayList<>();

    for (int i = 0; i < 14; i++) {
      for (int j = 0; j < 14; j++) {
        int[] position = {i, j};
        availablePositions.add(position);
      }
    }

    // Remove the positions that the snake is currently in
    for (int i = 0; i < this.positions.size() / 2 - 1; i++) {
      int j = (i + 1) * 2;
      for (int k = 0; k < availablePositions.size(); k++) {
        if (availablePositions.get(k)[0] == this.positions.get(j)
            && availablePositions.get(k)[1] == this.positions.get(j + 1)) {
          availablePositions.remove(k);
        }
      }
    }
    return availablePositions;
  }

  /** Handles the snake eating the apple and growing in length. */
  public void eatsApple() {
    if (this.positions.get(2) == this.applePosition[0]
        && this.positions.get(3) == this.applePosition[1]) {
      // Extend the snake's body
      for (int i = 0; i < 2; i++) {
        if (this.positions.get(this.positions.size() - 2)
            == this.positions.get(this.positions.size() - 4)) {
          // Extend horizontally
          this.positions.add(this.positions.get(this.positions.size() - 2));
          int difference =
              this.positions.get(this.positions.size() - 2)
                  - this.positions.get(this.positions.size() - 4);
          this.positions.add(this.positions.get(this.positions.size() - 2) + difference);
        } else {
          // Extend vertically
          int difference =
              this.positions.get(this.positions.size() - 2)
                  - this.positions.get(this.positions.size() - 4);
          this.positions.add(this.positions.get(this.positions.size() - 2) + difference);
          this.positions.add(this.positions.get(this.positions.size() - 2));
        }
      }
      // Generate a new apple position
      ArrayList<int[]> availablePositions = this.changeApplePosition();
      Random random = new Random();
      int[] newApplePosititon = availablePositions.get(random.nextInt(availablePositions.size()));
      this.applePosition[0] = newApplePosititon[0];
      this.applePosition[1] = newApplePosititon[1];
      applEaten++;
    }
  }

  /**
   * Returns the current position of the apple on the game board.
   *
   * @return An array representing the apple's position [x, y].
   */
  public int[] getApplePosition() {
    return this.applePosition;
  }

  /**
   * Changes the direction of the snake's movement.
   *
   * @param direction The new direction (1: right, -1: left, 2: down, -2: up).
   */
  public void changeDirection(int direction) {
    this.direction = direction;
  }

  /** Moves the snake based on its current direction. */
  public void move() {
    if (this.direction == 1) {
      // Move right
      positions.addAll(2, Arrays.asList(positions.get(2) + 1, positions.get(3)));
    } else if (this.direction == -1) {
      // Move left
      positions.addAll(2, Arrays.asList(positions.get(2) - 1, positions.get(3)));
    } else if (this.direction == 2) {
      // Move down
      positions.addAll(2, Arrays.asList(positions.get(2), positions.get(3) + 1));
    } else if (this.direction == -2) {
      // Move up
      positions.addAll(2, Arrays.asList(positions.get(2), positions.get(3) - 1));
    }
    positions.remove(positions.size() - 1);
    positions.remove(positions.size() - 1);
  }

  /**
   * Returns the current position of the snake's body.
   *
   * @return An ArrayList of integers representing the snake's body positions.
   */
  public ArrayList<Integer> getPosition() {
    return this.positions;
  }

  /**
   * Returns the current direction of the snake.
   *
   * @return The current direction (1: right, -1: left, 2: down, -2: up).
   */
  public int getDirection() {
    return this.direction;
  }

  /**
   * Returns the number of apples eaten by the snake.
   *
   * @return The number of apples eaten.
   */
  public int getAppleEaten() {
    return applEaten;
  }
}
