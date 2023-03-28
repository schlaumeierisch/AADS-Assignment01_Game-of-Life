package at.fhv.mme.gameoflife;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameBoard {
    private final int width;
    private final int height;
    private final int cellSize;

    private boolean[][] currentState;

    public GameBoard(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;

        this.currentState = new boolean[width / cellSize][height / cellSize];
    }

    public void setCell(int x, int y, boolean alive) {
        currentState[x][y] = alive;
    }

    public void init() {
        // define which cells are alive
        setCell(5, 5, true);
        setCell(5, 6, true);
        setCell(5, 7, true);
        setCell(6, 5, true);
        setCell(7, 7, true);
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.DARKGREY);
        gc.fillRect(0, 0, this.width, this.height);

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
}
