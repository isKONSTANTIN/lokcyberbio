package su.knst.lokcyberbio.world;

public class Preferences {
    protected int DNASize = 64;
    protected int worldWidth = 100;
    protected int worldHeight = 300;

    protected int botPunchedCost = 150;
    protected int botRegenCost = 50;
    protected int botStepCost = 1;
    protected int botMoveCost = 20;
    protected int botAnotherHitCost = 10;

    protected int botRegenAmount = 25;
    protected int botAnotherEatAmountMin = 10;
    protected int botPhotosynthesisAmount = 10;

    protected int botMaxHealth = 300;
    protected int botMaxEnergy = 1000;

    protected int maxInterpreterStepsPerBot = 64 * 2;
    protected int botDNAEmptyAddition = 10;

    public Preferences() {
    }

    public Preferences(int DNASize, int worldWidth, int worldHeight, int botPunchedCost, int botRegenCost, int botDoublingCost, int botStepCost, int botMoveCost, int botAnotherHitCost, int botRegenAmount, int botAnotherEatAmount, int botPhotosynthesisAmount, int botMaxHealth, int botMaxEnergy, int maxInterpreterStepsPerBot, int botDNAEmptyAddition) {
        this.DNASize = DNASize;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.botPunchedCost = botPunchedCost;
        this.botRegenCost = botRegenCost;
        this.botStepCost = botStepCost;
        this.botMoveCost = botMoveCost;
        this.botAnotherHitCost = botAnotherHitCost;
        this.botRegenAmount = botRegenAmount;
        this.botAnotherEatAmountMin = botAnotherEatAmount;
        this.botPhotosynthesisAmount = botPhotosynthesisAmount;
        this.botMaxHealth = botMaxHealth;
        this.botMaxEnergy = botMaxEnergy;
        this.maxInterpreterStepsPerBot = maxInterpreterStepsPerBot;
        this.botDNAEmptyAddition = botDNAEmptyAddition;
    }

    public int getBotDNAEmptyAddition() {
        return botDNAEmptyAddition;
    }

    public int getBotPhotosynthesisAmount() {
        return botPhotosynthesisAmount;
    }

    public int getBotMaxHealth() {
        return botMaxHealth;
    }

    public int getBotMaxEnergy() {
        return botMaxEnergy;
    }

    public int getBotAnotherHitCost() {
        return botAnotherHitCost;
    }

    public int getBotAnotherEatAmountMin() {
        return botAnotherEatAmountMin;
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

    public int getBotPunchedCost() {
        return botPunchedCost;
    }

    public int getBotRegenCost() {
        return botRegenCost;
    }

    public int getBotStepCost() {
        return botStepCost;
    }

    public int getBotRegenAmount() {
        return botRegenAmount;
    }

    public int getMaxInterpreterStepsPerBot() {
        return maxInterpreterStepsPerBot;
    }
}
