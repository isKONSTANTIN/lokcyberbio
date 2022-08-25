package su.knst.lokcyberbio.world;

import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.interpreter.DNACommand;
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
        int modification = random.nextInt(2) == 0 ? 1 : -1;

        return new Color(
                Math.max(0, Math.min(1, source.red + (random.nextFloat() * 0.05f) * modification)),
                Math.max(0, Math.min(1, source.green + (random.nextFloat() * 0.05f) * modification)),
                Math.max(0, Math.min(1, source.blue + (random.nextFloat() * 0.05f) * modification)),
                source.alpha
        );
    }

    public Direction randomDirection() {
        return DirectionProvider.DIRECTIONS[random.nextInt(8)];
    }

    public Bot duplicateBot(Bot source) {
        int[] newDNA = Arrays.copyOf(source.getDNA(), source.getDNA().length);
        int max = preferences.getBotDNAMaxCommand();

        Color sourceColor = source.getColor();
        Color newColor;

        if (random.nextInt(preferences.getRadiation()) == 0) {
            for (int i = 0; i < preferences.getDNAMutation(); i++){
                newDNA[random.nextInt(newDNA.length)] = random.nextInt(max);
            }

            newColor = childColor(sourceColor);
        }else {
            newColor = sourceColor;
        }

        return new Bot(newDNA, (int)(source.getEnergy() / 2f), newColor, preferences);
    }

    public Bot generateRandomBot() {
        int max = preferences.getBotDNAMaxCommand();

        int[] DNA = random.ints(0, max).limit(preferences.getDNASize()).toArray();
        Color color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);

        return new Bot(DNA, preferences.getBotMaxEnergy(), color, preferences);
    }

    public void reset() {
        this.random = new Random(seed);
    }
}
