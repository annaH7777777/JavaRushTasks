package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {

    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField = 0;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE*SIDE;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame(){
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[0].length; j++) {
                setCellValue(j, i, "");
                if(getRandomNumber(10)==0) {
                    countMinesOnField += 1;
                    gameField[i][j] = new GameObject(j, i, true);
                }
                    else {gameField[i][j] = new GameObject(j, i, false);}
                    setCellColor(j, i, Color.ORANGE);
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();
        //isGameStopped = false;
    }
    private List<GameObject> getNeighbors(GameObject gameObject){
        List<GameObject> list = new ArrayList<>();
        int x = gameObject.x;
        int y = gameObject.y;
        if(x != 0) list.add(gameField[y][x-1]);
        if(x != SIDE-1) list.add(gameField[y][x+1]);
        if(y !=0) list.add(gameField[y-1][x]);
        if(y != SIDE-1) list.add(gameField[y+1][x]);
        if(x != SIDE-1 && y != SIDE-1) list.add(gameField[y+1][x+1]);
        if(y != 0 && x != SIDE-1) list.add(gameField[y-1][x+1]);
        if(x != 0 && y != 0)list.add(gameField[y-1][x-1]);
        if(y != SIDE-1 && x != 0) list.add(gameField[y+1][x-1]);

        return list;
    }
    private void countMineNeighbors(){
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[0].length; j++) {
                if(!gameField[i][j].isMine) {
                    List <GameObject> neighbors = getNeighbors(gameField[i][j]);
                    for (int k = 0; k < neighbors.size(); k++) {
                        if(neighbors.get(k).isMine) gameField[i][j].countMineNeighbors +=1;
                    }
                }

            }
        }
    }
    private void openTile(int x, int y){
        if(!gameField[y][x].isOpen && !gameField[y][x].isFlag && !isGameStopped) {
            gameField[y][x].isOpen = true;
            countClosedTiles--;
            setCellColor(x, y, Color.GREEN);
            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            } else {
                if (gameField[y][x].countMineNeighbors == 0) {
                    List<GameObject> neighborList = getNeighbors(gameField[y][x]);
                    for (GameObject neighbor : neighborList) {
                        if (!neighbor.isOpen) openTile(neighbor.x, neighbor.y);
                    }
                    setCellValue(x, y, "");
                } else setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                score = score + 5;
                setScore(score);
                if(countClosedTiles == countMinesOnField) win();
            }
        }

    }

    @Override
    public void onMouseLeftClick(int x, int y) {
            super.onMouseLeftClick(x, y);

            if(!isGameStopped) {
                openTile(x, y);
            }
            else{
            restart();
        }
    }
    private void markTile(int x, int y){
        if(!isGameStopped) {
            if (!gameField[y][x].isOpen) {
                if (countFlags != 0 || gameField[y][x].isFlag) {
                    if (!gameField[y][x].isFlag) {
                        gameField[y][x].isFlag = true;
                        countFlags--;
                        setCellValue(x, y, FLAG);
                        setCellColor(x, y, Color.YELLOW);
                    } else {
                        gameField[y][x].isFlag = false;
                        countFlags++;
                        setCellValue(x, y, "");
                        setCellColor(x, y, Color.ORANGE);

                    }

                }
            }
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);
    }
    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.AQUAMARINE, "Game Over", Color.RED, 20);

    }
    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.AQUAMARINE, "YOU WON!", Color.RED, 20);
    }
    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE*SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();

    }
}
