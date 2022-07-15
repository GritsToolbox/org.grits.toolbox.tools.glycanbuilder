package org.grits.toolbox.tools.glycanbuilder.widgets.dialog;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.tools.glycanbuilder.core.io.SequenceFormat;

public class ImportSequenceDialog extends TitleAreaDialog {

	private Combo m_cmbFormat;
	private Text m_txtSequence;

	private SequenceFormat format;
	private String strSequence;

	public ImportSequenceDialog(Shell parentShell) {
		super(parentShell);
	}

	public SequenceFormat getSequenceFormat() {
		return this.format;
	}

	public String getSequence() {
		return this.strSequence;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Import structures from text format");
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout layout = new GridLayout(2, true);
		container.setLayout(layout);

		createControls(container);

		area.pack();
		return area;
	}

	private void createControls(Composite container) {
		Label lbl = new Label(container, SWT.NONE);
		lbl.setText("String encoded");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(lbl);

		this.m_cmbFormat = new Combo(container, SWT.READ_ONLY);
		this.m_cmbFormat.setItems(SequenceFormat.getNames());
		this.m_cmbFormat.select(0); // GWS
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(this.m_cmbFormat);

		this.m_txtSequence = new Text(container, SWT.MULTI|SWT.WRAP|SWT.BORDER);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, true).hint(SWT.DEFAULT, 100).applyTo(this.m_txtSequence);
	}

	@Override
	public void okPressed() {
		this.format = SequenceFormat.values()[this.m_cmbFormat.getSelectionIndex()];
		this.strSequence = this.m_txtSequence.getText();
		super.okPressed();
	}
}
