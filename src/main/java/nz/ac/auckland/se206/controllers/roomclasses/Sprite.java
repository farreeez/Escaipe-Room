package nz.ac.auckland.se206.controllers.roomclasses;

import javafx.scene.image.Image;
import nz.ac.auckland.se206.controllers.RoomController;

/** The `Sprite` class represents a game sprite, including its position, image, and name. */
public class Sprite {
  private double posX; // X-coordinate position of the sprite
  private double posY; // Y-coordinate position of the sprite
  private Image sprite; // Image representing the sprite
  private String name; // Name of the sprite
  private boolean clickable;
  private boolean glow;

  /**
   * Constructs a `Sprite` object with a given name, position, image, and clickability.
   *
   * @param name The name of the sprite.
   * @param posX The X-coordinate position of the sprite.
   * @param posY The Y-coordinate position of the sprite.
   * @param sprite The image representing the sprite.
   * @param clickable Indicates if the sprite is clickable by the player.
   */
  public Sprite(String name, double posX, double posY, Image sprite, boolean clickable) {
    this.posX = posX;
    this.posY = posY;
    this.sprite = sprite;
    this.name = name;
    this.clickable = clickable;
  }

  /**
   * Checks if the sprite is clickable.
   *
   * @return `true` if the sprite is clickable, `false` otherwise.
   */
  public boolean isClickable() {
    return clickable;
  }

  /**
   * Gets the current position of the sprite as an array of doubles.
   *
   * @return An array containing the Y-coordinate and X-coordinate of the sprite, in that order.
   */
  public double[] getpos() {
    double[] pos = {posY, posX};
    return pos;
  }

  /**
   * Sets the X-coordinate position of the sprite.
   *
   * @param x The new X-coordinate position for the sprite.
   */
  public void setPosX(double x) {
    posX = x;
  }

  /**
   * Sets the Y-coordinate position of the sprite.
   *
   * @param y The new Y-coordinate position for the sprite.
   */
  public void setPosY(double y) {
    posY = y;
  }

  /**
   * Calculates the distance between the sprite and a player's position.
   *
   * @param playerPosition An array containing the Y-coordinate and X-coordinate of the player.
   * @return The distance between the sprite and the player.
   */
  public double getDist(double[] playerPosition) {
    double positionX = playerPosition[1];
    double positionY = playerPosition[0];

    // Utilize a method from RoomController to calculate the distance
    return RoomController.distBetweenPoints(posX, posY, positionX, positionY);
  }

  /**
   * Gets the image (texture) of the sprite.
   *
   * @return The image representing the sprite.
   */
  public Image getTexture() {
    return sprite;
  }

  /**
   * Checks if the sprite has a particular name.
   *
   * @param str The name to compare with the sprite's name.
   * @return `true` if the sprite's name matches the provided name, `false` otherwise.
   */
  public boolean isName(String str) {
    return name.equals(str);
  }

  /**
   * Sets whether the UI element should have a glow effect.
   *
   * <p>This method allows you to control whether a glow effect should be applied to the UI element.
   * The glow effect is typically used to indicate interactivity or a highlighted state. The effect
   * is only applied if the element is clickable, as determined by the `clickable` property.
   *
   * @param glow A boolean value indicating whether to apply the glow effect. `true` applies the
   *     glow effect, while `false` removes it.
   * @see #isClickable()
   */
  public void setGlow(boolean glow) {
    if (clickable) {
      this.glow = glow;
    }
  }

  /**
   * Returns whether the UI element has a glow effect applied.
   *
   * <p>This method retrieves the current state of the glow effect applied to the UI element. A glow
   * effect is typically used to indicate interactivity or a highlighted state. The return value is
   * `true` if the glow effect is applied, and `false` if it's not.
   *
   * @return `true` if the UI element has a glow effect applied, `false` otherwise.
   */
  public boolean getGlow() {
    return glow;
  }
}
