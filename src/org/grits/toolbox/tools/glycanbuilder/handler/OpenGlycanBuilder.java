package org.grits.toolbox.tools.glycanbuilder.handler;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartStackImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.grits.toolbox.core.dataShare.IGritsConstants;
import org.grits.toolbox.core.service.IGritsUIService;
import org.grits.toolbox.tools.glycanbuilder.partdescriptor.GlycanBuilderPart;

@SuppressWarnings("restriction")
public class OpenGlycanBuilder {
	
	private Logger logger = Logger.getLogger(OpenGlycanBuilder.class);

	@Execute
	public Object execute(EModelService modelService, EPartService partService,
			MApplication application, IGritsUIService gritsUiService){
		logger.debug("START COMMAND : Open GlycanBuilder ...");
		MPart builderPart = partService.findPart(GlycanBuilderPart.ID);

		if(builderPart == null)
		{
			logger.debug("GlycanBuilder part not found. Creating GlycanBuilder");
			builderPart = partService.createPart(GlycanBuilderPart.ID);
	
			logger.debug("Adding GlycanBuilder to partstack - e4.primaryDataStack");
			PartStackImpl partStackImpl = (PartStackImpl) modelService.find(
					IGritsUIService.PARTSTACK_PRIMARY_DATA, application);
			partStackImpl.getChildren().add(builderPart);
		}

		partService.showPart(builderPart, PartState.ACTIVATE);
		gritsUiService.selectPerspective(IGritsConstants.ID_DEFAULT_PERSPECTIVE+".<Default Perspective>");

		logger.debug("...END COMMAND : Open GlycanBuilder");
		return null;
	}

}
