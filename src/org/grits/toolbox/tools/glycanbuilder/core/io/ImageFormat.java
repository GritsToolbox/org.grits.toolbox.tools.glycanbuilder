package org.grits.toolbox.tools.glycanbuilder.core.io;

import org.eclipse.swt.SWT;

public enum ImageFormat {

	BMP("bmp", "BMP", "BMP format", SWT.IMAGE_BMP),
	PNG("png", "PNG", "PNG format", SWT.IMAGE_PNG),
	JPEG("jpg", "JPEG", "JPEG format", SWT.IMAGE_JPEG),
	// TODO: It seems some images can not be saved as tiff format
//	TIFF("tiff", "TIFF", "TIFF format", SWT.IMAGE_TIFF),
	SVG("svg", "SVG", "SVG format", -1);
//	PDF("pdf", "PDF", "PDF format", -1),
//	PS("ps", "PS", "PS format", -1),
//	EPS("eps", "EPS", "EPS format", -1);

	private String m_strID;
	private String m_strName;
	private String m_strDesc;
	private int m_iImageFormatConstantInSWT;

	private ImageFormat(String strID, String strName, String strDesc, int iSWTImageConstant) {
		this.m_strID = strID;
		this.m_strName = strName;
		this.m_strDesc = strDesc;
		this.m_iImageFormatConstantInSWT = iSWTImageConstant;
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

	/**
	 * Returns image format constant if it is defined in SWT, otherwise {@code -1}.
	 * @return int of image format constant ({@code -1} if not defined in SWT)
	 * @see SWT#IMAGE_BMP
	 * @see SWT#IMAGE_PNG
	 * @see SWT#IMAGE_JPEG
	 * @see SWT#IMAGE_TIFF
	 */
	public int getSWTImageConstant() {
		return this.m_iImageFormatConstantInSWT;
	}

	public boolean isAvailableInSWT() {
		return (this.m_iImageFormatConstantInSWT > 0);
	}

	/**
	 * Returns ImageFormat with the given identifier.
	 * @param strID String of identifier
	 * @return ImageFormat with the given identifier
	 */
	public static ImageFormat forID(String strID) {
		for ( ImageFormat format : ImageFormat.values() ) {
			if ( !format.m_strID.equals(strID) )
				continue;
			return format;
		}
		return null;
	}

	public static String[] getFilterNames() {
		String[] strFormats = new String[ImageFormat.values().length];
		int i = 0;
		for ( ImageFormat format : ImageFormat.values() ) {
			strFormats[i] = format.getFilterName();
			i++;
		}
		return strFormats;
	}

	public static String[] getFilterExtensions() {
		String[] strFormats = new String[ImageFormat.values().length];
		int i = 0;
		for ( ImageFormat format : ImageFormat.values() ) {
			strFormats[i] = format.getFilterExtention();
			i++;
		}
		return strFormats;
	}
}
