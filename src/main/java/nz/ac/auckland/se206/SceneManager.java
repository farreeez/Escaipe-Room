package nz.ac.auckland.se206;

import java.util.HashMap;
import javafx.scene.Parent;

/**
 * The SceneManager class manages different UI scenes in the application and provides methods to
 * add, retrieve, and clear scenes.
 */
public class SceneManager {
  /**
   * This enum represents the various user interface (UI) screens or views available in the
   * application. Each enum constant corresponds to a specific UI screen that can be displayed to
   * the user.
   */
  public static enum AppUi {
    /**
     * The main room or central hub of the application where the user can access different
     * activities and features.
     */
    Room,

    /** The chat interface where the user can engage in text-based communication. */
    Chat,

    /** The plant management interface for managing and interacting with virtual plants. */
    PlantOpen,

    /** The Snake game interface for playing the Snake game. */
    Snake,

    /** The interface displayed when the user loses the Snake game. */
    SnakeLost,

    /** The interface displayed when the user wins the Snake game. */
    SnakeWon,

    /** The memory game interface for playing the memory game. */
    Memory,

    /** The reaction game interface for playing the reaction game. */
    Reaction,

    /**
     * The main menu or application entry point where the user can access various features and
     * activities.
     */
    MainMenu,

    /** The interface displayed when the user wins an in-game activity or challenge. */
    Won,

    /** The interface displayed when the user loses an in-game activity or challenge. */
    Lost,

    /**
     * The controls interface that provides information about how to interact with the application
     * and its features.
     */
    Controls,

    /**
     * The story interface where the user can access and experience the application's narrative or
     * storyline.
     */
    Story
  }

  // HashMap to store UI scenes mapped to their corresponding AppUi values
  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();

  /**
   * Adds a UI scene to the sceneMap.
   *
   * @param appUi The AppUi value representing the UI scene.
   * @param uiRoot The Parent representing the UI root of the scene.
   */
  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  /**
   * Retrieves the UI root of a specific scene.
   *
   * @param appUi The AppUi value representing the UI scene.
   * @return The Parent representing the UI root of the scene, or null if not found.
   */
  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi);
  }

  /** Clears all scenes from the sceneMap. */
  public static void clearScene() {
    sceneMap.clear();
  }
}
