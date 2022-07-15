package org.grits.toolbox.tools.glycanbuilder.core.renderer.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class with different methods for creating styled text image
 * and computing the bounds using TextLayout.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class TextRendererUtils {

	public static Image getStyledTextImage(Device device, String text, String font_face, int font_size) {
		TextLayout layout = createStyledTextLayout(device, text, font_face, font_size);
		Image img = new Image(device, layout.getBounds());
		GC gc = new GC(img);
		gc.setBackground(device.getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(device.getSystemColor(SWT.COLOR_BLACK));
		layout.draw(gc, 0, 0);
		gc.dispose();

		return img;
	}

	public static Rectangle getStyledTextBounds(String text, String font_face, int font_size) {
		return createStyledTextLayout(Display.getCurrent(), text, font_face, font_size).getBounds();
	}

	private static TextLayout createStyledTextLayout(Device device, String text, String font_face, int font_size) {
		// Adjust font size
		font_size = (int) (font_size / 0.75);

		TextLayout layout = new TextLayout(device);
		Font font = new Font(device, font_face, font_size, SWT.BOLD);
		layout.setFont(font);

		// Create text and extract ranges for the subscripts and superscripts
		String text_new = "";
		List<int[]> lRanges = null;
		List<int[]> lSubs = new ArrayList<>();
		List<int[]> lSups = new ArrayList<>();
		int[] range = new int[2];
		for ( int i=0; i<text.length(); i++ ) {
			char c = text.charAt(i);
			if ( c == '_' || c == '^' ) {
				lRanges = (c=='_')? lSubs : lSups;
				continue;
			}
			if ( c == '{' ) {
				range[0] = text_new.length();
				continue;
			}
			if ( c == '}' ) {
				range[1] = text_new.length()-1;
				lRanges.add(range);
				range = new int[2];
				continue;
			}
			text_new += c;
		}
		layout.setText(text_new);

		Font subFont = new Font(device, font_face, font_size, SWT.NORMAL);
		// subscription
		TextStyle style = new TextStyle(subFont, null, null);
		style.rise = -font_size/2;
		for ( int[] rangeSub : lSubs )
			layout.setStyle(style, rangeSub[0], rangeSub[1]);

		// superscription
		style = new TextStyle(subFont, null, null);
		style.rise += font_size/2;
		for ( int[] rangeSup : lSups )
			layout.setStyle(style, rangeSup[0], rangeSup[1]);

		return layout;
	}
}
