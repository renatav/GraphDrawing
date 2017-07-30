/*
 * Copyright 2012 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package gui.main.frame.icons;

import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Class to load images out of jar-files
 *
 * @author Michael Hagen
 */
public class ImageHelper {

    private ImageHelper() {
    }

    public static ImageIcon loadImage(String name) {
        ImageIcon image = null;
        try {
            URL url = ImageHelper.class.getResource(name);
            if (url != null) {
                java.awt.Image img = Toolkit.getDefaultToolkit().createImage(url);
                if (img != null) {
                    image = new ImageIcon(img);
                }
            }
        } catch (Throwable ex) {
            System.out.println("ERROR: loading image " + name + " failed. Exception: " + ex.getMessage());
        }
        return image;
    }
}
