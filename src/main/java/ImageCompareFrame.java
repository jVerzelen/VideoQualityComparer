import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JONI on 12-Jan-17.
 */
/**
 * Jframe that lets user compare screencaps and choose which he prefers
 */
public class ImageCompareFrame {
    private List<ImageIcon> imageIcons;
    private Map<ImageIcon, File> imageIconsWithOriginalFiles;
    private int currentlyShownImageNr;

    private JFrame frame;
    private JLabel imageLabel;
    private JButton selectBestButton = new JButton("Select as best");
    private File fileSelectedAsBest = new File("");
    private final ReentrantLock lock = new ReentrantLock();

    private static final int BEST_BUTTON_HEIGHT = 50;

    /**
     * lets user pick best screencap out of list
     * @param images images to compare
     * @return image user preferred
     */
    public File getBestImageFromImages(List<File> images){
        if (frame == null){
            createFrame();
        }
        imageIconsWithOriginalFiles = new HashMap<ImageIcon, File>();
        imageIcons = new ArrayList<ImageIcon>();

        for (File file : images) {
            try {
                BufferedImage img = ImageIO.read(file);
                ImageIcon icon = ImageHelper.getScaledIcon(img, imageLabel);
                imageIconsWithOriginalFiles.put(icon, file);
                imageIcons.add(icon);
            } catch (IOException e) {e.printStackTrace();}
        }

        currentlyShownImageNr = 0;
        imageLabel.setIcon(imageIcons.get(0));

        try {
            synchronized (lock) {
                lock.wait();
            }
            return fileSelectedAsBest;
        } catch (InterruptedException e) {e.printStackTrace();}

        return null;
    }

    private void createFrame(){
        frame = new JFrame("Compare screen captures");
        frame.setLayout(new BorderLayout());

        selectBestButton.addActionListener(e -> selectBest());
        selectBestButton.setPreferredSize(new Dimension(0,BEST_BUTTON_HEIGHT));

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changeImage();
            }
        });

        frame.add(selectBestButton, BorderLayout.NORTH);
        frame.add(imageLabel, BorderLayout.CENTER);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void selectBest() {
        fileSelectedAsBest = imageIconsWithOriginalFiles.get(imageIcons.get(currentlyShownImageNr));
        synchronized (lock) {
            lock.notify();
        }
    }

    private void changeImage(){
        currentlyShownImageNr++;
        if (currentlyShownImageNr >= imageIcons.size()){
            currentlyShownImageNr = 0;
        }
        imageLabel.setIcon(imageIcons.get(currentlyShownImageNr));
    }

    public void closeFrame() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}
