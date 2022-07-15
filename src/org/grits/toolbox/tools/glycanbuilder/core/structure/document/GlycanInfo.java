package org.grits.toolbox.tools.glycanbuilder.core.structure.document;

import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.LogUtils;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.grits.toolbox.tools.glycanbuilder.core.io.parser.GWSParser;

public class GlycanInfo {

	private String m_strID;
	private String m_strGWBSequence;
	private boolean m_bHasRoot;

	public GlycanInfo(Glycan glycan) {
		this.m_strID = "";
		this.setGlycan(glycan);
	}

	public void setID(String strID) {
		this.m_strID = strID;
	}

	public String getID() {
		return this.m_strID;
	}

	public String getSequence() {
		return this.m_strGWBSequence;
	}

	public boolean hasRoot() {
		return this.m_bHasRoot;
	}

	public void setGlycan(Glycan glycan) {
		this.m_strGWBSequence = GWSParser.toString(glycan);
		this.m_bHasRoot = ( glycan.getRoot() != null );
	}

	public Glycan getGlycan() {
		try {
			return GWSParser.fromString(this.m_strGWBSequence, new MassOptions());
		} catch (Exception e) {
			LogUtils.report(e);
		}
		return null;
	}

	/**
	 * Create a string representation of the structures contained in the document.
	 * 
	 * @see GWSParser
	 */
	public String toString() {
		return "<"+this.m_strID+">"+this.m_strGWBSequence;
	}

	/**
	 * Parse structures with ID from the input string using a specified parser.
	 * 
	 * @see #fromSequence(String)
	 */
	public static GlycanInfo fromString(String str) throws Exception {
		if ( !str.contains("<") || !str.contains(">") )
			return null;
		GlycanInfo gInfo = GlycanInfo.fromSequence( str.substring(str.indexOf(">")+1) );
		if ( gInfo == null )
			return null;
		gInfo.m_strID = str.substring(str.indexOf("<")+1, str.indexOf(">"));
		return gInfo;
	}

	/**
	 * Parse structures from the input string using a specified parser.
	 * 
	 * @see GWSParser
	 */
	public static GlycanInfo fromSequence(String strSequence) throws Exception {
		return new GlycanInfo( GWSParser.fromString(strSequence, new MassOptions()) );
	}
}
