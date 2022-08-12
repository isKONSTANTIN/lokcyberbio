package su.knst.lokcyberbio.interpreter.utils;

import su.knst.lokcyberbio.bot.Bot;

public interface CommandExecutor {
    ExecutorResult run(Bot source, int x, int y);
}
