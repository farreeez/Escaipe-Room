package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameTimer;

/**
 * The StoryController class is responsible for controlling the "story" screen of the application.
 * It provides methods for handling user interactions on this screen.
 */
public class StoryController {

  // this is the initialising the button
  @FXML private Button startButton;
  @FXML private Text timerText;

  /**
   * Initializes the StoryController.
   *
   * <p>This method is automatically called when the corresponding FXML file is loaded. It sets up
   * the GameTimer to display time information on the screen.
   */
  @FXML
  public void initialize() {
    GameTimer.addText(timerText);
  }

  /**
   * Handle the start button click event.
   *
   * <p>This method is called when the user clicks the start button on the "story" screen. It
   * navigates the player to the beginning of the game in a 3D room.
   *
   * @throws IOException If an error occurs during the transition to the game.
   */
  @FXML
  public void startGame() throws IOException {
    App.goToRoom();
  }
}
