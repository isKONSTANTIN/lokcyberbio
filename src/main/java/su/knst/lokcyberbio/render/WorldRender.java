package su.knst.lokcyberbio.render;

import org.lwjgl.opengl.*;
import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.interpreter.DNACommand;
import su.knst.lokcyberbio.world.Cell;
import su.knst.lokcyberbio.world.World;
import su.knst.lokutils.objects.Color;
import su.knst.lokutils.objects.Point;
import su.knst.lokutils.objects.Rect;
import su.knst.lokutils.objects.Size;
import su.knst.lokutils.render.VAO;
import su.knst.lokutils.render.VBO;
import su.knst.lokutils.render.context.RenderingContext;

import java.awt.*;
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

    protected boolean enabled = true;

    public WorldRender(World world) {
        this.world = world;
        this.worldSize = world.getPreferences().getWorldWidth() * world.getPreferences().getWorldHeight();
    }

    public void init() throws IOException {
        this.shader = new WorldShader();
        setOffset(Point.ZERO);
    }

    public void render(RenderingContext context) {
        if (!enabled)
            return;

        vao.bind();
        shader.bind();

        GL31.glDrawArraysInstanced(drawMode, 0, rectVBO.getSize() / 2, botsCount);

        shader.unbind();
        vao.unbind();
    }

    public void update(Size windowsResolution) {
        if (!enabled)
            return;

        this.windowsResolution = windowsResolution;
        shader.update(windowsResolution);

        refresh();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setOffset(Point offset) {
        if (!enabled)
            return;

        this.offset = offset;
    }

    public void deltaOffset(Point delta) {
        if (!enabled)
            return;

        this.offset = this.offset.offset(delta);
    }

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(RenderMode renderMode) {
        this.renderMode = renderMode;
    }

    public void zoom(float delta) {
        if (!enabled)
            return;

        float oldBotsSize = botsSize;

        botsSize = (float) Math.max(0.1, botsSize + delta * botsSize / 5f);

        offset = offset
                .setX(offset.x * (botsSize / oldBotsSize))
                .setY(offset.y * (botsSize / oldBotsSize));
    }

    protected List<PreparedCell> prepareBots() {
        ArrayList<PreparedCell> preparedCells = new ArrayList<>();
        Cell[][] cells = world.getCells();

        for (int y = 0; y < world.getPreferences().getWorldHeight(); y++) {
            for (int x = 0; x < world.getPreferences().getWorldWidth(); x++) {
                Cell cell = cells[x][y];

                if (renderMode == RenderMode.MINERALS){
                    preparedCells.add(new PreparedCell(x, y, 1f, new Color(0.1f, 0.1f, Math.max(0.1f, cell.getMinerals() / 1000f), 1)));
                    continue;
                }

                Bot bot = cell.getBot();

                if (bot == null)
                    continue;

                boolean isAlive = bot.isAlive();

                switch (renderMode){
                    case REAL:
                        preparedCells.add(new PreparedCell(x, y, isAlive ? 1f : 0.7f, new Color(bot.getColor().red / (isAlive ? 1f : 1.5f), bot.getColor().green / (isAlive ? 1f : 1.5f), bot.getColor().blue / (isAlive ? 1f : 1.5f), 1)));
                        break;
                    case TYPE:
                        int eat = 0;
                        int photo = 0;
                        int minerals = 0;

                        for (Integer c : bot.getDNA()) {
                            if (c == DNACommand.EAT_BOT.ordinal())
                                eat++;
                            else if (c == DNACommand.PHOTOSYNTHESIS.ordinal()) {
                                photo++;
                            }else if (c == DNACommand.EAT_MINERALS.ordinal()) {
                                minerals++;
                            }
                        }
                        int sum = eat + photo + minerals;

                        if (sum != 0)
                            preparedCells.add(new PreparedCell(x, y, isAlive ? 1f : 0.7f, new Color(eat / (float)sum, photo / (float)sum, minerals / (float)sum, 1)));
                        else {
                            preparedCells.add(new PreparedCell(x, y, isAlive ? 1f : 0.7f, new Color(0, 0, 0, 1)));
                        }

//                        for (Integer c : bot.getDNA()) {
//                            if (c == 0) {
//                                preparedCells.add(new PreparedCell(x, y, isAlive ? 1f : 0.7f, new Color(1,0, 0, 1)));
//                                break;
//                            } else if (c == 1) {
//                                preparedCells.add(new PreparedCell(x, y, isAlive ? 1f : 0.7f, new Color(0,1, 0, 1)));
//                                break;
//                            }
//                        }

                        break;
                    case ENERGY:
                        preparedCells.add(new PreparedCell(x, y, isAlive ? 1f : 0.7f,  new Color(0, bot.getEnergy() / (float)world.getPreferences().getBotMaxEnergy(), 0, 1)));
                        break;
                }
            }
        }

        return preparedCells;
    }

    protected void refresh() {
        if (rectVBO != null)
            rectVBO.delete();

        rectVBO = new VBO();
        Rect rect = new Rect(Point.ZERO, new Size(botsSize, botsSize));
        drawMode = rect.glDrawMode();
        rectVBO.putData(rect.toVertexes());

        List<PreparedCell> preparedCells = prepareBots();
        botsCount = preparedCells.size();

        if (botsPos != null)
            botsPos.delete();

        botsPos = new VBO();

        if (botsColors != null)
            botsColors.delete();

        botsColors = new VBO();

        if (botsSizes != null)
            botsSizes.delete();

        botsSizes = new VBO();

        float[] pos = new float[preparedCells.size() * 2];
        float[] colors = new float[preparedCells.size() * 4];
        float[] sizes = new float[preparedCells.size()];

        for (int b = 0; b < preparedCells.size(); b++) {
            PreparedCell bot = preparedCells.get(b);

            pos[b * 2] = bot.x * botsSize + offset.x + (1 - bot.size) * (botsSize / 2);
            pos[b * 2 + 1] = -bot.y * botsSize + offset.y + (1 - bot.size) * (botsSize / 2);

            colors[b * 4] = bot.color.red;
            colors[b * 4 + 1] = bot.color.green;
            colors[b * 4 + 2] = bot.color.blue;
            colors[b * 4 + 3] = bot.color.alpha;

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
        GL31.glVertexAttribPointer(2, 4, GL_FLOAT, false, Float.BYTES * 4, 0);
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
