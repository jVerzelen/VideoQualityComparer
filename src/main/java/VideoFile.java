import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JONI on 12-Jan-17.
 */

/**
 * Holds a video file, screencaps taken from it, and nr of times the user preferred the quality of this video
 */
public class VideoFile {
    private File file;
    private List<File> screencaps = new ArrayList<File>();
    private int nrOfTimesSelectedAsBest = 0;

    public VideoFile(File file) {
        this.file = file;
    }

    public void addScreencap(File screencap){
        screencaps.add(screencap);
    }

    public void addCountToNrOfTimesSelectedAsBest(){
        nrOfTimesSelectedAsBest++;
    }

    public int getNrOfTimesSelectedAsBest(){
        return nrOfTimesSelectedAsBest;
    }

    public File getFile() {
        return file;
    }

    public List<File> getScreencapsCopy(){
        return new ArrayList<File>(screencaps);
    }

    public int getSizeOfFileInMegabytes(){
        return (int)file.length() / 1000000;
    }
}
