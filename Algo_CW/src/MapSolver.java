//20221322_H.A.T.S.Samarakoon

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class MapNode {
    int row;
    int col;
    public MapNode(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        MapNode other = (MapNode) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

class MapParser {
    public static char[][] parseMap(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int rows = 0;
        int cols = 0;

        String line;
        while ((line = reader.readLine()) != null) {
            rows++;
            if (cols == 0) {
                cols = line.length();
            }
        }
        reader.close();

        char[][] map = new char[rows][cols];

        reader = new BufferedReader(new FileReader(filename));
        int row = 0;
        while ((line = reader.readLine()) != null) {
            for (int col = 0; col < cols; col++) {
                map[row][col] = line.charAt(col);
            }
            row++;
        }
        reader.close();
        return map;
    }
}

public class MapSolver {
    private char[][] map;
    private final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private final String[] directionNames = {"up", "down", "left", "right"};
    public MapSolver(char[][] map) {
        this.map = map;
    }

    public void printShortestPath() {
        int rows = map.length;
        int cols = map[0].length;
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        boolean[][] visited = new boolean[rows][cols];
        int[][] steps = new int[rows][cols];
        Map<String, String> path = new HashMap<>();
        int[] start = findStart();
        int[] finish = findFinish();

        queue.offer(new int[]{start[0], start[1], 0});
        steps[start[0]][start[1]] = 0;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            if (current[0] == finish[0] && current[1] == finish[1]) {
                printPath(path, finish);
                return;
            }

            if (visited[current[0]][current[1]]) {
                continue;
            }
            visited[current[0]][current[1]] = true;

            for (int i = 0; i < directions.length; i++) {
                int newRow = current[0] + directions[i][0];
                int newCol = current[1] + directions[i][1];
                if (isValid(newRow, newCol) && !visited[newRow][newCol] && map[newRow][newCol] != '0') {
                    int newCost = steps[current[0]][current[1]] + 1;
                    int priority = newCost + heuristic(newRow, newCol, finish[0], finish[1]);
                    queue.offer(new int[]{newRow, newCol, priority});
                    if (steps[newRow][newCol] == 0 || newCost < steps[newRow][newCol]) {
                        steps[newRow][newCol] = newCost;
                        path.put(newRow + "," + newCol, current[0] + "," + current[1]);
                    }
                }
            }
        }
        System.out.println("No path found.");
    }

    private int heuristic(int row, int col, int finishRow, int finishCol) {
        return Math.abs(row - finishRow) + Math.abs(col - finishCol);
    }

    private boolean isValid(int row, int col) {
        int rows = map.length;
        int cols = map[0].length;
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    private int[] findStart() {
        int rows = map.length;
        int cols = map[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (map[i][j] == 'S') {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    private int[] findFinish() {
        int rows = map.length;
        int cols = map[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (map[i][j] == 'F') {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }

    private void printPath(Map<String, String> path, int[] finish) {
        List<String> pathPositions = new ArrayList<>();
        String current = finish[0] + "," + finish[1];

        while (path.containsKey(current)) {
            pathPositions.add(0, current);
            current = path.get(current);
        }
        pathPositions.add(0, current);

        System.out.println("Shortest path:");

        String currentDirection = "";
        String direction = "";
        int moveCount = 0;

        int instructionCount = 1;
        for (int i = 0; i < pathPositions.size() - 1; i++) {
            String[] currentPos = pathPositions.get(i).split(",");
            String[] nextPos = pathPositions.get(i + 1).split(",");

            int currentRow = Integer.parseInt(currentPos[0]);
            int currentCol = Integer.parseInt(currentPos[1]);
            int nextRow = Integer.parseInt(nextPos[0]);
            int nextCol = Integer.parseInt(nextPos[1]);

            if (nextRow - currentRow == 1) {
                direction = "down";
            } else if (nextRow - currentRow == -1) {
                direction = "up";
            } else if (nextCol - currentCol == 1) {
                direction = "right";
            } else if (nextCol - currentCol == -1) {
                direction = "left";
            }

            if (!direction.equals(currentDirection) || i == pathPositions.size() - 2) {

                if (moveCount > 0) {
                    System.out.println(instructionCount + ". Move " + currentDirection + " to (" + (currentRow + 1) + "," + (currentCol + 1) + ")");
                    instructionCount++;
                }
                moveCount = 0;
                currentDirection = direction;
            }
            moveCount++;
        }
        System.out.println(instructionCount + ". Done!");
    }

    public static void main(String[] args) {
        try {
            char[][] mapData = MapParser.parseMap("puzzle_10.txt");
            MapSolver solver = new MapSolver(mapData);
            solver.printShortestPath();
        } catch (IOException e) {
            System.err.println("Error reading map file: " + e.getMessage());
        }
    }
}

