import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by JONI on 13-Jan-17.
 */
public abstract class ImageHelper {
    /**
     *
     * @param img image to use
     * @param targetComponent component image should fit into
     * @return largest ImageIcon that fits inside given component while keeping aspect ratio
     */
    public static ImageIcon getScaledIcon(BufferedImage img, Component targetComponent) {
        Dimension scaledDimension = getScaledDimension(new Dimension(img.getWidth(), img.getHeight()), targetComponent);
        return new ImageIcon(
                img.getScaledInstance(
                        (int) scaledDimension.getWidth(),
                        (int) scaledDimension.getHeight(),
                        Image.SCALE_SMOOTH));
    }

    /**
     *
     * @param imgSize size of the image
     * @param targetComponent component in which image should fit
     * @return largest dimension that fits component while keeping aspect ratio
     */
    public static Dimension getScaledDimension(Dimension imgSize, Component targetComponent) {
        Dimension boundary = new Dimension(targetComponent.getWidth(), targetComponent.getHeight());
        // * 1000 to get the same aspect ratio, but largest image possible that still fits the screen
        int original_width = imgSize.width*1000;
        int original_height = imgSize.height*1000;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        if (original_width > bound_width) {
            new_width = bound_width;
            new_height = (new_width * original_height) / original_width;
        }

        if (new_height > bound_height) {
            new_height = bound_height;
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
}
