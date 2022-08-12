package su.knst.lokcyberbio.interpreter.utils;

import su.knst.lokcyberbio.bot.Bot;

public interface CommandExecutorWithArg extends CommandExecutor {

    @Override
    default ExecutorResult run(Bot source, int x, int y) {
        return new ExecutorResult(false, 0);
    }

    ExecutorResult run(Bot source, int x, int y, int arg);
}
