import java.awt.*;

/**
 * Created by JONI on 13-Jan-17.
 */
public abstract class FrameHelper {
    public static void centerComponentOnScreen(Component component){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        component.setLocation(dim.width/2-component.getSize().width/2, dim.height/2-component.getSize().height/2);
    }
}
