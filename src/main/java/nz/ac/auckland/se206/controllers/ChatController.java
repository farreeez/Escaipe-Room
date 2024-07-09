package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import nz.Objectives;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GameTimer;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

/** Controller class for the chat view. */
public class ChatController {

  // Static fields
  private static String[] riddles = {"laboratory", "brain", "intelligence", "memory", "scientist"};
  private static ChatCompletionRequest riddleChatCompletionRequest;
  private static int hints = 6;

  /**
   * Executes a GPT-3 chat conversation by adding a user message to the chat completion request,
   * executing the request, and appending the model's response to a chat text area.
   *
   * @param msg The user's message to be added to the chat completion request.
   * @param chatTextArea The text area where the chat messages will be displayed.
   * @param chatCompletionRequest The chat completion request to which the user's message will be
   *     added.
   * @return The model's response as a ChatMessage or null in case of an ApiProxyException.
   * @throws ApiProxyException If an error occurs while interacting with the GPT-3 API.
   */
  private static ChatMessage runGpt(
      ChatMessage msg, TextArea chatTextArea, ChatCompletionRequest chatCompletionRequest)
      throws ApiProxyException {
    // Message that is sent to API is added
    chatCompletionRequest.addMessage(msg);
    try {
      // Message is sent to api and getting the result
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      // Adding the response to the chat
      chatCompletionRequest.addMessage(result.getChatMessage());
      appendChatMessage(result.getChatMessage(), chatTextArea);
      return result.getChatMessage();
    } catch (ApiProxyException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Appends a chat message to a text area with an appropriate role prefix.
   *
   * @param msg The ChatMessage to be appended to the chatTextArea.
   * @param chatTextArea The TextArea where the chat message will be displayed.
   */
  private static void appendChatMessage(ChatMessage msg, TextArea chatTextArea) {
    String rolePrefix = msg.getRole().equals("assistant") ? "Nasser" : "Player";
    chatTextArea.appendText(rolePrefix + ": " + msg.getContent() + "\n\n");
  }

  // Instance fields
  @FXML private TextArea riddleTextArea;
  @FXML private TextArea aiTextArea;
  @FXML private TextField inputText;
  @FXML private Button sendButton;
  @FXML private ImageView nasser;
  @FXML private Canvas game;
  @FXML private Label typing;
  @FXML private AnchorPane back;
  @FXML private Label lost;
  @FXML private Text timerText;
  @FXML private Text hintCounter;
  private ChatMessage lastMsg = new ChatMessage(null, null);
  private Image nasserImg;
  private Image player;
  private int step = 0;
  private boolean down;
  private ChatCompletionRequest aiChatCompletionRequest;
  private int count;
  private boolean up;
  private double orgPosY;
  private double boxStart;
  private boolean gameStart;

  // Timelines
  private Timeline timeline0 =
      new Timeline(
          new KeyFrame(
              Duration.millis(50),
              e -> {
                riddleTextArea.setVisible(GameState.riddleChat);
                aiTextArea.setVisible(GameState.aiChat);
              }));
  private Timeline timeline1 =
      new Timeline(
          new KeyFrame(
              Duration.millis(50),
              e -> {
                try {
                  if (GameState.begin) {
                    startGpt();
                  }
                } catch (ApiProxyException e1) {
                  e1.printStackTrace();
                }
              }));
  private boolean hint = true;
  private boolean allowGame;

  /**
   * Initializes the chat view, loading the riddle.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() throws ApiProxyException {
    riddleChatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.2).setTopP(0.5).setMaxTokens(100);
    aiChatCompletionRequest =
        new ChatCompletionRequest().setN(1).setTemperature(0.2).setTopP(0.5).setMaxTokens(100);

    timeline0.setCycleCount(Animation.INDEFINITE);
    timeline0.play();

    timeline1.setCycleCount(Animation.INDEFINITE);
    timeline1.play();

    nasserImg =
        new Image(
            "file:./src/main/java/nz/ac/auckland/se206/controllers/ChatTextures/miniNasser.png");
    player =
        new Image(
            "file:./src/main/java/nz/ac/auckland/se206/controllers/ChatTextures/midNasser.png");
    nasser.setImage(nasserImg);
    orgPosY = (263 * 0.75) - player.getHeight();
    down = false;
    up = false;
    allowGame = true;
    gameStart = false;
    gameBegin();
    boxStart = -101;
    MainMenuController.addHintCounter(hintCounter);

    back.setOnKeyPressed(
        event -> {
          KeyCode keyCode = event.getCode();
          if (keyCode == KeyCode.SPACE) {
            up = true;
            if (allowGame) {
              allowGame = false;
              lost.setText("");
              gameStart = true;
            }
          }
        });

    GameTimer.addText(timerText);
  }

  /**
   * Starts the GPT-3 chat conversation by sending messages to the GPT model for riddles and AI
   * prompts.
   *
   * @throws ApiProxyException If an error occurs while interacting with the GPT-3 API.
   */
  public void startGpt() throws ApiProxyException {
    // This is the method that is called when the task is run
    Random random = new Random(System.currentTimeMillis());
    // This is the message that is sent to the GPT model
    int riddle = random.nextInt(riddles.length);
    Task<Void> gptTask =
        new Task<Void>() {
          @Override
          // This is the method that is called when the task is run
          protected Void call() throws Exception {
            // This is the message that is sent to the GPT model
            runGpt(
                new ChatMessage(
                    "user", GptPromptEngineering.getRiddleWithGivenWord(riddles[riddle])),
                riddleTextArea,
                riddleChatCompletionRequest);
            // This is the message that is sent to the GPT model
            return null;
          }
        };
    // This is the thread that is run
    Thread riddleGpt = new Thread(gptTask);
    riddleGpt.start();
    Task<Void> aiTask =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            runGpt(
                new ChatMessage("user", GptPromptEngineering.getAiPrompt()),
                aiTextArea,
                aiChatCompletionRequest);
            return null;
          }
        };
    Thread aiGpt = new Thread(aiTask);
    aiGpt.start();
    timeline1.stop();
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    if (GameState.riddleChat) {
      onSendMessageHelper(riddleTextArea, riddleChatCompletionRequest);
    } else {
      onSendMessageHelper(aiTextArea, aiChatCompletionRequest);
    }
  }

