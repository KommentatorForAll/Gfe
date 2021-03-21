import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class MusicHandler {

    /**
     * The volume each clip gets played at
     */
    public static int volume = 100;

    /**
     * the clip, which is currently played
     */
    public static Clip currentClip;

    /**
     * Loads a clip from the given location.
     * Due to java restrictions, this must be a wav file and even then it sometimes doesn't work with certain encryptions.
     * @see Utils#loadAllClips for non-manual loading
     * @param location the file location of the audio clip
     * @return a newly generated clip
     * @throws IllegalArgumentException if the file isn't a wav File and couldn't be loaded
     */
    public static Clip loadClip(String location) {
        Clip in = null;
        AudioInputStream audioIn;
        try {
            System.out.println(location);
            audioIn = AudioSystem.getAudioInputStream(new File(location));
            in = AudioSystem.getClip();
            in.open( audioIn );
        } catch (Exception e) {
            System.err.println("Error while loading sound: " + location);
            e.printStackTrace();
            if (!location.endsWith(".wav")) throw new IllegalArgumentException("File must be a wav file");
        }
        return in;
    }

    /**
     * starts playing the clip
     * @param c the clip to play
     */
    public static void play(Clip c) {
        stop(c);
        loop(c,0);
    }

    /**
     * stops the clip
     * @param c the clip to stop
     */
    public static void stop(Clip c) {
        c.stop();
        c.setFramePosition(0);
    }

    /**
     * plays the clip in an continues loop
     * @param c the clip to loop
     */
    public static void loop(Clip c, int... amount) {
        currentClip = c;
        stop(c);
        //setVolume(c, volume);
        c.loop(amount.length > 0? amount[0]: Clip.LOOP_CONTINUOUSLY);
        //c.loop(Clip.LOOP_CONTINUOUSLY);
        c.start();
    }

    /**
     * Changes the volume of the clip
     * @param c the clip to change the volume on
     * @param volume the volume to set it to
     * @throws IllegalArgumentException if the volume is not in range between 0 and 200
     */
    public static void setVolume(Clip c, int volume) {
        if (0 > volume || volume > 200) throw new IllegalArgumentException("Volume must be in range 0, 200");
        FloatControl gainC = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
        double gain = volume/100.0;
        float dB = (float) (Math.log(gain) / Math.log(10.0) * 20.0);
        gainC.setValue(dB);
    }

    /**
     * sets the default volume sounds get played at
     * @param volume the volume
     * @throws IllegalArgumentException if volume is not in range 0,200
     */
    public static void setVolume(int volume) {
        MusicHandler.volume = volume;
        //if (currentClip != null) setVolume(currentClip, volume);
    }
}
