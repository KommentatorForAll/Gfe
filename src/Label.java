/**
 * an empty button, made just to show some ~~pride~~ text
 */
public class Label extends Button{

    /**
     * Creates a new Label.
     */
    public Label() {
        backgroundHoverColor = backgroundColor;
    }

    /**
     * Creates a new label with the given text
     * @param text The text, displayed on the label
     */
    public Label(String text) {
        this();
        setText(text);
    }

    /**
     * does nothing
     */
    @Override
    public void tick() {}

    /**
     * does nothing when clicked
     * @param e the click event information
     */
    @Override
    public void clickEvent(MouseEventInfo e) {}
}
