package su.knst.lokcyberbio.interpreter;

import org.lwjgl.system.linux.XPropertyEvent;
import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.interpreter.utils.*;
import su.knst.lokcyberbio.world.Cell;
import su.knst.lokcyberbio.world.Preferences;
import su.knst.lokcyberbio.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Interpreter {
    protected static World world;
    protected CommandsExecutors commandsExecutors;
    protected int maxBotSteps;
    protected long botsCount;
    protected long steps;

    public Interpreter(World world) {
        Interpreter.world = world;
        this.commandsExecutors = new CommandsExecutors(world);
        this.maxBotSteps = world.getPreferences().getMaxInterpreterStepsPerBot();
    }

    public long getBotsCount() {
        return botsCount;
    }

    public long getSteps() {
        return steps;
    }

    public void reset() {
        this.steps = 0;
    }

    public static int getValueFromDNAArg(int arg, int max) {
        return Math.round((Math.abs(arg) / (float)world.getPreferences().getBotDNAMaxCommand()) * max);
    }

    public void step() {
        Preferences preferences = world.getPreferences();
        botsCount = 0;
        steps++;

        for (int y = 0; y < preferences.getWorldHeight(); y++) {
            for (int x = 0; x < preferences.getWorldWidth(); x++) {
                Cell cell = world.getCells()[x][y];

                int moveMinerals = (int)(cell.getMinerals() / 1.5f);
                if (moveMinerals > 50 && steps % 5 == 0) {
                    for (int i = 1; i < 8; i+=2){
                        Direction direction = DirectionProvider.DIRECTIONS[i];

                        Cell cellN = world.getCell(x + direction.deltaX, y + direction.deltaY);

                        if (cellN == cell || cellN.getMinerals() > 2000)
                            continue;

                        int move = (int)((moveMinerals / 6f) * (direction.deltaY == -1 ? 2 : 0.5f));

                        if (cell.getMinerals() < move)
                            break;

                        cell.deltaMinerals(-move);
                        cellN.deltaMinerals(move);
                    }
                }

                if (world.getCellsHandled()[x][y])
                    continue;

                Bot bot = cell.getBot();

                if (bot == null)
                    continue;

                if (!bot.isAlive()) {
                    cell.setBot(null);
                    cell.deltaMinerals(bot.getEnergy());

                    continue;
                }

                if (bot.getEnergy() == preferences.getBotMaxEnergy()){
                    boolean doubled = false;
                    List<Direction> list = Arrays.asList(DirectionProvider.DIRECTIONS);
                    Collections.shuffle(list);

                    for (Direction direction : list) {
                        if (world.duplicateBot(x, y, direction.deltaX, direction.deltaY)) {
                            doubled = true;
                            break;
                        }
                    }

                    if (!doubled) {
                        cell.setBot(null);
                        cell.deltaMinerals(bot.getEnergy());

                        continue;
                    }
                }

                botsCount++;

                stepBot(bot, x, y);
            }
        }
    }

    protected void stepBot(Bot bot, int x, int y) {
        int[] dna = bot.getDNA();
        DNACommand[] dnaCommands = DNACommand.values();

        int pointer = 0;

        int minerals = world.getCells()[x][y].getMinerals();

        //if (minerals > 2000)
        //    bot.changeHealth(-minerals / 2000);

        for (int i = 0; i < maxBotSteps; i++){
            if (pointer >= dna.length || pointer < 0)
                break;

            if (!bot.isAlive())
                return;

            int rawCommand = dna[pointer];

            bot.step();

            if (rawCommand >= dnaCommands.length || rawCommand < 0)
                continue;

            DNACommand command = dnaCommands[rawCommand];
            ExecutorResult result;

            if (command.needArgument) {
                if (pointer + 1 >= dna.length)
                    break;

                pointer++;

                result = commandsExecutors.run(command, bot, x, y, dna[pointer]);
            }else
                result = commandsExecutors.run(command, bot, x, y);

            if (command.breakable && result.ok)
                break;

            pointer += result.skip;

            pointer++;
        }
    }
}
