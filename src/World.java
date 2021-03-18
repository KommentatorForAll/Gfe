import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * The world everything takes place in.
 * Different Elements can be added, moved and others.
 * It creates events once a key is pressed or an element is clicked on.
 */
public abstract class World implements Tickable {

    /**
     * The size of each field.
     */
    public int pixelSize;

    /**
     * The amount of fields on the x axis
     */
    public int width,
    /**
     * The amount of fields on the y axis of the world
     */
            height;

    /**
     * All objects in the world.
     */
    public Collection<WorldObj> objects;

    /**
     * The engine the world is running on.
     * Default amount of ticks per second: 20
     */
    public static Engine e = new Engine(20);

    /**
     * If the world border is a hard edge.
     * Hard edge means, one is unable to go out of bounds, but stays at the border.
     * Default value: {@link false}
     */
    public boolean hardEdge;

    /**
     * The ui of the world. used to draw and Show the world.
     */
    public WorldUI ui;

    /**
     * The frame, every world is drawn on.
     */
    public static GameUI mainframe;

    /**
     * the random number generator of the world.
     */
    public Random random;

    /**
     * just some private stuff for resizing the world
     */
    private int sw, sh;


    /**
     * generates a standard world of 10x10 fields, 16 pixel width/height each.
     */
    public World() {
        this(10, 10, 16);
    }

    /**
     * generates a new world.
     * @param width amount of fields in the x axis
     * @param height amount of fields at the y axis
     * @param pixelSize the size of each field.
     */
    public World(int width, int height, int pixelSize) {
        if(width <= 0 || height <= 0 || pixelSize <= 0) throw new IllegalArgumentException("Size must not be less or equal 0");
        ui = new WorldUI(pixelSize*width, pixelSize*height, pixelSize);
        mainframe = mainframe == null?new GameUI("game", this) : mainframe;
        this.width = width;
        this.height = height;
        this.pixelSize = pixelSize;
        updateSW();
        random = new Random();
        objects = new CopyOnWriteArrayList<>();
        start();
    }

    /**
     * resizes the world
     * @param width new width of the world
     * @param height new height of the world
     */
    public final void resizeField(int width, int height) {
        if(width <= 0 || height <= 0 || pixelSize <= 0) throw new IllegalArgumentException("Size must not be less or equal 0");
        this.width = width;
        this.height = height;
        updateSW();
        updateSize();
    }

    /**
     * sets the new fieldsize
     * @param pixelSize the size of each field
     */
    public final void resizeField(int pixelSize) {
        if(width <= 0 || height <= 0 || pixelSize <= 0) throw new IllegalArgumentException("Size must not be less or equal 0");
        this.pixelSize = pixelSize;
        ui.pxsize = pixelSize;
        updateSW();
        updateSize();
    }

    /**
     * Resizes the world by the given amount.
     * @param amount the amount to resize the world by
     */
    public final void resizeField(double amount) {
        width *= amount;
        height *= amount;
        if (((width*pixelSize > sw || height*pixelSize > sh)&&amount>1) || ((sw > (width+1)*pixelSize || sh > (height+1)*pixelSize)&&amount <1))
            pixelSize /= amount;
        ui.pxsize = pixelSize;
        updateSize();
    }

    /**
     *updates the oveerall size of the world.
     */
    public final void updateSize() {
        if(width <= 0 || height <= 0 || pixelSize <= 0) throw new IllegalArgumentException("Size must not be less or equal 0");
        ui.setSize(width*pixelSize, height*pixelSize);
        ui.repaint();
    }

    /**
     * used for resizing the world.
     */
    private void updateSW() {
        sw = width*pixelSize;
        sh = height*pixelSize;
    }

    /**
     * @return the amount of fields the world has on the x axis
     */
    public final int getWidth() {return width;}

    /**
     * @return the amount of fields the world has on the y axis
     */
    public final int getHeight() {return height;}

    /**
     * Fetches the absolut width of the world
     * @return the width in pixel the world is wide
     */
    public final int getAbsWidth() {return width*pixelSize;}

    /**
     * Fetches the absolute height of the world
     * @return the height in pixel of the world
     */
    public final int getAbsHeight() {return height*pixelSize;}

    /**
     * gets called every tick. this is the super version of the tick method, calls tick()
     */
    public final void _tick() {
        tick();
        handleKeys();
        handleMouse();
        objects.forEach(o -> {if (o.world != null) o._tick();});
        ui.paint(objects);
    }

    /**
     * the tick method, which gets called every tick. has to be overwritten
     */
    public abstract void tick();

