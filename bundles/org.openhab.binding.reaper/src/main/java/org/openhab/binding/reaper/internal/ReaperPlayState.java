import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kuba Wolanin - Initial contribution
 */
public class ReaperPlayState {

    private int playState;

    private static final String STOPPED = "STOPPED";
    private static final String PLAYING = "PLAYING";
    private static final String PAUSED = "PAUSED";
    private static final String RECORDING = "RECORDING";
    private static final String RECORD_PAUSED = "RECORD_PAUSED";

    /**
     * Returns play state in a human readable format
     */
    public String getPlayState() {
        switch (playState) {
            case 0:
                return STOPPED;
            case 1:
                return PLAYING;
            case 2:
                return PAUSED;
            case 5:
                return RECORDING;
            case 6:
                return RECORD_PAUSED;
        }
    }

}