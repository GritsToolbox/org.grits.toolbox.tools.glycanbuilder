package org.grits.toolbox.tools.glycanbuilder.widgets.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class DatabaseInformationComposite {

	private static Integer TEXT_LENGTH_NAME_VERSION = 64;
	private static Integer TEXT_LENGTH_DESCRITION = 1024;

	private FormToolkit m_toolkit;

	private Text m_txtDBName;
	private Text m_txtDBVersion;
	private Text m_txtDesc;

	private String m_strErrorMessage;

	public DatabaseInformationComposite(Composite parent, FormToolkit toolkit) {
		this.m_toolkit = toolkit;
		createControls(parent);
	}

	public DatabaseInformationComposite(Composite parent) {
		this.m_toolkit = new FormToolkit(parent.getDisplay());
		this.m_toolkit.setBackground(parent.getBackground());
		createControls(parent);
	}

	private void createControls(Composite parent) {
		// create a new group for the information and fill horizontal layout
//		Composite container = this.m_toolkit.createComposite(parent);
		// 2 column grid
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		// controls for name (label + text)
		Label lbl = this.m_toolkit.createLabel(parent, "Name");
		lbl.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		this.m_txtDBName = this.m_toolkit.createText(parent, "", SWT.BORDER);
		this.m_txtDBName.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE));
		this.m_txtDBName.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		// controls for version number
		lbl = this.m_toolkit.createLabel(parent, "Version");
		lbl.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.MIDDLE));
		this.m_txtDBVersion = this.m_toolkit.createText(parent, "", SWT.BORDER);
		this.m_txtDBVersion.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE));
		this.m_txtDBVersion.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		// controls for description
		lbl = this.m_toolkit.createLabel(parent, "Description");
		lbl.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP));
		this.m_txtDesc = this.m_toolkit.createText(parent, "", SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		data.maxWidth = 120;
		data.heightHint = 50;
		this.m_txtDesc.setLayoutData(data);
		this.m_txtDesc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	}

	public void setDatabaseName(String strName) {
		this.m_txtDBName.setText(strName);
	}

	public String getDatabaseName() {
		return this.m_txtDBName.getText();
	}

	public void setDatabaseVersion(String strVersion) {
		this.m_txtDBVersion.setText(strVersion);
	}

	public String getDatabaseVersion() {
		return this.m_txtDBVersion.getText();
	}

	public String getDatabaseDescription() {
		return this.m_txtDesc.getText();
	}

	public void setDatabaseDescription(String strDesc) {
		this.m_txtDesc.setText(strDesc);
	}

	public boolean isEmpty() {
		return ( this.m_txtDBName.getText().isEmpty()
			  || this.m_txtDBVersion.getText().isEmpty()
			  || this.m_txtDesc.getText().isEmpty()
			);
	}

	public void clearForm() {
		this.m_txtDBName.setText("");
		this.m_txtDBVersion.setText("");
		this.m_txtDesc.setText("");
	}

	private void setErrorMessage(String strMessage) {
		this.m_strErrorMessage = strMessage;
	}

	public String getErrorMessage() {
		return this.m_strErrorMessage;
	}

	public boolean isValidInput() {
		// check if there is a name
		if ( this.m_txtDBName.getText().isEmpty() ) {
			this.setErrorMessage("Please provide a name for the database.");
			return false;
		} else {
			// check length
			if ( this.m_txtDBName.getText().length() > TEXT_LENGTH_NAME_VERSION ) {
				this.setErrorMessage("The database name can not be longer than "
						+ TEXT_LENGTH_NAME_VERSION.toString() + " characters.");
				return false;
			}
		}
		// check version
		if ( this.m_txtDBVersion.getText().isEmpty() ) {
			this.setErrorMessage("Please provide a version for the database.");
			return false;
		} else {
			// check length
			if (this.m_txtDBVersion.getText().length() > TEXT_LENGTH_NAME_VERSION) {
				this.setErrorMessage("The database version can not be longer than "
						+ TEXT_LENGTH_NAME_VERSION.toString() + " characters.");
				return false;
			}
		}
		// description
		if ( !this.m_txtDesc.getText().isEmpty() ) {
			// check length
			if (this.m_txtDesc.getText().length() > TEXT_LENGTH_DESCRITION) {
				this.setErrorMessage("The description can not be longer than "
						+ TEXT_LENGTH_DESCRITION.toString() + " characters.");
				return false;
			}
		}
		this.setErrorMessage("");
		return true;
	}

	/**
	 * Add ModifyListener to all text controls.
	 * @param listener ModifyListener
	 */
	public void addModifyListener(ModifyListener listener) {
		this.m_txtDBName.addModifyListener(listener);
		this.m_txtDBVersion.addModifyListener(listener);
		this.m_txtDesc.addModifyListener(listener);
	}

	/**
	 * Remove ModifyListener from all text controls.
	 * @param listener ModifyListener
	 */
	public void removeModifyListener(ModifyListener listener) {
		this.m_txtDBName.removeModifyListener(listener);
		this.m_txtDBVersion.removeModifyListener(listener);
		this.m_txtDesc.removeModifyListener(listener);
	}
}
