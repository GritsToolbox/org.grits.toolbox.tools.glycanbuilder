package org.grits.toolbox.tools.glycanbuilder.preference;

import org.apache.log4j.Logger;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.preference.share.PreferenceEntity;
import org.grits.toolbox.core.utilShare.XMLUtils;

public class GraphicOptionsPreferenceLoader {
	private static final Logger logger = Logger.getLogger(GraphicOptionsPreferenceLoader.class);

	public static GraphicOptionsPreference getGraphicOptionsPreference()  {
		GraphicOptionsPreference preferences = null;
		try {
			PreferenceEntity preferenceEntity = GraphicOptionsPreference.getPreferenceEntity();
			if( preferenceEntity != null ) 
				preferences = (GraphicOptionsPreference)
					XMLUtils.getObjectFromXML(preferenceEntity.getValue(), GraphicOptionsPreference.class);
		} catch (UnsupportedVersionException ex) {
			logger.error(ex.getMessage(), ex);
		} catch( Exception ex ) {
			logger.error(ex.getMessage(), ex);
		}
		if( preferences == null ) // well, either no preferences yet or some error. initialize to defaults and return
			preferences = new GraphicOptionsPreference();
		return preferences;
	}

}
