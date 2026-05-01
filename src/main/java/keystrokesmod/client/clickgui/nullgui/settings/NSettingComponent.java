package keystrokesmod.client.clickgui.nullgui.settings;

/**
 * Abstract base for all NULL-GUI setting components.
 * Each setting component knows how to draw itself, handle clicks, and report its height.
 */
public abstract class NSettingComponent {

    protected int x, y, width;
    public boolean visable = true;

    public void setPosition(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    /** Draw the component. Called every frame. */
    public abstract void draw(int mouseX, int mouseY);

    /** @return total height consumed by this component */
    public abstract int getHeight();

    /** @return true if the click was consumed */
    public boolean mouseDown(int mouseX, int mouseY, int button) {
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
    }

    public void keyTyped(char c, int key) {
    }
}
