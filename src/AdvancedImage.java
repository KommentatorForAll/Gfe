import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

/**
 * An extension of BufferedImages, able to be cloned, having a default positioning when drawn with supporting functions
 */
public class AdvancedImage extends BufferedImage implements Cloneable {

    /**
     * The default position, when being drawn.
     * @see ImagePosition
     * Default value: {@link ImagePosition#CENTER}
     */
    public ImagePosition imgpos = ImagePosition.CENTER;

    /**
     * The default sizing setting when drawn.
     * @see ImageSizing
     * Default value: {@link ImageSizing#NONE}
     */
    public ImageSizing imgs = ImageSizing.NONE;

    /**
     * Creates a new AdvancedImage of ARGB type.
     * @param width width of the image
     * @param height height of the image
     */
    public AdvancedImage(int width, int height) {
        super(width, height, TYPE_INT_ARGB);
    }

    /**
     * Creates a new AdvancedImage.
     * @param width width of the Image
     * @param height height of the image
     * @param imageType image type (see BufferedImage for more information)
     */
    public AdvancedImage(int width, int height, int imageType) {
        super(width, height, imageType);
    }

    /**
     * Creates a new AdvancedImage from a buffered image
     * @param img The Image, which shall be copied
     */
    public AdvancedImage(BufferedImage img) {
        super(img.getWidth(), img.getHeight(), img.getType());
        drawImage(img);
    }

    /**
     * draws a string onto the image.
     * @param s the string to draw
     * @param color the color to paint the string in
     * @param font the font of the string
     * @deprecated
     * @see AdvancedImage#drawText(Color, Font, String)
     */
    public void draw(String s, Color color, Font font) {
        drawText(color, font, s);
    }

    /**
     * draws an Image onto this image.
     * @param img the image to draw
     * @deprecated
     * @see AdvancedImage#drawImage(AdvancedImage)
     */
    public void draw(AdvancedImage img) {
        drawImage(img);
    }

    /**
     * draws a BufferedImage onto this image.
     * @param img the image to draw
     */
    public void drawImage(BufferedImage img) {
        drawImage(img, 0,0);
    }

    /**
     * Draws a BufferedImage onto this image at the given position
     * @param img the image to be drawn
     * @param x x position of the top left corner
     * @param y y position of the top left corner
     */
    public void drawImage(BufferedImage img, int x, int y) {
        drawImage(img, x, y, 1);
    }

    /**
     * Draws a BufferedImage onto this image at the given position
     * @param img the image to be drawn
     * @param x x position of the top left corner
     * @param y y position of the top left corner
     * @param opaque the opaqueness of the drawn image
     */
    private void drawImage(BufferedImage img, int x, int y, float opaque) {
        drawImage_(img, x, y, opaque);
    }

