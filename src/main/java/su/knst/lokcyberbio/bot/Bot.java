package su.knst.lokcyberbio.bot;

import su.knst.lokcyberbio.world.Preferences;
import su.knst.lokutils.objects.Color;

public class Bot {
    protected int[] DNA;
    protected Color color;
    protected int health;
    protected int energy;

    protected Preferences preferences;

    public Bot(int[] DNA, int health, int energy, Color color, Preferences preferences) {
        this.DNA = DNA;
        this.health = health;
        this.energy = energy;
        this.color = color;
        this.preferences = preferences;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void changeHealth(int delta) {
        health = Math.max(0, Math.min(preferences.getBotMaxHealth(), health + delta));
    }

    public void changeEnergy(int delta) {
        energy = Math.max(0, Math.min(preferences.getBotMaxEnergy(), energy + delta));
    }

    public void step() {
        changeEnergy(-preferences.getBotStepCost());
    }

    public void hit() {
        changeEnergy(-preferences.getBotAnotherHitCost());
    }

    public Color getColor() {
        return color;
    }

    public void eatAnother(int energy) {
        changeEnergy(energy);
    }

    public void moved() {
        changeEnergy(-preferences.getBotMoveCost());
    }

    public void photosynthesis(float multiplier) {
        changeEnergy((int)(multiplier * preferences.getBotPhotosynthesisAmount()));
    }

    public void regenerated() {
        float regenProportion = preferences.getBotRegenAmount() / (float)preferences.getBotRegenCost();
        int availableEnergy = Math.min(energy, preferences.getBotRegenCost());

        changeEnergy(-availableEnergy);
        changeHealth(Math.round(regenProportion * availableEnergy));
    }

    public void doubled() {
        setEnergy(energy / 3);
    }

    public boolean isAlive() {
        return health > 0 && energy > 0;
    }

    public int[] getDNA() {
        return DNA;
    }

    public int getHealth() {
        return health;
    }

    public int getEnergy() {
        return energy;
    }
}
