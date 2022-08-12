package su.knst.lokcyberbio.interpreter;

import jdk.jfr.internal.tool.PrettyWriter;
import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.interpreter.utils.*;
import su.knst.lokcyberbio.world.Preferences;
import su.knst.lokcyberbio.world.World;

import java.util.HashMap;

public class Interpreter {
    protected World world;
    protected int maxBotSteps;

    protected HashMap<DNACommand, CommandExecutor> commandsExecutors = new HashMap<>();

    public Interpreter(World world) {
        this.world = world;
        this.maxBotSteps = world.getPreferences().getMaxInterpreterStepsPerBot();

        initExecutors();
    }

    protected void initExecutors() {
        addExecutor(DNACommand.EAT_BOT, (bot, x, y, arg) -> {
            Direction direction = DirectionProvider.getDirection(arg);

            if (direction == null)
                direction = world.getRandomEngine().randomDirection();

            return new ExecutorResult(world.eatBot(x, y, direction.deltaX, direction.deltaY), 0);
        });

        addExecutor(DNACommand.MOVE_POINTER, (bot, x, y, arg) -> new ExecutorResult(true, arg));

        addExecutor(DNACommand.PHOTOSYNTHESIS, (bot, x, y) -> {
            bot.photosynthesis(y / (float)world.getPreferences().getWorldHeight());

            return new ExecutorResult(true, 0);
        });

        addExecutor(DNACommand.CHECK_ENERGY, (bot, x, y, arg) ->
                new ExecutorResult(true, bot.getEnergy() > world.getPreferences().getBotMaxEnergy() / 2f ? 0 : 2)
        );

        addExecutor(DNACommand.CHECK_BOT, (bot, x, y, arg) -> {
            Direction direction = DirectionProvider.getDirection(arg);

            if (direction == null)
                return new ExecutorResult(false, 2);

            boolean result = world.checkBot(x + direction.deltaX, y + direction.deltaY);

            return new ExecutorResult(result, result ? 0 : 2);
        });

        addExecutor(DNACommand.MOVE, (bot, x, y, arg) -> {
            Direction direction = DirectionProvider.getDirection(arg);

            if (direction == null)
                direction = world.getRandomEngine().randomDirection();

            return new ExecutorResult(world.moveBot(x, y, direction.deltaX, direction.deltaY), 0);
        });

        addExecutor(DNACommand.CHECK_HEALTH, (bot, x, y) ->
                new ExecutorResult(true, bot.getHealth() > world.getPreferences().getBotMaxHealth() / 2f ? 0 : 2)
        );

        addExecutor(DNACommand.REGENERATE, (bot, x, y) -> {
            bot.regenerated();

            return new ExecutorResult(true, 0);
        });

        addExecutor(DNACommand.CHECK_DNA_BOT, (bot, x, y, arg) -> {
            Direction direction = world.getRandomEngine().randomDirection();

            if (direction == null)
                return new ExecutorResult(false, 2);

            Bot alien = world.getBot(x + direction.deltaX, y + direction.deltaY);

            if (alien == null)
                return new ExecutorResult(true, 2);

            int[] alienDNA = alien.getDNA();
            int[] sourceDNA = bot.getDNA();

            for (int i = 0; i < sourceDNA.length; i++) {
                if (alienDNA[i] != sourceDNA[i])
                    return new ExecutorResult(true, 2);
            }

            return new ExecutorResult(true, 0);
        });

        addExecutor(DNACommand.DUPLICATE, (bot, x, y) -> {
            Direction direction = world.getRandomEngine().randomDirection();

            return new ExecutorResult(world.duplicateBot(x, y, direction.deltaX, direction.deltaY), 0);
        });
    }

    protected void addExecutor(DNACommand command, CommandExecutor executor) {
        if (commandsExecutors.containsKey(command))
            return;

        commandsExecutors.put(command, executor);
    }

    protected void addExecutor(DNACommand command, CommandExecutorWithArg executor) {
        if (commandsExecutors.containsKey(command))
            return;

        commandsExecutors.put(command, executor);
    }

    public void step() {
        Bot[][] bots = world.getBots();
        Preferences preferences = world.getPreferences();

        for (int y = 0; y < preferences.getWorldHeight(); y++) {
            for (int x = 0; x < preferences.getWorldWidth(); x++) {
                Bot bot = bots[x][y];

                if (bot == null || !bot.isAlive())
                    continue;

                stepBot(bot, x, y);
            }
        }
    }

    protected void stepBot(Bot bot, int x, int y) {
        int[] dna = bot.getDNA();
        DNACommand[] dnaCommands = DNACommand.values();

        int pointer = 0;
        bot.changeHealth(-1);
        for (int i = 0; i < maxBotSteps; i++){
            if (pointer >= dna.length || pointer < 0)
                break;

            bot.step();

            if (!bot.isAlive())
                return;

            int rawCommand = dna[pointer];

            if (rawCommand >= dnaCommands.length || rawCommand < 0)
                break;

            DNACommand command = dnaCommands[rawCommand];
            ExecutorResult result;

            if (command.needArgument) {
                if (pointer + 1 >= dna.length)
                    break;

                pointer++;
                int arg = dna[pointer];

                result = ((CommandExecutorWithArg) commandsExecutors.get(command)).run(bot, x, y, arg);
            }else
                result = commandsExecutors.get(command).run(bot, x, y);

            if (command.breakable && result.ok)
                break;

            pointer += result.skip;

            pointer++;
        }
    }
}
