import java.awt.event.KeyEvent;

public class KeyEventInfo {
    public int type;
    public KeyEvent e;
    public KeyEventInfo(KeyEvent e, int type) {
        this.e = e;
        this.type = type;
    }
}