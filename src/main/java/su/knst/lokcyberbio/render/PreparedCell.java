package su.knst.lokcyberbio.render;

import su.knst.lokutils.objects.Color;

public class PreparedCell {
    public final int x;
    public final int y;

    public final float size;
    public final Color color;

    public PreparedCell(int x, int y, float size, Color color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
    }
}
