package su.knst.lokcyberbio.interpreter.utils;

public enum DNACommand {
    EAT_BOT(true, true),
    MOVE_POINTER(false, true),
    PHOTOSYNTHESIS(true, false),
    REGENERATE(true, false),
    MOVE(true, true),
    CHECK_ENERGY(false, true),
    CHECK_BOT(false, true),
    CHECK_HEALTH(false, false),
    CHECK_DNA_BOT(false, true),
    DUPLICATE(true, false);

    public final boolean breakable;
    public final boolean needArgument;

    DNACommand(boolean breakable, boolean needArgument) {
        this.breakable = breakable;
        this.needArgument = needArgument;
    }
}
