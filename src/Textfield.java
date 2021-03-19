import java.awt.*;

/**
 * A textfield, used to enter Text or numbers by the user.
 */
public class Textfield extends Button {

    /**
     * maximum length of the included textfield.
     * -1 for no length limit
     */
    public int maxsize = -1, maxvalue = -1;

    /**
     * If only Numbers are allowed in the textfield
     */
    public boolean isNumeric,
    /**
     * if the Texfield is currently selected aka if one is writing into it
     * Default Value: {@link true}
     */
            isSelected;
    /**
     * The color, the Border becomes, when the Field is active
     */
    public Color selectedBorderColor;

    /**
     * The characters the user is able to input into the textfield.
     * Must be a {@link java.util.regex.Pattern} string
     */
    public String charset = "[ -~äöüÄÖÜ]";

    /**
     * Deselects all other textfields and selects this one
     * @param e the click event information
     */
    @Override
    public final void clickEvent(MouseEventInfo e) {
        world.objectsOf(Textfield.class).forEach(Textfield::deselect);
        isSelected = true;
        Color tmp = borderColor;
        setBorderColor(selectedBorderColor);
        selectedBorderColor = tmp;
    }

    /**
     * deselects the current button
     */
    public final void deselect() {
        if (isSelected) {
            isSelected = false;
            Color tmp = borderColor;
            setBorderColor(selectedBorderColor);
            selectedBorderColor = tmp;
        }
    }

    /**
     * does nothing
     */
    @Override
    public void tick() {

    }

    /**
     * sets the Bordercolor, for when the field is selected
     * @param clr the new Color
     */
    public final void setSelectedBorderColor(Color clr) {
        if (isSelected) {
            setBorderColor(clr);
        }
        else {
            selectedBorderColor = clr;
        }
    }

    /**
     * Called when a key is typed.
     * @param c the ascii value of the pressed key
     */
    public final void keyTyped(int c) {
        if (!(""+ (char) c).matches(charset)) {
            if (c == 8) {
                if (!text.isEmpty()) {
                    setText(text.substring(0, text.length()-1));
                }
                if (isNumeric && text.isEmpty()) {
                    setText("0");
                }
            }
            return;
        }

        if (isNumeric) {
            if (!(((char) c)+"").matches("\\d")) {
                return;
            }
        }
        if (text.isEmpty()) {
            if ((((char) c)+"").matches("\\s")) {
                return;
            }
        }
        if (isNumeric && text.equals("0")) {
            text = (char) c+"";
        }
        else
            text+=(char)c;
        if (maxsize != -1) {
            if (text.length() > maxsize) {
                text = text.substring(0, maxsize);
            }
            if (isNumeric && maxvalue != -1 && Integer.parseInt(text) > maxvalue) {
                text = text.substring(0,text.length()-2);
            }
        }
        update();
    }
}
