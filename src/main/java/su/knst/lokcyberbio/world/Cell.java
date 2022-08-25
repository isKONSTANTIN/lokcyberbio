package su.knst.lokcyberbio.world;

import su.knst.lokcyberbio.bot.Bot;

public class Cell {
    protected Bot bot;
    protected int minerals;

    public Cell() {
    }

    public Bot getBot() {
        return bot;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public int getMinerals() {
        return minerals;
    }

    public void setMinerals(int minerals) {
        this.minerals = minerals;
    }

    public void deltaMinerals(int delta) {
        minerals += delta;
    }
}
