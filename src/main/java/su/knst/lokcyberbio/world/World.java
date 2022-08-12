package su.knst.lokcyberbio.world;

import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.interpreter.Interpreter;
import su.knst.lokutils.objects.Point;

public class World {
    protected final Preferences preferences;
    protected Bot[][] bots;
    protected Interpreter interpreter;
    protected RandomEngine randomEngine;

    public World(Preferences preferences) {
        this.preferences = preferences;
        this.interpreter = new Interpreter(this);
        this.bots = new Bot[preferences.getWorldWidth()][preferences.getWorldHeight()];
        this.randomEngine = new RandomEngine(0, preferences);
    }

    public void spawnBots(int count) {
        for (int i = 0; i < count; i++) {
            Point pos = randomEngine.getRandomMapCoords();
            Bot bot = randomEngine.generateRandomBot();

            Bot source = bots[(int)pos.x][(int)pos.y];

            if (source == null)
                bots[(int)pos.x][(int)pos.y] = bot;
        }
    }

    public void restart() {
        this.bots = new Bot[preferences.getWorldWidth()][preferences.getWorldHeight()];
        spawnBots(500);
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

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public RandomEngine getRandomEngine() {
        return randomEngine;
    }

    public boolean checkBot(int x, int y) {
        x = convertX(x);
        y = convertY(y);

        return bots[x][y] != null;
    }

    public boolean eatBot(int x, int y, int deltaX, int deltaY) {
        x = convertX(x);
        y = convertY(y);

        int toX = convertX(x + deltaX);
        int toY = convertY(y + deltaY);

        Bot source = bots[x][y];
        Bot to = bots[toX][toY];

        if (to == null)
            return false;

        if (to.isAlive()) {
            source.hit();

            if (source.getEnergy() == 0)
                return false;
        }

        source.eatAnother(Math.max(preferences.botAnotherEatAmountMin, to.getEnergy()));
        bots[toX][toY] = null;

        return true;
    }

    public boolean duplicateBot(int x, int y, int deltaX, int deltaY) {
        x = convertX(x);
        y = convertY(y);

        int toX = convertX(x + deltaX);
        int toY = convertY(y + deltaY);

        Bot source = bots[x][y];
        Bot to = bots[toX][toY];

        if (to != null && to.isAlive())
            return false;

        bots[toX][toY] = randomEngine.duplicateBot(source);

        source.doubled();

        return true;
    }

    public Bot getBot(int x, int y) {
        x = convertX(x);
        y = convertY(y);

        return bots[x][y];
    }

    public boolean moveBot(int x, int y, int deltaX, int deltaY) {
        x = convertX(x);
        y = convertY(y);

        int toX = convertX(x + deltaX);
        int toY = convertY(y + deltaY);

        Bot source = bots[x][y];
        Bot to = bots[toX][toY];

        source.moved();

        if (to != null)
            return false;

        bots[toX][toY] = source;
        bots[x][y] = null;

        return true;
    }

    public Bot[][] getBots() {
        return bots;
    }
}
