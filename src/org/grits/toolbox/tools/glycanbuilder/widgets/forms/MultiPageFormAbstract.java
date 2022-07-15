package org.grits.toolbox.tools.glycanbuilder.widgets.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class MultiPageFormAbstract {

	private FormToolkit toolkit;
	private CTabFolder folder;
	private List<FormAbstract> forms;

	public MultiPageFormAbstract(Composite parent, int style) {
		this.folder = new CTabFolder(parent, style);
		this.toolkit = new FormToolkit(parent.getDisplay());
		this.forms = new ArrayList<>();
		addPages();
		// Reflow the forms
		for ( FormAbstract form : forms )
			form.getForm().reflow(true);
	}

	public FormToolkit getToolkit() {
		return this.toolkit;
	}

	public CTabFolder getFolder() {
		return this.folder;
	}

	protected abstract void addPages();

	public void addPage(FormAbstract form) {
		CTabItem item = new CTabItem(this.folder, SWT.NONE);
		item.setText(form.getTabText());
		item.setControl(form.getForm());
		folder.setSelection(item);
		this.forms.add(form);
	}

	
}
