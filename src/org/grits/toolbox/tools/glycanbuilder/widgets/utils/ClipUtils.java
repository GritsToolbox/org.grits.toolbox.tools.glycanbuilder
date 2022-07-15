package org.grits.toolbox.tools.glycanbuilder.widgets.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.grits.toolbox.tools.glycanbuilder.core.io.parser.GWSParser;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;

public class ClipUtils {

	//---------------------
	// Common methods

	public static void copyImageToClipboad(Image img) {
		if ( img == null || img.isDisposed() )
			return;
		Clipboard cb = new Clipboard(Display.getCurrent());
		cb.setContents(
				new Object[] {img.getImageData()},
				new Transfer[] {ImageTransfer.getInstance()}
			);
		cb.dispose();
	}

	public static ImageData getImageDataFromClipboard() {
		Clipboard cb = new Clipboard(Display.getCurrent());
		ImageData data = (ImageData)cb.getContents(ImageTransfer.getInstance());
		cb.dispose();
		return data;
	}

	public static Image getImageFromClipboard() {
		ImageData data = getImageDataFromClipboard();
		Image img = new Image(Display.getCurrent(), data);
		return img;
	}

	public static void copyTextToClipboad(String txt) {
		if ( txt == null || txt.isEmpty() )
			return;
		// Reset line breaks
		if ( txt.contains("\r\n") )
			txt = txt.replace("\r\n", "\n");
		String txtNew = "";
		for ( String line : txt.split("\n") ) {
			if ( !txtNew.isEmpty() )
				txtNew += System.lineSeparator();
			txtNew += line;
		}
		txt = txtNew;
		Clipboard cb = new Clipboard(Display.getCurrent());
		cb.setContents(new Object[] {txt}, new Transfer[] {TextTransfer.getInstance()});
		cb.dispose();
	}

	public static String getTextFromClipboad() {
		Clipboard cb = new Clipboard(Display.getCurrent());
		String txt = (String)cb.getContents(TextTransfer.getInstance());
		cb.dispose();
		return txt;
	}

	//---------------------
	// Methods for glycans

	private static String strGWS = null;

	/**
	 * Copy the given glycans to clipboard. GWS sequence is saved to member variables of this.
	 * Image of the glycan(s) is copied to clipboard so that user can paste the image through the OS system.
	 * @param lGlycans List of Glycans to copy
	 * @param bws BuilderWorkspaceSWT to create image
	 */
	public static void copyToClipboard(List<Glycan> lGlycans, BuilderWorkspaceSWT bws) {
		// Export glycans as GWS text
		strGWS = "";
		for ( Glycan g : lGlycans ) {
			if ( !strGWS.isEmpty() )
				strGWS += ";";
			strGWS += GWSParser.toString(g);
		}

		// Clone glycans with reducing end
		List<Glycan> lClones = new ArrayList<>();
		for ( Glycan g : lGlycans )
			lClones.add( g.clone(true) );
		// Create glycan image
		Color bgColorOld = bws.getGlycanRenderer().getBackgroundColor();
		bws.getGlycanRenderer().setBackgroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		Image img = bws.getGlycanRenderer().getImage(
				lClones,
				true,
				bws.getGraphicOptions().SHOW_MASSES,
				bws.getGraphicOptions().SHOW_REDEND
			);
		bws.getGlycanRenderer().setBackgroundColor(bgColorOld);
		// Set glycan image to clipboad
		copyImageToClipboad(img);
		img.dispose();
	}

	/**
	 * Copy saved GWS sequence(s) to Clipboard.
	 * If you have copied image to Clipboard it will be removed after this.
	 * @return {@code true} if the copy is succeeded
	 */
	public static boolean copyGWSToClipboard() {
		if ( strGWS == null || strGWS.isEmpty() )
			return false;
		copyTextToClipboad(strGWS);
		return true;
	}

	public static String getGWSFromClipboard() {
//		Clipboard cb = new Clipboard(Display.getCurrent());
//		return (String)cb.getContents(TextTransfer.getInstance());
		return strGWS;
	}

	public static boolean canGetGlycanFromClipboard() {
		String strGWSs = getGWSFromClipboard();
		if ( strGWSs == null )
			return false;
		for ( String strGWS : strGWSs.split(";") ) {
			if ( strGWS == null || strGWS.isEmpty() )
				continue;
			try {
				GWSParser.fromString(strGWS, new MassOptions());
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public static List<Glycan> getGlycansFromClipboard(MassOptions defaultMassOptions) {
		MassOptions massOptions = (defaultMassOptions == null)? new MassOptions() : defaultMassOptions;
		List<Glycan> lGlycans = new ArrayList<>();
		String strGWSs = getGWSFromClipboard();
		if ( strGWSs == null )
			return lGlycans;
		for ( String strGWS : strGWSs.split(";") ) {
			if ( strGWS == null || strGWS.isEmpty() )
				continue;
			try {
				Glycan g = GWSParser.fromString(strGWS, massOptions);
				lGlycans.add(g);
			} catch (Exception e) {
			}
		}
		return lGlycans;
	}
}
