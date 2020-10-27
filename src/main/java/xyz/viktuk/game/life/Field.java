package xyz.viktuk.game.life;

import java.io.Serializable;
import java.util.Random;

public class Field implements Serializable {
    private final boolean[][] values;
    private final int width;
    private final int height;

    private static final Random random = new Random();

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        values = new boolean[width][height];
    }

    public boolean getValue(int x, int y) {
        x = normalizeX(x);
        y = normalizeY(y);
        return values[x][y];
    }

    public void setValue(int x, int y, boolean value) {
        x = normalizeX(x);
        y = normalizeY(y);
        values[x][y] = value;
    }

    public void reverse(int x, int y) {
        x = normalizeX(x);
        y = normalizeY(y);
        values[x][y] = !values[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void copyValuesFrom(Field field){
        if(width!=field.width || height!= field.height){
            throw new IllegalArgumentException();
        }
        for (int x = 0; x < width; x++) {
            System.arraycopy(field.values[x], 0, values[x], 0, height);
        }
    }

    public void clear(){
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                values[x][y] = false;
            }
        }
    }

    public void randomize(){
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                values[x][y] = random.nextBoolean();
            }
        }
    }

    private int normalize(int value, int maxValue) {
        if (value >= maxValue) {
            value = value % maxValue;
        } else if (value < 0) {
            value = maxValue + value % maxValue;
        }
        return value;
    }

    private int normalizeX(int x){
        return normalize(x, width);
    }

    private int normalizeY(int y){
        return normalize(y, height);
    }
}
