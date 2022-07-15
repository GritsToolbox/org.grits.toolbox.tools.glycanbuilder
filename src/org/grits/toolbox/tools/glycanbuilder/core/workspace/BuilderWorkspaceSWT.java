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

package org.grits.toolbox.tools.glycanbuilder.core.workspace;

import org.eclipse.swt.graphics.Device;
import org.eurocarbdb.application.glycanbuilder.CompositionOptions;
import org.eurocarbdb.application.glycanbuilder.Configuration;
import org.eurocarbdb.application.glycanbuilder.FileHistory;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.ResidueHistory;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.file.FileConstants;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.GlycanRendererSWT;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.awt.GlycanRendererAWT;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.awt.SVGUtils;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.style.LinkageStyleDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.style.ResiduePlacementDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.style.ResidueStyleDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.CoreDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.CrossRingFragmentDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.ResidueDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.TerminalDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanDocument;

/**
 * A BuilderWorkspace is a container for all the documents, dictionaries and
 * options that are used in the GlycanBuilder application. During initialization
 * the workspace read all the configuration from a file (if provided) and
 * initialize all static dictionaries and options. A workspace must be
 * initialized before any other action using the GlycanBuilder classes.
 * 
 * @author Alessio Ceroni (a.ceroni@imperial.ac.uk)
 * @author Masaaki Matsubara (matsubara@uga.edu)
 */

public class BuilderWorkspaceSWT {

	protected static boolean loaded = false;

	protected boolean autosave;

	private Device device;

	// style
	protected ResiduePlacementDictionary theResiduePlacementDictionary;
	protected ResidueStyleDictionary theResidueStyleDictionary;
	protected LinkageStyleDictionary theLinkageStyleDictionary;

	// configuration
	protected Configuration theConfiguration;
	protected FileHistory theFileHistory;
	protected ResidueHistory theResidueHistory;
	protected MassOptions theMassOptions;
	protected GraphicOptionsSWT theGraphicOptions;
	protected CompositionOptions theCompositionOptions;

	// Doc
	protected GlycanDocument theGlycanDoc;

	// renderers
	protected GlycanRendererSWT theGlycanRenderer;

	/**
	 * Construct with empty. Initialize the dictionaries from the default files
	 * and set all the options to their default values.
	 */
	public BuilderWorkspaceSWT(Device device) {
		this(device, null, false);
	}

	/**
	 * Load the configuration from a file. Initialize the dictionaries from the
	 * default files and set all the options to the values stored in the
	 * configuration.
	 * 
	 * @param config_file
	 *            the configuration file
	 * @param create
	 *            if <code>true</code> create a configuration file from the default
	 *            value in case the file does not exists
	 */
	public BuilderWorkspaceSWT(Device device, String config_file, boolean create) {
		this.device = device;
		init(config_file, create, false);
	}

	public Device getParentDevice() {
		return this.device;
	}

	protected void createConfiguration() {
		theConfiguration = new Configuration();
		theFileHistory = new FileHistory();
		theResidueHistory = new ResidueHistory();
		theMassOptions = new MassOptions();
		theGraphicOptions = new GraphicOptionsSWT();
		theCompositionOptions = new CompositionOptions();
	}

	/**
	 * Load the configuration from a file. Initialize the dictionaries from the
	 * default files and set all the options to the values stored in the
	 * configuration.
	 * 
	 * @param config_file
	 *            the configuration file
	 * @param create
	 *            if <code>true</code> create a configuration file from the default
	 *            value in case the file does not exists
	 * @param keep_configuration
	 *            if <code>true</code> the configuration is not reloaded
	 */
	public void init(String config_file, boolean create, boolean keep_configuration) {
		if (!keep_configuration || theConfiguration == null) {

			// create configuration instances
			createConfiguration();

			// initialize configuration
			if (config_file != null && theConfiguration.open(config_file))
				retrieveFromConfiguration();
			else {
				storeToConfiguration(true);
				if (config_file != null && create)
					theConfiguration.save(config_file);
			}

			// initialize dictionaries
			if (!loaded) {
				// Load old residue types to ResidueDictionary in original GlycanBuilder for use of Glycan
				org.eurocarbdb.application.glycanbuilder.ResidueDictionary.loadDictionary(FileConstants.RESIDUE_TYPES_FILE);
				ResidueDictionary.loadDictionary(FileConstants.RESIDUE_TYPES_FILE);
				TerminalDictionary.loadDictionary(FileConstants.TERMINAL_TYPES_FILE);
				CoreDictionary.loadDictionary(FileConstants.CORE_TYPES_FILE);
				CrossRingFragmentDictionary.loadDictionary(FileConstants.CROSS_RING_FRAGMENT_TYPES_FILE);

				loaded = true;
			}

			// initialize style
			theResiduePlacementDictionary = new ResiduePlacementDictionary();
			theResidueStyleDictionary = new ResidueStyleDictionary();
			theLinkageStyleDictionary = new LinkageStyleDictionary();

			loadStyles(theGraphicOptions.NOTATION);
			setDisplay(theGraphicOptions.DISPLAY);
		}

		theGlycanDoc = new GlycanDocument();
		theGlycanDoc.setDefaultMassOptions(theMassOptions);

		// initialize renderers
		theGlycanRenderer = new GlycanRendererSWT(this.device);
		theGlycanRenderer.setGraphicOptions(theGraphicOptions);
		theGlycanRenderer.setResiduePlacementDictionary(theResiduePlacementDictionary);
		theGlycanRenderer.setResidueStyleDictionary(theResidueStyleDictionary);
		theGlycanRenderer.setLinkageStyleDictionary(theLinkageStyleDictionary);

	}

