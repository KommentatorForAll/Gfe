import java.awt.Font;
import java.awt.Color;

/**
 * A class to ask the User for input.
 * Only one Dialog may be opened at once
 */
public class Dialog extends World {

    /**
     * Font of the text when opening a new dialog.
     * Default Value: a Serif, plain font of size 20
     */
    public static Font font = new Font(Font.SERIF, Font.PLAIN, 20);

    /**
     * The Color of the diplayed text
     * Default Value: {@link Color#WHITE}
     */
    public static Color textColor = Color.WHITE;

    /**
     * if there has been a result
     */
    private boolean gotResult;

    /**
     * the result object, set after hitting the "apply" button
     */
    private Object result;

    /**
     * Creates a new Dialog window.
     * @param dialog the dialog to be shown
     * @param initValue the initial value of the textfield
     * @param type the type of dialog to be diplayed
     */
    private Dialog(String dialog, String initValue, int type) {
        super(10,5,64);
        AdvancedImage img = new AdvancedImage(640,320);
        img.fill(Color.BLACK);
        setBackground(img);
        Label l = new Label(dialog);
        l.setFont(font);
        l.setBackgroundColor(Color.BLACK);
        l.setBorderColor(Color.BLACK);
        l.setTextColor(textColor);
        addObject(l, 5,2);
        Textfield tf = new Textfield();
        tf.setSize(128,64);
        tf.setFont(font);
        tf.setText(initValue);
        switch(type) {
            case 1:
                tf.isNumeric = true;
            case 0:
                addObject(tf, 5,4);
                Button apply = new Button() {
                    @Override
                    public void clickEvent(MouseEventInfo e) {
                        result = tf.text;
                        gotResult = true;
                    }

                    @Override
                    public void tick() {

                    }
                };
                apply.setText("apply");
                apply.setSize(64,32);
                apply.setFont(font);
                addObject(apply, 3,4);
                break;

            case 2:
                Button confirm = new Button() {
                    @Override
                    public void clickEvent(MouseEventInfo e) {
                        result = true;
                        gotResult = true;
                    }

                    @Override
                    public void tick() {

                    }
                }, abord = new Button() {
                    @Override
                    public void clickEvent(MouseEventInfo e) {
                        result = false;
                        gotResult = true;
                    }

                    @Override
                    public void tick() {

                    }
                };
                confirm.setText("Yes");
                confirm.setFont(font);
                confirm.setSize(64,32);
                abord.setText("No");
                abord.setFont(font);
                abord.setSize(64,32);
                addObject(confirm, 3,4);
                addObject(abord, 7,4);
                break;
        }
    }

    /**
     * does nothing acutally
     */
    @Override
    public void tick() {

    }

    /**
     * opens a new Dialog. Returns an object (boolean or String depending on type)
     * @param dialog what the user is being asked
     * @param initValue the initial Value, first displayed in the textfield (does not apply for {@link Dialog#askConfirm(String, String...)}
     * @param type the type of dialog opened
     * @return the users response
     */
    public static Object ask(String dialog, String initValue, int type) {
        Dialog d = new Dialog(dialog, initValue, type);
        World origin = switchWorld(d).get(0);
        while (!d.gotResult) {
            try {
                Thread.sleep((long)(1000/e.tps));
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        switchWorld(origin);
        return d.result;
    }

    /**
     * Asks the user for a string input.
     * @param dialog the Message displayed to the user
     * @param initValue the initial value, displayed in the textfield
     * @return the users response
     */
    public static String askString(String dialog, String... initValue) {
        return (String)ask(dialog, initValue.length == 0?"" : initValue[0], 0);
    }

    /**
     * Asks the user for confirmation
     * @param dialog the Message displayed to the user
     * @param initValue just leave it empty. Doesn't do anything
     * @return the users response
     */
    public static boolean askConfirm(String dialog, String... initValue) {
        return (boolean) ask(dialog, initValue.length == 0?"":initValue[0], 2);
    }

    /**
     * Asks the user to input an Integer (Number
     * @param dialog The message diplayed to the user
     * @param initValue The initial Value, displayed in the Textfield
     * @return the users response
     */
    public static int askNumber(String dialog, String... initValue) {
        return (int)ask(dialog, initValue.length == 0?"" : initValue[0], 1);
    }
}
