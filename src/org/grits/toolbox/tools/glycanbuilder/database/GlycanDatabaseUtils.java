package org.grits.toolbox.tools.glycanbuilder.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.util.structure.glycan.database.GlycanDatabase;
import org.grits.toolbox.util.structure.glycan.database.GlycanStructure;

public class GlycanDatabaseUtils {

	public static final Logger logger = Logger.getLogger(GlycanDatabaseUtils.class);

	private static String m_strGDB = "";
	private static GlycanDatabase m_gDB = null;

	/**
	 * Loads GlycanDatabase from a file using JAXB unmarshaller.
	 * The file path is located by FileDialog.
	 * @return GlycanDatabase deserialized from the file user specified
	 */
	public static GlycanDatabase loadGlycanDatabase() {
		Shell shell = Display.getCurrent().getActiveShell();
		// Open file dialog
		FileDialog fd = new FileDialog(shell, SWT.OPEN);
		fd.setText("Load glycan database");
		fd.setFilterNames(new String[] {"Glycan database file (*.xml)"});
		fd.setFilterExtensions(new String[] {"*.xml"});
		String strFilepath = fd.open();

		return loadGlycanDatabase(strFilepath);
	}

	/**
	 * Loads GlycanDatabase from the given file path using JAXB unmarshaller.
	 * @param strGDBFilepath String of database file name to open
	 * @return GlycanDatabase deserialized from the file path
	 */
	public static GlycanDatabase loadGlycanDatabase(String strGDBFilepath) {
		if ( strGDBFilepath == null )
			return null;

		if ( m_strGDB.equals(strGDBFilepath) )
			return m_gDB;
		m_strGDB = strGDBFilepath;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GlycanDatabase.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			m_gDB = (GlycanDatabase) jaxbUnmarshaller.unmarshal( new FileInputStream(strGDBFilepath) );
			return m_gDB;
		} catch (JAXBException e) {
			logger.error("An error during deserializing GlycanDatabase object.", e);
		} catch (FileNotFoundException e) {
			logger.error("GlycanDatabase XML file not found.", e);
		}
		return null;
	}

	public static void saveGlycanDatabase(GlycanDatabase database) {
		Shell shell = Display.getCurrent().getActiveShell();
		// Open file dialog
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText("Save glycan database");
		fd.setOverwrite(true);
		fd.setFilterNames(new String[] {"Glycan database file (*.xml)"});
		fd.setFilterExtensions(new String[] {"*.xml"});
		String strFilepath = fd.open();

		saveGlycanDatabase(database, strFilepath);
	}

	public static void saveGlycanDatabase(GlycanDatabase database, String strGDBFilepath) {
		if ( strGDBFilepath == null )
			return;

		try {
			JAXBContext context = JAXBContext.newInstance(GlycanDatabase.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(database, new File(strGDBFilepath));
		} catch (JAXBException e) {
			logger.error("An error during deserializing GlycanDatabase object.", e);
		}
	}

	public static List<String> extractGlycanSequences(GlycanDatabase database) {
		List<String> lSequences = new ArrayList<>();
		for ( GlycanStructure structure : database.getStructures() ) {
			lSequences.add(((GlycanStructure)structure).getGWBSequence());
		}
		return lSequences;
	}

	public static GlycanDatabase createGlycanDatabase(
			List<String> lSequences, String strPrefix, String strSufix, boolean bTrimSameStructure) {
		if ( lSequences == null || lSequences.isEmpty() )
			return null;
		HashSet<String> lStoredSequences = new HashSet<>();
		int i = 1;
		GlycanDatabase database = new GlycanDatabase();
		for ( String sequence : lSequences ) {
			if ( bTrimSameStructure && lStoredSequences.contains(sequence) )
				continue;
			lStoredSequences.add(sequence);
			GlycanStructure structure = new GlycanStructure();
			structure.setGWBSequence(sequence);
			String strID = ((strPrefix == null)? "" : strPrefix ) + i + ((strSufix == null)? "" : strSufix);
			structure.setId(strID);
			database.addStructure(structure);
			i++;
		}
		return database;
	}
}
