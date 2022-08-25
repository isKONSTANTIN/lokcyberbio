package su.knst.lokcyberbio.interpreter;

import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.interpreter.utils.*;
import su.knst.lokcyberbio.world.Cell;
import su.knst.lokcyberbio.world.World;

import java.util.HashMap;

public class CommandsExecutors {
    protected HashMap<DNACommand, CommandExecutor> commandsExecutors = new HashMap<>();
    protected World world;

    public CommandsExecutors(World world) {
        this.world = world;

        init();
    }

    public ExecutorResult run(DNACommand command, Bot bot, int x, int y, int arg) {
        return ((CommandExecutorWithArg) commandsExecutors.get(command)).run(bot, x, y, arg);
    }

    public ExecutorResult run(DNACommand command, Bot bot, int x, int y) {
        return commandsExecutors.get(command).run(bot, x, y);
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

    protected void init() {
        addExecutor(DNACommand.EAT_BOT, this::eatBot);
        addExecutor(DNACommand.EAT_MINERALS, this::eatMinerals);
        addExecutor(DNACommand.MOVE_POINTER, this::movePointer);
        addExecutor(DNACommand.PHOTOSYNTHESIS, this::photosynthesis);
        addExecutor(DNACommand.CHECK_ENERGY, this::checkEnergy);
        addExecutor(DNACommand.CHECK_BOT, this::checkBot);
        addExecutor(DNACommand.MOVE, this::move);
        addExecutor(DNACommand.MOVE_ENERGY, this::moveEnergy);
        addExecutor(DNACommand.CHECK_DNA_BOT, this::checkDNA);
        addExecutor(DNACommand.BREAK, this::breakInterpreter);
    }

    protected ExecutorResult breakInterpreter(Bot bot, int x, int y) {
        return new ExecutorResult(true, 0);
    }

    protected ExecutorResult eatBot(Bot bot, int x, int y, int arg) {
        Direction direction = DirectionProvider.getDirection(arg);

        world.eatBot(x, y, direction.deltaX, direction.deltaY);

        return new ExecutorResult(true, 0);
    }

    protected ExecutorResult movePointer(Bot bot, int x, int y, int arg) {
        return new ExecutorResult(true, arg);
    }

    protected ExecutorResult photosynthesis(Bot bot, int x, int y) {
        bot.photosynthesis(y / (float)world.getPreferences().getWorldHeight());

        return new ExecutorResult(true, 0);
    }

    protected ExecutorResult eatMinerals(Bot bot, int x, int y) {
        Cell cell = world.getCells()[x][y];

        if (cell.getMinerals() > world.getPreferences().getBotEatMineralCost()) {
            bot.eatMineral(world.getPreferences().getBotEatMineralAmount());
            cell.deltaMinerals(-world.getPreferences().getBotEatMineralCost());
        }

        return new ExecutorResult(true, 0);
    }

    protected ExecutorResult checkBot(Bot bot, int x, int y, int arg) {
        Direction direction = DirectionProvider.getDirection(arg);

        boolean result = world.checkBot(x + direction.deltaX, y + direction.deltaY);

        return new ExecutorResult(result, result ? 0 : 2);
    }

    protected ExecutorResult move(Bot bot, int x, int y, int arg) {
        Direction direction = DirectionProvider.getDirection(arg);

        return new ExecutorResult(world.moveBot(x, y, direction.deltaX, direction.deltaY), 0);
    }

    protected ExecutorResult checkEnergy(Bot bot, int x, int y, int arg) {
        return new ExecutorResult(true, bot.getEnergy() >= Interpreter.getValueFromDNAArg(arg, world.getPreferences().getBotMaxEnergy()) ? 0 : 2);
    }

    protected ExecutorResult moveEnergy(Bot bot, int x, int y, int arg) {
        Direction direction = DirectionProvider.getDirection(arg);

        return new ExecutorResult(world.moveEnergy(x, y, direction.deltaX, direction.deltaY, (int)(bot.getEnergy() * world.getPreferences().getBotMoveEnergyAmount())), 0);
    }

    protected ExecutorResult checkDNA(Bot bot, int x, int y, int arg) {
        Direction direction = DirectionProvider.getDirection(arg);

        if (direction == null)
            return new ExecutorResult(false, 2);

        Bot alien = world.getBot(x + direction.deltaX, y + direction.deltaY);

        if (alien == null)
            return new ExecutorResult(true, 2);

        int[] alienDNA = alien.getDNA();
        int[] sourceDNA = bot.getDNA();

        for (int i = 0; i < alienDNA.length; i++) {
            if (alienDNA[i] != sourceDNA[i])
                return new ExecutorResult(true, 2);
        }

        return new ExecutorResult(true, 0);
    }
}
