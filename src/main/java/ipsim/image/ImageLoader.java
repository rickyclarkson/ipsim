package ipsim.image;

import javax.swing.ImageIcon;
import java.net.URL;

/**
	Adapted from code by Ciaran Jessup.
*/
public final class ImageLoader
{
	public static ImageIcon loadImage(final URL url)
	{
		return new ImageIcon(url);
	}
}