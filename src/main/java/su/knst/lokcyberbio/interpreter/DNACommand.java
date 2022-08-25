package su.knst.lokcyberbio.interpreter;

public enum DNACommand {
    EAT_BOT(true, true),
    PHOTOSYNTHESIS(true, false),
    EAT_MINERALS(true, false),

    MOVE_POINTER(false, true),
    BREAK(true, false),
    MOVE(true, true),
    MOVE_ENERGY(true, true),
    CHECK_ENERGY(false, true),
    CHECK_BOT(false, true),
    CHECK_DNA_BOT(false, true);

    public final boolean breakable;
    public final boolean needArgument;

    DNACommand(boolean breakable, boolean needArgument) {
        this.breakable = breakable;
        this.needArgument = needArgument;
    }
}
