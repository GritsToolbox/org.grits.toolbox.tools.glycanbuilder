package org.grits.toolbox.tools.glycanbuilder.widgets.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eurocarbdb.application.glycanbuilder.CoreType;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.eurocarbdb.application.glycanbuilder.TerminalType;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.GlycanRendererSWT;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.ResidueRendererSWT;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.CoreDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.ResidueDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.TerminalDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;

public class GlycanIconProvider {

	/**
	 * Listener for events raised when the icon images are changed.
	 */
	public interface IconUpdateListener {
		/**
		 * Called when residue icon images have updated.
		 */
		public void residueIconUpdated();
		/**
		 * Called when glycan icon images have updated.
		 */
		public void glycanIconUpdated();
	}

	private static final int ICON_SIZE_DEFAULT = 24;

	private static int iIconSize = ICON_SIZE_DEFAULT;
	private static int iIconSizeOld = ICON_SIZE_DEFAULT;

	private static BuilderWorkspaceSWT bws;

	static {
		bws = new BuilderWorkspaceSWT( Display.getDefault() );
		bws.setNotation(GraphicOptionsSWT.NOTATION_SNFG);
		// Reset custom settings to compact
		bws.getGraphicOptions().initCustomDisplay(GraphicOptionsSWT.DISPLAY_COMPACT);
		// Reduce the margins
		bws.getGraphicOptions().MARGIN_TOP_CUSTOM = 12;
		bws.getGraphicOptions().MARGIN_BOTTOM_CUSTOM = 12;
		bws.getGraphicOptions().MARGIN_LEFT_CUSTOM = 12;
		bws.getGraphicOptions().MARGIN_RIGHT_CUSTOM = 12;
		// Set display custom
		bws.setDisplay(GraphicOptionsSWT.DISPLAY_CUSTOM);
		updateImages();
	}

	// For residue type icons
	private static String strOldNotation;
	private static Map<ResidueType, Image> mapResToIcon;

	private static Map<CoreType, Image> mapCoreToIcon;
	private static Map<TerminalType, Image> mapTerminalToIcon;

	private static List<GlycanIconProvider.IconUpdateListener> lListeners;

	public static void addIconUpdateListener(GlycanIconProvider.IconUpdateListener l) {
		if ( l == null )
			return;
		if ( lListeners == null )
			lListeners = new LinkedList<>();
		lListeners.add(l);
	}

	public static void removeIconUpdateListener(GlycanIconProvider.IconUpdateListener l) {
		if ( lListeners == null )
			return;
		if ( l == null )
			return;
		lListeners.remove(l);
	}

	public static void setIconSize(int size) {
		iIconSize = size;
		updateImages();
	}

	public static void setNotation(String notation) {
		if ( notation != null )
			bws.setNotation(notation);
		updateImages();
	}

	public static Image getResidueIcon(ResidueType resType) {
		if ( mapResToIcon == null )
			return null;
		return mapResToIcon.get(resType);
	}

	public static Image getGlycanCoreIcon(CoreType coreType) {
		if ( mapCoreToIcon == null )
			return null;
		return mapCoreToIcon.get(coreType);
	}

	public static Image getGlycanTerminalIcon(TerminalType termType) {
		if ( mapTerminalToIcon == null )
			return null;
		return mapTerminalToIcon.get(termType);
	}

	private static void updateImages() {
		boolean updateRes = true;
		boolean updateGlycan = true;
		if (strOldNotation != null && strOldNotation.equals(bws.getGraphicOptions().NOTATION) ) {
			if ( iIconSize == iIconSizeOld )
				updateRes = false;
			updateGlycan = false;
		}
		strOldNotation = bws.getGraphicOptions().NOTATION;
		iIconSizeOld = iIconSize;

		if ( updateRes )
			updateResidueIcons();
		if ( updateGlycan )
			updateGlycanIcons();
	}

	private static void updateResidueIcons() {
		// Dispose old icons
		if ( mapResToIcon != null )
			for ( Image img : mapResToIcon.values() )
				img.dispose();
		mapResToIcon = new HashMap<>();
		ResidueRendererSWT rr = bws.getGlycanRenderer().getResidueRenderer();
		for ( ResidueType resType : ResidueDictionary.allResidues() )
			mapResToIcon.put(resType, rr.getImage(resType, iIconSize) );
		// Perform listeners
		if ( lListeners != null )
			for ( GlycanIconProvider.IconUpdateListener l : lListeners )
				l.residueIconUpdated();
	}

	private static void updateGlycanIcons() {
		// Update icons
		updateGlycanCoreIcons();
		updateGlycanTerminalIcons();
		// Perform listeners
		if ( lListeners != null )
			for ( GlycanIconProvider.IconUpdateListener l : lListeners )
				l.glycanIconUpdated();
	}

	private static void updateGlycanCoreIcons() {
		// Dispose old icons
		if ( mapCoreToIcon != null )
			for ( Image img : mapCoreToIcon.values() )
				img.dispose();
		mapCoreToIcon = new HashMap<>();
		GlycanRendererSWT gr = bws.getGlycanRenderer();
		for ( CoreType ct : CoreDictionary.getCores()) {
			Glycan g = Glycan.fromString(ct.getStructure());
			mapCoreToIcon.put(ct, gr.getImage(g, true, false, false, .25));
		}
	}

	private static void updateGlycanTerminalIcons() {
		if ( mapTerminalToIcon != null )
			for ( Image img : mapTerminalToIcon.values() )
				img.dispose();
		mapTerminalToIcon = new HashMap<>();
		GlycanRendererSWT gr = bws.getGlycanRenderer();
		for ( TerminalType tt : TerminalDictionary.getTerminals()) {
			Glycan g = Glycan.fromString(tt.getStructure());
			mapTerminalToIcon.put(tt, gr.getImage(g, true, false, false, .25));
		}
	}
}