    /**
     * internal function to draw an image
     * @param image image to draw
     * @param x position to draw at
     * @param y position to draw at
     * @param opaque the opaqueness of the image
     */
    private void drawImage_(BufferedImage image, int x, int y, float opaque) {
        Graphics2D g2d = createGraphics();
        g2d.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC, opaque));
        g2d.drawImage(image, x, y, null);
        g2d.dispose();
    }

    /**
     * Draws an AdvancedImage onto this image, using its given position and sizing options
     * @param img the image to be drawn
     */
    public void drawImage(AdvancedImage img) {
        int x = 0, y = 0;
        switch (img.imgpos) {
            case CENTER:
                x = getWidth()/2;
                y = getHeight()/2;
                break;

        }
        drawImage(img, x, y, 1);
    }

    /**
     * Draws an AdvancedImage onto this image, using its given position and sizing options
     * @param img the image to be drawn
     * @param x x position of the image
     * @param y y position of the image
     * @param opaque the opaqueness the image is drawn with
     */
    public void drawImage(AdvancedImage img, int x, int y, float opaque) {
        int mw = getWidth(), mh = getHeight(), iw = img.getWidth(), ih = img.getHeight(), wx = x, wy = y;
        AdvancedImage i;
        switch (img.imgs) {
            case TILE:
                i = new AdvancedImage(img);
                while (wy < mh) {
                    while (wx < mw) {
                        i.drawImage((BufferedImage) img, wx, wy, opaque);
                        wx += iw;
                    }
                    wx = x;
                    wy += ih;
                }
                break;
            case STRETCH:
                i = img.scale(mw-x, mw-y);
                break;

            case NONE:
            default:
                i = new AdvancedImage(img);
                break;
        }
        switch (img.imgpos) {
            case TOP_LEFT:
                drawImage_((BufferedImage) i, x, y, opaque);
                break;

            case CENTER:
                drawImage_((BufferedImage) i, x-i.getWidth()/2, y-i.getHeight()/2, opaque);
        }
    }

    /**
     * Scales an image to the given dimensions
     * @param width width of the new image
     * @param height height of the new image
     * @return a scaled version of the image
     */
    public AdvancedImage scale(int width, int height) {
        double hscale = 1.0*height/getHeight(null);
        double wscale = 1.0*width/getWidth(null);
        AdvancedImage after = new AdvancedImage(width, height, getType());
        AffineTransform scaleInstance = AffineTransform.getScaleInstance(wscale, hscale);
        AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        scaleOp.filter(this, after);
        return after;
    }

    /**
     * Clones this image
     * @return a fabric new clone. Produced to execute order 66
     */
    public AdvancedImage clone() {
        BufferedImage img = deepCopy(this);
        AdvancedImage i = new AdvancedImage(img);
        i.imgpos = imgpos;
        i.imgs = imgs;
        return i;
    }

    /**
     * Returns a deep Copy of a given BufferedImage
     * @param bi the image to be deep copied
     * @return a new Buffered image, being an as deep copy of the original as its soul
     */
    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    /**
     * Enhanced toString function for AdvancedImages.
     * Calls the toString function of BufferedImages and adds its positioning as well as its sizing to it.
     * @return stringyfied image information
     */
    @Override
    public String toString() {
        return super.toString() + " sizing: " + imgs + "; Positioning: " + imgpos;
    }

    /**
     * prints out an rgb-mesh of the Image itself.
     * Can be quite useful for debugging (especially when looking for the point, where you lost your meta data)
     */
    public void print(){
        System.out.println(toARGBMesh());
    }

    /**
     * creates a String version of the image, containing its ARGB data as hexcode.
     * Hexcode is formatted as 0xAARRGGBB
     * @return a string of the image
     */
    public String toARGBMesh() {
        int w = getWidth(), h = getHeight();
        StringBuilder out = new StringBuilder();
        out.append("[\n");
        for (int i = 0; i<w; i++) {
            out.append("[");
            for (int j = 0; j<h; j++) {
                out.append("[").append(String.format("%8s", Integer.toHexString(getRGB(i, j))).replace(" ", "0")).append("] ");
            }
            out.append("]\n");
        }
        out.append("]");
        return out.toString();
    }

    /**
     * rotates the image and puts it onto a new image
     * @param angle the angle the image gets rotated by.
     * @return a rotated version of this image
     */
    public AdvancedImage rotate(double angle) {
        return rotateImageByDegrees(this, angle);
    }

    /**
     * rotates the image.
     * !!meta data / rgb values on alpha-0 pixels is not getting copied!!
     * @param img the image which is gonna be rotated
     * @param angle the angle it gets rotated by
     * @return a rotated image
     */
    public static AdvancedImage rotateImageByDegrees(AdvancedImage img, double angle) {
        angle = -angle;
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2.0, (newHeight - h) / 2.0);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.setComposite(
                AlphaComposite.getInstance(AlphaComposite.SRC, 1));
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        AdvancedImage i = new AdvancedImage(rotated);
        i.imgpos = img.imgpos;
        i.imgs = img.imgs;

        return i;
    }

    /**
     * replaces the given color by a new one
     * !!this is highly inefficient for large images!!
     * @param oldColor the color which shall be replaced
     * @param newColor the color it gets replaced with
     * @return an image with the replaced colors
     */
    public AdvancedImage replaceColor(Color oldColor, Color newColor) {
        return replaceColor(this, oldColor, newColor);
    }

    /**
     * replaces the given color by a new one
     * !!this is highly inefficient for large images!!
     * @param oldColor the color which shall be replaced
     * @param newColor the color it gets replaced with
     * @return an image with the replaced colors
     */
    public AdvancedImage replaceColor(int oldColor, int newColor) {
        return replaceColor(this, oldColor, newColor);
    }

    /**
     * replaces the given color by a new one
     * !!this is highly inefficient for large images!!
     * @param image the image the color replacement gets done for
     * @param target the color which shall be replaced
     * @param preferred the color it gets replaced with
     * @return an image with the replaced colors
     */
    public static AdvancedImage replaceColor(AdvancedImage image, Color target, Color preferred) {
        return replaceColor(image, target.getRGB(), preferred.getRGB());
    }

    /**
     * replaces the given color by a new one
     * !!this is highly inefficient for large images!!
     * @param image the image the color replacement gets done for
     * @param target the color which shall be replaced
     * @param preferred the color it gets replaced with
     * @return an image with the replaced colors
     */
    public static AdvancedImage replaceColor(AdvancedImage image, int target, int preferred) {
        int width = image.getWidth();
        int height = image.getHeight();
        AdvancedImage newImage = new AdvancedImage(width, height, image.getType());
        int color;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                color = image.getRGB(i, j);
                if (color == target) {
                    newImage.setRGB(i, j, preferred);
                }
                else {
                    newImage.setRGB(i, j, color);
                }
            }
        }

        return newImage;
    }

    /**
     * fills the image with the given color
     * @param clr the color this image gets filled with
     */
    public void fill(Color clr) {
        drawRect(0,0, getWidth(), getHeight(), clr);
    }

    /**
     * draws a rectangle onto this image
     * @param x x position of the rectangle
     * @param y y position of the rectangle
     * @param width width of the rectangle
     * @param height height of the rectangle
     * @param clr the color which should be used
     */
    public void drawRect(int x, int y, int width, int height, Color clr) {
        Graphics2D g = createGraphics();
        g.setColor(clr);
        g.fillRect(x,y, width, height);
        g.dispose();
    }

    /**m
     *  draws an oval onto the image
     * @param x x position of the center
     * @param y y position of the center
     * @param width width of the oval
     * @param height height of the oval
     * @param clr color of the oval
     */
    public void drawOval(int x, int y, int width, int height, Color clr) {
        Graphics2D g = createGraphics();
        g.setColor(clr);
        g.drawOval(x,y, width, height);
        g.dispose();
    }

    /**
     * draws a string onto the image
     * @param color color of the text
     * @param font font of the text
     * @param text the text which gets drawn
     * @param x x position of the center
     * @param y y position of the center
     */
    public void drawText(Color color, Font font, String text, int x, int y) {
        drawText(color, font, text, x, y, VerticalAlignment.CENTER, HorizontalAlignment.CENTER);
    }

    /**
     * draws a string onto the image
     * @param color color of the text
     * @param font font of the text
     * @param text the text which gets drawn
     * @param x x position of the center
     * @param y y position of the center
     * @param verticalAlignment vertical alignment of the text
     * @param horizontalAlignment horizontal alignment of the text
     */
    public void drawText(Color color, Font font, String text, int x, int y, VerticalAlignment verticalAlignment, HorizontalAlignment horizontalAlignment)
    {
        Graphics2D graphics = createGraphics();
        if (graphics == null)
        {
            System.err.println("No valid graphics");
            return;
        }

        graphics.setColor(color);
        graphics.setFont(font);

        if (x < 0 || x >= getWidth())
        {
            x = getWidth() / 2;
        }
        if (y < 0 || y >= getHeight())
        {
            y = getHeight() / 2;
        }

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        String[] lines = text.split("[\n\r]");
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        int height = fontMetrics.getHeight();
        if(verticalAlignment == VerticalAlignment.CENTER)
        {
            y -= (height * (lines.length / 2));
        }
        else if (verticalAlignment == VerticalAlignment.BOTTOM)
        {
            y -= height * lines.length;
        }
        y += (height / 4);
        for (int i = 0; i < lines.length; i++)
        {
            int modX = x;
            if(horizontalAlignment == HorizontalAlignment.CENTER)
            {
                modX = x - (fontMetrics.stringWidth(lines[i]) / 2);
            }
            else if(horizontalAlignment == HorizontalAlignment.RIGHT)
            {
                modX = x - fontMetrics.stringWidth(lines[i]);
            }
            graphics.drawString(lines[i], modX, y + i * height);
        }

        graphics.dispose();
    }

    /**
     * draws the text onto the image
     * @param color color the text is painted in
     * @param font the font of the text
     * @param text the text to write
     */
    public void drawText(Color color, Font font, String text) {
        drawText(color, font, text, getWidth()/2, getHeight()/2);
    }

    /**
     * fetches the dimension of the image.
     * @return the images dimension as {width, height}
     */
    public int[] getDimension() {
        return new int[] {getWidth(), getHeight()};
    }

    /**
     * Enum for how the Image is positioned, when drawn by a supporting class
     */
    public enum ImagePosition {
        /**
         * the x and y position define the top left corner of the image
         */
        TOP_LEFT,

        /**
         * the x and y position define the center of the image
         */
        CENTER
    }

    /**
     * an enum for what to do, if the image doesn't fit the bounds of what it is drawn onto
     */
    public enum ImageSizing {

        /**
         * leaving the image as it is. It will be untouched.
         */
        NONE,

        /**
         * draws the image over and over again, until the bounds are reached
         */
        TILE,

        /**
         * Stretches the image until it fits the size.
         * May (and is intended to) cause distribution
         */
        STRETCH,

        /**
         * When the image is too big, it gets cropped until it fits.
         * I actually have no idea what it does and why one would use it
         */
        CROP
    }

    /**
     * Manages the vertical alignment of drawn text
     */
    enum VerticalAlignment
    {
        /**
         * y coordinate is the topmost pixel of the text
         */
        TOP,

        /**
         * y coordinate is the center coordinate of the text
         */
        CENTER,

        /**
         * y coordinate is the lowest pixel of the text
         */
        BOTTOM
    }

    /**
     * Manages the Horizontal alignment of the text.
     */
    enum HorizontalAlignment
    {
        /**
         * x coordinate is the leftmost pixel of the text
         */
        LEFT,

        /**
         * x coordinate is in the center of the text
         */
        CENTER,

        /**
         * x coordinate is the rightmost pixel of the text
         */
        RIGHT
    }
}
