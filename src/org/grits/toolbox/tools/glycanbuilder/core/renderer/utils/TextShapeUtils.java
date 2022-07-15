/*
*   EuroCarbDB, a framework for carbohydrate bioinformatics
*
*   Copyright (c) 2006-2009, Eurocarb project, or third-party contributors as
*   indicated by the @author tags or express copyright attribution
*   statements applied by the authors.  
*
*   This copyrighted material is made available to anyone wishing to use, modify,
*   copy, or redistribute it subject to the terms and conditions of the GNU
*   Lesser General Public License, as published by the Free Software Foundation.
*   A copy of this license accompanies this distribution in the file LICENSE.txt.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*   or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
*   for more details.
*
*   Last commit: $Rev: 1210 $ by $Author: glycoslave $ on $Date:: 2009-06-11 #$  
*/

package org.grits.toolbox.tools.glycanbuilder.core.renderer.utils;

import org.eclipse.core.internal.resources.OS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class with different methods for computing coordinates and dimensions
 * of geometrical shapes for texts.
 * 
 * @author Alessio Ceroni (a.ceroni@imperial.ac.uk)
 * @author Masaaki Matsubara (matsubara@uga.edu)
 */

public class TextShapeUtils {

	/**
	 * Return Rectangle for the dimensions of a text in a given font.
	 */
	static public Rectangle textBounds(Device device, String text, String font_face, int font_size) {
		if (text.length() == 0)
			return new Rectangle(0, 0, 0, font_size);

		Font font = new Font(device, font_face, font_size, SWT.NORMAL);
		Rectangle ret = textBounds(font, text);
		font.dispose();
		return ret;
	}

	/**
	 * Return Rectangle for the dimensions of a text in a given font.
	 */
	static public Rectangle textBounds(Font font, String text) {
		// compute text bounds
		TextLayout tl = new TextLayout(font.getDevice());
		tl.setText(text);
		tl.setFont(font);
		Rectangle ret = tl.getBounds();
		tl.dispose();
		return ret;
	}

	/**
	 * Return the shape used to draw a text in a given font.
	 */
	static public Path getTextPath(Device device, String text, String font_face, int font_size) {
		return getTextPath(device, 0, 0, text, font_face, font_size);
	}

	/**
	 * Return the shape used to draw a text in a given font. Initialize the shape at
	 * the given coordinates.
	 */
	static public Path getTextPath(Device device, double x, double y, String text, String font_face, int font_size) {
		// compute text layout
		Font font = new Font(device, font_face, font_size, SWT.NORMAL);
//		TextLayout tl = new TextLayout(device);
//		tl.setText(text);
//		tl.setFont(font);
		Path p = new Path(device);
		p.addString(text, (float) x, (float) y, font);
		font.dispose();
		return p;
	}

	private static double dCurrentDPIScale = 1.0d;
	static {
		final Display display = Display.getDefault();
		// Access display thread safely
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				dCurrentDPIScale = display.getDPI().x / 72;
				if (dCurrentDPIScale < 1.0) {
					dCurrentDPIScale = 1.0d;
				}
			}
		});
	}

	/**
	 * Re-calculate size to adjust to current DPI
	 * @param scale double value of original size in pixel
	 * @return adjusted size with DPI
	 */
	public static double scaleWithDPI(double size) {
		return size * dCurrentDPIScale;
	}

	static public double getFontSizePixel(int fontSize) {
		// Adjust font size to fit node size
		double size = fontSize / 0.75;
//		// Adjust the size to fit current DPI
		return scaleWithDPI(size);
	}
}