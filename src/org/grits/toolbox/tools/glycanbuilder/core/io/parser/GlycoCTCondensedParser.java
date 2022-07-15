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
*   Last commit: $Rev$ by $Author$ on $Date::             $  
*/

package org.grits.toolbox.tools.glycanbuilder.core.io.parser;

import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.LogUtils;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.grits.toolbox.tools.glycanbuilder.core.io.ErrorDialogUtils;


/**
   Read and write glycan structure in the GlycoCT condensed format using the
   MolecularFramework and ResourceDB libraries.

   @author Alessio Ceroni (a.ceroni@imperial.ac.uk)
*/

public class GlycoCTCondensedParser extends GlycoCTParser {

	private SugarImporterGlycoCTCondensed cond_importer = null;
    private SugarExporterGlycoCTCondensed cond_exporter = null;
    
    /**
       Default constructor. Initialize the MolecularFramework objects.
     */
    public GlycoCTCondensedParser(boolean tolerate) {
    super(tolerate);

    try {
        cond_importer = new SugarImporterGlycoCTCondensed(); 
        cond_exporter = new SugarExporterGlycoCTCondensed();    
    }
    catch( Exception e ) {        
        LogUtils.report(e);
    }
    }

    public String writeGlycan(Glycan structure) {
    	return toGlycoCTCondensed(structure);
    }
    
    public Glycan readGlycan(String buffer, MassOptions default_mass_options) throws Exception {
    	return fromGlycoCTCondensed(buffer,default_mass_options);
    }

    /**
       Get the MolecularFramework object used to parse the GlycoCT
       condensed string.
     */

    public SugarImporterGlycoCTCondensed getImporterCondensed() {
    	return cond_importer;
    }

    /**
       Get the MolecularFramework object used to produce the GlycoCT
       condensed string.
     */

    public SugarExporterGlycoCTCondensed getExporterCondensed() {
    	return cond_exporter;
    }
    
    /**
       Return a GlycoCT condensed representation of a glycan
       structure. Equivalent to a call to {@link #writeGlycan}
     */

    public String toGlycoCTCondensed(Glycan structure) {
    try {
        cond_exporter.start(toSugar(structure));
        return cond_exporter.getHashCode();
    }
    catch( Exception e) {
        LogUtils.report(e);
		MultiStatus status = ErrorDialogUtils.createMultiStatus(e.getLocalizedMessage(), e);
		ErrorDialog.openError(null, "Error", "Error in converting Glycan to GlycoCT condensed", status);
        return "";
    }
    }
  
    public Sugar toSugar(Glycan structure) throws Exception {
		return toSugar(structure,null);
	}

	/**
       Create a glycan structure from its GlycoCT condensed
       representation. Equivalent to a call to {@link #readGlycan}.
       @param default_mass_opt the mass options to use for the new
       structure if they are not specified in the string
       representation
       @throws Exception if the string cannot be parsed
    */
    public Glycan fromGlycoCTCondensed(String str, MassOptions default_mass_opt) throws Exception {
    	return fromSugar(cond_importer.parse(str),default_mass_opt);
    }
     
}
