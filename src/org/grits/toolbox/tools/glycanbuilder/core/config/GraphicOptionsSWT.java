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
*   Last commit: $Rev: 1930 $ by $Author: david@nixbioinf.org $ on $Date:: 2010-07-29 #$  
*/

package org.grits.toolbox.tools.glycanbuilder.core.config;

import org.eurocarbdb.application.glycanbuilder.GraphicOptions;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.utils.TextShapeUtils;

/**
 * An extended GraphicOptions class for adjusting the node and font size
 * for SWT.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 * @see GraphicOptions
 */
public class GraphicOptionsSWT extends GraphicOptions {

	// visualization options
	/**
	 * Cartoon notation used to display the structures, influences the graphic style
	 * of residues and linkages
	 */
	/** SNFG */
	public static final String NOTATION_SNFG = "snfg";

	/**
	 * Available notations.
	 */
	public static final String[] NOTATIONS = new String[] {
			NOTATION_SNFG,
			NOTATION_CFG,
			NOTATION_CFGBW,
			NOTATION_CFGLINK,
			NOTATION_UOXF,
			NOTATION_UOXFCOL,
			NOTATION_TEXT
		};

	/**
	 * Available display modes.
	 */
	public static final String[] DISPLAYS = new String[] {
			DISPLAY_NORMALINFO,
			DISPLAY_NORMAL,
			DISPLAY_COMPACT,
			DISPLAY_CUSTOM
		};

	/**
	 * A flag for considering structure and font size adjustment
	 */
	private boolean IS_ADJUSTED = true;

	public boolean SHOW_ID = false;

	/**
	 * Set the display variable and init all the graphic options accordingly.
	 */
	public void setDisplay(String display) {
		super.setDisplay(display);

		// Change minimum of node font size
		int iNodeFontSize = 14;
		if ( display.equals(DISPLAY_CUSTOM) ) {
			iNodeFontSize = NODE_FONT_SIZE_CUSTOM;
		}
		NODE_FONT_SIZE = Math.max(6, // Change from 10
				(int) (iNodeFontSize * SCALE));

		if ( IS_ADJUSTED )
			doAdjust();
	}

	public void doAdjust() {
		// Scaling factor applied to the node spaces and sizes except for font size.
		// It must be adjusted based on current DPI value.
		double SCALE_FOR_SPACE = TextShapeUtils.scaleWithDPI(1.0);
		// Scaling factor applied to font size.
		// Handling of font sizes is different from AWT to SWT. AWT font is smaller than SWT font.
		// To use the original (AWT) font size, the font size has to be scaled to 75%.
		double SCALE_FOR_FONT = 0.75;

		MARGIN_LEFT = (int) (MARGIN_LEFT * SCALE_FOR_SPACE);
		MARGIN_TOP = (int) (MARGIN_TOP * SCALE_FOR_SPACE);
		MARGIN_RIGHT = (int) (MARGIN_RIGHT * SCALE_FOR_SPACE);
		MARGIN_BOTTOM = (int) (MARGIN_BOTTOM * SCALE_FOR_SPACE);

		NODE_SIZE = (int) (NODE_SIZE * SCALE_FOR_SPACE);

		NODE_FONT_SIZE = (int) (NODE_FONT_SIZE * SCALE_FOR_FONT);

		COMPOSITION_FONT_SIZE = (int) (COMPOSITION_FONT_SIZE * SCALE_FOR_FONT);

		LINKAGE_INFO_SIZE = (int) (LINKAGE_INFO_SIZE * SCALE_FOR_FONT);

		NODE_SUB_SPACE = (int) (NODE_SUB_SPACE * SCALE_FOR_SPACE);

		STRUCTURES_SPACE = (int) (STRUCTURES_SPACE * SCALE_FOR_SPACE);

		MASS_TEXT_SPACE = (int) (MASS_TEXT_SPACE * SCALE_FOR_SPACE);
		MASS_TEXT_SIZE = (int) (MASS_TEXT_SIZE * SCALE_FOR_FONT);

		NODE_SPACE = (int) (NODE_SPACE*SCALE_FOR_SPACE);
	}

	/**
	 * Set consider adjusting structure size to current DPI and font size from AWT to SWT
	 * @param adjust boolean value for considering the adjustment
	 */
	public void adjustSize(boolean adjust) {
		IS_ADJUSTED = adjust;
	}

	@Override
	public void copy(GraphicOptions other) {
		super.copy(other);
		if ( other instanceof GraphicOptionsSWT )
			this.SHOW_ID = ((GraphicOptionsSWT)other).SHOW_ID;
	}

	/**
	 * Create a copy of the object.
	 */
	@Override
	public GraphicOptionsSWT clone() {
		GraphicOptionsSWT ret = new GraphicOptionsSWT();
		ret.copy(this);
		return ret;
	}

	/**
	 * Create a copy of the object.
	 */
	public GraphicOptions cloneSuper() {
		GraphicOptions ret = new GraphicOptions();
		ret.copy(this);
		// Resizes using setDisplay()
		ret.setDisplay(this.DISPLAY);
		return ret;
	}

}
