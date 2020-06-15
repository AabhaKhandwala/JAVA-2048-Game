package com.codegym.games.game2048;

import com.codegym.engine.cell.*;
import java.util.*;

public class Game2048 extends Game {

    private static final int SIDE = 4;
    private int[][] gameField = new int[SIDE][SIDE];
    private int xCordinate;
    private int yCordinate;
    private boolean isGameStopped = false;
    private int score = 0;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }

    private void createGame() {
        gameField = new int[SIDE][SIDE];
        createNewNumber();
        createNewNumber();

    }

    private void drawScene() {
        for (int i = 0; i < SIDE; i++)
            for (int j = 0; j < SIDE; j++) {
                setCellColoredNumber(i, j, gameField[j][i]);
            }
    }

    private void createNewNumber() {

        do {

            xCordinate = getRandomNumber(SIDE);
            yCordinate = getRandomNumber(SIDE);

        } while (gameField[xCordinate][yCordinate] != 0);

        if (getRandomNumber(10) == 9) {
            gameField[xCordinate][yCordinate] = 4;
        } else {
            gameField[xCordinate][yCordinate] = 2;
        }
        if (getMaxTileValue() == 2048)
            win();
        // else
        // gameField[randomX][randomY] = twoOrFour
    }

    private Color getColorByValue(int value) {
        // return a cell color based on value
        switch (value) {
            case 0:
                return Color.WHITE;
            case 2:
                return Color.BLUE;
            case 4:
                return Color.RED;
            case 8:
                return Color.GREEN;
            case 16:
                return Color.CYAN;
            case 32:
                return Color.GRAY;
            case 64:
                return Color.MAGENTA;
            case 128:
                return Color.ORANGE;
            case 256:
                return Color.PINK;
            case 512:
                return Color.YELLOW;
            case 1024:
                return Color.PURPLE;
            case 2048:
                return Color.BROWN;
            default:
                return Color.WHITE;
        }
    }

    private void setCellColoredNumber(int xCordinate, int yCordinate, int value) {
        String strValue;
        Color cellColor = getColorByValue(value);
        if (value != 0) {
            strValue = Integer.toString(value);
            setCellValueEx(xCordinate, yCordinate, cellColor, strValue);
        } else if (value == 0) {
            strValue = "";
            setCellValueEx(xCordinate, yCordinate, cellColor, strValue);
        }

    }

    private boolean compressRow(int[] row) {
        int temp = 0;
        int[] rowtemp = row.clone();
        boolean isChanged = false;
        for (int i = 0; i < row.length; i++) {
            for (int j = 0; j < row.length - i - 1; j++) {
                if (row[j] == 0) {
                    temp = row[j];
                    row[j] = row[j + 1];
                    row[j + 1] = temp;
                }
            }
        }
        if (!Arrays.equals(row, rowtemp))
            isChanged = true;
        return isChanged;
    }

    private boolean mergeRow(int[] row) {

        boolean isChanged = false;
        for (int i = 0; i < row.length - 1; i++) {

            if ((row[i] == row[i + 1]) && (row[i] != 0)) {
                score += (row[i] + row[i + 1]);
                setScore(score);
                row[i] = 2 * row[i];
                row[i + 1] = 0;
                isChanged = true;

            }
        }
        return isChanged;
    }

    @Override
    public void onKeyPress(Key key) {

        if (isGameStopped) { // Checks if the game is stopped before the game over conditions
            if (key == Key.SPACE)
                restart();
            return;
        }
        if (!canUserMove()) {
            gameOver();
            if (key == Key.SPACE) {
                restart();
            }
            return;
        }

        switch (key) { // this block should not be reachable if the game is stopped
            case LEFT:
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break;
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
            default:
                return;
        }
        drawScene();

    }

    private void restart() {
        isGameStopped = false;
        score = 0;
        setScore(score);
        createGame();
        drawScene();
    }

    private void rotateClockwise() {
        // Traverse each cycle
        for (int i = 0; i < SIDE / 2; i++) {
            for (int j = i; j < SIDE - i - 1; j++) {
                // Swap elements of each cycle in clockwise direction
                int temp = gameField[i][j];
                gameField[i][j] = gameField[SIDE - 1 - j][i];
                gameField[SIDE - 1 - j][i] = gameField[SIDE - 1 - i][SIDE - 1 - j];
                gameField[SIDE - 1 - i][SIDE - 1 - j] = gameField[j][SIDE - 1 - i];
                gameField[j][SIDE - 1 - i] = temp;
            }
        }
    }

    private void moveLeft() {
        boolean compress; // variable to get return from compressRow
        boolean merge; // variable to get return from mergeRow
        boolean compresss; // variable to get return from compressRow
        int move = 0; // to check if compressRow or mergeRow occurs
        for (int i = 0; i < SIDE; i++) {
            compress = compressRow(gameField[i]);
            merge = mergeRow(gameField[i]);
            compresss = compressRow(gameField[i]);
            if (compress || merge || compresss)
                move++;
        }
        if (move != 0) {
            createNewNumber();
        }
    }

    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private int getMaxTileValue() {
        int max = 0;
        for (int i = 0; i < SIDE; i++)
            for (int j = 0; j < SIDE; j++)
                if (gameField[i][j] > max)
                    max = gameField[i][j];

        return max;
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.GOLD, "You Win", Color.BLACK, 75);
    }

    private boolean canUserMove() {
        for (int r = 0; r < SIDE; r++)
            for (int c = 0; c < SIDE; c++)
                if (gameField[r][c] == 0)
                    return true;

        for (int r = 0; r < SIDE - 1; r++)
            for (int c = 0; c < SIDE; c++)
                if (gameField[r][c] == gameField[r + 1][c])
                    return true;

        for (int r = 0; r < SIDE; r++)
            for (int c = 0; c < SIDE - 1; c++)
                if (gameField[r][c] == gameField[r][c + 1])
                    return true;

        return false;
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.GOLD, "Game Over ", Color.BLACK, 75);
    }

}