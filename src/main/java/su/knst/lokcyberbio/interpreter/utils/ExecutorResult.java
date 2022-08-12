package su.knst.lokcyberbio.interpreter.utils;

public class ExecutorResult {
    public final boolean ok;
    public final int skip;

    public ExecutorResult(boolean ok, int skip) {
        this.ok = ok;
        this.skip = skip;
    }
}
