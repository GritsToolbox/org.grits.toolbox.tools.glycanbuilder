package org.grits.toolbox.tools.glycanbuilder.widgets.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.LogUtils;
import org.grits.toolbox.tools.glycanbuilder.core.io.GlycanIOUtils;
import org.grits.toolbox.tools.glycanbuilder.core.io.ImageFormat;
import org.grits.toolbox.tools.glycanbuilder.core.io.SequenceFormat;
import org.grits.toolbox.tools.glycanbuilder.core.io.parser.GWSParser;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanDocument;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanInfo;
import org.grits.toolbox.tools.glycanbuilder.database.GlycanDatabaseUtils;
import org.grits.toolbox.tools.glycanbuilder.widgets.canvas.GlycanCanvasComposite;
import org.grits.toolbox.tools.glycanbuilder.widgets.dialog.DatabaseExportDialog;
import org.grits.toolbox.util.structure.glycan.database.GlycanDatabase;
import org.grits.toolbox.util.structure.glycan.database.GlycanStructure;

public class FileForm extends FormAbstract {

	private boolean m_bIsChanged;

	private Button m_btnNew;
	private Button m_btnOpen;
	private Button m_btnInsert;
	private Button m_btnSave;
	private Button m_btnSaveAs;
	private Map<SequenceFormat, Button> m_mapSecFormatToButtonForExportSelection;
	private Map<SequenceFormat, Button> m_mapSecFormatToButtonForExportAll;
	private Map<ImageFormat, Button> m_mapImgFormatToButtonForExportSelection;
	private Map<ImageFormat, Button> m_mapImgFormatToButtonForExportCurrent;
	private Map<SequenceFormat, Button> m_mapSecFormatToButtonForImport;
	private ImportTextComposite m_importText;
	private Button m_btnImportDatabase;
	private Button m_btnExportDatabase;
	private DatabaseInformationComposite m_dbInfo;

	public FileForm(MultiPageFormAbstract parent, String tabText) {
		super(parent, tabText);
		this.m_mapSecFormatToButtonForExportSelection = new HashMap<>();
		this.m_mapSecFormatToButtonForExportAll = new HashMap<>();
		this.m_mapImgFormatToButtonForExportSelection = new HashMap<>();
		this.m_mapImgFormatToButtonForExportCurrent = new HashMap<>();
		this.m_mapSecFormatToButtonForImport = new HashMap<>();
		createControl();
	}

	public void setChanged(boolean changed) {
		this.m_bIsChanged = changed;
	}