  /**
   * Handles the sending of a user message in the chat interface, updates game-related states, and
   * interacts with the GPT model.
   *
   * @param chatTextArea The TextArea where chat messages are displayed.
   * @param chatCompletionRequest The ChatCompletionRequest for interacting with the GPT model.
   */
  private void onSendMessageHelper(
      TextArea chatTextArea, ChatCompletionRequest chatCompletionRequest) {
    String message = inputText.getText();
    // if the message is empty, do nothing
    if (message.trim().isEmpty()) {
      return;
    } else {
      back.requestFocus();
    }
    inputText.clear();
    // This is the timeline that is run
    Timeline timeline1 =
        new Timeline(
            new KeyFrame(
                Duration.millis(15),
                e -> {
                  loading();
                  if (gameStart) {
                    gameBegin();
                  }
                }));
    // This is the task that is run

    if (GameState.difficulty.equals("medium") && message.toLowerCase().contains("hint")) {
      hints--;
      hintCounter.setText("Hints Left: " + (hints - 1));
    }

    Task<Void> gptTask =
        new Task<Void>() {
          @Override
          // This is the method that is called when the task is run
          protected Void call() throws Exception {

            ChatMessage msg = new ChatMessage("user", message);
            // add the message to the chat

            appendChatMessage(msg, chatTextArea);
            if (hints <= 0 && hint) {
              hint = false;
              runGpt(
                  new ChatMessage("user", GptPromptEngineering.ranOutOfHints()),
                  aiTextArea,
                  aiChatCompletionRequest);
              runGpt(
                  new ChatMessage("user", GptPromptEngineering.ranOutOfHints()),
                  riddleTextArea,
                  riddleChatCompletionRequest);
            } else {
              lastMsg = runGpt(msg, chatTextArea, chatCompletionRequest);
              if (lastMsg.getRole().equals("assistant")
                  && lastMsg.getContent().startsWith("Correct")
                  && GameState.riddleChat) {
                Objectives.updateObj(3);
                GameState.isRiddleResolved = true;
                if (GameState.isReactionGameCompleted
                    && GameState.isMemoryGameCompleted
                    && GameState.snakeDead) {
                  GameState.won = true;
                  App.setRoot(SceneManager.getUiRoot(AppUi.Won));
                }
              }
            }
            // this is so that the user can't spam the chat
            Platform.runLater(
                () -> {
                  timeline1.stop();
                  typing.setText("");
                  boxStart = -100;
                  lost.setText("Eyes Up here!");
                  allowGame = false;
                });
            return null;
          }
        };
    // This is the thread that is run
    Thread riddle = new Thread(gptTask);
    timeline1.setCycleCount(Animation.INDEFINITE);
    lost.setText("");
    lost.setText("Press space to jump.");
    gameStart = false;
    allowGame = true;
    timeline1.play();
    riddle.start();
  }

