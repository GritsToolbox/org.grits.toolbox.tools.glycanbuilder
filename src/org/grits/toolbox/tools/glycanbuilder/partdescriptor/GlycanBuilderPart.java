package org.grits.toolbox.tools.glycanbuilder.partdescriptor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.core.dataShare.IGritsConstants;
import org.grits.toolbox.core.service.IGritsUIService;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanDocument;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;
import org.grits.toolbox.tools.glycanbuilder.preference.GraphicOptionsPreference;
import org.grits.toolbox.tools.glycanbuilder.widgets.GlycanBuilder;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.GlycanIconProvider;
import org.osgi.service.event.Event;

public class GlycanBuilderPart {

	public static String ID = "org.grits.toolbox.tools.glycanbuilder"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(GlycanBuilderPart.class);

	private GlycanBuilder builder;
	private BuilderWorkspaceSWT bws;

	@Inject private MDirtyable dirtyable;
	private MPart part;

	@PostConstruct
	public void postConstruct(Composite parent, final MPart part) {
		logger.info("Create GlycanBuilder");

		this.part = part;
		this.dirtyable.setDirty(false);

		this.builder = new GlycanBuilder(parent);
		this.bws = this.builder.getGlycanCanvasWithToolBar().getGlycanCanvas().getBuilderWorkspace();

		// Load GraphicOptions preference
		GraphicOptionsSWT preference = GraphicOptionsPreference.readPreference().getPreference();
		this.bws.getGraphicOptions().copy(preference);
		this.bws.setNotation(bws.getGraphicOptions().NOTATION);
		GlycanIconProvider.setNotation(bws.getGraphicOptions().NOTATION);
		this.builder.getBuilderMultiForm().getViewSettingsForm().loadSettingsFromWorkspace(this.bws);
		this.builder.getGlycanCanvasWithToolBar().getShortcutToolBar().updateOrientationIcon();

		// Add listener of GlycanDocument to set dirty when state is changed
		this.bws.getGlycanDocument().addListener(new GlycanDocument.Listener() {
			@Override
			public void stateSaved() {
				dirtyable.setDirty(true);
			}

			@Override
			public void stateLoaded() {
				dirtyable.setDirty(true);
			}

			@Override
			public void fileSaved() {
				dirtyable.setDirty(false);
			}

			@Override
			public void fileOpened() {
				dirtyable.setDirty(false);
			}
		});
		part.getContext().set(GlycanBuilder.class, this.builder);
	}

	@PreDestroy
	public void preDestroy() {
		logger.info("Save preference of GlycanBuilder");
		// Save preference
		GraphicOptionsPreference preference = new GraphicOptionsPreference();
		preference.setPreference(this.bws.getGraphicOptions());
		preference.writePreference();
	}

	@Inject
	@Optional
	public void handleBringToTop(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event, IGritsUIService gritsUiService) {
		if (this.part == event.getProperty(UIEvents.EventTags.ELEMENT)) {
			// Switch perspective
			gritsUiService.selectPerspective(IGritsConstants.ID_DEFAULT_PERSPECTIVE+".<Default Perspective>");
		}
	}

	@Persist
	public void doSave() {
		this.bws.getGlycanDocument().save();
	}

}
