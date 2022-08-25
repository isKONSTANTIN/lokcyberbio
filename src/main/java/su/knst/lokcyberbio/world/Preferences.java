package su.knst.lokcyberbio.world;

import su.knst.lokcyberbio.interpreter.DNACommand;

public class Preferences {
    protected int DNASize = 64;
    protected int worldWidth = 200;
    protected int worldHeight = 200;

    protected int botDoublingCost = 60;
    protected int botStepCost = 1;
    protected int botMoveCost = 10;
    protected int botAnotherHitCost = 50;

    protected float botMoveEnergyAmount = 0.5f;
    protected int botPhotosynthesisAmount = 30;
    protected int botEatMineralAmount = 60;
    protected int botEatMineralCost = 100;
    protected int botMaxEnergy = 800;

    protected int maxInterpreterStepsPerBot = 64 * 2;
    protected int botDNAMaxCommand = Math.max(DNACommand.values().length, 64);
    protected int radiation = 4; // 1 / 4
    protected int DNAMutation = 1; // 1 per radiation event

    public Preferences() {
    }

    public int getRadiation() {
        return radiation;
    }

    public int getDNAMutation() {
        return DNAMutation;
    }

    public int getBotEatMineralAmount() {
        return botEatMineralAmount;
    }

    public int getBotEatMineralCost() {
        return botEatMineralCost;
    }

    public float getBotMoveEnergyAmount() {
        return botMoveEnergyAmount;
    }

    public int getBotDoublingCost() {
        return botDoublingCost;
    }

    public int getBotDNAMaxCommand() {
        return botDNAMaxCommand;
    }

    public int getBotPhotosynthesisAmount() {
        return botPhotosynthesisAmount;
    }

    public int getBotMaxEnergy() {
        return botMaxEnergy;
    }

    public int getBotAnotherHitCost() {
        return botAnotherHitCost;
    }

    public int getDNASize() {
        return DNASize;
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getBotMoveCost() {
        return botMoveCost;
    }

    public int getBotStepCost() {
        return botStepCost;
    }

    public int getMaxInterpreterStepsPerBot() {
        return maxInterpreterStepsPerBot;
    }
}
