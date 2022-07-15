package org.grits.toolbox.tools.glycanbuilder.preference;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.preference.share.PreferenceEntity;
import org.grits.toolbox.core.preference.share.PreferenceReader;
import org.grits.toolbox.core.preference.share.PreferenceWriter;
import org.grits.toolbox.core.utilShare.XMLUtils;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;

@XmlRootElement(name = "graphicOptionsPreference")
public class GraphicOptionsPreference {

	private static final Logger logger = Logger.getLogger(GraphicOptionsPreference.class);
	private static final String PREFERENCE_NAME_ALL = "org.grits.toolbox.tools.glycanbuilder.preference.GraphicOptionsPreference";

	private static final String CURRENT_VERSION = "1.0";

	private GraphicOptionsSWT preference;

	public GraphicOptionsPreference() {
		preference = new GraphicOptionsSWT();
		preference.NOTATION = GraphicOptionsSWT.NOTATION_SNFG;
		preference.SHOW_MASSES = true;
		preference.SHOW_REDEND = true;
		preference.doAdjust();
	}

	public GraphicOptionsSWT getPreference() {
		return this.preference;
	}
	@XmlTransient
	public void setPreference(GraphicOptionsSWT preference) {
		this.preference = preference.clone();
	}

	public static String getPreferenceID() {
		return PREFERENCE_NAME_ALL;
	}

	public String getNotation() {
		return this.preference.NOTATION;
	}
	@XmlAttribute(name = "notation")
	public void setNotation(String notation) {
		this.preference.NOTATION = notation;
	}

	public String getDisplay() {
		return this.preference.DISPLAY;
	}
	@XmlAttribute(name = "display")
	public void setDisplay(String display) {
		this.preference.setDisplay(display);
	}

	public int getOrientation() {
		return this.preference.ORIENTATION;
	}
	@XmlAttribute(name = "orientation")
	public void setOrientation(int orientation) {
		this.preference.ORIENTATION = orientation;
	}

	public double getScale() {
		return this.preference.SCALE;
	}
	@XmlAttribute(name = "scale")
	public void setScale(double scale) {
		this.preference.SCALE = scale;
	}

	public String getShowFeatures() {
		return	""+
				((this.preference.COLLAPSE_MULTIPLE_ANTENNAE)? '1' : '0' )+
				((this.preference.SHOW_MASSES               )? '1' : '0' )+
				((this.preference.SHOW_REDEND               )? '1' : '0' );
	}
	@XmlAttribute(name="showFeatures")
	public void setShowFeatures(String showOptions) {
		this.preference.COLLAPSE_MULTIPLE_ANTENNAE = (showOptions.charAt(0) == '1');
		this.preference.SHOW_MASSES                = (showOptions.charAt(1) == '1');
		this.preference.SHOW_REDEND                = (showOptions.charAt(2) == '1');
	}

	public boolean getShowID() {
		return this.preference.SHOW_ID;
	}
	@XmlAttribute(name="showID")
	public void setShowID(boolean showID) {
		this.preference.SHOW_ID = showID;
	}

	private static String DELIMITER = "|";

	public String getCustomSettings() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.preference.NODE_SIZE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.NODE_FONT_SIZE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.NODE_FONT_FACE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.COMPOSITION_FONT_SIZE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.COMPOSITION_FONT_FACE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.LINKAGE_INFO_SIZE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.LINKAGE_INFO_FONT_FACE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.NODE_SPACE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.NODE_SUB_SPACE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.STRUCTURES_SPACE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.MASS_TEXT_SPACE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.MASS_TEXT_SIZE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.MASS_TEXT_FONT_FACE_CUSTOM).append(DELIMITER);
		sb.append(this.preference.SHOW_INFO_CUSTOM);
		return sb.toString();
	}

	public void setCustomSettings(String customSettings) {
		String[] custom = customSettings.split("\\"+DELIMITER);
		this.preference.NODE_SIZE_CUSTOM              = Integer.parseInt(custom[0]);
		this.preference.NODE_FONT_SIZE_CUSTOM         = Integer.parseInt(custom[1]);
		this.preference.NODE_FONT_FACE_CUSTOM         = custom[2];
		this.preference.COMPOSITION_FONT_SIZE_CUSTOM  = Integer.parseInt(custom[3]);
		this.preference.COMPOSITION_FONT_FACE_CUSTOM  = custom[4];
		this.preference.LINKAGE_INFO_SIZE_CUSTOM      = Integer.parseInt(custom[5]);
		this.preference.LINKAGE_INFO_FONT_FACE_CUSTOM = custom[6];
		this.preference.NODE_SPACE_CUSTOM             = Integer.parseInt(custom[7]);
		this.preference.NODE_SUB_SPACE_CUSTOM         = Integer.parseInt(custom[8]);
		this.preference.STRUCTURES_SPACE_CUSTOM       = Integer.parseInt(custom[9]);
		this.preference.MASS_TEXT_SPACE_CUSTOM        = Integer.parseInt(custom[10]);
		this.preference.MASS_TEXT_SIZE_CUSTOM         = Integer.parseInt(custom[11]);
		this.preference.MASS_TEXT_FONT_FACE_CUSTOM    = custom[12];
		this.preference.SHOW_INFO_CUSTOM              = Boolean.parseBoolean(custom[13]);

		if ( this.preference.DISPLAY.equals(GraphicOptionsSWT.DISPLAY_CUSTOM) )
			this.preference.setDisplay(this.preference.DISPLAY);
	}

	public boolean writePreference() {
		PreferenceEntity preferenceEntity = new PreferenceEntity(getPreferenceID());
		preferenceEntity.setVersion(CURRENT_VERSION);
		preferenceEntity.setValue(XMLUtils.marshalObjectXML(this));
		return PreferenceWriter.savePreference(preferenceEntity);
	}

	public static PreferenceEntity getPreferenceEntity() throws UnsupportedVersionException {
		PreferenceEntity preferenceEntity = PreferenceReader.getPreferenceByName(PREFERENCE_NAME_ALL);
		return preferenceEntity;
	}

	public static GraphicOptionsPreference readPreference() {
		return GraphicOptionsPreferenceLoader.getGraphicOptionsPreference();
	}

}
