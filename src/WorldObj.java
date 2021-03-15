import javax.imageio.ImageIO;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * superclass of all objects being able to be added to the world
 */
public abstract class WorldObj implements Tickable {

    /**
     * The position of the object
     */
    public int x, y;

    /**
     * The sprite of the Object.
     * If you want to use Buffered images use the {@link AdvancedImage#AdvancedImage(BufferedImage)} constructor
     */
    public AdvancedImage img;

    /**
     * The world the object is in.
     * May be null, if not present in any world.
     */
    public World world;

    /**
     * Creates a new Object with an empty image
     */
    public WorldObj() {
        this(new AdvancedImage(1,1));
    }

    /**
     * Creates a new Object with the given Image.
     * @param img The sprite of the Object
     */
    public WorldObj(AdvancedImage img) {
        this.img = img;
    }

    /**
     * sets the x position of the object
     * @param x the new x position
     */
    public final void setX(int x) {
        this.x = world!=null?world.hardEdge?Math.max(0,Math.min(x,world.width)):x:x;
    }

    /**
     * sets the y position of the object
     * @param y the new y position
     */
    public final void setY(int y) {
        this.y = world!=null?world.hardEdge?Math.max(0,Math.min(y,world.width)):y:y;
    }

    /**
     * sets the new position of the object
     * @param x the new x position
     * @param y the new y position
     */
    public final void setLocation(int x, int y) {
        setX(x);
        setY(y);
    }

    /**
     * moves the object by the given amount
     * @param dir a vector as {xdistance, ydistance}
     * @throws IndexOutOfBoundsException thrown, when vector has less than 2 elements
     */
    public final void move(int[] dir) throws IndexOutOfBoundsException{
        move(dir[0], dir[1]);
    }

    /**
     * moves the object by the given distance
     * @param x x distance to travel
     * @param y y distance to travel
     */
    public final void move(int x, int y) {
        setX(this.x + x);
        setY(this.y + y);
    }

    /**
     * sets the image of the object
     * @param img the new image
     */
    public final void setImage(AdvancedImage img) {
        this.img = img;
    }

    /**
     * sets the image to a file location
     * @param name location of the image
     * @throws IOException thrown when there is no such image
     */
    public final void setImage(String name) throws IOException {
        img = new AdvancedImage(ImageIO.read(new File(name)));
    }

    /**
     * The raw Mouseevent, used to being able to execute stuff, while ensuring it gets called even if the {@link WorldObj#mouseEvent(MouseEventInfo)} function is overwritten
     * @param e the event caused
     */
    public void _mouseEvent(MouseEventInfo e) {
        mouseEvent(e);
    }

    /**
     * gets called, when one uses the mouse on the object
     * @param e the mouse event called
     */
    public void mouseEvent(MouseEventInfo e) {}

    public final void _tick() {
        tick();
    }

    /**
     * gets called every tick
     */
    public abstract void tick();

    /**
     * checks if the object is at the border of the world
     */
    public final boolean isAtEdge() {
        return x == 0 || x == world.width-1 || y == 0 || y == world.height-1;
    }

    /**
     * returns if the actor is out of bounds of the world.
     */
    public final boolean oob() {return isOutOfBounds();}

    /**
     * checks if the actor is out of bounds. requires hardedge to be false.
     */
    public final boolean isOutOfBounds() {
        return x < 0 || x >= world.width || y < 0 || y >= world.height;
    }

    public String toString() {
        return super.toString() + "; in world: " + (world==null? "none" : world + "; at position x="+x+", y="+y);
    }

    /**
     * returns the range the object is in.
     * @return the area the object is in, relative to its x and y position
     */
    public final int[][] getDimension() {
        int[][] ret = new int[2][2];
        ret[0] = new int[] {0,0};
        ret[1] = img.getDimension();

        switch (img.imgpos) {
            case CENTER:
                int[] tmp = new int[] {ret[1][0]/2, ret[1][1]/2};
                ret[0] = new int[] {-tmp[0], -tmp[1]};
                ret[1] = tmp;
        }
        return ret;
    }


    /**
     * Returns the hitbox of the Object. Used for collision detection
     * !!if you use custom shapes, make sure to overwrite the {@link WorldObj#isTouching(WorldObj)} as well
     * @return the shape of the object
     */
    public Shape getShape() {
        int[][] dim = getDimension();
        int[] size = img.getDimension();
        Point offset = world.getOffset();
        switch(img.imgpos) {
            case CENTER:
                return new Rectangle(dim[0][0] + (this.x*world.pixelSize)+offset.x, dim[0][1] + (this.y*world.pixelSize)+offset.y, size[0], size[1]);

            case TOP_LEFT:
                return new Rectangle(x, y, size[0], size[1]);
        }
        return new Rectangle();
    }

    /**
     * Checks if the object is at the field x y
     * @param x the field x coordinate
     * @param y the fields y coordinate
     * @return if the object is touching/intersecting the field
     */
    public final boolean isAt(int x, int y) {
        return isAt(x, y, false);
    }

    /**
     * checks if the object is present at the x and y position
     * @param x the x position to check
     * @param y the y position to check
     * @param absolute if the coordinates are field positions or frame coordinates
     * @return if the object is interfiering the point
     */
    public final boolean isAt(int x, int y, boolean absolute) {
        Shape me = getShape();
        if (absolute) {
            return me.contains(x,y);
        }
        else {
            Rectangle r = new Rectangle(x*world.pixelSize, y*world.pixelSize, world.pixelSize, world.pixelSize);
            return me.intersects(r);
        }
    }

    /**
     * returns if ths object touches the other object
     * @param o the object the collision is checked for
     * @return if the two object touch
     */
    public boolean isTouching(WorldObj o) {
        Area a = new Area(o.getShape());
        a.intersect(new Area(getShape()));
        return a.isEmpty();
    }

    /**
     * returns if any object of the given class are touching
     * @param cls the class to check
     * @return if any is touching
     */
    public <T extends WorldObj> boolean isTouching(Class<T> cls) {
        return world.objectsOf(cls).stream().anyMatch(this::isTouching);
    }

    /**
     * returns a list of touching objects
     * @param cls to check of. any if null
     * @return the list of touching objects
     */
    public <T extends WorldObj> List<T> getTouching(Class<T> cls) {
        return world.objectsOf(cls).stream().filter(this::isTouching).collect(Collectors.toList());
    }
}
