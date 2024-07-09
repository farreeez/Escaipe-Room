package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;

  /**
   * Main method to launch the game.
   *
   * @param args Command-line arguments.
   */
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Set the root node of the application scene.
   *
   * @param fxml The root node to set.
   * @throws IOException If there is an error loading the FXML file.
   */
  public static void setRoot(Parent fxml) throws IOException {
    scene.setRoot(fxml);
  }

  /**
   * Load an FXML file from the "src/main/resources/fxml" directory.
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The root node of the loaded FXML file.
   * @throws IOException If the file is not found or cannot be loaded.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    SceneManager.addUi(AppUi.Memory, App.loadFxml("memory"));

    SceneManager.addUi(AppUi.Reaction, App.loadFxml("reaction_game"));
    SceneManager.addUi(AppUi.PlantOpen, App.loadFxml("plantOpen"));
    SceneManager.addUi(AppUi.SnakeLost, App.loadFxml("snakeLost"));
    SceneManager.addUi(AppUi.SnakeWon, App.loadFxml("snakeWon"));
    SceneManager.addUi(AppUi.Snake, App.loadFxml("snake"));
    SceneManager.addUi(AppUi.Room, loadFxml("room"));
    SceneManager.addUi(AppUi.Chat, loadFxml("chat"));
    SceneManager.addUi(AppUi.Story, loadFxml("story"));
    SceneManager.addUi(AppUi.MainMenu, loadFxml("mainMenu"));
    SceneManager.addUi(AppUi.Lost, loadFxml("lost"));
    SceneManager.addUi(AppUi.Won, loadFxml("won"));

    Parent root = SceneManager.getUiRoot(AppUi.MainMenu);
    scene = new Scene(root, 960, 720);
    stage.setScene(scene);
    stage.show();
    root.requestFocus();
    stage.setOnCloseRequest(
        (WindowEvent event) -> {
          Platform.exit();
          System.exit(0);
        });
  }

  /**
   * Quit the application gracefully by exiting JavaFX and the JVM.
   *
   * @throws Exception If there is an error during application exit.
   */
  public static void quit() throws Exception {
    Platform.exit();
    System.exit(0);
  }

  /**
   * Navigate to the "Room" scene in the application.
   *
   * @throws IOException If there is an error loading the "Room" scene.
   */
  public static void goToRoom() throws IOException {
    setRoot(SceneManager.getUiRoot(AppUi.Room));
    GameState.room = true;
  }

  /**
   * Get the primary scene of the application.
   *
   * @return The primary scene.
   */
  public static Scene getScene() {
    return scene;
  }
}
