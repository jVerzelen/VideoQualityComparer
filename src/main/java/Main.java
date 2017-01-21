import java.io.File;
import java.util.List;

/**
 * Created by JONI on 11-Jan-17.
 */
public class Main {
    public static void main(String[] args) {
        //user chooses which videos to compare
        List<File> filesSelectedByUser = ComparisonSetup.selectFiles();

        //user chooses how many images of the video he wishes to compare
        int nrOfImagesWanted = ComparisonSetup.selectNrOfScreencaps();

        //screencaptures are taken
        List<VideoFile> videoFiles = ComparisonSetup.takeScreencapEveryXSeconds(filesSelectedByUser, nrOfImagesWanted);

        //user compares the screencaptures
        ComparisonSetup.letUserSelectBestScreencaps(videoFiles);

        //shows the user an overview of the result
        new ResultsFrame(videoFiles).show();
    }
}
