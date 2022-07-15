package org.grits.toolbox.tools.glycanbuilder.widgets.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.file.FileConstants;

public class GeneralIconProvider {

	private static Map<String, Image> mapFilenameToIconImage = new HashMap<>();

	private static Image loadIconImage(String filename) {
		if ( filename == null )
			return null;
		if ( !mapFilenameToIconImage.containsKey(filename)
			|| mapFilenameToIconImage.get(filename).isDisposed() ) {
			InputStream stream = GeneralIconProvider.class.getResourceAsStream(filename);
			if ( stream == null )
				return null;
			Image img = new Image(Display.getCurrent(), stream);
			mapFilenameToIconImage.put(filename, img);
		}
		return mapFilenameToIconImage.get(filename);
	}

	// Common icons
	public static Image getCutIcon() {
		return loadIconImage(FileConstants.ICON_COMMON_CUT);
	}

	public static Image getCopyIcon() {
		return loadIconImage(FileConstants.ICON_COMMON_COPY);
	}

	public static Image getPasteIcon() {
		return loadIconImage(FileConstants.ICON_COMMON_PASTE);
	}

	public static Image getDeleteIcon() {
		return loadIconImage(FileConstants.ICON_COMMON_DELETE);
	}

	// Edit glycan icons
	public static Image getBracketIcon() {
		return loadIconImage(FileConstants.ICON_BRACKET);
	}

	public static Image getRepeatIcon() {
		return loadIconImage(FileConstants.ICON_REPEAT);
	}

	public static Image getChangeDisplayIcon() {
		return loadIconImage(FileConstants.ICON_CHANGE_DISPLAY);
	}

	public static Image getResiduePropertiesIcon() {
		return loadIconImage(FileConstants.ICON_RESIDUE_PROPERTIES);
	}

	public static Image getOrientationIcon(int orientation) {
		if ( orientation == GraphicOptionsSWT.RL )
			return loadIconImage(FileConstants.ICON_ORIENTATION_RL);
		if ( orientation == GraphicOptionsSWT.BT )
			return loadIconImage(FileConstants.ICON_ORIENTATION_BT);
		if ( orientation == GraphicOptionsSWT.LR )
			return loadIconImage(FileConstants.ICON_ORIENTATION_LR);
		if ( orientation == GraphicOptionsSWT.TB )
			return loadIconImage(FileConstants.ICON_ORIENTATION_TB);
		return null;
	}
}
