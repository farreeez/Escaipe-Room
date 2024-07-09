package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import nz.Objectives;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GameTimer;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * The ReactionGameController class is responsible for controlling the user interface and logic
 * related to the Reaction game within the application. It provides functionality for playing the
 * Reaction game, measuring reaction times, and handling user input. This class is used in
 * conjunction with the associated FXML file to create the Reaction game interface.
 */
public class ReactionGameController {
  @FXML private ImageView black;
  @FXML private ImageView onelight;
  @FXML private ImageView twolight;
  @FXML private ImageView threelight;
  @FXML private ImageView fourlight;
  @FXML private ImageView fivelight;
  @FXML private ImageView greenLight;
  @FXML private Rectangle rectangle;
  @FXML private Label gameTime;
  @FXML private Label jumpStartLabel;
  @FXML private Button returnButton;
  @FXML private Label passedText;
  @FXML private Text timerText;
  @FXML private Button ttsButton;
  @FXML private Button stTts;
  @FXML private Label title;
  @FXML private Label instructions;

  private boolean activeGame = false;
  private Timer timerr;
  private Timer timer;
  private long startTime;
  private long endTime;
  private int opacityChangeCount = 0;
  private boolean lightsOut = false;
  private boolean jumpStart = false;
  private boolean passed = false;
  private boolean canStart = true;
  private boolean clicked = false;
  private TextToSpeech ttss = new TextToSpeech();

  /**
   * Initializes the ReactionGameController. This method is called when the FXML file is loaded and
   * sets the initial values of variables.
   */
  public void initialize() {
    gameTime.setText("Reaction Time: ");
    black.setOpacity(1);
    onelight.setOpacity(0);
    twolight.setOpacity(0);
    threelight.setOpacity(0);
    fourlight.setOpacity(0);
    fivelight.setOpacity(0);
    greenLight.setOpacity(0);
    jumpStartLabel.setOpacity(0);
    returnButton.setOpacity(0);
    passedText.setOpacity(0);
    activeGame = false;

    // Create a timeline to update the timerText every second
    GameTimer.addText(timerText);
  }

  /**
   * Handles the click event when lights are clicked. This method manages the game's logic,
   * including jump-start detection, game initialization, and reaction time calculation.
   *
   * @param event The MouseEvent representing the click event.
   * @throws IOException If there is an I/O error.
   */
  @FXML
  public void clickLights(MouseEvent event) throws IOException {
    clicked = true;
    System.out.println("canStart: " + canStart);
    System.out.println("ActiveGame: " + activeGame);
    System.out.println("passed: " + passed);
    System.out.println("lightsOut: " + lightsOut);
    System.out.println("Clicked: " + clicked);
    // This is the code that is run when the lights are clicked
    if (activeGame == false && passed == false && canStart == true) {
      System.out.println("lights clicked");
      jumpStart = false;
      jumpStartLabel.setOpacity(0);
      black.setOpacity(1);
      onelight.setOpacity(0);
      twolight.setOpacity(0);
      threelight.setOpacity(0);
      fourlight.setOpacity(0);
      fivelight.setOpacity(0);
      greenLight.setOpacity(0);
      opacityChangeCount = 0;
      startSequence();
    } else if (activeGame == true && lightsOut == false) {
      jumpStart = true;
      // JUMP START ALERT
      // open jumpStart.fxml

      jumpStartLabel.setOpacity(1);
      returnButton.setOpacity(0);

      // Reset the lights
      black.setOpacity(0);
      onelight.setOpacity(0);
      twolight.setOpacity(0);
      threelight.setOpacity(0);
      fourlight.setOpacity(0);
      fivelight.setOpacity(0);
      greenLight.setOpacity(0);

      activeGame = false;
      opacityChangeCount = 0;
      timerr.cancel();

    } else if (activeGame == true && lightsOut == true) {
      // time here minus the time from before
      endTime = System.currentTimeMillis(); // Capture the end time
      long elapsedTime = endTime - startTime; // Calculate elapsed time in milliseconds
      double seconds = elapsedTime / 1000.0; // Convert to seconds
      gameTime.setText("Reaction Time: " + seconds + " Seconds");
      if (seconds <= 0.5) {
        passed = true;
        returnButton.setOpacity(1);
        passedText.setOpacity(1);
        ttsButton.setVisible(false);
        stTts.setVisible(false);
      }
      activeGame = false;
      lightsOut = false;
      canStart = true;
      GameState.isReactionGameCompleted = true;

      // Update the objectives after activity completed
      Objectives.updateObj(0);
    }
  }