	private void createControl() {
		getForm().setText(Messages.getString("File.title")); //$NON-NLS-1$

		Composite sectionClient;
		ExpandableComposite exp;
		Button btn;

		// Save and load structures
		btn = getToolkit().createButton(getForm().getBody(),
				Messages.getString("File.new"), //$NON-NLS-1$
				SWT.FLAT);
		btn.setToolTipText(Messages.getString("File.new_desc")); //$NON-NLS-1$
		this.m_btnNew = btn;

		btn = getToolkit().createButton(getForm().getBody(),
				Messages.getString("File.open"), //$NON-NLS-1$
				SWT.FLAT);
		btn.setToolTipText(Messages.getString("File.open_desc")); //$NON-NLS-1$
		this.m_btnOpen = btn;

		btn = getToolkit().createButton(getForm().getBody(),
				Messages.getString("File.insert"), //$NON-NLS-1$
				SWT.FLAT);
		btn.setToolTipText(Messages.getString("File.insert_desc")); //$NON-NLS-1$
		this.m_btnInsert = btn;

		btn = getToolkit().createButton(getForm().getBody(),
				Messages.getString("File.save"), //$NON-NLS-1$
				SWT.FLAT);
		btn.setToolTipText(Messages.getString("File.save_desc")); //$NON-NLS-1$
		this.m_btnSave = btn;

		btn = getToolkit().createButton(getForm().getBody(),
				Messages.getString("File.save_as"), //$NON-NLS-1$
				SWT.FLAT);
		btn.setToolTipText(Messages.getString("File.save_as_desc")); //$NON-NLS-1$
		this.m_btnSaveAs = btn;

		// Export to
		sectionClient = this.createNewSection(Messages.getString("File.export")); //$NON-NLS-1$

		/// Sequences (selection)
		exp = this.createExpandableComposite(sectionClient);
		exp.setText(Messages.getString("File.export_sequence_selection")); //$NON-NLS-1$
		exp.setToolTipText(Messages.getString("File.export_sequence_selection_desc")); //$NON-NLS-1$
		for ( SequenceFormat format : SequenceFormat.values()) {
			btn = getToolkit().createButton((Composite)exp.getClient(), format.getName(), SWT.FLAT);
			btn.setToolTipText(format.getDescription());
			this.m_mapSecFormatToButtonForExportSelection.put(format, btn);
		}

		/// Sequences (all)
		exp = this.createExpandableComposite(sectionClient);
		exp.setText(Messages.getString("File.export_sequence_all")); //$NON-NLS-1$
		exp.setToolTipText(Messages.getString("File.export_sequence_all_desc")); //$NON-NLS-1$
		for ( SequenceFormat format : SequenceFormat.values()) {
			btn = getToolkit().createButton((Composite)exp.getClient(), format.getName(), SWT.FLAT);
			btn.setToolTipText(format.getDescription());
			this.m_mapSecFormatToButtonForExportAll.put(format, btn);
		}

		/// Images (selection)
		exp = this.createExpandableComposite(sectionClient);
		exp.setText(Messages.getString("File.export_image_selection")); //$NON-NLS-1$
		exp.setToolTipText(Messages.getString("File.export_image_selection_desc")); //$NON-NLS-1$
		for ( ImageFormat format : ImageFormat.values() ) {
			btn = getToolkit().createButton((Composite)exp.getClient(), format.getName(), SWT.FLAT);
			btn.setToolTipText(format.getDescription());
			this.m_mapImgFormatToButtonForExportSelection.put(format, btn);
		}

		/// Images (current page)
		exp = this.createExpandableComposite(sectionClient);
		exp.setText(Messages.getString("File.export_image_current")); //$NON-NLS-1$
		exp.setToolTipText(Messages.getString("File.export_image_current_desc")); //$NON-NLS-1$
		for ( ImageFormat format : ImageFormat.values() ) {
			btn = getToolkit().createButton((Composite)exp.getClient(), format.getName(), SWT.FLAT);
			btn.setToolTipText(format.getDescription());
			this.m_mapImgFormatToButtonForExportCurrent.put(format, btn);
		}

		// Import from
		sectionClient = this.createNewSection(Messages.getString("File.import")); //$NON-NLS-1$
		/// Sequence format file
		exp = this.createExpandableComposite(sectionClient);
		exp.setText(Messages.getString("File.import_sequence")); //$NON-NLS-1$
		for ( SequenceFormat format : SequenceFormat.values()) {
			btn = getToolkit().createButton((Composite)exp.getClient(), format.getName(), SWT.FLAT);
			btn.setToolTipText(format.getDescription());
			this.m_mapSecFormatToButtonForImport.put(format, btn);
		}
		/// Text input
		exp = this.createExpandableComposite(sectionClient);
		exp.setText(Messages.getString("File.import_text")); //$NON-NLS-1$
		this.m_importText = new ImportTextComposite((Composite)exp.getClient(), this.getToolkit());

		// Save/load structures to/from glycan database
		sectionClient = this.createNewSection(Messages.getString("File.database")); //$NON-NLS-1$

		Group grp = new Group(sectionClient, SWT.NONE);
		grp.setBackground(sectionClient.getBackground());
		grp.setText(Messages.getString("File.db_info")); //$NON-NLS-1$
		this.m_dbInfo = new DatabaseInformationComposite(grp, this.getToolkit());

		btn = getToolkit().createButton(sectionClient,
				Messages.getString("File.import_db"), //$NON-NLS-1$
				SWT.FLAT);
		btn.setToolTipText(Messages.getString("File.import_db_desc")); //$NON-NLS-1$
		this.m_btnImportDatabase = btn;

		btn = getToolkit().createButton(sectionClient,
				Messages.getString("File.export_db"), //$NON-NLS-1$
				SWT.FLAT);
		btn.setToolTipText(Messages.getString("File.export_db_desc")); //$NON-NLS-1$
		this.m_btnExportDatabase = btn;

//		btn = getToolkit().createButton(sectionClient,
//				Messages.getString("File.insert_db"), //$NON-NLS-1$
//				SWT.FLAT);
//		btn.setToolTipText(Messages.getString("File.insert_db_desc")); //$NON-NLS-1$
//		this.m_btnInsertDatabase = btn;

	}

	public void addListenerToGlycanDocument(GlycanDocument doc) {
		// Add listener to GlycanDocument
		doc.addListener(new GlycanDocument.Listener() {

			@Override
			public void stateSaved() {
				m_bIsChanged = true;
			}

			@Override
			public void stateLoaded() {
				m_bIsChanged = true;
			}

			@Override
			public void fileSaved() {
				m_bIsChanged = false;
			}

			@Override
			public void fileOpened() {
				m_bIsChanged = false;
			}
		});

	}

