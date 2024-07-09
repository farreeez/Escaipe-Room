package nz.ac.auckland.se206;

/** Represents the state of the game. */
public class GameState {

  /** Indicates whether the riddle has been resolved. */
  public static boolean isRiddleResolved = false;

  public static boolean isMemoryGameCompleted = false;

  public static boolean isReactionGameCompleted = false;

  public static boolean won = false;

  public static boolean ended = false;

  // indicates whether the player is currently in the room or not
  public static boolean room = false;

  // shows if the snake has been beaten
  public static boolean snakeDead = false;

  // shows if the player has beaten the game
  public static boolean riddleChat = false;
  public static boolean aiChat = false;
  // shows if the player has beaten the game
  public static String difficulty = "";

  public static boolean begin = false;
}
