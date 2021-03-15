import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

/**
 * A Button with a list of options to choose from.
 * Selected item is changed with left- and right-click.
 */
public class ScrollButton extends Button {

    /**
     * The options the user is able to choose from
     */
    public ArrayList<Object> options =  new ArrayList<>();

    /**
     * The currently selected Item
     */
    public Object selected;

    /**
     * If the button wraps around, when reaching an edge of the options
     */
    public boolean loop = true;

    /**
     * Creates a new Scrollbutton.
     * @param options the options one is able to select
     * @param selected the at the beginning selected image.
     * @throws IllegalArgumentException when selected is not an option
     */
    public ScrollButton(Collection<AdvancedImage> options, AdvancedImage selected) {
        this( Arrays.asList(options.toArray()), (Object) selected);
    }

    /**
     * Creates a new Scrollbutton.
     * @param options the options one is able to select
     * @param selected the at the beginning selected image.
     * @throws IllegalArgumentException when selected is not an option
     */
    public ScrollButton(Collection<String> options, String selected) {
        this( Arrays.asList(options.toArray()), (Object) selected);
    }

    private ScrollButton(Collection<Object> options, Object selected) {
        if(!options.contains(selected)) throw new IllegalArgumentException();
        this.options.addAll(options);
        select(selected);
    }

    /**
     * selects and returns the next option.
     * @return the newly selected option
     */
    public Object next() {
        int ind = options.indexOf(selected)+1;
        if (ind >= options.size())
            ind = loop? ind%options.size():ind-1;
        select(options.get(ind));
        return selected;
    }

    /**
     * selects and returns the previous option.
     * @return the newly selected option.
     */
    public Object previous() {
        int ind = options.indexOf(selected)-1;
        if (ind < 0)
            ind = loop? options.size()-1:0;
        select(options.get(ind));
        return selected;
    }

    /**
     * selects the specified object.
     * @param o the object to select
     * @throws IllegalArgumentException if the object is not an option.
     */
    public void selectSpecific(Object o) {
        if (!options.contains(o)) throw new IllegalArgumentException();
        selected = o;
        if(selected instanceof String) {
            setText((String) selected);
        }
        else {
            setBackgroundImage((AdvancedImage) selected);
        }
    }

    /**
     * Selects the given. object must be in the list
     * @param o the object to select
     * @throws IllegalArgumentException when no equal item is in the list
     */
    public void select(Object o) throws IllegalArgumentException {
        for (Object obj : options) {
            if (obj.equals(o)) {
                selectSpecific(obj);
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * returns the currently selected object
     * @return the current selection
     */
    public Object getSelected() {
        return selected;
    }

    /**
     * adds an image to the options
     * @param img the image to add.
     */
    public void addImgOption(AdvancedImage img) {
        options.add(img);
    }

    /**
     * adds a list of images as options to select
     * @param imgs newly avalable options
     */
    public void addImgOptions(Collection<AdvancedImage> imgs) {
        options.addAll(imgs);
    }

    /**
     * adds an further option to the list
     * @param s the new option
     */
    public void addStrOption(String s) {
        options.add(s);
    }

    /**
     * adds a list of options.
     * @param strs the options, which are going to be available
     */
    public void addStrOptions(Collection<String> strs) {
        options.addAll(strs);
    }

    /**
     * called when the mouse is clicked on the object.
     * @param e the event, created by the click
     */
    @Override
    public void clickEvent(MouseEventInfo e) {
        int bnr = e.e.getButton();
        switch (bnr) {
            case 1:
                next();
                break;
            case 3:
                previous();
                break;
        }

    }

    /**
     * does nothing
     */
    @Override
    public void tick() {

    }

    /**
     * selects an random entry from the options.
     */
    public void selectRandom() {
        Random r = new Random();
        selectSpecific(options.get(r.nextInt(options.size())));
    }
}
