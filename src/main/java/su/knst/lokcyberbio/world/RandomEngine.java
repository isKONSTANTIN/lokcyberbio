package su.knst.lokcyberbio.world;

import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.interpreter.utils.DNACommand;
import su.knst.lokcyberbio.interpreter.utils.Direction;
import su.knst.lokcyberbio.interpreter.utils.DirectionProvider;
import su.knst.lokutils.objects.Color;
import su.knst.lokutils.objects.Point;

import java.util.Arrays;
import java.util.Random;

public class RandomEngine {
    protected long seed;
    protected Random random;
    protected Preferences preferences;

    public RandomEngine(long seed, Preferences preferences) {
        this.seed = seed;

        this.random = new Random(seed);
        this.preferences = preferences;
    }

    public Point getRandomMapCoords() {
        return new Point(random.nextInt(preferences.getWorldWidth()), random.nextInt(preferences.getWorldHeight()));
    }

    protected Color childColor(Color source) {
        return new Color(
                Math.max(0, Math.min(1, source.red + (random.nextFloat() * 0.05f - 0.0025f))),
                Math.max(0, Math.min(1, source.green + (random.nextFloat() * 0.05f - 0.0025f))),
                Math.max(0, Math.min(1, source.blue + (random.nextFloat() * 0.05f - 0.0025f))),
                source.alpha
        );
    }

    public Direction randomDirection() {
        return DirectionProvider.getDirection(random.nextInt(8));
    }

    public Bot duplicateBot(Bot source) {
        int[] newDNA = Arrays.copyOf(source.getDNA(), source.getDNA().length);
        int max = DNACommand.values().length + preferences.getBotDNAEmptyAddition();
        Color sourceColor = source.getColor();
        Color newColor;

        if (random.nextInt(50) == 0) {
            newDNA[random.nextInt(newDNA.length)] = random.nextInt(max);

            newColor = childColor(sourceColor);
        }else {
            newColor = sourceColor;
        }

        return new Bot(newDNA, preferences.getBotMaxHealth(), (int)(source.getEnergy() / 2f), newColor, preferences);
    }

    public Bot generateRandomBot() {
        int max = DNACommand.values().length + preferences.getBotDNAEmptyAddition();

        int[] DNA = random.ints(0, max).limit(preferences.getDNASize()).toArray();
        Color color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);

        return new Bot(DNA, preferences.getBotMaxHealth(), preferences.getBotMaxEnergy(), color, preferences);
    }
}
