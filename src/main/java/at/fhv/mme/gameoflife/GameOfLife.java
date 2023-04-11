package at.fhv.mme.gameoflife;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GameOfLife extends Application {
    private static final int BOARD_WIDTH = 500;
    private static final int BOARD_HEIGHT = 500;
    private static final int CELL_SIZE = 20;

    private GameBoard gameBoard;

    // animation timer to keep simulating/drawing
    private AnimationTimer timer;
    private long lastUpdate = 0;
    private int boardSpeed = 1000;

    @Override
    public void start(Stage stage) {
        boolean[][] initialState = new boolean[BOARD_WIDTH / CELL_SIZE][BOARD_HEIGHT / CELL_SIZE];
        gameBoard = new GameBoard(BOARD_WIDTH, BOARD_HEIGHT, CELL_SIZE, initialState);
        gameBoard.draw();

        BorderPane root = new BorderPane();
        BorderPane buttonPane = initButtonPane();

        root.setCenter(gameBoard);
        root.setBottom(buttonPane);

        Scene scene = new Scene(root, BOARD_WIDTH, BOARD_HEIGHT + 50);
        stage.setScene(scene);
        stage.setTitle("Game of Life");
        stage.show();
    }

    public BorderPane initButtonPane() {
        BorderPane bottomPane = new BorderPane();

        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(15));
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        // SAVE & LOAD BUTTONS -----------------------------------------------------------------------------------------
        Button saveButton = new Button("Save");
        Button loadButton = new Button("Load");

        saveButton.setOnAction(e -> gameBoard.save());
        loadButton.setOnAction(e -> gameBoard.load());

        // START, NEXT & STOP BUTTONS ----------------------------------------------------------------------------------
        Button startButton = new Button("Start");
        Button nextButton = new Button("Next");
        Button stopButton = new Button("Stop");
        stopButton.setDisable(true);

        // start button functionality
        startButton.setOnAction(e -> {
            gameBoard.setRunning(true);

            // disable start button & enable stop button
            startButton.setDisable(true);
            nextButton.setDisable(true);
            stopButton.setDisable(false);

            saveButton.setDisable(true);
            loadButton.setDisable(true);

            // set timer and update board every 1000ms
            timer = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    long currentTime = System.currentTimeMillis();

                    // every second, the game board is updated
                    if (currentTime - lastUpdate >= boardSpeed) {
                        gameBoard.update();
                        gameBoard.draw();
                        lastUpdate = currentTime;
                    }
                }
            };
            timer.start();
        });

        // next button functionality
        nextButton.setOnAction(e -> {
            // update board one time
            gameBoard.update();
            gameBoard.draw();
        });

        // stop button functionality
        stopButton.setOnAction(e -> {
            gameBoard.setRunning(false);

            // disable stop button, enable start & next button
            stopButton.setDisable(true);
            startButton.setDisable(false);
            nextButton.setDisable(false);

            saveButton.setDisable(false);
            loadButton.setDisable(false);

            // stop timer
            timer.stop();
        });

        buttonBox.getChildren().addAll(nextButton, startButton, stopButton, saveButton, loadButton);
        bottomPane.setLeft(buttonBox);

        // SPEED SETTINGS ----------------------------------------------------------------------------------------------
        HBox speedBox = new HBox();
        speedBox.setPadding(new Insets(15));
        speedBox.setSpacing(10);
        speedBox.setAlignment(Pos.CENTER_RIGHT);

        Label speedLabel = new Label("Speed (ms):");
        TextField speedInput = new TextField();
        speedInput.setText(String.valueOf(boardSpeed));
        speedInput.setPrefWidth(50);

        Button setSpeedButton = new Button("Set");
        setSpeedButton.setOnAction(e -> {
            if (!speedInput.getText().isEmpty() && speedInput.getText().matches("\\d*")) {
                int newBoardSpeed = Integer.parseInt(speedInput.getText().replaceAll(",", ""));

                if (newBoardSpeed >= 10 && newBoardSpeed <= 2500) {
                    boardSpeed = newBoardSpeed;
                } else {
                    speedInput.setText(String.valueOf(boardSpeed));
                }
            } else {
                speedInput.setText(String.valueOf(boardSpeed));
            }
        });

        speedBox.getChildren().addAll(speedLabel, speedInput, setSpeedButton);
        bottomPane.setRight(speedBox);

        return bottomPane;
    }

    public static void main(String[] args) {
        launch();
    }
}