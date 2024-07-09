package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;

/**
 * The WinController class is responsible for controlling the "win" screen of the application. It
 * provides methods for handling user interactions on this screen.
 */
public class WinController {
  @FXML private Rectangle btnMenu;
  @FXML private Button exitButton;

  /**
   * Handle the main menu button click event.
   *
   * <p>This method is called when the user clicks the main menu button on the "win" screen. It
   * performs the action of quitting the application.
   *
   * @throws Exception If an exception occurs during the application quitting process.
   */
  @FXML
  private void onMainMenuButton() throws Exception {
    App.quit();
  }
}
