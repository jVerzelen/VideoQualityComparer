import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by JONI on 13-Jan-17.
 */

/**
 * Shows result of video comparison
 */
public class ResultsFrame {
    private JFrame frame;

    private static final float FONT_SIZE = 15f;
    private static final int VERTICAL_GAP_BETWEEN_RESULTS = 15;
    private static final int BORDER_SIZE = 30;

    /**
     * shows info about the video files and provides a link to the files
     * user can then decide which file(s) to keep based on info shown
     * @param videoFiles videofiles to show in report
     */
    public ResultsFrame(List<VideoFile> videoFiles) {
        videoFiles.sort(Comparator.comparingInt(VideoFile::getNrOfTimesSelectedAsBest));

        frame = new JFrame("Results");

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridLayout(videoFiles.size(), 2,0, VERTICAL_GAP_BETWEEN_RESULTS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE,BORDER_SIZE,BORDER_SIZE,BORDER_SIZE));

        for (final VideoFile file : videoFiles) {
            resultPanel.add(getVideoFileButtonPart(file.getFile()));
            resultPanel.add(getVideoFileInfo(file));
        }

        frame.add(resultPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();

        FrameHelper.centerComponentOnScreen(frame);
    }

    private JPanel getVideoFileInfo(VideoFile videoFile) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(videoFile.getFile().getName());
        JLabel sizeLabel = new JLabel(videoFile.getSizeOfFileInMegabytes() + " MB");
        JLabel bestPanel = new JLabel(videoFile.getNrOfTimesSelectedAsBest() + " times selected as best");

        nameLabel.setFont(nameLabel.getFont().deriveFont(FONT_SIZE));
        sizeLabel.setFont(sizeLabel.getFont().deriveFont(FONT_SIZE));
        bestPanel.setFont(bestPanel.getFont().deriveFont(FONT_SIZE));

        infoPanel.add(nameLabel);
        infoPanel.add(sizeLabel);
        infoPanel.add(bestPanel);

        return infoPanel;
    }

    private JPanel getVideoFileButtonPart(File file) {
        JButton openFolderButton = new JButton("Open location in explorer");
        openFolderButton.setFont(openFolderButton.getFont().deriveFont(FONT_SIZE));
        openFolderButton.addActionListener(getOpenFileActionListener(file));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openFolderButton);

        return buttonPanel;
    }

    public void show() {
        frame.setVisible(true);
    }

    private static ActionListener getOpenFileActionListener(File file){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Runtime.getRuntime().exec("explorer.exe /select, "+file.toString());
                } catch (IOException e1) {e1.printStackTrace();}
            }
        };
    }
}