package org.grits.toolbox.tools.glycanbuilder.widgets.dialog;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eurocarbdb.application.glycanbuilder.Residue;

public class RepititionPropertyDialog extends TitleAreaDialog {

	private Residue m_resEndRep;

	private Spinner m_spnMinRep;
	private Spinner m_spnMaxRep;

	public RepititionPropertyDialog(Shell parentShell, Residue resEndRep) {
		super(parentShell);
		this.m_resEndRep = resEndRep;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Reptition properties");
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout layout = new GridLayout(2, true);
		container.setLayout(layout);

		createControls(container);
		setValues(this.m_resEndRep);

		area.pack();
		return area;
	}

	private void createControls(Composite parent) {
		Label lbl;
		Spinner spn;

		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		};

		lbl = new Label(parent, SWT.NONE);
		lbl.setText("Min repitition");

		spn = new Spinner(parent, SWT.BORDER);
		spn.setMinimum(-1);
		spn.setMinimum(999);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(spn);
		spn.addSelectionListener(listener);
		this.m_spnMinRep = spn;

		lbl = new Label(parent, SWT.NONE);
		lbl.setText("Max repitition");

		spn = new Spinner(parent, SWT.BORDER);
		spn.setMinimum(-1);
		spn.setMinimum(999);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(spn);
		spn.addSelectionListener(listener);
		this.m_spnMaxRep = spn;
	}

	private void setValues(Residue resEndRep) {
		this.m_spnMinRep.setSelection(resEndRep.getMinRepetitions());
		this.m_spnMaxRep.setSelection(resEndRep.getMaxRepetitions());
	}

	private boolean validate() {
		this.setErrorMessage(null);
		int iMin = this.m_spnMinRep.getSelection();
		int iMax = this.m_spnMaxRep.getSelection();
		if ( iMin >= 0 && iMax >= 0 && iMin > iMax ) {
			this.setErrorMessage("Maximum value must be equal or larger than minimum value.");
			return false;
		}
		return true;
	}

	private void save() {
		this.m_resEndRep.setMinRepetitions( this.m_spnMinRep.getText() );
		this.m_resEndRep.setMaxRepetitions( this.m_spnMaxRep.getText() );
	}

	@Override
	protected void okPressed() {
		if ( !validate() ) {
			MessageBox box = new MessageBox(this.getShell(), SWT.OK|SWT.ICON_ERROR);
			box.setMessage(this.getErrorMessage());
			box.open();
			return;
		}
		save();
		super.okPressed();
	}

}
