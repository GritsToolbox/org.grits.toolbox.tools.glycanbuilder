package org.grits.toolbox.tools.glycanbuilder.core.structure.document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eurocarbdb.application.glycanbuilder.LogUtils;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.TextUtils;
import org.grits.toolbox.tools.glycanbuilder.core.io.ErrorDialogUtils;
import org.grits.toolbox.tools.glycanbuilder.core.io.SequenceFormat;
import org.grits.toolbox.tools.glycanbuilder.core.structure.mass.GlycanMassCalculator;

/**
 * Document object containing a set of glycan structures.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class GlycanDocument {

	public interface Listener {
		public void stateSaved();
		public void stateLoaded();
		public void fileSaved();
		public void fileOpened();
	}

	private List<Listener> m_lListeners;

	public void addListener(Listener l) {
		if ( !this.m_lListeners.contains(l) )
			this.m_lListeners.add(l);
	}

	public void removeListener(Listener l) {
		if ( this.m_lListeners.contains(l) )
			this.m_lListeners.remove(l);
	}

	private StateManager m_stateManager;

	private LinkedList<GlycanInfo> m_lGlycans;

	private MassOptions m_massOptionsDefault;

	private String m_strFilelocation;

	public GlycanDocument() {
		this.m_lListeners = new LinkedList<>();
		this.m_stateManager = new StateManager();
		this.m_massOptionsDefault = new MassOptions();
		initData();
	}

	public void initData() {
		this.m_lGlycans = new LinkedList<>();
	}

	public LinkedList<GlycanInfo> getStructures() {
		return this.m_lGlycans;
	}

	public boolean isEmpty() {
		return this.m_lGlycans.isEmpty();
	}

	public void setDefaultMassOptions(MassOptions massOptions) {
		this.m_massOptionsDefault = massOptions;
	}

	public MassOptions getDefaultMassOptions() {
		return this.m_massOptionsDefault;
	}

	public String getCurrentFilelocation() {
		return this.m_strFilelocation;
	}

	public void reset() {
		this.m_stateManager.reset();
		initData();
		this.m_massOptionsDefault = new MassOptions();
		this.m_strFilelocation = null;
	}

	//----------------
	// Structure operation

	/**
	 * Order the structures by mass-to-charge ratio.
	 */
	public void orderStructures(boolean descending) {
		if (m_lGlycans.size() <= 1)
			return;

		// create map
		TreeMap<Double, List<GlycanInfo>> mapSortedMassToStructures = new TreeMap<Double, List<GlycanInfo>>();
		if (descending)
			mapSortedMassToStructures = new TreeMap<Double, List<GlycanInfo>>(new Comparator<Double>() {
				public int compare(Double o1, Double o2) {
					return -o1.compareTo(o2);
				}
			});

		// sort glycans
		for (GlycanInfo s : m_lGlycans) {
			// Use mass calculator
			GlycanMassCalculator massCalc = new GlycanMassCalculator(s.getGlycan());
			double mz = massCalc.computeMZ();
//			double mz = s.computeMZ();

			List<GlycanInfo> lGlycans = mapSortedMassToStructures.get(mz);
			if (lGlycans == null) {
				lGlycans = new LinkedList<GlycanInfo>();
				mapSortedMassToStructures.put(mz, lGlycans);
			}

			lGlycans.add(s);
		}

		// change document
		this.m_lGlycans.clear();
		for (List<GlycanInfo> lGlycans : mapSortedMassToStructures.values())
			this.m_lGlycans.addAll(lGlycans);

		this.saveState();
	}

	/**
	 * Add structure into the document.
	 */
	public void addStructure(GlycanInfo _structure) {
		this.addStructurePVT(_structure);
		this.saveState();
	}

	/**
	 * Add structures into the document.
	 */
	public void addStructures(List<GlycanInfo> _structures) {
		this.addStructuresPVT(_structures);
		this.saveState();
	}

	/**
	 * Reset the document to contain only this structure.
	 */
	public void setStructure(GlycanInfo _structure) {
		this.m_lGlycans.clear();
		this.addStructurePVT(_structure);
		this.saveState();
	}

	/**
	 * Reset the document to contain only this list of structure.
	 */
	public void setStructures(List<GlycanInfo> _structures) {
		this.m_lGlycans.clear();
		this.addStructuresPVT(_structures);
		this.saveState();
	}

	private void addStructurePVT(GlycanInfo gInfo) {
		if (gInfo != null && gInfo.hasRoot()) {
			this.m_lGlycans.add(gInfo);
		}
	}

	private void addStructuresPVT(List<GlycanInfo> gInfos) {
		if (gInfos != null && gInfos.size() > 0) {
			for (GlycanInfo gInfo : gInfos)
				addStructurePVT(gInfo);
		}
	}

	public void removeStructures(List<GlycanInfo> gInfos) {
		for ( GlycanInfo info : gInfos )
			this.m_lGlycans.remove(info);
		this.saveState();
	}

	public boolean hasDuplicatedIDs() {
		HashSet<String> setIDs = new HashSet<>();
		for ( GlycanInfo info : this.m_lGlycans ) {
			if ( info.getID() == null || info.getID().isEmpty() )
				continue;
			if ( setIDs.contains(info.getID()) )
				return true;
			setIDs.add( info.getID() );
		}
		return false;
	}

	public boolean isDuplicatedID(GlycanInfo infoToCheck) {
		for ( GlycanInfo info : this.m_lGlycans ) {
			if ( info.getID() == null || info.getID().isEmpty() )
				continue;
			if ( info.equals(infoToCheck) )
				continue;
			if ( info.getID().equals(infoToCheck.getID()) )
				return true;
		}
		return false;
	}

	//----------------
	// Serialization

	/**
	 * Create a string representation of the structures contained in the document.
	 */
	public String toString() {
		String sequence = "";
		for (Iterator<GlycanInfo> i = this.m_lGlycans.iterator(); i.hasNext();) {
			sequence += i.next().toString();
			if (i.hasNext())
				sequence += ";";
		}
		return sequence;
	}

	/**
	 * Create a string representation of the structures contained in the document.
	 */
	public String toSequence() {
		String sequence = "";
		for (Iterator<GlycanInfo> i = this.m_lGlycans.iterator(); i.hasNext();) {
			sequence += i.next().getSequence();
			if (i.hasNext())
				sequence += ";";
		}
		return sequence;
	}

	/**
	 * Parse structures from the input string using a specified parser.
	 */
	public List<GlycanInfo> parseString(String str) throws Exception {
		// parse structures
		List<GlycanInfo> parsed = new LinkedList<GlycanInfo>();
		for (String t : TextUtils.tokenize(str, ";")) {
			parsed.add( GlycanInfo.fromString(t) );
		}
		return parsed;
	}

	/**
	 * Parse structures from the input string using a specified parser.
	 */
	public List<GlycanInfo> parseSequence(String str) {
		// parse structures
		List<GlycanInfo> parsed = new LinkedList<GlycanInfo>();
		for (String t : TextUtils.tokenize(str, ";")) {
			try {
				parsed.add( GlycanInfo.fromSequence(t) );
			} catch (Exception e) {
				LogUtils.report(e);
				MultiStatus status = ErrorDialogUtils.createMultiStatus(e.getLocalizedMessage(), e);
				ErrorDialog.openError(null, "Error in parse sequence",
						"The following sequence could not be parsed:\n\n"+t, status);
			}
		}
		return parsed;
	}

	//----------------
	// Undo and redo management

	private boolean isChanged() {
		String strLast = this.m_stateManager.getCurrentState();
		String strCur = this.toString();
		return ( !strLast.equals(strCur) );
	}

	/**
	 * @return true if undo is performed
	 */
	public boolean undo() {
		if ( !this.m_stateManager.canUndo() )
			return false;
		this.m_stateManager.undo();
		if ( !this.isChanged() )
			return false;
		this.loadState();
		return true;
	}

	/**
	 * @return true if redo is performed
	 */
	public boolean redo() {
		if ( !this.m_stateManager.canRedo() )
			return false;
		this.m_stateManager.redo();
		if ( !this.isChanged() )
			return false;
		this.loadState();
		return true;
	}

	public void saveState() {
		if ( !this.isChanged() )
			return;
		String strState = this.toString();
		this.m_stateManager.addState(strState);
		// Performs listeners
		for ( Listener l : this.m_lListeners )
			l.stateSaved();
	}

	public void loadState() {
		if ( !this.isChanged() )
			return;
		String strState = this.m_stateManager.getCurrentState();
		try {
			this.m_lGlycans.clear();
			this.addStructuresPVT(this.parseString(strState));
			// Performs listeners
			for ( Listener l : this.m_lListeners )
				l.stateLoaded();
		} catch (Exception e) {
			LogUtils.report(e);
		}
	}

	//-------------------
	// Save/load structures to/from file

	/**
	 * Save the structures into a file as GWS sequences. If the save is first time, open file dialog to locate the file.
	 * The file will be overwritten from the second save.
	 * @return {@code true} if the save is succeeded
	 */
	public boolean save() {
		String strSequence = this.toSequence();
		if ( this.m_strFilelocation == null ) {
			// Open file dialog
			FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
			fd.setText("Save glycans as GWS sequence format");
			fd.setOverwrite(true);
			SequenceFormat format = SequenceFormat.GWS;
			fd.setFilterNames(new String[] {format.getFilterName()});
			fd.setFilterExtensions(new String[] {format.getFilterExtention()});
			String strSelectedPath = fd.open();
			if ( strSelectedPath == null )
				return false;
			this.m_strFilelocation = strSelectedPath;
		}
		try {
			FileOutputStream fos = new FileOutputStream(this.m_strFilelocation);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(strSequence,0,strSequence.length());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		for ( Listener l : this.m_lListeners )
			l.fileSaved();
		return true;
	}

	public boolean saveAs() {
		this.m_strFilelocation = null;
		return this.save();
	}

	/**
	 * Load structures from the saved file. If the save is not performed before, open file dialog to open a file containing structures.
	 * @param strFilelocation String of file location path
	 * @param toInsert A flag to insert structures from file
	 * @return {@code true} if the load is succeeded
	 */
	public boolean load(String strFilelocation, boolean toInsert) {
		if ( strFilelocation == null ) {
			// Open file dialog
			FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
			fd.setText("Load glycans from file as GWS sequence format");
			SequenceFormat format = SequenceFormat.GWS;
			fd.setFilterNames(new String[] {format.getFilterName()});
			fd.setFilterExtensions(new String[] {format.getFilterExtention()});
			String strSelectedPath = fd.open();
			if ( strSelectedPath == null )
				return false;
			this.m_strFilelocation = strSelectedPath;
		}
		String strSequence = "";
		try {
			FileInputStream fos = new FileInputStream(this.m_strFilelocation);
			BufferedReader br = new BufferedReader(new InputStreamReader(fos));
			String line;
			while ( (line = br.readLine()) != null )
				strSequence += line.trim();
			br.close();
		} catch (IOException e) {
			LogUtils.report(e);
			MultiStatus status = ErrorDialogUtils.createMultiStatus(e.getLocalizedMessage(), e);
			ErrorDialog.openError(null, "Error in file read", "The file cannot be read.", status);
			return false;
		}

		if ( !toInsert )
			this.m_lGlycans.clear();
		this.addStructuresPVT(this.parseSequence(strSequence));
		return true;
	}

	/**
	 * Open structures from file
	 * @param strFilelocation String of file location path
	 * @return {@code true} if the open is succeeded
	 */
	public boolean open(String strFilelocation) {
		String strOldFilelocation = this.m_strFilelocation;
		if ( !this.load(strFilelocation, false) ) {
			// Reset file location to old one
			this.m_strFilelocation = strOldFilelocation;
			return false;
		}
		this.saveState();
		for ( Listener l : this.m_lListeners )
			l.fileOpened();
		return true;
	}

	/**
	 * Insert structures from file to current canvas
	 * @param strFilelocation String of file location path
	 * @return {@code true} if the open is succeeded
	 */
	public boolean insert(String strFilelocaiton) {
		String strOldFilelocation = this.m_strFilelocation;
		if ( !this.load(strFilelocaiton, true) ) {
			// Reset file location to old one
			this.m_strFilelocation = strOldFilelocation;
			return false;
		}
		// Reset file location to old one
		this.m_strFilelocation = strOldFilelocation;
		this.saveState();
		return true;
	}

}
