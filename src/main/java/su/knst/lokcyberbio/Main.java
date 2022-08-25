package su.knst.lokcyberbio;

import su.knst.lokcyberbio.applications.SettingsApplication;
import su.knst.lokcyberbio.applications.WorldApplication;
import su.knst.lokcyberbio.render.WorldRender;
import su.knst.lokcyberbio.world.Preferences;
import su.knst.lokcyberbio.world.World;

public class Main {
    public static void main(String[] args) {
        World world = new World(new Preferences(), 0);
        WorldRender render = new WorldRender(world);

        WorldApplication worldApplication = new WorldApplication(world, render);
        SettingsApplication settingsApplication = new SettingsApplication(worldApplication, world, render);

        worldApplication.open();
        settingsApplication.open();
    }
}
