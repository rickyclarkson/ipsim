package ipsim.image;

import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Adapted from code by Ciaran Jessup.
 */
public final class ImageLoader {
    public static ImageIcon loadImage(final URL url) {
        return new ImageIcon(url);
    }
}