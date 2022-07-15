package org.grits.toolbox.tools.glycanbuilder.core.io;

public enum SequenceFormat {

	GWS("gws", "GWS", "GlycoWorkbench structure format", ";"),
	GCT_C("glycoct_condensed", "GlycoCT{condensed}", "GlycoCT{condensed} format", null),
//	GCT_XML("glycoct_xml", "GlycoCT{xml}", "GlycoCT{xml} file format", null),
//	WURCS("wurcs", "WURCS", System.lineSeparator());
	WURCS("wurcs", "WURCS", "WURCS format", System.lineSeparator());

	private String m_strID;
	private String m_strName;
	private String m_strDesc;
	private String m_strDelimiter;

	private SequenceFormat(String strID, String strName, String strDesc, String strDelimiter) {
		this.m_strID = strID;
		this.m_strName = strName;
		this.m_strDesc = strDesc;
		this.m_strDelimiter = strDelimiter;
	}

	public String getIdentifier() {
		return this.m_strID;
	}

	public String getName() {
		return this.m_strName;
	}

	public String getDescription() {
		return this.m_strDesc;
	}

	public String getFilterExtention() {
		return "*."+this.m_strID;
	}

	public String getFilterName() {
		return this.m_strDesc+" ("+this.getFilterExtention()+")";
	}

	public String getDelimiter() {
		return this.m_strDelimiter;
	}

	public boolean supportMultipleStructures() {
		return (this.m_strDelimiter != null);
	}

	/**
	 * Returns ImageFormat with the given identifier.
	 * @param strID String of identifier
	 * @return ImageFormat with the given identifier
	 */
	public static SequenceFormat forID(String strID) {
		for ( SequenceFormat format : SequenceFormat.values() ) {
			if ( !format.m_strID.equals(strID) )
				continue;
			return format;
		}
		return null;
	}

	public static String[] getNames() {
		String[] strFormats = new String[SequenceFormat.values().length];
		int i = 0;
		for ( SequenceFormat format : SequenceFormat.values() ) {
			strFormats[i] = format.getName();
			i++;
		}
		return strFormats;
	}

	public static String[] getFilterNames() {
		String[] strFormats = new String[SequenceFormat.values().length];
		int i = 0;
		for ( SequenceFormat format : SequenceFormat.values() ) {
			strFormats[i] = format.getFilterName();
			i++;
		}
		return strFormats;
	}

	public static String[] getFilterExtentions() {
		String[] strFormats = new String[SequenceFormat.values().length];
		int i = 0;
		for ( SequenceFormat format : SequenceFormat.values() ) {
			strFormats[i] = format.getFilterExtention();
			i++;
		}
		return strFormats;
	}

}
