package nz.ac.auckland.se206.controllers.plant;

import java.io.IOException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * The SnakeLostController class is responsible for controlling the behavior and user interface of
 * the Snake game when the player loses. It provides methods for handling player interactions and
 * transitioning to other game scenes.
 */
public class SnakeLostController {
  @FXML private Text chatTextArea;

  @FXML private Label chatTextArea1;
  @FXML private Text timerText;
  @FXML private Rectangle ttsRectangle;
  private TextToSpeech ttss = new TextToSpeech();

  /**
   * Initializes the SnakeLostController. This method is automatically called when the associated
   * FXML is loaded.
   */
  public void initialize() {
    // Initialization code goes here

    // Create a timeline to update the timerText every second
    GameTimer.addText(timerText);
  }

  /**
   * Performs text-to-speech (TTS) conversion and playback. This method speaks out any text that is
   * in the chatTextArea.
   */
  @FXML
  private void textToSpeech() {
    // Create a new TextToSpeech object
    Task<Void> tsk =
        // Create a new task to run the text to speech in a new thread
        new Task<Void>() {
          @Override
          // This is the method that is called when the task is run
          protected Void call() throws Exception {
            ttss.speak(chatTextArea1.getText() + chatTextArea.getText());
            return null;
          }
        };
    // Create a new thread to run the task
    Thread thread = new Thread(tsk);
    thread.start();
  }

  /**
   * Handles the "Play Again" button click event. Redirects the user to the Snake game when the
   * button is pressed.
   *
   * @throws IOException If an I/O error occurs while switching scenes.
   */
  @FXML
  private void playAgain() throws IOException {
    ttss.stop();
    App.setRoot(SceneManager.getUiRoot(AppUi.Snake));
  }

  /**
   * Handles the "Return to Room" button click event. Redirects the user to the main room when the
   * button is pressed.
   *
   * @throws IOException If an I/O error occurs while switching scenes.
   */
  @FXML
  public void stoptts() {
    ttss.stop();
  }

  @FXML
  private void returnRoom() throws IOException {
    ttss.stop();
    App.goToRoom();
  }
}
