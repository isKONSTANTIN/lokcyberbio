package su.knst.lokcyberbio.applications;

import su.knst.lokcyberbio.applications.utils.SyncApplication;
import su.knst.lokcyberbio.gui.MainMenuBuilder;
import su.knst.lokcyberbio.render.RenderMode;
import su.knst.lokcyberbio.render.WorldRender;
import su.knst.lokcyberbio.world.World;
import su.knst.lokutils.gui.layout.FreeLayout;
import su.knst.lokutils.gui.layout.ListLayout;
import su.knst.lokutils.gui.layout.ScrollLayout;
import su.knst.lokutils.gui.panels.GuiPanel;
import su.knst.lokutils.gui.panels.scroll.ScrollBar;
import su.knst.lokutils.gui.panels.scroll.ScrollPanel;
import su.knst.lokutils.gui.style.GUIStyle;
import su.knst.lokutils.objects.Point;
import su.knst.lokutils.objects.Size;
import su.knst.lokutils.render.Window;
import su.knst.lokutils.render.context.RenderingContext;
import su.knst.lokutils.tools.property.PropertyBasic;

public class SettingsApplication extends SyncApplication {
    protected PropertyBasic<Long> bots = new PropertyBasic<>(0L);
    protected PropertyBasic<Long> steps = new PropertyBasic<>(0L);
    protected PropertyBasic<String> view = new PropertyBasic<>("REAL");
    protected PropertyBasic<Float> ups = new PropertyBasic<>(0f);

    protected WorldApplication worldApplication;
    protected World world;
    protected WorldRender render;

    public SettingsApplication(WorldApplication worldApplication, World world, WorldRender render) {
        super(new Window().setResizable(true).setTitle("LCB - Settings").setResolution(new Size(512,512)));

        this.worldApplication = worldApplication;
        this.world = world;
        this.render = render;
    }

    @Override
    public void renderEvent(RenderingContext context) {
        super.renderEvent(context);
    }

    @Override
    public void updateEvent() {
        super.updateEvent();

        if (worldApplication.getTasksCount() > 0)
            return;

        worldApplication.addTask(() -> worldApplication.upsCounter.getPerSecond().get())
                .whenComplete((r, t) -> {
                    if (t != null)
                        return;

                    addTask(() -> {
                        ups.set(r);

                        return null;
                    });
                });

        worldApplication.addTask(() -> worldApplication.world.getSteps())
                .whenComplete((r, t) -> {
                    if (t != null)
                        return;

                    addTask(() -> {
                        steps.set(r);

                        return null;
                    });
                });

        worldApplication.addTask(() -> worldApplication.world.getBotsCount())
                .whenComplete((r, t) -> {
                    if (t != null)
                        return;

                    addTask(() -> {
                        bots.set(r);

                        return null;
                    });
                });
    }

    @Override
    public void initEvent() {
        this.window.setWindowCloseCallback((win) -> this.close());

        GUIStyle.getDefault().asset(GuiPanel.class)
                .color("background", GUIStyle.getDefault().getPalette().elementBackground().setAlpha(0.3f));

        FreeLayout freeLayout = GUIController.getLayout();

        MainMenuBuilder builder = new MainMenuBuilder();
        builder.setGap(4);

        builder.section("Stats");
        builder.stat("UPS", ups);
        builder.stat("Bots", bots);
        builder.stat("Steps", steps);
        builder.stat("View", view);

        builder.section("Utils");
        builder.button("Restart world", () -> worldApplication.addTask(world::restart));
        builder.button("Spawn bots", () -> worldApplication.addTask(() -> world.spawnBots(500)));

        builder.button("Skip 10000 steps", () -> {
            worldApplication.addTask(() -> {
                for (int i = 0; i < 10000; i++){
                    world.step();
                }
            });
        });

        builder.section("View");

        builder.button("Real", () -> {
            worldApplication.addTask(() -> render.setRenderMode(RenderMode.REAL));
            view.set("REAL");
        });

        builder.button("Energy", () -> {
            worldApplication.addTask(() -> render.setRenderMode(RenderMode.ENERGY));
            view.set("ENERGY");
        });

        builder.button("Type", () -> {
            worldApplication.addTask(() -> render.setRenderMode(RenderMode.TYPE));
            view.set("TYPE");
        });

        builder.button("Minerals", () -> {
            worldApplication.addTask(() -> render.setRenderMode(RenderMode.MINERALS));
            view.set("MINERALS");
        });

        builder.gap();

        builder.toggle("Render").addChangeListener((v, o, n) -> render.setEnabled(n));

        builder.gap();

        PropertyBasic<Float> slider = builder.slider("Speed");
        slider.addChangeListener((v, o, n) -> worldApplication.addTask(() -> worldApplication.setSpeed(n)));
        slider.set(0.0f);
        ScrollPanel scrollLayout = new ScrollPanel();

        ListLayout menuLayout = builder.build();

        menuLayout.size().track(() -> new Size(scrollLayout.size().get().width - (scrollLayout.getVBar().active() ? scrollLayout.getVBar().size().get().width + 4 : 0), menuLayout.minimumSize().get().height), menuLayout.minimumSize(), scrollLayout.size());
        scrollLayout.layout().addObject(menuLayout, Point.ZERO);

        scrollLayout.size().track(() -> freeLayout.size().get().relativeTo(8, 8), freeLayout.size());
        freeLayout.addObject(scrollLayout, new Point(4, 4));
    }

    @Override
    public void closeEvent() {
        super.closeEvent();
    }
}
