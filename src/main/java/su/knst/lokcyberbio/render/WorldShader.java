package su.knst.lokcyberbio.render;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import su.knst.lokutils.gui.core.shaders.GUIShader;
import su.knst.lokutils.objects.Point;
import su.knst.lokutils.objects.Size;

import java.io.IOException;

public class WorldShader extends GUIShader {
    public WorldShader() throws IOException {
        super("#/su/knst/lokcyberbio/resources/shaders/world.vert", "#/su/knst/lokcyberbio/resources/shaders/world.frag");
    }
}
