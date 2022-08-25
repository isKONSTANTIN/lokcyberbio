package su.knst.lokcyberbio.world;

import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.bot.DNAConstructor;
import su.knst.lokcyberbio.interpreter.Interpreter;
import su.knst.lokcyberbio.interpreter.DNACommand;
import su.knst.lokutils.objects.Color;
import su.knst.lokutils.objects.Point;

public class World {
    protected final Preferences preferences;
    protected Cell[][] cells;
    protected boolean[][] cellsHandled;
    protected Interpreter interpreter;
    protected RandomEngine randomEngine;

    public World(Preferences preferences, long seed) {
        this.preferences = preferences;
        this.interpreter = new Interpreter(this);
        initCells();
        this.randomEngine = new RandomEngine(seed, preferences);
    }

    protected void initCells() {
        int width = preferences.getWorldWidth();
        int height = preferences.getWorldHeight();

        this.cells = new Cell[width][height];
        this.cellsHandled = new boolean[width][height];

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                cells[x][y] = new Cell();
                cellsHandled[x][y] = false;
            }
        }
    }

    protected void resetHandledCells() {
        int width = preferences.getWorldWidth();
        int height = preferences.getWorldHeight();

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                cellsHandled[x][y] = false;
            }
        }
    }

    public void spawnBots(int count) {
        for (int i = 0; i < count; i++) {
            Point pos = randomEngine.getRandomMapCoords();
            Bot bot = randomEngine.generateRandomBot();

            Bot source = cells[(int)pos.x][(int)pos.y].getBot();

            if (source == null || !source.isAlive())
                cells[(int)pos.x][(int)pos.y].setBot(bot);
        }
    }

    public void restart() {
        initCells();
        this.interpreter.reset();
        this.randomEngine.reset();
    }

    protected int convertX(int x) {
        return x - preferences.worldWidth * (int)Math.floor(x / (float)preferences.worldWidth);
    }

    protected int convertY(int y) {
        return Math.min(preferences.worldHeight - 1, Math.max(0, y));
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void step() {
        resetHandledCells();
        interpreter.step();
    }

    public long getBotsCount() {
        return interpreter.getBotsCount();
    }

    public long getSteps() {
        return interpreter.getSteps();
    }

    public RandomEngine getRandomEngine() {
        return randomEngine;
    }

    public boolean checkBot(int x, int y) {
        x = convertX(x);
        y = convertY(y);

        return cells[x][y].getBot() != null;
    }

    public boolean eatBot(int x, int y, int deltaX, int deltaY) {
        x = convertX(x);
        y = convertY(y);

        int toX = convertX(x + deltaX);
        int toY = convertY(y + deltaY);

        Bot source = cells[x][y].getBot();
        Bot to = cells[toX][toY].getBot();

        source.hit();

        if (to == null || source.getEnergy() == 0)
            return false;

        source.eatAnother(to.getEnergy());

        cells[toX][toY].setBot(null);
        cellsHandled[toX][toY] = true;
        cellsHandled[x][x] = true;

        return true;
    }

    public boolean duplicateBot(int x, int y, int deltaX, int deltaY) {
        x = convertX(x);
        y = convertY(y);

        int toX = convertX(x + deltaX);
        int toY = convertY(y + deltaY);

        Bot source = cells[x][y].getBot();
        Bot to = cells[toX][toY].getBot();

        if (to != null)
            return false;

        cells[toX][toY].setBot(randomEngine.duplicateBot(source));

        source.doubled();
        cellsHandled[toX][toY] = true;
        cellsHandled[x][x] = true;

        return true;
    }

    public Bot getBot(int x, int y) {
        return getCell(x, y).getBot();
    }

    public Cell getCell(int x, int y) {
        x = convertX(x);
        y = convertY(y);

        return cells[x][y];
    }

    public boolean moveBot(int x, int y, int deltaX, int deltaY) {
        x = convertX(x);
        y = convertY(y);

        int toX = convertX(x + deltaX);
        int toY = convertY(y + deltaY);

        Bot source = cells[x][y].getBot();
        Bot to = cells[toX][toY].getBot();

        source.moved();

        if (to != null)
            return false;

        cells[toX][toY].setBot(source);
        cells[x][y].setBot(null);

        cellsHandled[toX][toY] = true;
        cellsHandled[x][x] = true;

        return true;
    }

    public boolean moveEnergy(int x, int y, int deltaX, int deltaY, int amount) {
        x = convertX(x);
        y = convertY(y);

        int toX = convertX(x + deltaX);
        int toY = convertY(y + deltaY);

        Bot source = cells[x][y].getBot();
        Bot to = cells[toX][toY].getBot();

        if (to == null || !to.isAlive())
            return false;

        if (source.getEnergy() < amount)
            return false;

        source.changeEnergy(-amount);
        to.changeEnergy(amount);

        return true;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public boolean[][] getCellsHandled() {
        return cellsHandled;
    }
}