    public final void handleKeys() {
        mainframe.inpManager.handleKeys();
    }

    public final void handleMouse() {
        mainframe.inpManager.handleMouse();
    }

    /**
     * adds an object to the world, at the given position
     * @param obj the object which should be added
     * @param x the x position of the object
     * @param y the y position of the object
     */
    public final void addObject(WorldObj obj, int x, int y) {
        objects.add(obj);
        obj.setLocation(x,y);
        obj.world = this;
    }

    /**
     * removes an object from the world
     * @param obj the object to remove
     */
    public final void removeObject(WorldObj obj) {
        objects.remove(obj);
        obj.world = null;
    }

    /**
     * removes all objects of the collection
     * @param objs the objects to remove
     */
    public final <T extends WorldObj> void removeObjects(Collection<T> objs) {
        objects.removeAll(objs);
        objs.forEach(o -> o.world = null);
    }

    /**
     * removes all objects of the given class
     * @param cls the class of the objects to remove
     */
    public final <T extends WorldObj> void removeObjects(Class<T> cls) {
        objects.removeAll(objectsOf(cls));
    }

    /**
     * removes all objects of the given interface
     * @param inter the interface of the objects to remove
     * @throws IllegalArgumentException when the interface is null or not an interface
     */
    public final void removeObjectsOfInterface(Class<?> inter) {
        if (inter == null || !inter.isInterface()) throw new IllegalArgumentException("Class must not be null or a non-Interface");
        objects.removeAll(objectsOfInterface(inter));
    }

    /**
     * deltes all objects from the world
     */
    public final void removeAll() {
        objects.forEach(o -> o.world = null);
        objects.clear();
    }

    /**
     * sets the ticks per second to the given value
     * @param tps the tps which are gonna be run
     */
    public static void setTps(double tps) {
        if (tps <= 0) throw new IllegalArgumentException("Tps must not be less or equal 0");
        e.tps = tps;
    }

    /**
     * starts the engine
     */
    public final void start() {
        e.addObject(this);
    }

    /**
     * stops the engine
     */
    public final void stop() {
        e.removeObject(this);
    }

    /**
     * switches the active world
     * @param world the world to be switched to
     */
    public static List<World> switchWorld(World world) {
        List<World> olds = e.tickables.stream().filter(t -> t instanceof World).map(t -> (World) t).collect(Collectors.toList());
        e.removeObjects(World.class);
        e.addObject(world);
        switchFocus(world);
        return olds;
    }

    /**
     * switches the focus of the ui to the new world.
     * @param world the new world to select
     */
    public static void switchFocus(World world) {
        mainframe.switchWorld(world);
    }

    /**
     * sets the background image
     * @param img the new background
     */
    public final void setBackground(AdvancedImage img) {
        ui.setBackground(img);
    }

    /**
     * Fetches the background image
     * @return the image in the background
     */
    public final AdvancedImage getBackground() {return ui.backgroundImage;}

    /**
     * Returns if an object is exactly at the given position
     * @param x x position
     * @param y y position
     * @return if an object is exactly at x, y
     */
    public final boolean isObjectAt(int x, int y) {
        return objects.stream().anyMatch((o) -> o.x == x && o.y == y);
    }

    /**
     * returns if an object of the given class is exactly at the position
     * @param x x position
     * @param y y position
     * @param cls class of objects to check for
     */
    public final <T extends WorldObj> boolean isObjectAt(int x, int y, Class<T> cls) {
        return objectsOf(cls).stream().anyMatch(o -> o.x == x && o.y ==y);
    }

    /**
     * returns a list of all objects of the given class
     * @param cls the class all fetched objects are from
     * @return a list of objects
     */
    public final <T extends WorldObj> List<T> objectsOf(Class<T> cls) {
        if (cls == null) return (List<T>) objects;
        return objects.stream().filter(cls::isInstance).map(o -> (T) o).collect(Collectors.toList());
    }

    /**
     * Returns all objects which are not from the given class
     * @param cls the class to exclude
     * @return all objects not instance of the the class
     */
    public final List<WorldObj> objectsOfNot(Class<? extends WorldObj> cls) {
        if (cls == null) return (List<WorldObj>) objects;
        return objects.stream().filter(o -> !cls.isInstance(o)).collect(Collectors.toList());
    }

    /**
     * Removes all objects except those instance of the given class(es)
     * @param cls the class(es) to exclude from the purge
     */
    @SafeVarargs
    public final void removeObjectsExclusive(Class<? extends WorldObj>... cls) {
        Collection<WorldObj> c = new ArrayList<>(objects);
        Arrays.asList(cls).forEach(cl -> c.removeAll(objectsOf(cl)));
        removeObjects(c);
    }

