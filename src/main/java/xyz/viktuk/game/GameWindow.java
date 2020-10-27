package xyz.viktuk.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class GameWindow {
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final GraphicsDevice gd = ge.getDefaultScreenDevice();
    private final GraphicsConfiguration gc = gd.getDefaultConfiguration();
    private final BufferStrategy buffer;
    private BufferedImage bi;

    private final JFrame app;
    private final Canvas canvas;

    private FrameRenderer renderer;

    private Color background = Color.WHITE;

    private boolean showFPS;

    private int fps;
    private int frames;
    private long totalTime;
    private long curTime;

    public GameWindow(String title) {
        this(title, -1);
    }

    public GameWindow(String title, int frameDelay) {
        this(title, 640, 480, frameDelay);
    }

    public GameWindow(String title, int width, int height, int frameDelay) {
        app = new JFrame();
        app.setTitle(title);
        app.setIgnoreRepaint(true);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create canvas for painting...
        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setSize(width, height);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();

        // Add canvas to game window...
        app.add(canvas);
        app.pack();

        app.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                canvas.setSize(app.getSize());
                bi = gc.createCompatibleImage(canvas.getWidth(), canvas.getHeight());
            }
        });

        // Create BackBuffer...
        canvas.createBufferStrategy(2);
        buffer = canvas.getBufferStrategy();

        bi = gc.createCompatibleImage(width, height);

        if (frameDelay < 0) {
            new Thread(() -> {
                while (true) {
                    drawFrame();
                }
            }).start();
        } else if (frameDelay > 0) {
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    drawFrame();
                }
            }, 0, frameDelay);
        }
    }

    protected void drawFrame() {
        Graphics graphics = null;
        Graphics2D g2d = null;
        try {
            long lastTime = curTime;
            curTime = System.currentTimeMillis();
            if (showFPS) {
                totalTime += curTime - lastTime;
                if (totalTime > 1000) {
                    totalTime -= 1000;
                    fps = frames;
                    frames = 0;
                }
                ++frames;
            }

            int width = canvas.getWidth();
            int height = canvas.getHeight();

            // clear back buffer...
            g2d = bi.createGraphics();
            g2d.setColor(background);
            g2d.fillRect(0, 0, width, height);

            if (renderer != null) {
                renderer.render(g2d, width, height);
            }

            if (showFPS) {
                // display frames per second...
                g2d.setFont(new Font("Courier New", Font.PLAIN, 12));
                g2d.setColor(Color.GREEN);
                g2d.drawString(String.format("FPS: %s", fps), 20, 20);
            }

            // Blit image and flip...
            graphics = buffer.getDrawGraphics();
            graphics.drawImage(bi, 0, 0, null);
            if (!buffer.contentsLost())
                buffer.show();

            // Let the OS have a little time...
            Thread.yield();
        } finally {
            // release resources
            if (graphics != null)
                graphics.dispose();
            if (g2d != null)
                g2d.dispose();
        }
    }

    public void setSize(int width, int height){
        app.setSize(width, height);
    }

    public void setTitle(String title) {
        app.setTitle(title);
    }

    public void setVisible(boolean visible) {
        app.setVisible(visible);
    }

    public void setResizable(boolean resizable) {
        app.setResizable(resizable);
    }

    public void setShowFPS(boolean showFPS) {
        this.showFPS = showFPS;
    }

    public void setRenderer(FrameRenderer renderer) {
        this.renderer = renderer;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public void addKeyListener(KeyListener keyListener) {
        canvas.addKeyListener(keyListener);
    }

    public void removeKeyListener(KeyListener keyListener) {
        canvas.removeKeyListener(keyListener);
    }

    public KeyListener[] getKeyListeners() {
        return canvas.getKeyListeners();
    }

    public void addMouseListener(MouseListener mouseListener) {
        canvas.addMouseListener(mouseListener);
    }

    public void removeMouseListener(MouseListener mouseListener) {
        canvas.removeMouseListener(mouseListener);
    }

    public MouseListener[] getMouseListeners() {
        return canvas.getMouseListeners();
    }

    public int getWidth(){
        return canvas.getWidth();
    }

    public int getHeight(){
        return canvas.getHeight();
    }
}
