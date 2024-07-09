package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nz.Objectives;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GameTimer;

/**
 * The MemoryController class is responsible for controlling the user interface and logic related to
 * the Memory game within the application. It implements the Initializable interface to handle
 * initialization tasks when the associated FXML file is loaded.
 */
public class MemoryController implements Initializable {

  @FXML private Button button0;
  @FXML private Button button1;
  @FXML private Button button2;
  @FXML private Button button3;
  @FXML private Button button4;
  @FXML private Button button5;
  @FXML private Button button6;
  @FXML private Button button7;
  @FXML private Button button8;
  @FXML private Button retryButton;
  @FXML private Button exitButton;
  @FXML private Button startGame;
  @FXML private VBox game;
  @FXML private AnchorPane startAnchor;
  @FXML private Pane introPane;
  @FXML private Pane gamePane;
  @FXML private Pane lossPane;
  @FXML private Text roundText;
  @FXML private Text livesText;
  @FXML private Button playAgain;
  @FXML private Button exit;
  @FXML private Pane winPane;
  @FXML private Text timerText;
  @FXML private Button ttsButton;
  @FXML private Text text1;
  @FXML private Text text2;
  @FXML private Text text3;
  @FXML private Text text4;
  @FXML private Text text5;

  // An arraylist which contains all the buttons
  private ArrayList<String> possibleButtons =
      new ArrayList<>(
          Arrays.asList(
              "button0", "button1", "button2", "button3", "button4", "button5", "button6",
              "button7", "button8"));

  private ArrayList<Button> buttons = new ArrayList<>();

  // Arraylist which will store the order of the buttons which the user will have to press
  private ArrayList<String> pattern = new ArrayList<>();

  private int patternOrder = 0;

  private Random random = new Random();

  private int counter = 0;
  private int turn = 1;
  private int requiredScore = 5;
  private int lives = 3;

  /**
   * Initializes the MemoryController with the specified URL and ResourceBundle. This method is
   * called automatically by the JavaFX framework when the associated FXML file is loaded.
   *
   * @param url The location used to resolve relative paths for the root object, or null if the
   *     location is not known.
   * @param resourceBundle The resources used to localize the root object, or null if the root
   *     object was not localized.
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    gamePane.setVisible(false);
    introPane.setVisible(true);
    lossPane.setVisible(false);
    winPane.setVisible(false);

    // Create a timeline to update the timerText every second
    GameTimer.addText(timerText);
    // Initialize the 'buttons' ArrayList with references to the buttons defined in the FXML file.
    buttons.addAll(
        Arrays.asList(
            button0, button1, button2, button3, button4, button5, button6, button7, button8));
    // Set focusTraversable to false for each button
    for (Button button : buttons) {
      button.setFocusTraversable(false);
      changeButtonColor(button, "#80518a");
    }
  }

  /**
   * An event handler for button clicks in the game.
   *
   * @param event The ActionEvent triggered by the button click.
   * @throws IOException If there is an I/O error.
   */
  @FXML
  private void onSquareClicked(ActionEvent event) throws IOException {
    // This method is called when a button is clicked.
    // It checks if the clicked button matches the current button in the 'pattern' list.
    // If it matches, it changes the button color to light green and increments the 'counter'.
    // If it doesn't match, it changes the button color to red and decrements the 'lives' count."

    if (((Control) event.getSource()).getId().equals(pattern.get(counter))) {
      Button button = buttons.get(getIndexOfButton(event));
      changeButtonColor(button, "-fx-base: lightgreen");
      counter++;
    } else {
      Button button = buttons.get(getIndexOfButton(event));
      changeButtonColor(button, "-fx-base: red");
      lives--;
      livesText.setText("Lives: " + lives);

      if (lives == 0) {
        lossPane.setVisible(true);
        gamePane.setVisible(false);
        return;
      }

      return;
    }

    // If the user has pressed all the buttons in the current pattern, it's time for the next turn.
    if (counter == turn) {
      nextTurn();
    }
  }