	protected void retrieveFromConfiguration() {
		theFileHistory.retrieve(theConfiguration);
		theResidueHistory.retrieve(theConfiguration);
		theMassOptions.retrieve(theConfiguration);
		theGraphicOptions.retrieve(theConfiguration);
		theCompositionOptions.retrieve(theConfiguration);
	}

	protected void storeToConfiguration(boolean save_options) {
		theFileHistory.store(theConfiguration);
		theResidueHistory.store(theConfiguration);
		if (save_options) {
			theMassOptions.store(theConfiguration);
			theGraphicOptions.store(theConfiguration);
			theCompositionOptions.store(theConfiguration);
		}
	}

	/**
	 * Store the configuration to a file. If the <code>autosave</code> flag is set
	 * all the option values are first stored in the configuration.
	 * 
	 * @param config_file
	 *            the destination configuration file
	 */
	public void exit(String config_file) {
		if (config_file != null) {
			storeToConfiguration(autosave);
			theConfiguration.save(config_file);
		}
	}

	/**
	 * Return the value of the <code>autosave</code> flag
	 * 
	 * @see #exit(String)
	 */
	public boolean getAutoSave() {
		return autosave;
	}

	/**
	 * Set the value of the <code>autosave</code> flag
	 * 
	 * @see #exit(String)
	 */
	public void setAutoSave(boolean flag) {
		autosave = flag;
	}

	/**
	 * Return the configuration object.
	 */
	public Configuration getConfiguration() {
		return theConfiguration;
	}

	/**
	 * Return the list of recently opened files.
	 */
	public FileHistory getFileHistory() {
		return theFileHistory;
	}

	/**
	 * Store the file history into the configuration.
	 */
	public void storeFileHistory() {
		theFileHistory.store(theConfiguration);
	}

	/**
	 * Return the list of recently added residues.
	 */
	public ResidueHistory getResidueHistory() {
		return theResidueHistory;
	}

	/**
	 * Store the residue history into the configuration.
	 */
	public void storeResidueHistory() {
		theResidueHistory.store(theConfiguration);
	}

	/**
	 * Return the mass options used when creating new structures.
	 */
	public MassOptions getDefaultMassOptions() {
		return theMassOptions;
	}

	/**
	 * Set the mass options values.
	 */
	public void setDefaultMassOptions(MassOptions mass_opt) {
		if (mass_opt == null)
			return;
		theMassOptions = mass_opt;
		theGlycanDoc.setDefaultMassOptions(theMassOptions);
	}

	/**
	 * Store the mass options into the configuration.
	 */
	public void storeDefaultMassOptions() {
		theMassOptions.store(theConfiguration);
	}

	/**
	 * Return the graphic options used to display structures.
	 */
	public GraphicOptionsSWT getGraphicOptions() {
		return theGraphicOptions;
	}

	/**
	 * Store the graphic options into the configuration.
	 */
	public void storeGraphicOptions() {
		theGraphicOptions.store(theConfiguration);
	}

	/**
	 * Return the options used to create new glycan compositions.
	 */
	public CompositionOptions getCompositionOptions() {
		return theCompositionOptions;
	}

	/**
	 * Store the composition options into the configuration.
	 */
	public void storeCompositionOptions() {
		theCompositionOptions.store(theConfiguration);
	}

