package su.knst.lokcyberbio;

import org.lwjgl.opengl.GL11;
import su.knst.lokcyberbio.bot.Bot;
import su.knst.lokcyberbio.render.RenderMode;
import su.knst.lokcyberbio.render.WorldRender;
import su.knst.lokcyberbio.world.Preferences;
import su.knst.lokcyberbio.world.World;
import su.knst.lokutils.applications.Application;
import su.knst.lokutils.gui.controls.*;
import su.knst.lokutils.gui.controls.button.GuiButton;
import su.knst.lokutils.gui.controls.margin.GuiMargin;
import su.knst.lokutils.gui.controls.margin.Margin;
import su.knst.lokutils.gui.controls.slider.GuiSlider;
import su.knst.lokutils.gui.eventsystem.events.MouseMoveEvent;
import su.knst.lokutils.gui.eventsystem.events.MouseScrollEvent;
import su.knst.lokutils.gui.layout.Alignment;
import su.knst.lokutils.gui.layout.FreeLayout;
import su.knst.lokutils.gui.layout.ListLayout;
import su.knst.lokutils.gui.panels.GuiPanel;
import su.knst.lokutils.gui.style.GUIStyle;
import su.knst.lokutils.objects.Point;
import su.knst.lokutils.objects.Rect;
import su.knst.lokutils.objects.Size;
import su.knst.lokutils.render.Window;
import su.knst.lokutils.render.context.RenderingContext;
import su.knst.lokutils.render.tools.GLFastTools;

import java.io.IOException;

public class Main extends Application {
    protected World world;
    protected WorldRender render;

    protected int stepSkipped = 0;
    protected float speed;

    public Main() {
        super(new Window().setResizable(true).setTitle("LCB").setResolution(new Size(1024,512)));

        this.world = new World(new Preferences());
        world.spawnBots(800);
    }

    @Override
    public void initEvent() {
        this.window.setWindowCloseCallback((win) -> this.close());

        this.GUIController.getLayout().getCustomersContainer().addCustomer(MouseMoveEvent.class, (e) -> {
            if (e.startPosition.x <= 200)
                return;

            render.deltaOffset(new Point(e.endPosition.x - e.lastPosition.x, e.endPosition.y - e.lastPosition.y));
        });

        this.GUIController.getLayout().getCustomersContainer().addCustomer(MouseScrollEvent.class, (e) -> {
            render.zoom(e.scrollDelta.y);
        });

        try {
            render = new WorldRender(world);
        } catch (IOException e) {
            e.printStackTrace();
        }

        GUIStyle.getDefault().asset(GuiPanel.class)
                .color("background", GUIStyle.getDefault().getPalette().elementBackground().setAlpha(0.3f));

        FreeLayout freeLayout = GUIController.getLayout();

        GuiPanel<ListLayout> guiPanel = new GuiPanel<>(new ListLayout());
        guiPanel.setRounded(0);
        guiPanel.setPadding(10);
        ListLayout layout = guiPanel.getRootLayout();
        layout.setGap(4);
        guiPanel.size().track(() -> new Size(200, freeLayout.size().get().height));
        freeLayout.addObject(guiPanel, Point.ZERO);

        GuiButton newWorld = new GuiButton("Restart world");
        newWorld.setAction(() -> {
            world.restart();
        });
        layout.addObject(newWorld);

        layout.addObject(new GuiSeparate());
        layout.addObject(new GuiText("View"), Alignment.CENTER);
        layout.addObject(new GuiSeparate());


        GuiButton realView = new GuiButton("Real");
        realView.setAction(() -> {
            render.setRenderMode(RenderMode.REAL);
        });
        layout.addObject(realView);

        GuiButton energyView = new GuiButton("Energy");
        energyView.setAction(() -> {
            render.setRenderMode(RenderMode.ENERGY);
        });
        layout.addObject(energyView);

        GuiButton healthView = new GuiButton("Health");
        healthView.setAction(() -> {
            render.setRenderMode(RenderMode.HEALTH);
        });
        layout.addObject(healthView);

        layout.addObject(new GuiLineSpace().setHeight(5));

        GuiSlider speedSlider = new GuiSlider();
        speedSlider.fullness().addChangeListener((v, o, n) -> speed = n);
        speedSlider.fullness().set(0.5f);
        layout.addObject(new GuiText("Speed"));
        layout.addObject(speedSlider);

    }

    @Override
    public void updateEvent() {
        super.updateEvent();

        if (speed < 0.01)
            return;

        if (speed >= 0.5) {
            for (int i = 0; i < Math.max(1, 50 * (speed - 0.5f) * 2); i++)
                world.getInterpreter().step();
        }else {
            if (stepSkipped >= 60 * (1 - speed * 2)) {
                world.getInterpreter().step();
                stepSkipped = 0;
            }else
                stepSkipped++;
        }

    }

    @Override
    public void renderEvent(RenderingContext context) {
        super.renderEvent(context);
        render.update(GUIController.getLayout().size().get());
        render.render(context);
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.open();
    }
}
