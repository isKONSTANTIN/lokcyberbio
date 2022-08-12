package su.knst.lokcyberbio.render;

import org.lwjgl.opengl.*;
import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.world.World;
import su.knst.lokutils.objects.Point;
import su.knst.lokutils.objects.Rect;
import su.knst.lokutils.objects.Size;
import su.knst.lokutils.render.VAO;
import su.knst.lokutils.render.VBO;
import su.knst.lokutils.render.context.RenderingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class WorldRender {
    protected int worldSize;
    protected World world;

    protected float botsSize = 5;
    protected WorldShader shader;

    protected int drawMode;
    protected VAO vao;
    protected VBO rectVBO;
    protected VBO botsPos;
    protected VBO botsColors;
    protected VBO botsSizes;

    protected int botsCount;
    protected RenderMode renderMode = RenderMode.REAL;
    protected Size windowsResolution;
    protected Point offset;

    public WorldRender(World world) throws IOException {
        this.world = world;
        this.worldSize = world.getPreferences().getWorldWidth() * world.getPreferences().getWorldHeight();

        shader = new WorldShader();
        setOffset(Point.ZERO);
    }

    public void render(RenderingContext context) {
        vao.bind();
        shader.bind();

        GL31.glDrawArraysInstanced(drawMode, 0, rectVBO.getSize() / 2, botsCount);

        shader.unbind();
        vao.unbind();
    }

    public void update(Size windowsResolution) {
        this.windowsResolution = windowsResolution;
        shader.update(windowsResolution);

        refresh();
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    public void deltaOffset(Point delta) {
        this.offset = this.offset.offset(delta);
    }

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(RenderMode renderMode) {
        this.renderMode = renderMode;
    }

    public void zoom(float delta) {
        float oldBotsSize = botsSize;

        botsSize = (float) Math.max(0.1, botsSize + delta * botsSize / 5f);

        offset = offset
                .setX(offset.x * (botsSize / oldBotsSize))
                .setY(offset.y * (botsSize / oldBotsSize));
    }

    protected List<PreparedBot> prepareBots() {
        ArrayList<PreparedBot> preparedBots = new ArrayList<>();
        Bot[][] bots = world.getBots();

        for (int y = 0; y < world.getPreferences().getWorldHeight(); y++) {
            for (int x = 0; x < world.getPreferences().getWorldWidth(); x++) {
                Bot bot = bots[x][y];

                if (bot == null)
                    continue;

                boolean isAlive = bot.isAlive();

                switch (renderMode){
                    case REAL:
                        preparedBots.add(new PreparedBot(x, y, isAlive ? 1f : 0.7f, bot.getColor().red / (isAlive ? 1f : 1.5f), bot.getColor().green / (isAlive ? 1f : 1.5f), bot.getColor().blue / (isAlive ? 1f : 1.5f)));
                        break;
                    case HEALTH:
                        preparedBots.add(new PreparedBot(x, y, isAlive ? 1f : 0.7f, bot.getHealth() / (float)world.getPreferences().getBotMaxHealth(), 0, 0));
                        break;
                    case ENERGY:
                        preparedBots.add(new PreparedBot(x, y, isAlive ? 1f : 0.7f, 0, bot.getEnergy() / (float)world.getPreferences().getBotMaxEnergy(), 0));
                        break;
                }
            }
        }

        return preparedBots;
    }

    protected void refresh() {
        if (rectVBO != null)
            rectVBO.delete();

        rectVBO = new VBO();
        Rect rect = new Rect(Point.ZERO, new Size(botsSize, botsSize));
        drawMode = rect.glDrawMode();
        rectVBO.putData(rect.toVertexes());

        List<PreparedBot> preparedBots = prepareBots();
        botsCount = preparedBots.size();

        if (botsPos != null)
            botsPos.delete();

        botsPos = new VBO();

        if (botsColors != null)
            botsColors.delete();

        botsColors = new VBO();

        if (botsSizes != null)
            botsSizes.delete();

        botsSizes = new VBO();

        float[] pos = new float[preparedBots.size() * 2];
        float[] colors = new float[preparedBots.size() * 3];
        float[] sizes = new float[preparedBots.size()];

        for (int b = 0; b < preparedBots.size(); b++) {
            PreparedBot bot = preparedBots.get(b);
            pos[b * 2] = bot.x * botsSize + offset.x + (1 - bot.size) * (botsSize / 2);
            pos[b * 2 + 1] = -bot.y * botsSize + offset.y + (1 - bot.size) * (botsSize / 2);

            colors[b * 3] = bot.r;
            colors[b * 3 + 1] = bot.g;
            colors[b * 3 + 2] = bot.b;

            sizes[b] = bot.size;
        }

        botsPos.putData(pos);
        botsColors.putData(colors);
        botsSizes.putData(sizes);

        if (vao != null)
            vao.delete();

        vao = new VAO();
        vao.bind();
        rectVBO.bind();

        GL31.glEnableVertexAttribArray(0);
        GL31.glVertexAttribPointer(0, 2, GL_FLOAT, false, Float.BYTES * 2, 0);

        rectVBO.unbind();


        botsPos.bind();

        GL31.glEnableVertexAttribArray(1);
        GL31.glVertexAttribPointer(1, 2, GL_FLOAT, false, Float.BYTES * 2, 0);
        glVertexAttribDivisor(1, 1);

        botsPos.unbind();


        botsColors.bind();

        GL31.glEnableVertexAttribArray(2);
        GL31.glVertexAttribPointer(2, 3, GL_FLOAT, false, Float.BYTES * 3, 0);
        glVertexAttribDivisor(2, 1);

        botsColors.unbind();


        botsSizes.bind();

        GL31.glEnableVertexAttribArray(3);
        GL31.glVertexAttribPointer(3, 1, GL_FLOAT, false, Float.BYTES, 0);
        glVertexAttribDivisor(3, 1);

        botsSizes.unbind();

        vao.unbind();
    }

}
