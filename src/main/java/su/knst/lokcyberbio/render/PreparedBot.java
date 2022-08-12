package su.knst.lokcyberbio.render;

public class PreparedBot {
    protected int x;
    protected int y;

    protected float size;

    protected float r;
    protected float g;
    protected float b;

    public PreparedBot(int x, int y, float size, float r, float g, float b) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }
}
