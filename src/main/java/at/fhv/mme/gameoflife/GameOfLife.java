package at.fhv.mme.gameoflife;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        Canvas canvas = new Canvas(BOARD_WIDTH, BOARD_HEIGHT);
        root.setCenter(canvas);

        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // initialize the game board
        this.gameBoard = new GameBoard(BOARD_WIDTH, BOARD_HEIGHT, CELL_SIZE);
        gameBoard.init();
        gameBoard.draw(gc);

        // initialize control buttons (start/stop); stop button is disabled at beginning
        Button startButton = new Button("Start");
        Button nextButton = new Button("Next");
        Button stopButton = new Button("Stop");
        stopButton.setDisable(true);

        // start button functionality
        startButton.setOnAction(e -> {
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
                        gameBoard.draw(gc);
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
            gameBoard.draw(gc);
        });

        // stop button functionality
        stopButton.setOnAction(e -> {
            // disable stop button, enable start & next button
            stopButton.setDisable(true);
            startButton.setDisable(false);
            nextButton.setDisable(false);

            // stop timer
            timer.stop();
        });

        HBox buttons = new HBox(10, startButton, nextButton, stopButton);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(20));
        root.setBottom(buttons);
    }

    public static void main(String[] args) {
        launch();
    }
}