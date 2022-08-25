package su.knst.lokcyberbio.applications;

import su.knst.lokcyberbio.applications.utils.SyncApplication;
import su.knst.lokcyberbio.misc.PerSecondCounter;
import su.knst.lokcyberbio.render.WorldRender;
import su.knst.lokcyberbio.world.World;
import su.knst.lokutils.gui.eventsystem.events.MouseMoveEvent;
import su.knst.lokutils.gui.eventsystem.events.MouseScrollEvent;
import su.knst.lokutils.objects.Point;
import su.knst.lokutils.objects.Size;
import su.knst.lokutils.render.Window;
import su.knst.lokutils.render.context.RenderingContext;

import java.io.IOException;

public class WorldApplication extends SyncApplication {
    protected World world;
    protected WorldRender render;

    protected PerSecondCounter upsCounter = new PerSecondCounter();
    protected float speed = 0.5f;
    protected int stepSkipped = 0;

    public WorldApplication(World world, WorldRender render) {
        super(new Window().setResizable(true).setTitle("LCB - World").setResolution(new Size(1024,512)));

        this.world = world;
        this.render = render;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void renderEvent(RenderingContext context) {
        super.renderEvent(context);

        render.update(GUIController.getLayout().size().get());
        render.render(context);
    }

    @Override
    public void updateEvent() {
        super.updateEvent();

        if (speed < 0.01)
            return;

        if (speed >= 0.5) {
            for (int i = 0; i < Math.max(1, 500 * (speed - 0.5f) * 2); i++) {
                world.step();
                upsCounter.check();
            }
        } else {
            if (stepSkipped >= 60 * (1 - speed * 2)) {
                world.step();
                upsCounter.check();
                stepSkipped = 0;
            } else
                stepSkipped++;
        }
    }

    @Override
    public void initEvent() {
        this.GUIController.getLayout().getCustomersContainer().addCustomer(MouseMoveEvent.class, (e) -> {
            if (!render.isEnabled())
                return;

            render.deltaOffset(new Point(e.endPosition.x - e.lastPosition.x, e.endPosition.y - e.lastPosition.y));
        });

        this.GUIController.getLayout().getCustomersContainer().addCustomer(MouseScrollEvent.class, (e) -> {
            if (!render.isEnabled())
                return;

            render.zoom(e.scrollDelta.y);
        });

        try {
            this.render.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeEvent() {
        super.closeEvent();
    }
}
