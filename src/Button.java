import java.awt.*;

/**
 * A basic Button one is able to put into ones world.
 * When being clicked an event is called.
 */
public abstract class Button extends WorldObj {

    /**
     * A line of text one is able to display on the button.
     * Default Value: "" (empty string)
     */
    public String text = "";

    /**
     * the name of the Button.
     */
    public String name = "";

    /**
     * the width of the Button in pixel
     * If you want it to occupy one field of the world use {@link World#pixelSize}
     * Default Value: 400 px
     */
    public int width = 400;

    /**
     * The height of the Button in pixel
     * If you want it to occupy one field of the world use {@link World#pixelSize}
     * Default Value: 100 px
     */
    public int height = 100;

    /**
     * The size of the border around the image.
     * Default Value: 5 px
     */
    public int borderWidth = 5;

    /**
     * The Color the text has, when drawn onto the button.
     * Default Value: {@link Color#BLACK}
     */
    public Color textColor = Color.BLACK;

    /**
     * The Background color of the Button.
     * Default Value: {@link Color#WHITE}
     */
    public Color backgroundColor = Color.WHITE;

    /**
     * The Color the button turns into, while being pressed and not released
     * Default Value: {@link Color#LIGHT_GRAY}
     */
    public Color backgroundHoverColor = Color.LIGHT_GRAY;

    /**
     * The Border color of the Button.
     * for the default size, look at {@link Button#borderWidth}
     * Default Value: {@link Color#BLACK}
     */
    public Color borderColor = Color.BLACK;

    /**
     * The custom font for each button.
     * If none is present (font == null) the {@link Button#defaultFont} is used instead
     */
    public Font font;

    /**
     * The default Font of the Buttons text.
     * Default Value: SERIF, plain font of size 20 px
     */
    public static Font defaultFont = new Font(Font.SERIF, Font.PLAIN, 20);

    /**
     * An optional Image, which gets drawn onto the Button.
     * Default Value: {@link null}
     */
    public AdvancedImage backgroundImage;

    /**
     * If one is able to use the button
     * Default Value: {@link true}
     */
    public boolean enabled = true;

    /**
     * Creates a new Button.
     * This actually does nothing, but redraw itself.
     */
    public Button() {
        super(null);
        update();
    }

    /**
     * updates the displayed button
     */
    public final void update() {
        AdvancedImage img = new AdvancedImage(width, height);
        img.fill(enabled?borderColor:Color.RED);
        img.drawRect(borderWidth, borderWidth, width-(borderWidth*2), height-(borderWidth*2), backgroundColor);
        if (backgroundImage != null) img.drawImage(backgroundImage);
        if (!text.isEmpty()) img.drawText(textColor, font==null?defaultFont:font, text);
        setImage(img);
    }

    /**
     * called when the button is clicked.
     * @param e the event created by the click
     */
    public final void _mouseEvent(MouseEventInfo e) {
        if (!enabled) return;
        if (e.type == MouseEventInfo.MOUSE_PRESSED) {
            Color tmp = backgroundColor;
            backgroundColor = backgroundHoverColor;
            backgroundHoverColor = tmp;
            update();
        }

        else if (e.type == MouseEventInfo.MOUSE_CLICKED) {
            clickEvent(e);
        }

        else if (e.type == MouseEventInfo.MOUSE_RELEASED) {
            Color tmp = backgroundColor;
            backgroundColor = backgroundHoverColor;
            backgroundHoverColor = tmp;
            update();
        }
        mouseEvent(e);
    }

    /**
     * Called once the button is clicked.
     * @param e the click event information
     */
    public abstract void clickEvent(MouseEventInfo e);

    /**
     * updates the size of the button
     * @param width the new width of the button in pixel
     * @param height the new height of the button in pixel
     */
    public final void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        update();
    }

    /**
     * sets the new border width, around the buttons background. set to 0 for no border
     * @param width the new width in pixel
     */
    public final void setBorderWidth(int width) {
        borderWidth = width;
        update();
    }

    /**
     * sets the font, which is used to write on the button
     * @param font the new font
     */
    public final void setFont(Font font) {
        this.font = font;
        update();
    }

    /**
     * sets the currently displayed text of the button
     * @param text the text to be displayed
     */
    public final void setText(String text) {
        this.text = text;
        update();
    }

    /**
     * updates the text color to the specified color
     * @param textColor the color, the text should shine in
     */
    public final void setTextColor(Color textColor) {
        this.textColor = textColor;
        update();
    }

    /**
     * sets the background color of the image
     * @param backgroundColor the color the background of the button should be
     */
    public final void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        update();
    }

    /**
     * the color, the background is in, when clicked and hold
     * @param backgroundHoverColor the new color
     */
    public final void setBackgroundHoverColor(Color backgroundHoverColor) {
        this.backgroundHoverColor = backgroundHoverColor;
        update();
    }

    /**
     * sets the color, surrounding the button
     * @param borderColor the new color
     */
    public final void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        update();
    }

    /**
     * sets the image the button has
     * @param backgroundImage the image displayed on the button
     */
    public final void setBackgroundImage(AdvancedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
        update();
    }

    /**
     * Sets the name of the button. doesn't change anything of its appearance
     * @param name the new name of the button
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * An update function one can add code to. Should call {@link Button#update()} though.
     */
    public void _update() {}
}