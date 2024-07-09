package nz.ac.auckland.se206.controllers.plant;

import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nz.Objectives;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GameTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.Snake;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * The SnakeController class is responsible for controlling the behavior and user interface of the
 * Snake game. It handles player input, game logic, and rendering the game on the screen. It also
 * includes methods for managing the Snake's movement and gameplay.
 */
public class SnakeController {
  @FXML private Rectangle black;
  @FXML private Pane pane;
  @FXML private Label appleCnt;
  @FXML private Canvas canvas;
  @FXML private Text timerText;
  @FXML private Rectangle ttsRectangle;
  @FXML private Text chatTextArea;

  private Snake snake = new Snake();
  private double sizeOfCell = 48;
  private GraphicsContext gc1;
  private Image img;
  private Timeline timeline =
      new Timeline(
          new KeyFrame(
              Duration.millis(135),
              e -> {
                try {
                  snakeGame();
                } catch (IOException e1) {
                  System.out.println(e1);
                }
              }));
  private TextToSpeech ttss = new TextToSpeech();

  /**
   * Handles the text-to-speech functionality. This method is called when the text-to-speech button
   * is pressed.
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
            ttss.speak(chatTextArea.getText());
            return null;
          }
        };
    // Create a new thread to run the task
    Thread thread = new Thread(tsk);
    thread.start();
  }

  /**
   * Initializes the Snake game view. This method is automatically called when the FXML is loaded.
   */
  public void initialize() {
    // Initialization code goes here

    // Create a timeline to update the timerText every second
    GameTimer.addText(timerText);

    img =
        new Image(
            "file:./src/main/java/nz/ac/auckland/se206/controllers/RoomClasses"
                + "/textures/sprites/wormGame.png");
  }

  /**
   * Handles the "Exit" button click event.
   *
   * @throws IOException If an I/O error occurs while switching scenes.
   */
  @FXML
  private void exit() throws IOException {
    ttss.stop();
    App.goToRoom();
  }

  /** Handles the "Play" button click event. */
  @FXML
  private void clickPlay() {
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
    gc1 = canvas.getGraphicsContext2D();
    black.requestFocus();
  }

  /**
   * Manages the Snake game logic and graphics.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void snakeGame() throws IOException {
    snake.move();
    snake.eatsApple();
    gc1.drawImage(img, 0, 0, 670, 670);

    // paints the grid
    for (int i = 0; i < 14; i++) {
      gc1.strokeLine(0, 0 + sizeOfCell * i, 670, 0 + sizeOfCell * i);
      gc1.strokeLine(0 + sizeOfCell * i, 0, 0 + sizeOfCell * i, 670);
    }

    // draws the snake
    gc1.setFill(new Color(0.666, 0.2, 0.416, 1));
    double sizeOfSnake = 44;
    for (int i = 0; i < snake.getPosition().size() / 2 - 1; i++) {
      int j = (i + 1) * 2;
      gc1.fillRect(
          snake.getPosition().get(j) * sizeOfCell + 2,
          snake.getPosition().get(j + 1) * sizeOfCell + 2,
          sizeOfSnake,
          sizeOfSnake);
    }

    // draws the apple
    gc1.setFill(Color.RED);
    gc1.fillRect(
        snake.getApplePosition()[0] * sizeOfCell,
        snake.getApplePosition()[1] * sizeOfCell,
        sizeOfCell,
        sizeOfCell);

    appleCnt.setText(String.valueOf(snake.getAppleEaten()));
    if (gameEnds()) {
      timeline.stop();

      int applesEaten = snake.getAppleEaten();
      snake = new Snake();
      if (!GameState.ended) {
        if (applesEaten >= 5) {
          GameState.snakeDead = true;
          Objectives.updateObj(2);
          // switch to you won root;
          App.setRoot(SceneManager.getUiRoot(AppUi.SnakeWon));
        } else {
          // switch to you Lost root;
          App.setRoot(SceneManager.getUiRoot(AppUi.SnakeLost));
        }
      }
    }
  }

  /**
   * Checks if the game has ended.
   *
   * @return True if the game has ended; otherwise, false.
   */
  private boolean gameEnds() {
    // if the player wins and the snake has filled the entire grid return true
    if (snake.getAppleEaten() >= 5) {
      return true;
    }

    // if the snake touched the board return true
    if (snake.getPosition().get(2) > 13
        || snake.getPosition().get(2) < 0
        || snake.getPosition().get(3) < 0
        || snake.getPosition().get(3) > 13) {
      return true;
    }

    // if the snake ate itself return true
    int length = snake.getPosition().size() / 2 - 1;
    for (int i = 0; i < length; i++) {
      int l = (i + 1) * 2;
      for (int j = 0; j < length; j++) {
        int k = (j + 1) * 2;
        if (snake.getPosition().get(l) == snake.getPosition().get(k) && k != l) {
          if (snake.getPosition().get(l + 1) == snake.getPosition().get(k + 1)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Handles direction changes for the snake in response to a KeyEvent.
   *
   * @param move The KeyEvent associated with the direction change.
   */
  @FXML
  private void directionChange(KeyEvent move) {
    // Get the KeyCode associated with the KeyEvent.
    KeyCode keyCode = move.getCode();

    // Check the KeyCode and the current snake direction to determine the new direction.
    if (keyCode == KeyCode.UP && snake.getDirection() != 2) {
      // If the UP key is pressed and the snake is not currently moving down, change direction to
      // up.
      snake.changeDirection(-2);
    } else if (keyCode == KeyCode.DOWN && snake.getDirection() != -2) {
      // If the DOWN key is pressed and the snake is not currently moving up, change direction to
      // down.
      snake.changeDirection(2);
    } else if (keyCode == KeyCode.LEFT && snake.getDirection() != 1) {
      // If the LEFT key is pressed and the snake is not currently moving right, change direction to
      // left.
      snake.changeDirection(-1);
    } else if (keyCode == KeyCode.RIGHT && snake.getDirection() != -1) {
      // If the RIGHT key is pressed and the snake is not currently moving left, change direction to
      // right.
      snake.changeDirection(1);
    }
  }

  @FXML
  public void stoptts() {
    ttss.stop();
  }
}
