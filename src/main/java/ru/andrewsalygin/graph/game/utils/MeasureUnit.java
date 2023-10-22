package ru.andrewsalygin.graph.game.utils;

public class MeasureUnit {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private float width;
    private float height;
    private int size1;
    private int size2;
    private float screenResolutionX;
    private float screenResolutionY;

    public MeasureUnit(float x1, float y1, float x2, float y2, float width, float height, int size1, int size2,
                       float screenResolutionX, float screenResolutionY) {
        this.x1 = screenResolutionX * x1;
        this.y1 = screenResolutionY * y1;
        this.x2 = screenResolutionX * x2;
        this.y2 = screenResolutionY * y2;
        this.size1 = (int) (screenResolutionX * size1); // ? screenResolutionY
        this.size2 = (int) (screenResolutionX * size2); // ? screenResolutionY
        this.width = width;
        this.height = height;
        this.screenResolutionX = screenResolutionX;
        this.screenResolutionY = screenResolutionY;
    }

    public float getScreenResolutionX() {
        return screenResolutionX;
    }

    public void setScreenResolutionX(float screenResolutionX) {
        this.screenResolutionX = screenResolutionX;
        setWidth(width);
        setX1(x1);
        setX2(x2);
    }

    public float getScreenResolutionY() {
        return screenResolutionY;
    }

    public void setScreenResolutionY(float screenResolutionY) {
        this.screenResolutionY = screenResolutionY;
        setHeight(height);
        setY1(y1);
        setY2(y2);
    }

    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = screenResolutionX * x1;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = screenResolutionY * y1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = screenResolutionX * x2;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = screenResolutionY * y2;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = screenResolutionX * width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = screenResolutionY * height;
    }

    public int getSize1() {
        return size1;
    }

    public void setSize1(int size1) {
        this.size1 = (int) (screenResolutionX * size1); // Y?
    }

    public int getSize2() {
        return size2;
    }

    public void setSize2(int size2) {
        this.size2 = (int) (screenResolutionX * size2); // Y?
    }
}
