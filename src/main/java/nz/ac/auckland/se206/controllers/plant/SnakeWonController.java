package nz.ac.auckland.se206.controllers.plant;

import java.io.IOException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * Controller for the Snake game's victory (won) screen. Handles user interactions and game
 * completion.
 */
public class SnakeWonController {
  @FXML private Text txtArea;

  @FXML private Label labelArea;
  @FXML private Text timer;
  private TextToSpeech textToSpeech = new TextToSpeech();

  /**
   * Initializes the SnakeWonController. This method is automatically called when the associated
   * FXML is loaded.
   */
  public void initialize() {
    // Initialization code goes here

    // Create a timeline to update the timerText every second
    GameTimer.addText(timer);
  }

  /**
   * Performs text-to-speech (TTS) conversion and playback. This method speaks out any text that is
   * in the chatTextArea. This method is called when the text to speech button is pressed.
   */
  @FXML

  // This is the method that is called when the text to speech button is pressed
  private void convertTextToSpeech() {

    // Create a new TextToSpeech object
    // Create a new task to run the text to speech in a new thread
    Task<Void> tasks =
        new Task<Void>() {
          @Override
          // This is the method that is called when the task is run
          protected Void call() throws Exception {
            // These are the words that will be spoken
            textToSpeech.speak(labelArea.getText() + txtArea.getText());
            return null;
          }
        };
    Thread thread = new Thread(tasks);
    thread.start();
  }

  /**
   * Handles the "Play Again" button click event. Redirects the user to the Snake game when the
   * button is pressed.
   *
   * @throws IOException If an I/O error occurs while switching scenes.
   */
  @FXML
  public void stoptts() {
    textToSpeech.stop();
  }

  /**
   * Handles the "Return to Room" button click event. Redirects the user to the main room when the
   * button is pressed.
   *
   * @throws IOException If an I/O error occurs while switching scenes.
   */
  @FXML
  private void playAgain() throws IOException {
    textToSpeech.stop();
    App.setRoot(SceneManager.getUiRoot(AppUi.Snake));
  }

  /**
   * Handles the "Return to Room" button click event. Redirects the user to the main room when the
   * button is pressed.
   *
   * @throws IOException If an I/O error occurs while switching scenes.
   */
  @FXML
  private void returnRoom() throws IOException {
    textToSpeech.stop();
    App.goToRoom();
  }
}
