import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A utility class capable of loading images, extracting assets from a compressed jar etc.
 */
public class Utils {

    /**
     * The location of the (eventually extracted) assets folder
     */
    public static String assets;

    /**
     * The location of the appdata folder, dependent on the OS one is running
     */
    public static String localAppdata = fetchAppdataFolder();

    /**
     * The Jar loaded from, when loaded in a jar
     * null if loaded from a folder structure
     */
    public static JarFile me;

    /**
     * if there was an attempt to fetch the jar file
     */
    private static boolean attemptedJar;


    /**
     * loads an image from anywhere on your pc.
     * if no extention is given, .png is added automaticly
     * note: this works in both, in folder as well as in jar structures.
     * this also works with folders in folders.
     * @param filelocation the location of the image
     * @return the loaded image. !!may be null if no image was found!!
     */
    public static AdvancedImage loadImage(String filelocation) {
        if (!filelocation.matches("\\..+$")) {
            filelocation += ".png";
        }
        try {
            InputStream is = Utils.class.getResourceAsStream(filelocation);
            if (is == null)
                return new AdvancedImage(ImageIO.read(new File(filelocation)));
            return new AdvancedImage(ImageIO.read(is));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * loads an image from the assets folder, located at the src root.
     * @param filename name of the image
     * @return the loaded image !!may be null if no image was found!!
     */
    public static AdvancedImage loadImageFromAssets(String filename) {
        if (!attemptedJar) attemptJar();
        return loadImage(assets+"sprites/" +filename);
    }

    /**
     * returns the dimension of the given string.
     * @param font the font of the text
     * @param text the text itself
     * @return the dimension of the string, when drawn as {width, height}
     */
    public static int[] getStringDimensions(java.awt.Font font, String text) {
        Graphics2D graphics = (Utils.loadImageFromAssets("Invis.png")).createGraphics();
        if (graphics == null)
        {
            return new int[] {0,0};
        }

        graphics.setFont(font);

        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        String[] lines = text.split("[\n\r]");
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        int height = fontMetrics.getHeight();
        int width = Arrays.stream(lines).map(fontMetrics::stringWidth).max(Comparator.comparingInt(a -> a)).orElse(0);
        return new int[] {width, height};
    }

    /**
     * Bc T is just pain in the ass
     * @param T Testosterone
     * @return yes.
     */
    public <T> int pain(ArrayList<T> T) {
        return Integer.MAX_VALUE;
    }

    /**
     * fetches the OS the game is run on
     * @return the OS currently booted
     */
    public static String getOS() {
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) return "windows";
        if (os.contains("Mac")) return "mac";
        if (os.contains("Linux")) return "linux";
        return os;
    }

    /**
     * returns the appdata folder of the running os
     * @return the appdata folders location
     */
    public static String fetchAppdataFolder() {
        return fetchAppdataFolder(getOS());
    }

    /**
     * Fetches the appdata folder from the given os
     * @param os the operating system, the program is running on
     * @return the path for appdata
     */
    public static String fetchAppdataFolder(String os) {
        String dir = System.getenv("APPDATA");
        if (dir != null)
            return dir+"/";
        switch (os) {
            case "mac":
                return System.getProperty("user.home")+"/Library/Application Support/";//"bad os, we don't support such crap";
            case "linux":
                return System.getProperty("user.home")+"/.config/";
            default:
                return "./";
        }
    }

    /**
     * loads a font from the assets/fonts folder
     * @param name the font name to load
     * @return the newly created font
     */
    public static Font loadFontFromAssets(String name) {
        // Creates an awt font from the file as true type font (.ttf)
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(assets+"fonts/"+name));
            //generates an graphics environment for idk what, not my script, but scisneromams i'm just commenting this crap
            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            //registers the font in the graphics environment
            graphicsEnvironment.registerFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return font;
    }

    /**
     * Loads all fonts from the assets folder as java.awt.Font
     * This is needed as Greenfoot sucks and one requies a huge script using awt fonts to center some text
     * @return an hashmap of fonts as name -> font
     */
    public static HashMap<String, Font> loadAllFonts() {
        if (!attemptedJar) attemptJar();
        HashMap<String, java.awt.Font> fonts = new HashMap<>();
        File dir = new File(assets+"fonts/");
        File[] dirFiles = dir.listFiles();
        String name;
        if (dirFiles != null) {
            for (File child : dirFiles) {
                name = child.getName();
                if (!name.endsWith(".ttf")) continue;
                try {
                    fonts.put(name.replaceAll("\\.\\w+$", ""), loadFontFromAssets(name));
                }
                catch (Exception e) {
                    System.err.println("Error while loading font {}".replace("{}", name));
                    e.printStackTrace();
                }
            }
        }
        return fonts;
    }