  /**
   * Handles the start of a new game. Clears the pattern, generates a new button sequence, and shows
   * it to the user.
   */
  @FXML
  private void onBeginAction() {
    // This method is called when the "Start" button is clicked.
    // It clears the 'pattern' list, generates a new button sequence, and shows it to the user.

    gamePane.setVisible(true);
    introPane.setVisible(false);
    lossPane.setVisible(false);
    winPane.setVisible(false);
    pattern.clear();

    counter = 0;
    turn = 1;
    lives = 3;

    roundText.setText("Round (Square Count): " + turn);
    livesText.setText("Lives: " + lives);

    pattern.add(possibleButtons.get(random.nextInt(possibleButtons.size())));
    showPattern();
  }

  /**
   * Advances the game to the next turn. This method increments the turn count, generates a new
   * random button sequence, and displays it to the user. If the required score is reached, the game
   * is marked as completed.
   *
   * @throws IOException If there is an I/O error.
   */
  private void nextTurn() throws IOException {
    // This method advances the game to the next turn.
    // It increments the 'turn' count, adds a new random button to the 'pattern', and shows it.
    if (turn == requiredScore) {
      GameState.isMemoryGameCompleted = true;
      gamePane.setVisible(false);
      winPane.setVisible(true);
      Objectives.updateObj(1);
      return;
    }

    counter = 0;
    turn++;

    roundText.setText("Round (Square Count): " + turn);

    pattern.add(possibleButtons.get(random.nextInt(possibleButtons.size())));
    showPattern();
  }

  /**
   * Extracts the index of the clicked button from the event.
   *
   * @param event The ActionEvent representing the button click.
   * @return The index of the clicked button.
   */
  private int getIndexOfButton(ActionEvent event) {
    // This method extracts the index of the clicked button from the event.
    String buttonId = ((Control) event.getSource()).getId();
    return Integer.parseInt(buttonId.substring(buttonId.length() - 1));
  }

  /**
   * Extracts the index of a button from its name.
   *
   * @param button The name of the button.
   * @return The index of the button.
   */
  private int getIndexOfButton(String button) {
    return Integer.parseInt(button.substring(button.length() - 1));
  }

  /** Displays the current pattern of buttons to the user using animations. */
  private void showPattern() {
    // Disable buttons before showing the pattern
    setButtonsEnabled(false);

    // This method displays the current 'pattern' of buttons to the user.
    // It uses animations to briefly highlight each button in the sequence.

    double pauseTime = 0.1;
    if (turn == 2 || turn == 3) {
      pauseTime = 0.05;
    } else if (turn == 4 || turn == 5) {
      pauseTime = 0.01;
    }

    PauseTransition pause = new PauseTransition(Duration.seconds(pauseTime));
    pause.setOnFinished(
        e -> {
          Timeline timeline =
              new Timeline(
                  new KeyFrame(
                      Duration.seconds(0.65),
                      event -> {
                        showNext();
                      }));
          timeline.setCycleCount(pattern.size());
          timeline.setOnFinished(
              event -> {
                // Enable buttons after the pattern is shown
                setButtonsEnabled(true);
              });
          timeline.play();
        });
    pause.play();
  }

  /** Highlights the next button in the pattern sequence. */
  private void showNext() {
    // This method highlights the next button in the 'pattern' sequence.
    Button button = buttons.get(getIndexOfButton(pattern.get(patternOrder)));
    changeButtonColor(button, "-fx-base: #452b4a");
    patternOrder++;

    // If all buttons in the pattern have been shown, reset the pattern order.
    if (patternOrder == turn) {
      patternOrder = 0;
    }
  }

  /**
   * Changes the color of a button and resets it after a brief pause.
   *
   * @param button The button to change the color of.
   * @param color The new color for the button.
   */
  private void changeButtonColor(Button button, String color) {
    // This method changes the color of a button and resets it after a brief pause.
    button.setStyle(color);
    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
    pause.setOnFinished(
        e -> {
          button.setStyle("-fx-base: #80518a");
        });
    pause.play();
  }

  /**
   * Handles the "Leave Room" button click event, allowing the user to exit the memory game and
   * return to the main application room.
   *
   * @throws IOException If there is an I/O error when transitioning to the room.
   */
  @FXML
  private void onLeaveRoom() throws IOException {
    App.goToRoom();
  }

  /**
   * Enables or disables the buttons in the game interface based on the specified 'enabled' flag.
   *
   * @param enabled true to enable the buttons, false to disable them.
   */
  private void setButtonsEnabled(boolean enabled) {
    for (Button button : buttons) {
      button.setDisable(!enabled);
    }
  }
}
