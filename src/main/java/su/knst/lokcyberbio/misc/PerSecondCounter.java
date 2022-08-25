package su.knst.lokcyberbio.misc;

import su.knst.lokutils.tools.property.PropertyBasic;

public class PerSecondCounter {
    protected PropertyBasic<Float> perSecond = new PropertyBasic<>(0f);
    protected long lastUpdate;
    protected int checked;

    public PerSecondCounter() {
    }

    public void check() {
        long time = System.currentTimeMillis();

        if (lastUpdate == 0)
            lastUpdate = time;

        checked++;

        if (time - lastUpdate >= 1000) {
            perSecond.set(Math.round(checked * ((float)(time - lastUpdate) / 1000) * 100) / 100f);
            checked = 0;
            lastUpdate = time;
        }
    }

    public PropertyBasic<Float> getPerSecond() {
        return perSecond;
    }
}
