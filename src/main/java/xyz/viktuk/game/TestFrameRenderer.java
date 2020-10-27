package xyz.viktuk.game;

import xyz.viktuk.game.FrameRenderer;

import java.awt.*;
import java.util.Random;

public class TestFrameRenderer implements FrameRenderer {
    private final Random rand = new Random();

    public void render(Graphics2D g2d, int width, int height) {
        // draw some rectangles...
        for (int i = 0; i < 20; ++i) {
            int r = rand.nextInt(256);
            int g = rand.nextInt(256);
            int b = rand.nextInt(256);
            g2d.setColor(new Color(r, g, b));
            int x = rand.nextInt(width / 2);
            int y = rand.nextInt(height / 2);
            int w = rand.nextInt(width / 2);
            int h = rand.nextInt(height / 2);
            g2d.fillRect(x, y, w, h);
        }
    }
}