  /**
   * Returns to the main game when the player has passed the mini-game.
   *
   * @param event The MouseEvent representing the click event on the return button.
   * @throws IOException If there is an I/O error.
   */
  @FXML
  public void returnToGame(MouseEvent event) throws IOException {
    if (passed == true) {
      ttss.stop();
      App.goToRoom();
    }
  }

  /*Stops tts from continuing to play. */
  @FXML
  public void stoptts() {
    ttss.stop();
  }

  /**
   * Starts the sequence of changing opacity for the lights. This method initiates the timer to
   * change the opacity of the lights, simulating a reaction time test.
   */
  public void startSequence() {
    // This is the timer that is used to change the opacity of the lights
    activeGame = true;
    timerr = new Timer();
    timerr.schedule(
        new TimerTask() {
          @Override
          // This is the method that is called when the timer is run
          public void run() {

            // This is the code that is run when the timer is run
            // This method is called to change the opacity of the lights
            changeOpacity();
            opacityChangeCount++;
            // This is the code that is run when the lights have all lit up
            if (opacityChangeCount == 5) {
              sequenceFinished();
            }
            // If there is a jump start then cancel the timer
            if (jumpStart == true) {
              timerr.cancel();
            }
          }
        },
        0,
        500); // Change opacity every 1 second
  }

  /**
   * Changes the opacity of the lights during the reaction game, simulating a race lights sequence.
   */
  private void changeOpacity() {
    if (jumpStart == true) {
      opacityChangeCount = 0;
      return;
    } else {

      // Switiching through the opacity changes
      // Basically the lights are going to change opacity
      // from 1 to 0 and 0 to 1
      // To represent a race lights in formula 1

      switch (opacityChangeCount) {
        case 0:
          onelight.setOpacity(1);
          black.setOpacity(0);
          break;
        case 1:
          twolight.setOpacity(1);
          onelight.setOpacity(0);
          break;
        case 2:
          threelight.setOpacity(1);
          twolight.setOpacity(0);
          break;
        case 3:
          fourlight.setOpacity(1);
          threelight.setOpacity(0);
          break;
        case 4:
          fivelight.setOpacity(1);
          fourlight.setOpacity(0);
          break;
      }
    }
  }

  /** Handles the completion of the light sequence, initiating the lights-out sequence. */
  private void sequenceFinished() {
    timerr.cancel();
    lightsOutSequence();
  }

  /** Initiates the lights-out sequence by making all lights go dark. */
  private void lightsOutSequence() {
    clicked = false;
    int randomTime = ThreadLocalRandom.current().nextInt(1000, 3000);
    timer = new Timer();
    timer.schedule(
        new TimerTask() {
          @Override
          public void run() {
            if (clicked == false) {
              canStart = false;
              lightsOut = true;
              fivelight.setOpacity(0);
              greenLight.setOpacity(1);

              // get time here
              startTime = System.currentTimeMillis();
              timer.cancel();
            }
          }
        },
        randomTime);
    canStart = true;
  }

  /**
   * Implements the functionality of the "Text to Speech" button. When clicked, this method uses the
   * TextToSpeech class to speak the title and instructions of the game.
   */
  @FXML
  private void textToSpeech() {
    // This is the text to speech method
    // A new object of the TextToSpeech class is created
    // A new task is created to run the text to speech in a new thread
    Task<Void> tsk =
        new Task<Void>() {
          @Override
          // This is the method that is called when the task is run
          protected Void call() throws Exception {
            ttss.speak(title.getText() + instructions.getText());
            return null;
          }
        };
    Thread thread = new Thread(tsk);
    thread.start();
  }
}
