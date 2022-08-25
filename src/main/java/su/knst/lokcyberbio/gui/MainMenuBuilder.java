package su.knst.lokcyberbio.gui;

import su.knst.lokutils.gui.controls.GuiLineSpace;
import su.knst.lokutils.gui.controls.GuiSeparate;
import su.knst.lokutils.gui.controls.GuiText;
import su.knst.lokutils.gui.controls.GuiToggle;
import su.knst.lokutils.gui.controls.button.ButtonAction;
import su.knst.lokutils.gui.controls.button.GuiButton;
import su.knst.lokutils.gui.controls.slider.GuiSlider;
import su.knst.lokutils.gui.core.GuiObject;
import su.knst.lokutils.gui.layout.*;
import su.knst.lokutils.objects.Size;
import su.knst.lokutils.tools.property.PropertyBasic;
import su.knst.lokutils.tools.property.Value;
import sun.awt.X11.XSizeHints;

public class MainMenuBuilder {
    protected ListLayout layout;

    public MainMenuBuilder() {
        this.layout = new ListLayout();
    }

    public void section(String name) {
        ListLayout section = new ListLayout();
        section.setGap(0);


        section.addObject(fullSeparate());
        section.addObject(new GuiText(name), Alignment.TOP_CENTER);
        section.addObject(fullSeparate());

        section.size().track(section.minimumSize());

        layout.addObject(new GuiLineSpace().setHeight(5));
        layout.addObject(section);
        layout.addObject(new GuiLineSpace().setHeight(5));
    }

    public PropertyBasic<Boolean> toggle(String name) {
        GuiToggle toggle = new GuiToggle();
        GuiText text = new GuiText(name);

        BaseLayout layout = new BaseLayout();

        layout.setGap(4);
        layout.addObject(toggle);
        layout.addObject(text);

        layout.calculateAll();

        layout.size().set(new Size(layout.getNaturalWidth(), layout.minimumSize().get().height));

        toggle.switchStatus();

        this.layout.addObject(layout);

        return toggle.status();
    }

    public PropertyBasic<Float> slider(String name) {
        GuiSlider slider = new GuiSlider();
        slider.size().track(() -> new Size(layout.size().get().width, 26), layout.size());

        GuiText text = new GuiText(name);

        BaseLayout layout = new BaseLayout();

        layout.setGap(4);

        layout.addObject(text);
        layout.addObject(slider);

        layout.calculateAll();

        layout.size().track(() -> new Size(this.layout.size().get().width, layout.minimumSize().get().height), this.layout.size());

        this.layout.addObject(layout);

        return slider.fullness();
    }

    public void button(String name, ButtonAction action) {
        GuiButton button = new GuiButton(name);
        button.setAction(action);

        layout.addObject(button);
    }

    public void object(GuiObject object, Alignment alignment) {
        layout.addObject(object, alignment);
    }

    public void object(GuiObject object) {
        layout.addObject(object);
    }

    public void stat(String name, Value value) {
        GuiText text = new GuiText();

        text.string().track(() -> name + ": " + value.get().toString(), value);
        layout.addObject(text);
    }

    public void gap() {
        layout.addObject(new GuiLineSpace().setHeight(5));
    }

    protected GuiSeparate fullSeparate() {
        GuiSeparate separate = new GuiSeparate();
        separate.size().track(() -> new Size(layout.size().get().width, 5), layout.size());
        separate.setLineSizePercent(1);

        return separate;
    }

    public void setGap(float gap) {
        layout.setGap(gap);
    }

    public ListLayout build() {
        return layout;
    }
}