  /**
   * Performs a loading animation by rotating an object and updating a text element to indicate
   * loading. The method is typically called as part of a task or timeline to provide a visual
   * loading effect.
   */
  private void loading() {
    // This is the method that is called when the task is run
    // rotate object
    Rotate rotate = new Rotate();
    // set the pivot point for the rotation
    rotate.setAngle(5);
    rotate.setPivotX(nasserImg.getWidth() / 2);
    rotate.setPivotY(nasserImg.getHeight() / 2);

    // add the rotation to the object
    if (count % 255 == 0) {
      if (step == 0) {
        typing.setText(" Press space to jump.");
        step = 1;
      } else if (step == 1) {
        typing.setText(" Press space to jump .");
        step = 2;
      } else if (step == 2) {
        typing.setText(" Press space to jump  .");
        step = 0;
      }
    }
    // Count iterator
    count += 15;

    nasser.getTransforms().add(rotate);
  }

  /**
   * Manages the game mechanics and graphics for a jumping game within the application. This method
   * is typically called as part of a game loop or animation timeline to update the game state.
   */
  private void gameBegin() {

    // This is the method that is called when the task is run
    GraphicsContext getGame = game.getGraphicsContext2D();

    double width = game.getWidth();
    double height = game.getHeight();

    double orgPosX = 0.1 * width;

    getGame.clearRect(0, 0, width, height);
    // This is the dimensions of the box

    int boxLength = 50;
    int boxHeight = 50;
    double boxEnd = boxStart + boxLength;
    // if the box is out of the screen, reset the box
    if (boxEnd < 0) {
      boxStart = width;
    }

    // draw the box
    getGame.setFill(Color.RED);
    getGame.fillRect(boxStart, (height * 0.75) - boxHeight, boxLength, boxHeight);

    boxStart -= 9;

    // draw the player
    if (up && (orgPosY > 30 || orgPosY == (height * 0.75) - player.getHeight())) {
      orgPosY -= 10;
      down = true;
    } else if (orgPosY < 30 || (down && !(orgPosY > (height * 0.75) - player.getHeight()))) {
      orgPosY += 10;
      up = false;
    } else {
      orgPosY = (height * 0.75) - player.getHeight();
      down = false;
    }

    // if the player hit the box, game over
    if (orgPosY >= (height * 0.75) - 50 - player.getHeight()
        && ((orgPosX <= boxEnd && orgPosX >= boxStart)
            && (orgPosX + player.getWidth() <= boxEnd
                && orgPosX + player.getWidth() >= boxStart))) {
      gameStart = false;
      allowGame = true;
      boxStart = -101;
      lost.setText("Press space to restart");
    }
    getGame.drawImage(player, orgPosX, orgPosY);
    getGame.setFill(Color.GREEN);
    getGame.fillRect(0, height * 0.75, width, height / 2);
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    if (GameState.aiChat) {
      aiTextArea.clear();
    }
    GameState.riddleChat = false;
    GameState.aiChat = false;
    App.goToRoom();
  }
}
