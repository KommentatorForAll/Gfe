import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class WorldUI extends JPanel {

    /**
     * The background image of the world.
     */
    public AdvancedImage backgroundImage;

    /**
     * the list of objects to draw onto the screen
     */
    public List<WorldObj> objs;

    /**
     * The order in which objects are on top and which are at the bottom.
     */
    public List<Class<? extends WorldObj>> paintOrder;

    /**
     * The fieldsize
     */
    public int pxsize;

    /**
     * The opaqueness of the background
     */
    public double bg_opaque=1;

    /**
     * Creates a new UI with the given height and width
     * @param width width of the ui
     * @param height height of the ui
     */
    public WorldUI(int width, int height) {
        this(width, height,1);
    }

    /**
     * Creates a new UI
     * @param width amount of fields on the x axis
     * @param height amount of fields on the y axis
     * @param pxsize size of the fields in px
     */
    public WorldUI(int width, int height, int pxsize) {
        super();
        objs = new ArrayList<>();
        paintOrder = new ArrayList<>();
        setSize(width, height);
        setPreferredSize(getSize());
        //setMaximumSize();
        setVisible(true);
        this.pxsize = pxsize;
    }

    /**
     * sets the background image.
     * !!may cause graphical bugs, when image is null or trans parent!!
     * @param img the new background image
     */
    public void setBackground(AdvancedImage img) {
        backgroundImage = img;
        repaint();
    }

    /**
     * paints the given objects
     * @param objects the objects to be pain(ted)
     */
    public void paint(Collection<WorldObj> objects) {
        objs = new ArrayList<>();
        objs.addAll(objects);
        repaint();
    }

    /**
     * paints all objects onto the screen
     * @param g the graphics element of the screen
     */
    public void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            ((Graphics2D) g).setComposite(
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) bg_opaque));
            switch (backgroundImage.imgs) {
                case TILE:
                    AdvancedImage tmp = (new AdvancedImage(backgroundImage)).scale(pxsize, pxsize);
                    for (int x = 0; x < getWidth(); x += pxsize) {
                        for (int y = 0; y < getHeight(); y += pxsize) {
                            g.drawImage(tmp, x, y, null);
                        }
                    }
                    break;

                case STRETCH:
                    int hscale = getHeight() / backgroundImage.getHeight(null);
                    int wscale = getWidth() / backgroundImage.getWidth(null);
                    AdvancedImage after = new AdvancedImage(getWidth(), getHeight(), backgroundImage.getType());
                    AffineTransform scaleInstance = AffineTransform.getScaleInstance(wscale, hscale);
                    AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    scaleOp.filter(backgroundImage, after);
                    g.drawImage(after, 0, 0, null);
                    break;

                case NONE:
                default:
                    g.drawImage(backgroundImage, 0, 0, null);
                    break;

            }
            ((Graphics2D)g).setComposite(
                    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F));
        }

        sortObjects(objs).forEach((obj) -> {
            AdvancedImage img = obj.img;
            int x = obj.x*pxsize, y = obj.y*pxsize;

            AdvancedImage i = new AdvancedImage(pxsize, pxsize, img.getType());
            int wx = 0, wy = 0, iw = img.getWidth(), ih = img.getHeight();
            switch (obj.img.imgs) {
                case TILE:
                    while (wy < pxsize) {
                        while (wx < pxsize) {
                            System.out.println(obj.rotation);
                            i.drawImage(img.rotate(obj.rotation), wx, wy, 1);
                            wx += iw;
                        }
                        wx = 0;
                        wy += ih;
                    }
                    break;
                case STRETCH:
                    i = img.scale(pxsize, pxsize);
                    break;

                case CROP:
                    i.drawImage(img, i.getWidth()/2, i.getHeight()/2);
                    break;

                case NONE:
                    i = new AdvancedImage(img);
                    break;
            }
            if (img.imgs == AdvancedImage.ImageSizing.NONE)
                switch (img.imgpos) {
                    case CENTER:
                        x += pxsize/2-obj.img.getWidth(null)/2;
                        y += pxsize/2-obj.img.getHeight(null)/2;
                }
            g.drawImage(i.rotate(obj.rotation), x, y, null);
        });
        //objs.forEach((obj, pos) -> g.drawImage(obj.img, pos[0]*pxsize+pxsize/2-obj.img.getWidth(null)/2, pos[1]*pxsize+pxsize/2-obj.img.getHeight(null)/2, null));
    }

    /**
     * returns a sorted set of the objects, sorted by the pain order
     * @param objs the objects to be sorted
     * @return a sorted set
     */
    public final Set<WorldObj> sortObjects(List<WorldObj> objs) {
        List<Class<? extends WorldObj>> po = new ArrayList<>(paintOrder);
        Collections.reverse(po);
        LinkedHashSet<WorldObj> ret = new LinkedHashSet<>(objs);
        po.forEach(cls -> {
            for (WorldObj o : objs) {
                if (cls.isInstance(o)) {
                    ret.remove(o);
                    ret.add(o);
                }
            }});
        return ret;
    }

    /**
     * sets the pain order
     * @param classes the paint order
     */
    @SafeVarargs
    public final void setPaintOrder(Class<? extends WorldObj>... classes) {
        paintOrder = Arrays.asList(classes);
    }

    /**
     * Sets the background opaquness to the given value.
     * @param opaque opaqueness to set to
     * @throws IllegalArgumentException when the opaquness is not between 0 and 1
     */
    public void setBackgroundOpaqueness(double opaque) {
        if (0>opaque || 1<opaque) throw new IllegalArgumentException("Opaquness must be between 0 and 1");
        bg_opaque = opaque;
    }
}