	/**
	 * Return the document containing the glycan structures.
	 */
	public GlycanDocument getGlycanDocument() {
		return theGlycanDoc;
	}

	/**
	 * Return the renderer used to create graphic representations of the structures.
	 */
	public GlycanRendererSWT getGlycanRenderer() {
		return theGlycanRenderer;
	}

	/**
	 * Return the renderer for AWT used to create graphic representations of the structures.
	 */
	public GlycanRendererAWT getGlycanRendererAWT() {
		return SVGUtils.convertRenderer(theGlycanRenderer);
	}

	/**
	 * Set the graphical notation used to represent the glycan structures. Load all
	 * style dictionaries with the values corresponding to this notation.
	 * 
	 * @see GraphicOptionsSWT#NOTATION
	 */
	public void setNotation(String notation) {
		theGraphicOptions.NOTATION = notation;
		loadStyles(notation);
	}

	/**
	 * Set the way structures are display in a specific notation.
	 * 
	 * @see GraphicOptionsSWT#DISPLAY
	 */
	public void setDisplay(String display) {
		theGraphicOptions.setDisplay(display);
	}

	protected void loadStyles(String notation) {

		if (notation.equals(GraphicOptionsSWT.NOTATION_UOXF)) {
			theResiduePlacementDictionary.loadPlacements(FileConstants.RESIDUE_PLACEMENTS_FILE_UOXF);
			theResidueStyleDictionary.loadStyles(FileConstants.RESIDUE_STYLES_FILE_UOXF);
			theLinkageStyleDictionary.loadStyles(FileConstants.LINKAGE_STYLES_FILE_UOXF);
		} else if (notation.equals(GraphicOptionsSWT.NOTATION_UOXFCOL)) {
			theResiduePlacementDictionary.loadPlacements(FileConstants.RESIDUE_PLACEMENTS_FILE_UOXF);
			theResidueStyleDictionary.loadStyles(FileConstants.RESIDUE_STYLES_FILE_UOXFCOL);
			theLinkageStyleDictionary.loadStyles(FileConstants.LINKAGE_STYLES_FILE_UOXF);
		}

		else if (notation.equals(GraphicOptionsSWT.NOTATION_TEXT)) {
			theResiduePlacementDictionary.loadPlacements(FileConstants.RESIDUE_PLACEMENTS_FILE_TEXT);
			theResidueStyleDictionary.loadStyles(FileConstants.RESIDUE_STYLES_FILE_TEXT);
			theLinkageStyleDictionary.loadStyles(FileConstants.LINKAGE_STYLES_FILE_TEXT);
		} else if (notation.equals(GraphicOptionsSWT.NOTATION_CFGLINK)) {
			theResiduePlacementDictionary.loadPlacements(FileConstants.RESIDUE_PLACEMENTS_FILE_CFGLINK);
			theResidueStyleDictionary.loadStyles(FileConstants.RESIDUE_STYLES_FILE_CFGLINK);
			theLinkageStyleDictionary.loadStyles(FileConstants.LINKAGE_STYLES_FILE_CFGLINK);
		} else if (notation.equals(GraphicOptionsSWT.NOTATION_CFGBW)) {
			theResiduePlacementDictionary.loadPlacements(FileConstants.RESIDUE_PLACEMENTS_FILE_CFGBW);
			theResidueStyleDictionary.loadStyles(FileConstants.RESIDUE_STYLES_FILE_CFGBW);
			theLinkageStyleDictionary.loadStyles(FileConstants.LINKAGE_STYLES_FILE_CFGBW);
		} else if (notation.equals(GraphicOptionsSWT.NOTATION_CFG)) {
			theResiduePlacementDictionary.loadPlacements(FileConstants.RESIDUE_PLACEMENTS_FILE_CFG);
			theResidueStyleDictionary.loadStyles(FileConstants.RESIDUE_STYLES_FILE_CFG);
			theLinkageStyleDictionary.loadStyles(FileConstants.LINKAGE_STYLES_FILE_CFG);
		} else {
			// else if (notation.equals(GraphicOptions.NOTATION_SNFG)) {
			theResiduePlacementDictionary.loadPlacements(FileConstants.RESIDUE_PLACEMENTS_FILE_SNFG);
			theResidueStyleDictionary.loadStyles(FileConstants.RESIDUE_STYLES_FILE_SNFG);
			theLinkageStyleDictionary.loadStyles(FileConstants.LINKAGE_STYLES_FILE_SNFG);
		}
	}

}
