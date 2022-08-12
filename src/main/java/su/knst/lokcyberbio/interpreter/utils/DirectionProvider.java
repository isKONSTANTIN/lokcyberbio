package su.knst.lokcyberbio.interpreter.utils;

public class DirectionProvider {
    protected static Direction[] DIRECTIONS = new Direction[] {
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
        if (arg > 7 || arg < 0)
            return null;

        return DIRECTIONS[arg];
    }
}
