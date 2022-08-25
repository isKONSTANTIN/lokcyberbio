package su.knst.lokcyberbio.interpreter.utils;

import static su.knst.lokcyberbio.interpreter.Interpreter.getValueFromDNAArg;

public class DirectionProvider {
    public static final Direction[] DIRECTIONS = new Direction[] {
            new Direction(-1, 1),
            new Direction(0, 1),
            new Direction(1, 1),

            new Direction(1, 0),

            new Direction(1, -1),
            new Direction(0, -1),
            new Direction(-1, -1),

            new Direction(-1, 0)
    };

    public static Direction getDirection(int arg) {
        return DIRECTIONS[getValueFromDNAArg(arg, 7)];
    }
}
