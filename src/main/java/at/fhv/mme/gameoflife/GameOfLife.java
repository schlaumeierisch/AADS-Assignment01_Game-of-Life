package at.fhv.mme.gameoflife;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GameOfLife extends Application {
    private static final int BOARD_WIDTH = 500;
    private static final int BOARD_HEIGHT = 500;
    private static final int CELL_SIZE = 20;

    private GameBoard gameBoard;

    // animation timer to keep simulating/drawing
    private AnimationTimer timer;
    private long lastUpdate = 0;

    @Override
    public void start(Stage stage) {
        boolean[][] initialState = new boolean[BOARD_WIDTH / CELL_SIZE][BOARD_HEIGHT / CELL_SIZE];
        gameBoard = new GameBoard(BOARD_WIDTH, BOARD_HEIGHT, CELL_SIZE, initialState);
        gameBoard.draw();

        BorderPane root = new BorderPane();
        BorderPane buttonPane = new BorderPane();

        // initialize control buttons (start/stop); stop button is disabled at beginning
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

            // set timer and update board every 1000ms
            timer = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    long currentTime = System.currentTimeMillis();

                    // every second, the game board is updated
                    if (currentTime - lastUpdate >= 1000) {
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
            // update board 1 time
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

            // stop timer
            timer.stop();
        });

        buttonPane.setLeft(nextButton);
        buttonPane.setCenter(startButton);
        buttonPane.setRight(stopButton);

        root.setCenter(gameBoard);
        root.setBottom(buttonPane);

        Scene scene = new Scene(root, BOARD_WIDTH, BOARD_HEIGHT + 50);
        stage.setScene(scene);
        stage.setTitle("Game of Life");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}