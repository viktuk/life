package xyz.viktuk.game.life;

import xyz.viktuk.game.FileUtils;
import xyz.viktuk.game.FrameRenderer;
import xyz.viktuk.game.GameWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class GameOfLife extends GameWindow implements FrameRenderer {

    private final static String GAME_TITLE = "Game of life by Viktuk v0.1";

    private final JFileChooser fileChooser = new JFileChooser();

    private final Field oldField;
    private final Field newField;
    private final int cellWidth;
    private final int cellHeight;

    private boolean running;

    long generation;

    public GameOfLife(int width, int height, int fieldWidth, int fieldHeight) {
        super(GAME_TITLE, 100);
        setRenderer(this);
        setResizable(false);
        setSize(width, height);
        setShowFPS(true);
        oldField = new Field(fieldWidth, fieldHeight);
        newField = new Field(fieldWidth, fieldHeight);
        cellWidth = width / fieldWidth;
        cellHeight = height / fieldHeight;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!running) {
                    oldField.reverse(e.getX() / cellWidth, e.getY() / cellHeight);
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == ' ') {
                    running = !running;
                    if (running) {
                        setTitle(GAME_TITLE + " - Running");
                    } else {
                        setTitle(GAME_TITLE + " - Stopped");
                    }
                }
                if (!running) {
                    if(e.getKeyChar() == '\n') {
                        oldField.clear();
                    }else if(e.getKeyChar() == 'r'){
                        oldField.randomize();
                    }else if(e.getKeyChar() == 's'){
                        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();
                            try {
                                FileUtils.save(file, oldField);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }else if(e.getKeyChar() == 'o'){
                        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();
                            try {
                                Field savedField = (Field) FileUtils.open(file);
                                oldField.copyValuesFrom(savedField);
                            } catch (IOException | ClassNotFoundException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        setVisible(true);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height) {
        if (!running) {
            drawGrid(g2d, width, height);
        }
        g2d.setColor(Color.RED);
        for (int x = 0; x < oldField.getWidth(); x++) {
            for (int y = 0; y < oldField.getHeight(); y++) {
                if (oldField.getValue(x, y)) {
                    g2d.fillRect(x * cellWidth, y * cellHeight, cellWidth, cellHeight);
                }
                if (!running) {
                    continue;
                }
                int neighbourCount = 0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) {
                            continue;
                        }
                        /*if(dx==dy) {
                            continue;
                        }*/
                        if (oldField.getValue(x + dx, y + dy)) {
                            neighbourCount++;
                        }
                    }
                }

                if (neighbourCount == 3) {
                    newField.setValue(x, y, true);
                } else if (neighbourCount == 2) {
                    newField.setValue(x, y, oldField.getValue(x, y));
                }

/*                if (neighbourCount > 4) {
                    newField.setValue(x, y, true);
                } else if (neighbourCount > 3) {
                    newField.setValue(x, y, oldField.getValue(x, y));
                }*/
            }
        }
        if (running) {
            oldField.copyValuesFrom(newField);
            newField.clear();
            generation++;
            g2d.setFont(new Font("Courier New", Font.PLAIN, 12));
            g2d.setColor(Color.GREEN);
            g2d.drawString(String.format("Generation: %s", generation), 20, 40);
        } else {
            generation = 0;
        }
    }

    private void drawGrid(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.BLACK);
        for (int x = 0; x < oldField.getWidth(); x++) {
            g2d.drawLine(x * cellWidth, 0, x * cellWidth, height);
            for (int y = 0; y < oldField.getHeight(); y++) {
                g2d.drawLine(0, y * cellHeight, width, y * cellHeight);
            }
        }
    }

    public static void main(String[] args) {
        new GameOfLife(800, 800, 100, 100);
    }
}