    /**
     * returns a list of all objects of the given Interface
     * @param inter the interface to get the objects of
     * @return the list of objects implementing the interface
     * @throws IllegalArgumentException when the class is either null or not an Interface
     */
    public final List<WorldObj> objectsOfInterface(Class<?> inter) {
        if (inter == null || !inter.isInterface()) throw new IllegalArgumentException("Class must not be null or a non-Interface");
        return objects.stream().filter(inter::isInstance).collect(Collectors.toList());
    }

    /**
     * returns all objects at the given position
     * @param x x position
     * @param y y position
     * @param cls class to check for
     * @return list of objects
     */
    public final <T extends WorldObj> List<T> objectsAt(int x, int y, Class<T> cls) {
        return objectsOf(cls).stream().filter(o -> o.x == x && o.y == y).collect(Collectors.toList());
    }

    /**
     * returns all objects at the given position
     * @param x x position
     * @param y y position
     * @param inter the Interface to chack for
     * @return list of objects
     * @throws IllegalArgumentException when the class is null or not an interface
     */
    public final List<WorldObj> objectsOfInterfaceAt(int x, int y, Class<?> inter) {
        if (inter == null || !inter.isInterface()) throw new IllegalArgumentException("Class must not be null or a non-Interface");
        return objectsOfInterface(inter).stream().filter(o -> o.x == x && o.y == y).collect(Collectors.toList());
    }

    /**
     * sets the order in which the objects are getting painted.
     * @param classes the classes
     */
    @SafeVarargs
    public final void setPaintOrder(Class<? extends WorldObj> ... classes) {
        ui.setPaintOrder(classes);
    }

    /**
     * returns the offset, due to the bar at the top of a jframe
     * @return the offset (to add to the point you are looking for) as a Point
     */
    public final Point getOffset() {
        Point p = ui.getLocation();
        p.x += (pixelSize/2);
        p.y += (pixelSize/2);
        return p;
    }

    /**
     * sets the background opaqueness of the world. a value less than 1 results in a fading of the drawn images
     * @param opaque opaqueness to set to
     * @throws IllegalArgumentException if the opaquness is not between (inclusive) 0 and 1
     */
    public final void setBackgroundOpaqueness(double opaque) {
        if (!((0<= opaque) && (opaque <= 1))) throw new IllegalArgumentException("Opaqueness has to be between 0 and 1");
        ui.setBackgroundOpaqueness(opaque);
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * returns all objects in range from a specified point
     * @param x x coordinate of the center
     * @param y y coordinate of the center
     * @param range range the objects have to be in
     * @param cls Class of the objects.
     * @return a list of objects in the specified range
     * @throws NullPointerException if {@param cls} is null
     */
    public List<WorldObj> objectsInRange(int x, int y, int range, Class<?> cls) {
        return objects.stream().filter(cls::isInstance).filter(o -> o.distanceTo(x,y) <= range).collect(Collectors.toList());
    }


    /**
     * event called, when a key is typed
     * @param key the char of the key
     */
    public void keyTyped(char key){}

    /**
     * event called, when a key is pressed
     * @param key the char of the key
     */
    public void keyPressed(char key){}

    /**
     * event called, when a key is released
     * @param key the char of the key
     */
    public void keyReleased(char key){}

    /**
     * event called, when a key is typed
     * @param key the ascii value of the key
     */
    public void keyTyped(int key){}

    /**
     * event called, when a key is pressed
     * @param key the ascii value of the key
     */
    public void keyPressed(int key){}

    /**
     * event called, when a key is released
     * @param key the ascii value of the key
     */
    public void keyReleased(int key){}

    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowOpened(WindowEvent e) {}
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowClosed(WindowEvent e) {}
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowClosing(WindowEvent e) {}
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowIconified(WindowEvent e) {}
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowDeiconified(WindowEvent e) {}
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowActivated(WindowEvent e) {}
    /**
     * called as a WindowListener, can be implemented for initial or final actions
     */
    public void windowDeactivated(WindowEvent e) {}

    public void mouseExited(MouseEvent e, WorldObj obj) {}
    public void mouseEntered(MouseEvent e, WorldObj obj) {}
    public void mouseReleased(MouseEvent e, WorldObj obj) {}
    public void mouseClicked(MouseEvent e, WorldObj obj) {}
    public void mousePressed(MouseEvent e, WorldObj obj) {}

}
