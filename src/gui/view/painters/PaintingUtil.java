package gui.view.painters;

import java.awt.Dimension;
import java.awt.FontMetrics;

public class PaintingUtil {

	
	public static Dimension calculateStringDimension(FontMetrics metrics, String text){

		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		int adv = metrics.stringWidth(text);
		//return size
		Dimension size = new Dimension(adv, hgt);
		return size;
	}
}
