import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by JONI on 11-Jan-17.
 */
public class ComparisonSetup {
    private static final File SCREENCAP_FOLDER = new File("screencaps");
    private static final File SUPPORTED_FORMATS_TXT = new File("supported_video_formats.txt");

    private static String getSupportedFileExtensions(){
        try{
            return Files.readAllLines(SUPPORTED_FORMATS_TXT.toPath()).stream().map(s -> "*."+s.toLowerCase()+";").collect(Collectors.joining());
        }catch (IOException e){
            return "*.mp4;*.mov;*.mkv;*.flv;*.avi;*.webm;*.wmv;*.wav;";
        }
    }

    public static List<File> selectFiles(){
        List<File> filesToReturn = new ArrayList<File>();

        while (true){
            FileDialog fileDialog = new FileDialog((Frame)null);
            fileDialog.setFile(getSupportedFileExtensions());
            fileDialog.setDirectory("samples");
            fileDialog.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            fileDialog.setVisible(true);
            try {
                filesToReturn.add(new File(fileDialog.getDirectory(), fileDialog.getFile()));
            }catch (NullPointerException e){
                if (filesToReturn.size() > 1){
                    break;
                }else {
                    showQuitOption("You need to select at least 2 files to compare.");
                }
            }
        }

        return filesToReturn;
    }

    private static class ProcessingProgressBar{
        private JFrame frame;
        private JProgressBar progressBar;
        private int tasksCompleted = 0;

        private ProcessingProgressBar(int nrOfTasks) {
            progressBar = new JProgressBar(0,nrOfTasks);
            frame = new JFrame("Capturing images to compare...");
            frame.setSize(380,90);
            FrameHelper.centerComponentOnScreen(frame);
            frame.add(progressBar);
            frame.setVisible(true);
        }

        private void addCompletedTask(){
            tasksCompleted++;
            progressBar.setValue(tasksCompleted);
            if (progressBar.getMaximum() == tasksCompleted){
                close();
            }
        }

        private void close(){
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
    }

    /**
     * takes multiple screencaps of each file given
     *
     * @param files the files of which you wish to take screencaps from
     * @param nrOfScreenshots number of screencaps you wish to take
     *                        screencap timing is spread out over the video
     *                        wil take screencap at the same time for each videofile
     * @return VideoFiles containing the original file plus the created screencaps
     */
    public static List<VideoFile> takeScreencapEveryXSeconds(List<File> files, int nrOfScreenshots){
        ProcessingProgressBar progressBar = new ProcessingProgressBar(files.size() * nrOfScreenshots);
        //nrOfScreenshots + 2 because you ignore the first and last one (first and last frame of video are most likely blank)
        nrOfScreenshots+=2;
        cleanScreencapFolder();

        List<VideoFile> videoFiles = files.stream().map(VideoFile::new).collect(Collectors.toList());
        Java2DFrameConverter frameConverter = new Java2DFrameConverter();

        for (VideoFile videoFile : videoFiles) {
            FFmpegFrameGrabber g = new FFmpegFrameGrabber(videoFile.getFile().toString());

            try {
                g.start();
                long takeImageEveryXFrames = g.getLengthInTime() / nrOfScreenshots;

                //ignore the first and last frame
                for (int i = 1; i < nrOfScreenshots - 1; i++) {
                    g.setTimestamp(takeImageEveryXFrames * i);

                    String fileName = ""+System.currentTimeMillis();

                    BufferedImage image = frameConverter.getBufferedImage(g.grab());

                    File screencapFile = new File(SCREENCAP_FOLDER, fileName + "   " + videoFile.getFile().getName() + ".jpg");
                    ImageIO.write(image, "jpg", screencapFile);
                    videoFile.addScreencap(screencapFile);
                    progressBar.addCompletedTask();
                }
            } catch (Exception e) {e.printStackTrace();}
            try {
                g.stop();
            } catch (FrameGrabber.Exception e) {e.printStackTrace();}
        }
        return videoFiles;
    }

    /**
     * Lets the user compare the screencaps of the videos and select the ones he thinks are better quality
     *
     * @param videoFiles videofiles to compare the screencaps from
     */
    public static void letUserSelectBestScreencaps(final List<VideoFile> videoFiles){
        ImageCompareFrame compareFrame = new ImageCompareFrame();

        int nrOfScreencaps = videoFiles.get(0).getScreencapsCopy().size();
        for (int i = 0; i < nrOfScreencaps; i++) {
            int finalI = i;
            Map<File, VideoFile> screencapsPlusVideoFiles = videoFiles.stream().collect(
                    Collectors.toMap(
                            x -> x.getScreencapsCopy().get(finalI),
                            x -> x
                    ));
            File bestImage = compareFrame.getBestImageFromImages(new ArrayList<File>(screencapsPlusVideoFiles.keySet()));
            screencapsPlusVideoFiles.get(bestImage).addCountToNrOfTimesSelectedAsBest();
        }

        compareFrame.closeFrame();
    }

    private static void cleanScreencapFolder(){
        Arrays.asList(SCREENCAP_FOLDER.listFiles()).forEach(File::delete);
    }

    public static int selectNrOfScreencaps() {
        while (true){
            try{
                String response = JOptionPane.showInputDialog("How many images to compare?");
                if (response == null){
                    showQuitOption();
                }else {
                    return Integer.valueOf(response);
                }
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Please enter a number", "Error", JOptionPane.OK_OPTION);
            }
        }
    }

    public static void showQuitOption(String extraInfo){
        extraInfo += "\n" + "Do you wish to quit?";
        if(JOptionPane.showConfirmDialog(null, extraInfo) == JOptionPane.YES_OPTION){
            System.exit(0);
        }
    }

    public static void showQuitOption(){
        if(JOptionPane.showConfirmDialog(null, "Do you wish to quit?") == JOptionPane.YES_OPTION){
            System.exit(0);
        }
    }
}