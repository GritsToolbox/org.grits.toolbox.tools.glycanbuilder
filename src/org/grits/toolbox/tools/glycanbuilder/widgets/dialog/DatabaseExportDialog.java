package org.grits.toolbox.tools.glycanbuilder.widgets.dialog;

import java.util.HashSet;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanDocument;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanInfo;
import org.grits.toolbox.tools.glycanbuilder.database.GlycanDatabaseUtils;
import org.grits.toolbox.tools.glycanbuilder.widgets.forms.DatabaseIDComposite;
import org.grits.toolbox.tools.glycanbuilder.widgets.forms.DatabaseInformationComposite;
import org.grits.toolbox.util.structure.glycan.database.GlycanDatabase;
import org.grits.toolbox.util.structure.glycan.database.GlycanStructure;

public class DatabaseExportDialog extends TitleAreaDialog {

	private GlycanDocument m_doc;

	private Text m_txtDBFile;
	private DatabaseInformationComposite m_dbInfo;
	private DatabaseIDComposite m_dbID;

	public DatabaseExportDialog(Shell parentShell, GlycanDocument doc) {
		super(parentShell);
		this.m_doc = doc;
	}

	public void setDatabaseInformation(DatabaseInformationComposite dbInfo) {
		this.m_dbInfo = dbInfo;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Export structures to database");
		validate();
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		createControls(container);

		area.pack();
		return area;
	}

	private void createControls(Composite container) {

		this.createFilesControl(container);

		Group grp;
		grp = new Group(container, SWT.NONE);
		grp.setText("Database Information");
		grp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		DatabaseInformationComposite dbInfo = new DatabaseInformationComposite(grp);
		if ( this.m_dbInfo != null ) {
			dbInfo.setDatabaseName(this.m_dbInfo.getDatabaseName());
			dbInfo.setDatabaseVersion(this.m_dbInfo.getDatabaseVersion());
			dbInfo.setDatabaseDescription(this.m_dbInfo.getDatabaseDescription());
		}
		dbInfo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				isValidInput();
			}
		});
		this.m_dbInfo = dbInfo;

		grp = new Group(container, SWT.NONE);
		grp.setText("Database IDs");
		grp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
		this.m_dbID = new DatabaseIDComposite(grp);
	}

	private void createFilesControl(Composite container) {
		// create the label in first column
		Label lbl = new Label(container, SWT.NONE);
		lbl.setText("Database file");
		lbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		// text field in the middle column
		this.m_txtDBFile = new Text(container, SWT.BORDER);
		this.m_txtDBFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.m_txtDBFile.setEditable(false);
		// add the button to the last column
		Button btnBrowse = new Button(container, SWT.NONE);
		GridData data = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		data.widthHint = 100;
		btnBrowse.setLayoutData(data);
		btnBrowse.setText("Save As");
		// click action for the browse button
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent a_event) {
				// create a file save dialog for xml files
				FileDialog dlgSave = new FileDialog(container.getShell(), SWT.SAVE);
				dlgSave.setOverwrite(true);
				dlgSave.setFilterNames(new String[] { "Glycan structure database (.xml)", "All files" });
				dlgSave.setFilterExtensions(new String[] { "*.xml", "*.*" });
				String strFile = dlgSave.open();
				// was something selected?
				if (strFile != null)
					m_txtDBFile.setText(strFile);
				validate();
			}
		});
	}

	private void validate() {
		if (this.isValidInput())
			this.setErrorMessage(null);
	}

	private boolean isValidInput() {
		// check if the file is there
		if (this.m_txtDBFile.getText().isEmpty()) {
			this.setErrorMessage("Please select a file to save the database.");
			return false;
		}
		if (!m_dbInfo.isValidInput()) {
			this.setErrorMessage(m_dbInfo.getErrorMessage());
			return false;
		}
		this.setErrorMessage(null);
		return true;
	}

	@Override
	public void okPressed() {
		if (!this.isValidInput())
			return;

		// Fill glycan database ids
		fillGlycanIDs(this.m_doc);

		GlycanDatabase database = new GlycanDatabase();
		// Add database info
		database.setName(m_dbInfo.getDatabaseName());
		database.setVersion(m_dbInfo.getDatabaseVersion());
		database.setDescription(m_dbInfo.getDatabaseDescription());

		// Add structures
		int i = 0;
		for (GlycanInfo gInfo : this.m_doc.getStructures()) {
			GlycanStructure gStructure = new GlycanStructure();
			gStructure.setGWBSequence(gInfo.getSequence());
			gStructure.setId(gInfo.getID());
			database.addStructure(gStructure);
			i++;
		}
		database.setStructureCount(i);

		GlycanDatabaseUtils.saveGlycanDatabase(database, this.m_txtDBFile.getText());

		super.okPressed();
	}

	private void fillGlycanIDs(GlycanDocument doc) {
		HashSet<String> setExistIDs = new HashSet<>();
		if (!this.m_dbID.isOverwriteIDs()) {
			for (GlycanInfo glycan : doc.getStructures())
				setExistIDs.add(glycan.getID());
		}

		int i = 1;
		for (GlycanInfo glycan : doc.getStructures()) {
			if (!this.m_dbID.isOverwriteIDs() && glycan.getID() != null && !glycan.getID().isEmpty())
				continue;
			// Generate ID
			String newID = this.m_dbID.getIDPrefix() + i++ + this.m_dbID.getIDPostfix();
			while (setExistIDs.contains(newID))
				newID = this.m_dbID.getIDPrefix() + i++ + this.m_dbID.getIDPostfix();
			glycan.setID(newID);
		}
	}

}
