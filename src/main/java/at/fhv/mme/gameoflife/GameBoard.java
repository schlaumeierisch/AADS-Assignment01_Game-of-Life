package at.fhv.mme.gameoflife;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameBoard extends Canvas {
    private final int width;
    private final int height;
    private final int cellSize;

    private boolean[][] currentState;

    private boolean isRunning = false;

    public GameBoard(int width, int height, int cellSize, boolean[][] initialState) {
        super(width, height);

        this.width = width;
        this.height = height;
        this.cellSize = cellSize;

        this.currentState = initialState;

        // user is able to configure the board by mouse clicks as long as the program is not running
        setOnMouseClicked(e -> {
            if (!isRunning) {
                int x = (int) (e.getX() / cellSize);
                int y = (int) (e.getY() / cellSize);

                currentState[x][y] = !currentState[x][y];
                draw();
            }
        });
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.DARKGREY);
        gc.fillRect(0, 0, width, height);

        for (int x = 0; x < width; x += cellSize) {
            for (int y = 0; y < height; y += cellSize) {
                if (currentState[x / cellSize][y / cellSize]) {
                    gc.setFill(Color.GREEN);
                    gc.fillRect(x, y, cellSize, cellSize);
                } else {
                    gc.strokeRect(x, y, cellSize, cellSize);
                }
            }
        }
    }

    public void update() {
        boolean[][] newState = new boolean[currentState.length][currentState[0].length];

        for (int i = 0; i < currentState.length; i++) {
            for (int j = 0; j < currentState[0].length; j++) {
                int livingNeighbors = countLivingNeighbors(i, j);

                if (currentState[i][j]) {
                    // cell is alive and got >= 3 neighbors ("Tot durch Überbevölkerung")
                    if (livingNeighbors >= 3) {
                        newState[i][j] = false;
                    }

                    // cell is alive and got < 2 neighbors ("Tot durch fehlende Nachbarn")
                    if (livingNeighbors < 2) {
                        newState[i][j] = false;
                    }

                    // cell got 2 or 3 neighbors ("Überleben")
                    if (livingNeighbors == 2 || livingNeighbors == 3) {
                        newState[i][j] = true;
                    }
                } else {
                    // cell is dead and got 3 neighbors ("Reproduktion/Geburt einer Zelle")
                    if (livingNeighbors == 3) {
                        newState[i][j] = true;
                    }
                }
            }
        }

        currentState = newState;
    }

    public int countLivingNeighbors(int x, int y) {
        int count = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < currentState.length && j >= 0 && j < currentState[0].length && !(i == x && j == y) && currentState[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    public void save() {
        // create a new .txt file with the current state of the GameBoard (matrix)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String fileName = LocalDateTime.now().format(formatter) + ".txt";
        String filePath = "src/main/resources/at/fhv/mme/gameoflife/saved_states/";

        File file = new File(filePath + fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int x = 0; x < currentState.length; x++) {
                for (int y = 0; y < currentState[x].length; y++) {
                    writer.write(currentState[x][y] + " ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        // load a saved GameBoard state, update the current state and draw the board
        Stage stage = new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file");
        fileChooser.setInitialDirectory(new File("src/main/resources/at/fhv/mme/gameoflife/saved_states/"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int numRows = lines.size();
        int numCols = lines.get(0).split(" ").length;
        boolean[][] loadedState = new boolean[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            String[] values = lines.get(i).split(" ");
            for (int j = 0; j < numCols; j++) {
                loadedState[i][j] = Boolean.parseBoolean(values[j]);
            }
        }

        currentState = loadedState;
        draw();
    }
}
