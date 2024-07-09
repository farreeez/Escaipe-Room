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
 * The PlantOpenController class is responsible for controlling the user interface and logic related
 * to the plant management and interaction features within the application. It provides
 * functionality for interacting with virtual plants, handling user input, and managing
 * plant-related activities. This class is used in conjunction with the associated FXML file to
 * create the plant management interface.
 */
public class PlantOpenController {

  @FXML private Text chatTextArea;
  @FXML private Label chatTextArea1;
  @FXML private Text timerText;
  @FXML private Rectangle ttsRectangle;
  private TextToSpeech ttss = new TextToSpeech();

  /**
   * Initializes the PlantOpenController. This method is automatically called when the associated
   * FXML is loaded.
   */
  public void initialize() {
    // Initialization code goes here

    // Create a timeline to update the timerText every second
    GameTimer.addText(timerText);
  }

  /**
   * Handles the "Play" button click event. Redirects the user to the Snake game when the button is
   * pressed.
   *
   * @throws IOException If an I/O error occurs while switching scenes.
   */
  @FXML
  private void play() throws IOException {
    ttss.stop();
    App.setRoot(SceneManager.getUiRoot(AppUi.Snake));
  }

  @FXML
  public void stoptts() {
    ttss.stop();
  }

  // This is the text to speech method
  /**
   * Performs text-to-speech (TTS) conversion and playback. This method is called when the
   * text-to-speech button is pressed.
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
    Thread thread = new Thread(tsk);
    thread.start();
  }
}
