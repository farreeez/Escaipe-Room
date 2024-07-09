package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GameTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * The MainMenuController class is responsible for controlling the main menu screen of the
 * application. It provides methods for handling user interactions on this screen.
 */
public class MainMenuController {

  // Static fields
  @FXML private static Text hints;
  private static int min = 0;

  public static int getMin() {
    return min;
  }

  public static void addHintCounter(Text hintCounter) {
    hints = hintCounter;
  }

  // Instance fields
  @FXML private RadioButton easyButton;
  @FXML private RadioButton mediumButton;
  @FXML private RadioButton hardButton;
  @FXML private RadioButton twoMinButton;
  @FXML private RadioButton fourMinButton;
  @FXML private RadioButton sixMinButton;
  @FXML private Button startButton;

  /**
   * Initialize the main menu controller.
   *
   * <p>This method is called when the corresponding FXML file is loaded.
   */
  @FXML
  public void initialize() {}

  /**
   * Handle the start button click event.
   *
   * <p>This method is called when the user clicks the start button on the main menu. It checks the
   * selected difficulty and time limit and initiates the game.
   *
   * @throws IOException If an error occurs during the transition to the game.
   */
  @FXML
  private void onStartSequence() throws IOException {
    // Check which difficulty level is selected and set it in the game state.
    if (easyButton.isSelected()) {
      GameState.difficulty = "easy";
      System.out.println("Difficulty set to easy");
    } else if (mediumButton.isSelected()) {
      GameState.difficulty = "medium";
      hints.setText("Hints Left: 5");
      System.out.println("Difficulty set to medium");
    } else if (hardButton.isSelected()) {
      GameState.difficulty = "hard";
      System.out.println("Difficulty set to hard");
    } else {
      // Show an error dialog if no difficulty is selected and return from the method.
      showErrorDialog("Please select a difficulty.");
      return;
    }

    // Check which time limit is selected and set it in the 'min' variable.
    if (twoMinButton.isSelected()) {
      min = 2;
    } else if (fourMinButton.isSelected()) {
      min = 4;
    } else if (sixMinButton.isSelected()) {
      min = 6;
    } else {
      // Show an error dialog if no time limit is selected and return from the method.
      showErrorDialog("Please select a time limit.");
      return;
    }

    // Start the game timer with the selected time limit.
    // This is where the game timer is started.
    GameState.begin = true;

    // Set the root UI scene to the Story scene using the SceneManager.
    GameTimer.startGameTimer(getMin());
    App.setRoot(SceneManager.getUiRoot(AppUi.Story));
  }

  /**
   * Show an error dialog with the specified message.
   *
   * @param message The error message to display in the dialog.
   */
  private void showErrorDialog(String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);

    alert.showAndWait();
  }
}