	public void addSelecionListenersForCanvas(GlycanCanvasComposite canvas) {
		Button btn;
		btn = this.m_btnNew;
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( !confirmSaveBeforeChange(canvas, ((Button)e.widget).getToolTipText()) )
					return;
				m_dbInfo.clearForm();
				canvas.resetDocument();
				canvas.updateView();
			}
		});

		btn = this.m_btnOpen;
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( !confirmSaveBeforeChange(canvas, ((Button)e.widget).getToolTipText()) )
					return;
				canvas.resetDocument();
				canvas.getBuilderWorkspace().getGlycanDocument().open(null);
				canvas.loadDocument();
				canvas.updateView();
			}
		});

		btn = this.m_btnInsert;
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				canvas.getBuilderWorkspace().getGlycanDocument().insert(null);
				canvas.loadDocument();
				canvas.updateView();
			}
		});

		btn = this.m_btnSave;
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				canvas.getBuilderWorkspace().getGlycanDocument().save();
			}
		});

		btn = this.m_btnSaveAs;
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				canvas.getBuilderWorkspace().getGlycanDocument().saveAs();
			}
		});

		// Export sequence (selection)
		for ( SequenceFormat format : this.m_mapSecFormatToButtonForExportSelection.keySet() ) {
			btn = this.m_mapSecFormatToButtonForExportSelection.get(format);
			btn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					List<Glycan> lGlycans = getSelectedGlycansFromCanvas(canvas);
					if ( lGlycans == null )
						return;
					GlycanIOUtils.saveGlycanSequence(lGlycans, format, false);
				}
			});
		}
		// Export sequence (all)
		for ( SequenceFormat format : this.m_mapSecFormatToButtonForExportAll.keySet() ) {
			btn = this.m_mapSecFormatToButtonForExportAll.get(format);
			btn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
//					List<Glycan> lGlycans = getAllGlycansFromCurrentCanvas(canvas);
					List<Glycan> lGlycans = getAllGlycans(canvas);
					if ( lGlycans == null )
						return;
					GlycanIOUtils.saveGlycanSequence(lGlycans, format, false);
				}
			});
		}
		// Export image (selection)
		for ( ImageFormat format : this.m_mapImgFormatToButtonForExportSelection.keySet() ) {
			btn = this.m_mapImgFormatToButtonForExportSelection.get(format);
			btn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					List<Glycan> lGlycans = getSelectedGlycansFromCanvas(canvas);
					if ( lGlycans == null )
						return;
					GlycanIOUtils.saveGlycanImage(lGlycans, canvas.getBuilderWorkspace(), format);
				}
			});
		}
		// Export image (current page)
		for ( ImageFormat format : this.m_mapImgFormatToButtonForExportCurrent.keySet() ) {
			btn = this.m_mapImgFormatToButtonForExportCurrent.get(format);
			btn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					List<Glycan> lGlycans = getAllGlycansFromCurrentCanvas(canvas);
					if ( lGlycans == null )
						return;
					GlycanIOUtils.saveGlycanImage(lGlycans, canvas.getBuilderWorkspace(), format);
				}
			});
		}
		// Import sequence from file
		for ( SequenceFormat format : this.m_mapSecFormatToButtonForImport.keySet() ) {
			btn = this.m_mapSecFormatToButtonForImport.get(format);
			btn.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					List<Glycan> lGlycans = GlycanIOUtils.loadGlycans(format, canvas.getBuilderWorkspace().getDefaultMassOptions());
					if ( lGlycans == null )
						return;
					canvas.addGlycans(lGlycans);
					canvas.updateView();
				}
			});
		}
		// Import sequence from text
		this.m_importText.addImportListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( m_importText.getSequence().isEmpty() ) {
					MessageBox box = new MessageBox(getForm().getShell(), SWT.OK|SWT.ICON_ERROR);
					box.setMessage("Please enter a sequence.");
					box.open();
					return;
				}
				List<Glycan> lGlycans = GlycanIOUtils.getGlycansFromSequence(
						m_importText.getSequence(), m_importText.getSequenceFormat(),
						canvas.getBuilderWorkspace().getDefaultMassOptions()
					);
				if ( lGlycans == null || lGlycans.isEmpty() )
					return;
				m_importText.clearText();
				canvas.addGlycans(lGlycans);
				canvas.updateView();
			}
		});

		// Database import
		btn = this.m_btnImportDatabase;
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( !confirmSaveBeforeChange(canvas, ((Button)e.widget).getToolTipText()) )
					return;
				canvas.resetDocument();

				GlycanDatabase database = GlycanDatabaseUtils.loadGlycanDatabase();
				if ( database == null)
					return;

				// Load and set database name, version and description
				boolean bOverwrite = true;
				if ( !m_dbInfo.isEmpty() ) {
					MessageBox box = new MessageBox(getForm().getShell(), SWT.YES|SWT.NO|SWT.ICON_WARNING);
					box.setMessage("Do you want to overwrite current database information with imported one?");
					if ( box.open() == SWT.NO)
						bOverwrite = false;
				}
				if ( bOverwrite ) {
					m_dbInfo.setDatabaseName(database.getName());
					m_dbInfo.setDatabaseVersion(database.getVersion());
					m_dbInfo.setDatabaseDescription(database.getDescription());
				}
				// Extract structures
				try {
					List<GlycanInfo> gInfos = new ArrayList<>();
					for ( GlycanStructure gStructure : database.getStructures() ) {
						Glycan glycan = GWSParser.fromString(
								gStructure.getGWBSequence(),
								canvas.getBuilderWorkspace().getDefaultMassOptions()
							);
						GlycanInfo gInfo = new GlycanInfo(glycan);
						gInfo.setID(gStructure.getId());
						gInfos.add(gInfo);
					}
					canvas.getBuilderWorkspace().getGlycanDocument().addStructures(gInfos);
					canvas.loadDocument();
					canvas.updateView();
				} catch (Exception e1) {
					MessageBox box = new MessageBox(getForm().getShell(), SWT.OK|SWT.ICON_ERROR);
					box.setMessage("A sequence can not be imported correctly.");
					box.open();
					LogUtils.report(e1);
				}
			}
		});

		// Database export
		btn = this.m_btnExportDatabase;
		btn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ( canvas.getBuilderWorkspace().getGlycanDocument().isEmpty() ) {
					MessageBox box = new MessageBox(getForm().getShell(), SWT.OK|SWT.ICON_WARNING);
					box.setMessage("No glycan in this canvas.");
					box.open();
					return;
				}
				if ( canvas.colorDuplicatedIDs() ) {
					MessageBox box = new MessageBox(getForm().getShell(), SWT.OK|SWT.ICON_ERROR);
					box.setText("Duplicated IDs are detected");
					box.setMessage("Please specify a unique or empty ID to each glycan.");
					box.open();
					return;
				}
				DatabaseExportDialog dlg = new DatabaseExportDialog(
						getForm().getShell(),
						canvas.getBuilderWorkspace().getGlycanDocument()
					);
				if ( !m_dbInfo.isEmpty() )
					dlg.setDatabaseInformation(m_dbInfo);
				dlg.open();
			}

		});
	}

	/**
	 * Confirm user wants to save structures
	 * @param canvas
	 * @return {@code true} if the confirmation is user wants to save the structure
	 */
	private boolean confirmSaveBeforeChange(GlycanCanvasComposite canvas, String title) {
		if ( !this.m_bIsChanged )
			return true;
		// Do not confirm if no glycan in the canvas
		if ( canvas.getAllGlycans().isEmpty() )
			return true;
		MessageBox box = new MessageBox(getForm().getShell(), SWT.YES|SWT.NO|SWT.CANCEL|SWT.ICON_QUESTION);
		box.setText(title);
		box.setMessage("Save changes to structures?");
		int ret = box.open();
		if ( ret == SWT.CANCEL )
			return false;
		if ( ret == SWT.YES )
			canvas.getBuilderWorkspace().getGlycanDocument().save();
		return true;
	}

	private List<Glycan> getAllGlycans(GlycanCanvasComposite canvas) {
		List<Glycan> lGlycans = new ArrayList<>();
		for ( GlycanInfo info : canvas.getBuilderWorkspace().getGlycanDocument().getStructures() ) {
			Glycan glycan = info.getGlycan();
			if ( glycan == null )
				continue;
			lGlycans.add(glycan);
		}
		return lGlycans;
	}

	private List<Glycan> getAllGlycansFromCurrentCanvas(GlycanCanvasComposite canvas) {
		// Extract glycans
		List<Glycan> lGlycans = canvas.getAllGlycans();
		if ( lGlycans.isEmpty() ) {
			MessageBox box = new MessageBox(canvas.getControl().getShell(), SWT.OK|SWT.ICON_WARNING);
			box.setMessage("No glycan in this canvas.");
			box.open();
			return null;
		}
		return lGlycans;
	}

	private List<Glycan> getSelectedGlycansFromCanvas(GlycanCanvasComposite canvas) {
		if ( canvas.hasSelection() ) {
			// Returns selected glycans if residues are selected through multiple glycans
			if ( canvas.getSelectedGlycanLabels().size() > 1 )
				return canvas.getSelectedGlycans();
			// Returns selected glycan if residues are selected only in the glycan
			List<Glycan> lGlycan = new ArrayList<>();
			lGlycan.add(canvas.getSelectedGlycanLabels().get(0).getGlycan());
			return lGlycan;
		}
		MessageBox box = new MessageBox(canvas.getControl().getShell(), SWT.OK|SWT.ICON_WARNING);
		box.setMessage("No glycan is selected.");
		box.open();
		return null;
	}
}
