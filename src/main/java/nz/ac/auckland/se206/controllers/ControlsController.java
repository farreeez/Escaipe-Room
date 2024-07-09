package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * The ControlsController class is responsible for controlling the "controls" screen of the
 * application. It provides a method for handling user interactions on this screen.
 */
public class ControlsController {

  /**
   * Handle the "Continue" button click event.
   *
   * <p>This method is called when the user clicks the "Continue" button on the "controls" screen.
   * It starts the game timer with the specified time limit and navigates the user to the story
   * screen.
   *
   * @throws IOException If an error occurs during the transition to the story screen.
   */
  @FXML
  private void continueBtn() throws IOException {

    // Start the game timer with the specified time limit.
    GameTimer.startGameTimer(MainMenuController.getMin());

    // Set the root UI scene to the Story screen using the SceneManager.
    App.setRoot(SceneManager.getUiRoot(AppUi.Story));
  }
}
