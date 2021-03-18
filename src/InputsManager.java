import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class InputsManager implements MouseListener, KeyListener, WindowListener {

    /**
     * A queue of the keys pressed. Gets emptied every tick and issues the according events
     */
    public Queue<KeyEventInfo> keys = new ConcurrentLinkedQueue<>();

    /**
     * A queue of the click events. Gets emptied every tick and issues the according events.
     */
    public Queue<MouseEventInfo> clicks = new ConcurrentLinkedQueue<>();

    /**
     * A list of keys which are currently pressed
     */
    public Set<KeyEvent> keysDown = new HashSet<>();

    /**
     * The frame to capture the events from
     */
    public GameUI frame;

    /**
     * Creates a new InputsManager.
     * @param frame The frame, inputs are captured from. Relays the events syncronized to its current world.
     */
    public InputsManager(GameUI frame) {
        this.frame = frame;
        frame.addMouseListener(this);
        frame.addKeyListener(this);
        frame.addWindowListener(this);
    }


    /**
     * Checks if the shift key is currently pressed
     * @return if the shift key is down (was down at any time during the last tick)
     */
    public boolean isShiftDown() {
        return keysDown.stream().anyMatch(KeyEvent::isShiftDown);
    }

    /**
     * Checks if the control key is currently pressed
     * @return if the control key is down (was down at any time during the last tick)
     */
    public boolean isControlDown() {
        return keysDown.stream().anyMatch(KeyEvent::isControlDown);
    }

    /**
     * Checks if the meta key is currently pressed
     * meta aka Windows aka Super aka windoof etc
     * @return if the meta key is down (was down at any time during the last tick)
     */
    public boolean isMetaDown() {
        return keysDown.stream().anyMatch(KeyEvent::isMetaDown);
    }

    /**
     * Checks if the alt key is currently pressed
     * @return if the alt key is down (was down at any time during the last tick)
     */
    public boolean isAltDown() {
        return keysDown.stream().anyMatch(KeyEvent::isAltDown);
    }

    /**
     * Checks if the shift key is currently pressed
     * @return if the shift key is down (was down at any time during the last tick)
     */
    public boolean isKeyDown(char key) {
        return keysDown.stream().anyMatch(k -> Character.toLowerCase(k.getKeyChar()) == key);
    }

    /**
     * returns a list of all keys currently pressed
     * @return the list of keys
     */
    public List<Character> getPressedKeys() {
        return keysDown.stream().map(KeyEvent::getKeyChar).collect(Collectors.toList());
    }

    /**
     * The event called by the JFrame. adds a new Keyevent to the queue
     * @param e the event created
     */
    @Override
    public final void keyTyped(KeyEvent e) {
        keys.add(new KeyEventInfo(e, 0));
    }

    /**
     * The event called by the JFrame. adds a new Keyevent to the queue
     * @param e the event created
     */
    @Override
    public final void keyPressed(KeyEvent e) {
        keys.add(new KeyEventInfo(e, 1));
        if (!keysDown.stream().anyMatch(k -> k.getKeyCode() == e.getKeyCode()))
            keysDown.add(e);
    }

    /**
     * The event called by the JFrame. adds a new Keyevent to the queue
     * @param e the event created
     */
    @Override
    public final void keyReleased(KeyEvent e) {
        keys.add(new KeyEventInfo(e, 2));
        keysDown.removeIf(k -> k.getKeyCode() == e.getKeyCode());
    }

    /**
     * handles all key events and issues the events in the sub classes.
     */
    public final void handleKeys() {
        KeyEventInfo e;
        while ((e = keys.poll()) != null)
        {
            switch (e.type) {
                case 0:
                    frame.world.keyTyped(e.e.getKeyChar());
                    frame.world.keyTyped((int)e.e.getKeyChar());
                    break;
                case 1:
                    frame.world.keyPressed(e.e.getKeyChar());
                    frame.world.keyPressed((int)e.e.getKeyChar());
                    KeyEventInfo finalE = e;
                    frame.world.objectsOf(Textfield.class).stream().filter(tf -> tf.isSelected).forEach(tf -> tf.keyTyped(finalE.e.getKeyChar()));
                    break;
                case 2:
                    frame.world.keyReleased(e.e.getKeyChar());
                    frame.world.keyReleased((int)e.e.getKeyChar());
                    break;
            }
        }
    }

    /**
     * Called by the JFrame, when an mouseevent occures. adds it to the queue
     * @param e the created event
     */
    public final void mousePressed(MouseEvent e) {
        clicks.add(new MouseEventInfo(e, MouseEventInfo.MOUSE_PRESSED));
    }



    /**
     * Called by the JFrame, when an mouseevent occures. adds it to the queue
     * @param e the created event
     */
    public final void mouseClicked(MouseEvent e) {
        clicks.add(new MouseEventInfo(e, MouseEventInfo.MOUSE_CLICKED));
    }



    /**
     * Called by the JFrame, when an mouseevent occures. adds it to the queue
     * @param e the created event
     */
    public final void mouseReleased(MouseEvent e) {
        clicks.add(new MouseEventInfo(e, MouseEventInfo.MOUSE_RELEASED));
    }


    /**
     * Called by the JFrame, when an mouseevent occures. adds it to the queue
     * @param e the created event
     */
    public final void mouseEntered(MouseEvent e) {
        clicks.add(new MouseEventInfo(e, MouseEventInfo.MOUSE_ENTERED));
    }


    /**
     * Called by the JFrame, when an mouseevent occures. adds it to the queue
     * @param e the created event
     */
    public final void mouseExited(MouseEvent e) {
        clicks.add(new MouseEventInfo(e,MouseEventInfo.MOUSE_EXITED));
    }


    /**
     * Handles all Mouse events.
     * Issues the events of the world as well as the clicked objects.
     */
    public void handleMouse() {
        MouseEventInfo e;
        WorldObj o;
        List<WorldObj> objs;
        while ((e = clicks.poll()) != null) {
            Point p = e.e.getPoint();
            objs = frame.world.objects.stream().filter(ob -> ob.isAt(p.x-frame.bardim[0], p.y-frame.bardim[1], true)).collect(Collectors.toList());
            o = objs.isEmpty()? null : objs.get(0);
            MouseEventInfo finalE = e;
            switch (e.type) {
                case MouseEventInfo.MOUSE_PRESSED:
                    frame.world.mousePressed(e.e, o);
                    break;
                case MouseEventInfo.MOUSE_CLICKED:
                    frame.world.mouseClicked(e.e, o);
                    break;
                case MouseEventInfo.MOUSE_RELEASED:
                    frame.world.mouseReleased(e.e, o);
                    break;
                case MouseEventInfo.MOUSE_ENTERED:
                    frame.world.mouseEntered(e.e, o);
                    break;
                case MouseEventInfo.MOUSE_EXITED:
                    frame.world.mouseExited(e.e, o);
            }
            objs.forEach(ob -> ob._mouseEvent(finalE));
        }
    }
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowOpened(WindowEvent e) {
        frame.world.windowOpened(e);
    }
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowClosed(WindowEvent e) {
        frame.world.windowClosed(e);
    }
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowClosing(WindowEvent e) {
        frame.world.windowClosing(e);
    }
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowIconified(WindowEvent e) {
        frame.world.windowIconified(e);
    }
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowDeiconified(WindowEvent e) {
        frame.world.windowDeiconified(e);
    }
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowActivated(WindowEvent e) {
        frame.world.windowActivated(e);
    }
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowDeactivated(WindowEvent e) {
        frame.world.windowDeactivated(e);
    }

}
