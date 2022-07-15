package org.grits.toolbox.tools.glycanbuilder.core.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.LogUtils;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.TextUtils;
import org.glycoinfo.GlycanFormatconverter.io.GlycoCT.WURCSToGlycoCT;
import org.glycoinfo.WURCSFramework.io.GlycoCT.WURCSExporterGlycoCT;
import org.grits.toolbox.tools.glycanbuilder.core.io.parser.GWSParser;
import org.grits.toolbox.tools.glycanbuilder.core.io.parser.GlycanParser;
import org.grits.toolbox.tools.glycanbuilder.core.io.parser.GlycoCTCondensedParser;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.awt.GlycanRendererAWT;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.awt.SVGUtils;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;

/**
 * Utility class for the save/load of glycan structure with a format to/from file.
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class GlycanIOUtils {

	public static String convertGlycoCTToWURCS(String strGlycoCT) {
		WURCSExporterGlycoCT exporter = new WURCSExporterGlycoCT();
		try {
			exporter.start(strGlycoCT);
			return exporter.getWURCS();
		} catch (Exception e) {
			LogUtils.report(e);
			MultiStatus status = ErrorDialogUtils.createMultiStatus(e.getLocalizedMessage(), e);
			ErrorDialog.openError(null, "Error", "Error in converting GlycoCT to WURCS:\n"+exporter.getValidationErrorLog(), status);
		}
		return null;
	}

	public static List<String> getGlycanSequence(List<Glycan> lGlycans, SequenceFormat format) {
		if ( lGlycans == null || lGlycans.isEmpty() )
			return null;

		if ( format == null )
			format = SequenceFormat.GWS;

		List<String> lSequences = new ArrayList<>();
		try {
			GlycanParser parser = new GWSParser();
			if ( format == SequenceFormat.GCT_C || format == SequenceFormat.WURCS )
				parser = new GlycoCTCondensedParser(false);
			for ( Glycan glycan : lGlycans ) {
				String sequence = parser.writeGlycan(glycan);
				if ( sequence.replaceAll("\n", "").isEmpty() )
					throw new Exception("Error in glycan sequence conversion");
				if ( format == SequenceFormat.WURCS ) {
					sequence = convertGlycoCTToWURCS(sequence);
					if ( sequence == null )
						return null;
				}
				lSequences.add(sequence);
			}
		} catch (Exception e) {
			LogUtils.report(e);
			return null;
		}

		return lSequences;
	}

	public static List<String> convertGlycanSecuenceWithCheck(List<Glycan> lGlycans, SequenceFormat format, boolean bMultiSave) {
		Shell shell = Display.getCurrent().getActiveShell();

		MessageBox box;

//		if ( !GlycanParserFactory.isSequenceFormat(format.getIdentifier()) ) {
//			if ( format != SequenceFormat.WURCS ) {
//				box = new MessageBox(shell, SWT.OK|SWT.ICON_ERROR);
//				box.setMessage("Cannot handle the format now.");
//				box.open();
//				return null;
//			}
//		}
//
		if ( lGlycans == null || lGlycans.isEmpty() ) {
			box = new MessageBox(shell, SWT.OK|SWT.ICON_ERROR);
			box.setMessage("No glycan in the canvas.");
			box.open();
			return null;
		}
		List<String> lSequences = getGlycanSequence(lGlycans, format);

		if ( lSequences == null || lSequences.isEmpty() ) {
			box = new MessageBox(shell, SWT.OK|SWT.ICON_ERROR);
			box.setMessage("The glycan could not be imported.");
			box.open();
			return null;
		}

		// Multiple structures confirmation
		if ( !bMultiSave && lSequences.size() > 1 ) {
			if ( !format.supportMultipleStructures() ) {
				box = new MessageBox(shell, SWT.YES|SWT.NO|SWT.ICON_WARNING);
				box.setText("Can not support multiple structues");
				box.setMessage("The selected format does not support multiple structures.\n"
					+"The structures will be exported to multiple files separately. Continue?");
				if ( box.open() == SWT.NO )
					return null;
			} else {
				box = new MessageBox(shell, SWT.YES|SWT.NO);
				box.setText("Can support multiple structues");
				box.setMessage("The selected format support multiple structures.\n"
						+"Do you want to export these structures into one file?");
				if ( box.open() == SWT.YES )
					bMultiSave = true;
			}
		}
		if ( bMultiSave && lSequences.size() > 1 && format.supportMultipleStructures() ) {
			// Merge sequences
			String strAllSeq = "";
			for ( String seq : lSequences ) {
				if ( !strAllSeq.isEmpty() )
					strAllSeq += format.getDelimiter();
				strAllSeq += seq;
			}
			lSequences.clear();
			lSequences.add(strAllSeq);
		}

		return lSequences;
	}

	/**
	 * Save the given glycan sequences into file(s) with specified format using FileDialog.
	 * @param lGlycans List of Glycans to save
	 * @param format SequenceFormat to be used for saving (all available sequence format will be shown in filter names if format is {@code null})
	 * @param bMultiSave a flag for saving multiple sequences into one file (only for the sequence formats supporting multiple structures)
	 */
	public static void saveGlycanSequence(List<Glycan> lGlycans, SequenceFormat format, boolean bMultiSave) {
		if ( lGlycans == null || lGlycans.isEmpty() )
			return;

		Shell shell = Display.getCurrent().getActiveShell();

		// Open file dialog
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText("Save glycans as sequence");
		fd.setOverwrite(true);
		if ( format == null ) {
			fd.setFilterNames(SequenceFormat.getFilterNames());
			fd.setFilterExtensions(SequenceFormat.getFilterExtentions());
		} else {
			fd.setFilterNames(new String[] {format.getFilterName()});
			fd.setFilterExtensions(new String[] {format.getFilterExtention()});
		}
		String strSelectedPath = fd.open();
		if ( strSelectedPath == null )
			return;

		if ( format == null )
			format = SequenceFormat.values()[fd.getFilterIndex()];

		// Serialize structures
		List<String> lSequences = convertGlycanSecuenceWithCheck(lGlycans, format, bMultiSave);
		if ( lSequences == null )
			return;


		// Write to file
		try {
			List<String> lFilepathes = new ArrayList<>();
			int nSufix = 1;
			for ( String seq : lSequences ) {
				String strFilepath = strSelectedPath;
				if ( lSequences.size() != 1 ) {
					int pos = strFilepath.lastIndexOf('.');
					strFilepath = strFilepath.substring(0, pos) + ("-"+nSufix++) + strFilepath.substring(pos);
				}
				FileOutputStream fos = new FileOutputStream(strFilepath);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

				bw.write(seq,0,seq.length());
				bw.newLine();
				bw.close();

				lFilepathes.add(strFilepath);
			}
			if ( lFilepathes.size() > 1 ) {
				MessageBox box = new MessageBox(shell, SWT.OK|SWT.ICON_INFORMATION);
				String message = "The sequences are saved into following files:\n";
				for ( String filepath : lFilepathes )
					message += filepath+"\n";
				box.setText("Sequences are saved successfully");
				box.setMessage(message);
				box.open();
			}
		} catch (Exception e) {
			LogUtils.report(e);
		}
	}

	public static String convertWURCSToGlycoCT(String strWURCS) throws Exception {
		WURCSToGlycoCT exporter = new WURCSToGlycoCT();
		exporter.start(strWURCS);
		if ( !exporter.getErrorMessages().isEmpty() )
			throw new Exception(exporter.getErrorMessages());
		return exporter.getGlycoCT();
	}

	public static List<Glycan> getGlycansFromSequence(String strSequence, SequenceFormat format, MassOptions massOptions ) {
		List<String> lSequences = new ArrayList<>();
		if ( format == SequenceFormat.GWS && strSequence.contains(";") ) {
			for (String t : TextUtils.tokenize(strSequence, ";"))
				if ( !t.isEmpty() )
					lSequences.add(t);
		} else if ( format.supportMultipleStructures() && strSequence.contains(format.getDelimiter()) ){
			for (String t : TextUtils.tokenize(strSequence, format.getDelimiter()))
				if ( !t.isEmpty() )
					lSequences.add(t);
		} else {
			lSequences.add(strSequence);
		}

		return getGlycansFromSequences(lSequences, format, massOptions);
	}

	public static List<Glycan> getGlycansFromSequences(List<String> lSequences, SequenceFormat format, MassOptions massOptions) {
		if ( lSequences == null || lSequences.isEmpty() )
			return null;

		if ( format == null )
			format = SequenceFormat.GWS;

		List<Glycan> lGlycans = new ArrayList<>();
		try {
			GlycanParser parser = new GWSParser();
			if ( format == SequenceFormat.GCT_C || format == SequenceFormat.WURCS )
				parser = new GlycoCTCondensedParser(false);
			for ( String sequence : lSequences ) {
				if ( format == SequenceFormat.WURCS )
					sequence = convertWURCSToGlycoCT(sequence);
				Glycan glycan = parser.readGlycan(sequence, massOptions);
				lGlycans.add(glycan);
			}
			return lGlycans;
		} catch (Exception e) {
			LogUtils.report(e);
			MultiStatus status = ErrorDialogUtils.createMultiStatus(e.getLocalizedMessage(), e);
			ErrorDialog.openError(null, "Error in import sequence", "The glycans cannot be converted by the selected format.", status);
		}

		return null;
	}

	public static List<Glycan> loadGlycans(SequenceFormat format, MassOptions massOptions) {
		Shell shell = Display.getCurrent().getActiveShell();

		// Open file dialog
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Load glycans as sequence format");
		if ( format == null ) {
			fd.setFilterNames(SequenceFormat.getFilterNames());
			fd.setFilterExtensions(SequenceFormat.getFilterExtentions());
		} else {
			fd.setFilterNames(new String[] {format.getFilterName(), "All files"});
			fd.setFilterExtensions(new String[] {format.getFilterExtention(), "*.*"});
		}
		String strSelectedPath = fd.open();
		if ( strSelectedPath == null )
			return null;

		if ( format == null )
			format = SequenceFormat.values()[fd.getFilterIndex()];

		// Read sequences from file
		String strSequence = "";
		try {
			FileInputStream fos = new FileInputStream(strSelectedPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fos));
			String line;
			while ( (line = br.readLine()) != null ) {
				strSequence += line.trim();
				if ( format != SequenceFormat.GWS )
					strSequence += System.lineSeparator();
			}
			br.close();
		} catch (IOException e) {
			LogUtils.report(e);
			return null;
		}
		if ( strSequence.isEmpty() ) {
			MessageBox box = new MessageBox(shell, SWT.OK|SWT.ICON_WARNING);
			box.setText("Error in import sequence");
			box.setMessage("There is no sequence in the file.");
			box.open();
			return null;
		}

		// Convert
		List<Glycan> lGlycans = getGlycansFromSequence(strSequence, format, massOptions);
		if ( lGlycans == null || lGlycans.isEmpty() )
			return null;

		return lGlycans;
	}

	/**
	 * Save the given glycans as image using FileDialog.
	 * User can select image format if specifying {@code format} as {@code null}.
	 * @param lGlycans List of Glycans to save
	 * @param bws BuilderWorkspaceSWT for rendering glycan image
	 * @param format ImageFormat to be used for the image format
	 */
	public static void saveGlycanImage(List<Glycan> lGlycans, BuilderWorkspaceSWT bws, ImageFormat format) {
		if ( lGlycans == null || lGlycans.isEmpty() )
			return;

		Shell shell = Display.getCurrent().getActiveShell();

		// Open file dialog
		FileDialog fd = new FileDialog(shell, SWT.SAVE );
		fd.setText("Save image");
		fd.setOverwrite(true);
		if ( format == null ) {
			fd.setFilterNames(ImageFormat.getFilterNames());
			fd.setFilterExtensions(ImageFormat.getFilterExtensions());
		} else {
			fd.setFilterNames(new String[] {format.getFilterName()});
			fd.setFilterExtensions(new String[] {format.getFilterExtention()});
		}
		String strSelectedPath = fd.open();
		if ( strSelectedPath == null )
			return;

		if ( format == null )
			format = ImageFormat.values()[fd.getFilterIndex()];
		// Use SWT image
		// For .bmp, .png and .jpg
		if ( format.isAvailableInSWT() ) {
			boolean opaque = true;
			Color colorOld = bws.getGlycanRenderer().getBackgroundColor();
			bws.getGlycanRenderer().setBackgroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			if ( format == ImageFormat.PNG )
				opaque = false;
			Image img = bws.getGlycanRenderer().getImage(
					lGlycans,
					opaque,
					bws.getGraphicOptions().SHOW_MASSES,
					bws.getGraphicOptions().SHOW_REDEND
				);
			bws.getGlycanRenderer().setBackgroundColor(colorOld);
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] {img.getImageData()};
			loader.save(strSelectedPath, format.getSWTImageConstant());
			return;
		}

		// Use AWT image
		// For .svg, .pdf, .ps and .eps
		GlycanRendererAWT gr = bws.getGlycanRendererAWT();
		SVGUtils.export(gr, strSelectedPath, lGlycans,
				gr.getGraphicOptions().SHOW_MASSES,
				gr.getGraphicOptions().SHOW_REDEND,
				format.getIdentifier());
	}

}