    /**
     * reads an image from a file
     * @param file the file to read the image from
     * @throws IOException thrown when an IOException occurs in ImageIO.read()
     */
    public static void readImage(File file) throws IOException
    {
        BufferedImage image = ImageIO.read(file);
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                System.out.printf(" %s ", Integer.toHexString(image.getRGB(x, y)));
            }
            System.out.println();
        }
    }

    /**
     * returns the first match of the value, in the map
     * @param m the map to search in
     * @param value the value to search for
     * @return key of the first occurrence of the value
     */
    public static Object getKey(Map m, Object value) {
        for (Object x : m.keySet()) {
            if (m.get(x).equals(value)) return x;
        }
        return null;
    }

    /**
     * reads a file from the assets. this works in both, folder and jar structures.
     * @param filename the file to read.
     * @return the string inside of that file.
     */
    public static String readFromAssets(String filename) {
        InputStream is = Utils.class.getResourceAsStream(assets+filename);
        Scanner sc;
        if (is == null) {
            try {
                sc = new Scanner(new File(assets+filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "";
            }
        }
        else
            sc = new Scanner(is);

        sc.useDelimiter("\\z");
        return sc.next();
    }

    /**
     * extracts an package from the jar if in jar
     * @param source the package to extract
     * @param to where to extract it
     * @return the location as file of the extracted package/file. returns the original location when not loaded from jar
     */
    public static File extract(String source, String to) {
        String dirPath = to+source.replaceAll("[^/]*\\..*$","");
        attemptJar();
        if (me == null) return new File("src/"+source);
        try {
            if (Files.exists(Paths.get(dirPath))) {
                return new File(to);
            }
            Files.createDirectories(Paths.get(dirPath));
            JarEntry je = me.getJarEntry(source);
            if ( je != null)
            {
                if (je.isDirectory()) {
                    me.stream().filter(e -> e.getName().startsWith(source)).forEach(e -> extractFile(e.getName(), to));
                    return new File(to);
                }
                else {
                    return extractFile(je.getName(), to);
                }
            }
        }
        catch(IOException ioe)
        {
            System.out.println("Exception: " + ioe);
            ioe.printStackTrace();
        }
        return null;
    }

    /**
     *  This method is responsible for extracting resource files from within the .jar to the temporary directory.
     *  @param sourceFile The filepath relative to the 'Resources/' directory within the .jar from which to extract the file.
     *  @return A file object to the extracted file
     **/
    public static File extractFile(String sourceFile, String to)
    {
        String dirPath = to+ sourceFile.replaceAll(".*\\..*$","");
        try {
            Files.createDirectories(Paths.get(dirPath));
            JarEntry jarEntry = me.getJarEntry(sourceFile);
            if ( jarEntry != null && !jarEntry.isDirectory())
            {
                //Getting the jarEntry into the inputStream
                InputStream inputStream = me.getInputStream(jarEntry);
                //Creating a output stream to a new file of our choice
                FileOutputStream fileOutputStream = new java.io.FileOutputStream(to+ sourceFile);
                while (inputStream.available() != 0)
                {
                    fileOutputStream.write(inputStream.read());
                }
                fileOutputStream.close();
                inputStream.close();
                return new File(to+sourceFile);
            }
            return new File(sourceFile);
        }
        catch(IOException ioe)
        {
            System.out.println("Exception: " + ioe);
            ioe.printStackTrace();
            return null;
        }
    }

    /**
     * checks if loaded from an jar file. Extracts the assets if loaded from jar.
     */
    public static void attemptJar() {
        if (attemptedJar) return;

        try {
            String fname = new File(Utils.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath())
                    .getName();
            me = new JarFile("./"+ fname);
            attemptedJar = true;
            if (assets == null) {
                assets = localAppdata + removeExt(fname)+"/";
            }
            extract("assets", assets);
            assets += "assets/";
        }
        catch (Exception e) {
            assets  = System.getProperty("user.dir")+"/src/assets/";
            System.err.println("error while fetching jar:");
            e.printStackTrace();
        }
        attemptedJar = true;
    }

    /**
     * Removes the file extension of a string
     * @param s the filename with extension
     * @return the filename without extension
     */
    public static String removeExt(String s) {
        return s.replaceAll("\\..*$", "");
    }
}
