import java.awt.event.MouseEvent;

/**
 * Wrapper class, to contain both, the event type of a mouseevent, as well as the event itself.
 */
public class MouseEventInfo {
    /**
     * When the mouse is pushed down
     */
    public static final int MOUSE_PRESSED = 0,
    /**
     * When the mouse is pushed down and released again
     */
            MOUSE_CLICKED = 1,
    /**
     * When the mouse gets released
     */
            MOUSE_RELEASED = 2,
    /**
     * When the mouse enters the frame
     */
            MOUSE_ENTERED = 3,
    /**
     * When the mouse exits the frame
     */
            MOUSE_EXITED = 4;

    /**
     * Which type the event is caused by
     */
    public int type;

    /**
     * The event itself.
     */
    public MouseEvent e;

    /**
     * Creates a new info version of the event
     * @param e the caused mouse event
     * @param type the type of the event
     */
    public MouseEventInfo(MouseEvent e, int type) {
        this.e = e;
        this.type = type;
    }
}
