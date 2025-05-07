package com.boardgame.utils.lightsout;

import java.util.ArrayList;
import java.util.List;

public class LightsoutGridConverter {
    public static boolean[][] toMatrix(List<String> grid) {
        int rows = grid.size();
        int cols = grid.get(0).length();
        boolean[][] matrix = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            String line = grid.get(i);
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = line.charAt(j) == '1';
            }
        }
        return matrix;
    }

    public static List<String> fromMatrix(boolean[][] matrix) {
        List<String> grid = new ArrayList<>();
        for (boolean[] row : matrix) {
            StringBuilder sb = new StringBuilder();
            for (boolean b : row) {
                sb.append(b ? '1' : '0');
            }
            grid.add(sb.toString());
        }
        return grid;
    }

}
