package su.knst.lokcyberbio.bot;

import su.knst.lokcyberbio.world.Preferences;
import su.knst.lokutils.objects.Color;

public class Bot {
    protected int[] DNA;
    protected Color color;
    protected int energy;

    protected Preferences preferences;

    public Bot(int[] DNA, int energy, Color color, Preferences preferences) {
        this.DNA = DNA;
        this.energy = energy;
        this.color = color;
        this.preferences = preferences;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
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

    public void eatMineral(int count) {
        changeEnergy(count);
    }

    public void doubled() {
        changeEnergy(-(preferences.getBotDoublingCost() + energy / 2));
    }

    public boolean isAlive() {
        return energy > 0;
    }

    public int[] getDNA() {
        return DNA;
    }

    public int getEnergy() {
        return energy;
    }
}
