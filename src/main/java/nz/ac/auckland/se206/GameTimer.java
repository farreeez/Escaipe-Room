package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nz.ac.auckland.se206.SceneManager.AppUi;

/** This class represents a game timer used to track the remaining time in a game. */
public class GameTimer {
  // Static fields for managing the timer
  private static Timeline timer;
  private static int minutes;
  private static int seconds = 0;
  private static List<Text> textList = new ArrayList<>();

  /**
   * Starts the game timer with an initial duration in minutes.
   *
   * @param min The initial duration of the timer in minutes.
   */
  public static void startGameTimer(int min) {
    // Create a Timeline that triggers the updateTimer() method every second
    timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer()));

    // Set the timer to run indefinitely
    timer.setCycleCount(Timeline.INDEFINITE);

    // Start the timer
    timer.play();

    // Set the initial game duration in minutes
    minutes = min;
  }

  /**
   * Stops the game timer, halting its counting and updating functionality. When called, this method
   * will pause the game timer, preventing any further countdown or updates to the timer display. It
   * effectively freezes the timer at its current time, which can later be resumed by starting the
   * timer again.
   *
   * <p>This method is useful when you want to temporarily suspend the timer's countdown, for
   * example, when a game or activity is paused or completed.
   *
   * @see #startGameTimer(int)
   */
  public static void stop() {
    // Stop the timer
    timer.stop();
  }

  // Method to update the timer's display
  private static void updateTimer() {
    if (seconds > 0) {
      // Decrease the remaining seconds
      seconds--;
    } else if (minutes > 0) {
      // Decrease the remaining minutes and set seconds to 59
      minutes--;
      seconds = 59;
    } else {
      // Stop the timer when time is up
      timer.stop();
      if (!GameState.won) {
        GameState.ended = true;
        try {
          App.setRoot(SceneManager.getUiRoot(AppUi.Lost));
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }

    // Format the remaining time as "MM:SS"
    String formattedTime = String.format("%02d:%02d", minutes, seconds);

    // Update all Text objects in the textList with the formatted time
    for (int i = 0; i < textList.size(); i++) {
      textList.get(i).setText(formattedTime);
    }
  }

  /**
   * Adds a Text object to the list of objects displaying the timer.
   *
   * @param text The Text object to add to the timer display.
   */
  public static void addText(Text text) {
    textList.add(text);
  }
}
