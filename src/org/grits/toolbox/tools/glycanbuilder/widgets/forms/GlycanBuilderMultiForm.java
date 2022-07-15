package org.grits.toolbox.tools.glycanbuilder.widgets.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.tools.glycanbuilder.widgets.canvas.GlycanCanvasWithToolBarComposite;

public class GlycanBuilderMultiForm extends MultiPageFormAbstract {

	private FileForm m_file;
	private ViewSettingsForm m_view;
	private AddStructureForm m_structure;

	public GlycanBuilderMultiForm(Composite parent, GlycanCanvasWithToolBarComposite canvas) {
		super(parent, SWT.BORDER);
		addListenersToForms(canvas);
	}

	public FileForm getFileForm() {
		return this.m_file;
	}

	public ViewSettingsForm getViewSettingsForm() {
		return this.m_view;
	}

	public AddStructureForm getAddStructureForm() {
		return this.m_structure;
	}

	@Override
	protected void addPages() {
		this.m_file = new FileForm(this, Messages.getString("File.label"));
		addPage(this.m_file);
		this.m_view = new ViewSettingsForm(this, Messages.getString("ViewSettings.label")); //$NON-NLS-1$
		addPage(this.m_view);
		this.m_structure = new AddStructureForm(this, Messages.getString("AddStructure.label")); //$NON-NLS-1$
		addPage(this.m_structure);
	}

	private void addListenersToForms(GlycanCanvasWithToolBarComposite canvas) {
		// To file
		this.m_file.addSelecionListenersForCanvas(canvas.getGlycanCanvas());
		this.m_file.addListenerToGlycanDocument(canvas.getGlycanCanvas().getBuilderWorkspace().getGlycanDocument());

		// To view settings
		this.m_view.loadSettingsFromWorkspace(canvas.getGlycanCanvas().getBuilderWorkspace());
		this.m_view.addCanvasUpdateListener(canvas);

		// To structure
		this.m_structure.addSelectionListenersForCanvas(canvas);
	}
}
