package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.Linkage;
import org.eurocarbdb.application.glycanbuilder.PositionManager;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.BBoxManager;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.GlycanRendererSWT;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.SWTColors;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanDocument;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanInfo;
import org.grits.toolbox.tools.glycanbuilder.core.structure.utils.GlycanSelectionState;
import org.grits.toolbox.tools.glycanbuilder.core.structure.utils.ResidueOperationUtils;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;
import org.grits.toolbox.tools.glycanbuilder.widgets.dialog.MassOptionsDialog;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.ClipUtils;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.MouseUtils;

/**
 * Class for drawing glycan and selecting its residues on a Label.
 * When some of the residues in a glycan is selected, the residues will be highlighted.
 * This is designed as a wrapper class of Label because Label class is not intended to be subclassed.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class GlycanLabel implements GlycanCanvasInterface {

	private Label m_label;
	private Text m_txtDatabaseID;

	private BuilderWorkspaceSWT m_bws;
	private GlycanDocument m_doc;

	private Glycan m_glycan;
	private GlycanInfo m_gInfo;

	private List<Residue> m_lResSelected;
	private Residue m_resCurrent;
	private List<Linkage> m_lLinkSelected;
	private Linkage m_linkCurrent;
	private PositionManager posManager;
	private BBoxManager bboxManager;

	private GlycanSelectionState m_gStateOld;

	/**
	 * Constructor with Glycan.
	 * @param parent
	 * @param bw
	 * @param glycan
	 */
	public GlycanLabel(Composite parent, BuilderWorkspaceSWT bw, GlycanInfo glycan) {
		this(parent, bw);
		setGlycan(glycan);
	}

	/**
	 * Default constructor
	 * @param parent Parent Composite
	 * @param bw BuilderWorkspaceSWT to be used to draw glycan
	 * @param glycan Glycan to be draw on this label
	 */
	public GlycanLabel(Composite parent, BuilderWorkspaceSWT bw) {
		this.m_bws = bw;
		this.m_doc = bw.getGlycanDocument();

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		container.setBackground(parent.getBackground());

		this.m_label = new Label(container, SWT.NONE);
		this.m_label.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
		this.m_label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		this.m_label.setBackground(container.getBackground());
		this.initSelection();

		this.setDefaultListeners();
	}

	protected void setDefaultListeners() {
		GlycanCanvasListenerProvider.addListenersToCanvas(this);
	}

	private void showDatabaseID() {
		if ( this.m_txtDatabaseID != null && !this.m_txtDatabaseID.isDisposed() ) {
			
			this.m_txtDatabaseID.setText(this.m_gInfo.getID());
			return;
		}

		Composite dbIDComp = new Composite(this.m_label.getParent(), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = this.m_bws.getGraphicOptions().MARGIN_LEFT / 2 + 3;
		dbIDComp.setLayout(layout);

		// Database ID (Label + Text)
		Label lbl = new Label(dbIDComp, SWT.NONE);
		lbl.setText("Glycan ID: ");

		final Text txt = new Text(dbIDComp, SWT.NONE);
		txt.setText(this.m_gInfo.getID());
		GridDataFactory.fillDefaults().grab(false, false).hint(200, SWT.DEFAULT).applyTo(txt);
		txt.addFocusListener(new FocusListener() {

			private String strOldID;

			@Override
			public void focusGained(FocusEvent e) {
				txt.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
				strOldID = m_gInfo.getID();
			}

			@Override
			public void focusLost(FocusEvent e) {
				// Reset background color
				txt.setBackground(m_label.getBackground());
				if ( strOldID.equals(m_gInfo.getID()) )
					return;

				if ( hasUniqueID() ) {
					m_doc.saveState();
					return;
				}

				// Show message if the ID is duplicated with the other ID
				MessageBox box = new MessageBox(m_label.getShell(), SWT.OK|SWT.ICON_ERROR);
				box.setText("Duplicated ID is detected");
				box.setMessage("Please specify a unique ID or set empty.");
				box.open();

				// Reset to old ID
				m_gInfo.setID(strOldID);
				txt.setText(strOldID);
			}
		});
		txt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				// Save ID to GlycanInfo
				m_gInfo.setID(txt.getText());
			}
		});
		txt.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				// Switches focus to label when enter key is pressed.
				// focusLost() of FocusListener will be triggered after this.
				if ( e.detail == SWT.TRAVERSE_RETURN )
					m_label.forceFocus();
			}
		});
		this.m_txtDatabaseID = txt;

		dbIDComp.moveAbove(this.m_label);
	}

	/**
	 * Returns {@code true} if the database ID is unique.
	 */
	public boolean hasUniqueID() {
		// Always returns true
		return true;
	}

	protected void colorDatabaseID(boolean toColor) {
		if ( this.m_txtDatabaseID == null || this.m_txtDatabaseID.isDisposed() )
			return;
		if ( toColor )
			this.m_txtDatabaseID.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		else
			this.m_txtDatabaseID.setForeground(m_label.getForeground());
	}

	/**
	 * Sets Glycan to this.
	 * @param glycan Glycan to be set
	 */
	public void setGlycan(GlycanInfo glycan) {
		if ( glycan == null )
			return;
		this.initSelection();
		this.m_gInfo = glycan;
		this.m_glycan = glycan.getGlycan();
	}

	public void initSelection() {
		this.m_resCurrent = null;
		this.m_linkCurrent = null;
		this.m_lResSelected = new ArrayList<>();
		this.m_lLinkSelected = new ArrayList<>();
	}

	/**
	 * Disposes this.
	 * @see org.eclipse.swt.widgets.Label#dispose()
	 */
	public void dispose() {
		if ( this.m_label.getImage() != null ) {
			this.m_label.getImage().dispose();
			this.m_label.setImage(null);
		}
		this.m_label.getParent().dispose();
		this.m_label = null;
		this.m_glycan = null;
		this.m_gInfo = null;
		this.m_resCurrent = null;
		this.m_linkCurrent = null;
		this.posManager = null;
		this.bboxManager = null;
	}

	@Override
	public BuilderWorkspaceSWT getBuilderWorkspace() {
		return this.m_bws;
	}

	@Override
	public List<Glycan> getAllGlycans() {
		List<Glycan> lGlycans = new ArrayList<Glycan>();
		lGlycans.add(this.m_glycan);
		return lGlycans;
	}

	@Override
	public List<Glycan> getSelectedGlycans() {
		return this.extractSelection();
	}

	public List<Glycan> extractSelection() {
		return ResidueOperationUtils.extractSelection(this.m_lResSelected, this.m_glycan.getMassOptions());
	}

	public Glycan getGlycan() {
		return this.m_glycan;
	}

	public GlycanInfo getGlycanInfo() {
		return this.m_gInfo;
	}

	/**
	 * Returns current Residue.
	 * @return Residue currently focused ({@code null} if no focused residue)
	 */
	public Residue getCurrentResidue() {
		return this.m_resCurrent;
	}

	/**
	 * Returns current Linkage.
	 * @return Linkage currently focused ({@code null} if no focused linkage)
	 */
	public Linkage getCurrentLinkage() {
		return this.m_linkCurrent;
	}

	/**
	 * Returns selected residues.
	 * @return List of Residues currently selected
	 */
	public List<Residue> getSelectedResidues() {
		return this.m_lResSelected;
	}

	@Override
	public boolean hasSelection() {
		return this.hasSelectedResidues();
	}

	public boolean hasSelectedResidues() {
		if ( !this.m_lResSelected.isEmpty() )
			return true;
		if ( this.m_resCurrent != null )
			return true;
		return false;
	}

	@Override
	public void resetSelection() {
		this.initSelection();
	}

	//----------------
	// Implementations of GlycanCanvasInterface

	@Override
	public Label getControl() {
		return this.m_label;
	}

	@Override
	public boolean addGlycan(Glycan g) {
		// Overwrite as a new Glycan
		this.setGlycan(new GlycanInfo(g));
		return true;
	}

	@Override
	public void undo() {
		if ( this.m_doc.undo() )
			this.loadDocument();
	}

	@Override
	public void redo() {
		if ( this.m_doc.redo() )
			this.loadDocument();
	}

	@Override
	public boolean canCut() {
		return this.hasSelectedResidues();
	}

	@Override
	public boolean canCopy() {
		return this.hasSelectedResidues();
	}

	@Override
	public boolean canPaste() {
		if ( this.getCurrentResidue() != null )
			return true;
		return false;
	}

	@Override
	public boolean canDelete() {
		return this.hasSelectedResidues();
	}

	@Override
	public void cut() {
		this.copy();
		this.delete();
	}

	@Override
	public void copy() {
		ClipUtils.copyToClipboard(this.extractSelection(), this.m_bws);
	}

	@Override
	public void paste() {
		List<Glycan> lGlycans = ClipUtils.getGlycansFromClipboard(this.m_glycan.getMassOptions());
		// Add glycans to current residue
		ResidueOperationUtils.addGlycans(this.m_resCurrent, this.m_glycan, lGlycans);
		this.saveDocument();
	}

	@Override
	public void delete() {
		this.removeResidues();
	}

	/**
	 * Add the given glycans to current residue.
	 * @param lGlycans
	 * @return {@code true} if the addition succeed, otherwise {@code false}.
	 */
	public boolean addGlycansToCurrentResidue(List<Glycan> lGlycans) {
		if ( !ResidueOperationUtils.addGlycans(this.m_resCurrent, this.m_glycan, lGlycans) )
			return false;
		this.saveDocument();
		return true;
	}

	/**
	 * Remove selected Residues.
	 * @see ResidueOperationUtils#removeResidues(Glycan, List)
	 */
	public void removeResidues() {
		if ( this.m_resCurrent != null && !this.m_lResSelected.contains(this.m_resCurrent) )
			this.m_lResSelected.add(this.m_resCurrent);
		this.m_resCurrent = null;
		// Change current residue to parent residue if only one residue is selected.
		if ( this.m_lResSelected.size() == 1 && this.m_lResSelected.get(0).hasParent() )
			this.m_resCurrent = this.m_lResSelected.get(0).getParent();
		if ( !ResidueOperationUtils.removeResidues(this.m_glycan, this.m_lResSelected) )
			return;
		this.m_lResSelected.clear();
		this.saveDocument();
	}

	@Override
	public boolean addResidue(Residue res) {
		return addResidueToCurrentResidue(res);
	}

	/**
	 * Adds a Residue with the given Residue as a child of current Residue.
	 * Returns false if no residue is selected.
	 * @param res Residue to add
	 * @return true if the addition is successful, otherwise false
	 */
	public boolean addResidueToCurrentResidue(Residue res) {
		if ( this.m_resCurrent == null )
			return false;
		if ( !this.m_resCurrent.addChild(res) )
			return false;

		this.setSelection(res);
		this.saveDocument();
		return true;
	}

	/**
	 * Insert the given Residue between the current residue and its parent.
	 * @param res Residue to insert
	 * @return true if the insertion is successful, otherwise false
	 * @see ResidueOperationUtils#insertResidueBefore(Residue, boolean, Residue)
	 */
	public boolean insertResidueBefore(Residue res) {
		if ( this.m_resCurrent == null )
			return false;
		if ( this.m_resCurrent.getParent() == null )
			return false;

		Residue resInserted
			= ResidueOperationUtils.insertResidueBefore(this.m_resCurrent, this.m_glycan.isComposition(), res);
		if ( resInserted == null )
			return false;
		this.m_resCurrent = resInserted;
		this.saveDocument();
		return true;
	}

	/**
	 * Insert the given Residue to the current linkage.
	 * @param res Residue to insert
	 * @return true if the insertion is successful, otherwise false
	 * @see ResidueOperationUtils#insertResidueBefore(Residue, boolean, Residue)
	 */
	public boolean insertResidue(Residue res) {
		if ( this.m_linkCurrent == null )
			return false;
		Residue resInserted
		= ResidueOperationUtils.insertResidueBefore(this.m_linkCurrent.getChildResidue(), this.m_glycan.isComposition(), res);
		if ( resInserted == null )
			return false;
		this.m_resCurrent = resInserted;
		this.saveDocument();
		return true;
	}

	/**
	 * Changes the type of current residue to the given one.
	 * @param resType ResidueType to set
	 * @return true if the change is successful, otherwise false
	 */
	public boolean changeResidueType(ResidueType resType) {
		if ( !ResidueOperationUtils.changeResidueType(this.m_resCurrent, resType, this.m_glycan.isComposition()) )
			return false;
		this.saveDocument();
		return true;
	}

	public boolean canAddBracket() {
		if ( this.m_glycan.getBracket() != null )
			return false;
		return true;
	}

	/**
	 * Add bracket to the glycan. Do nothing if the glycan already have bracket or is composition.
	 */
	public void addBracket() {
		if ( this.m_glycan.isComposition() )
			return;
		Residue bracket = this.m_glycan.addBracket();
		if ( bracket == null )
			return;
		this.setSelection(bracket);
		this.saveDocument();
	}

	public void createRepitition() {
		if ( !this.hasSelectedResidues() )
			return;
		try {
			ResidueOperationUtils.createRepetition(null, this.m_lResSelected);
			this.saveDocument();
		} catch (Exception e1) {
			// Show error message
			MessageDialog.openError(null, "Error while creating the repeating unit", e1.getMessage());
		}
	}

	public void changeReducingEndType() {
		MassOptionsDialog dlg = new MassOptionsDialog(this.m_label.getShell(), this.m_glycan.getMassOptions());
		dlg.open();
		this.m_glycan.setReducingEndType(this.m_glycan.getMassOptions().getReducingEndType());
		this.saveDocument();
	}

	//-----------------
	// For GlycanDocument

	public void loadDocument() {
		// Reset canvas
		this.initSelection();
		this.setGlycan( this.m_doc.getStructures().getFirst() );
	}

	public void saveDocument() {
		this.m_gInfo.setGlycan(this.m_glycan);
		this.m_doc.saveState();
	}

	//----------------
	// Selection operations

	@Override
	public void performResidueSelection(MouseEvent e) {
		Point p = new Point( e.x, e.y );

		Residue res = this.bboxManager.getNodeAtPoint(p);
		if (res != null) {
			if (MouseUtils.isSelectTrigger(e)) {
				this.setSelection(res);
			} else if (MouseUtils.isAddSelectTrigger(e)) {
				this.addOrRemoveSelection(res);
			} else if (MouseUtils.isSelectAllTrigger(e)) {
				this.addSelectionPathTo(res);
			}
		} else {
			Linkage lin = this.bboxManager.getLinkageAtPoint(p);
			if ( lin != null ) {
				if ( MouseUtils.isSelectTrigger(e)
				  || MouseUtils.isAddSelectTrigger(e)
				  || MouseUtils.isSelectAllTrigger(e) )
					this.setSelection(lin);
			} else {
				this.initSelection();
			}
		}
	}

	/**
	 * Set only the given Residue to residue selection and reset the other selections
	 * @param res Residue to set selection
	 */
	public void setSelection(Residue res) {
		if ( res == null )
			return;

		this.initSelection();
		if ( this.bboxManager != null )
			this.m_lResSelected.addAll(this.bboxManager.getLinkedResidues(res));
		this.m_lResSelected.add(res);
		this.m_resCurrent = res;
	}

	/**
	 * Set only the given Linkage to linkage selection and reset the other selections.
	 * @param lin Linkage to set selection
	 */
	public void setSelection(Linkage lin) {
		if ( lin == null )
			return;

		this.initSelection();
		this.m_lLinkSelected.add(lin);
		this.m_linkCurrent = lin;
	}

	/**
	 * Add the given Residue to the residue selection.
	 * If the Residue already contains in the residue selection, remove the Residue from the selection.
	 * @param res Residue to add/remove
	 */
	public void addOrRemoveSelection(Residue res) {
		if ( res == null )
			return;

		if ( !this.m_lResSelected.contains(res) ) {
			if ( this.bboxManager != null )
				this.m_lResSelected.addAll(this.bboxManager.getLinkedResidues(res));
			this.m_lResSelected.add(res);
			this.m_resCurrent = res;
		} else {
			// Remove the Residue from list if already selected, otherwise add it to the list
			if ( this.bboxManager != null )
				this.m_lResSelected.removeAll(this.bboxManager.getLinkedResidues(res));
			this.m_lResSelected.remove(res);
			// Change current residue to last selected one. If no residue in the list it will be null.
			if ( !this.m_lResSelected.isEmpty() )
				this.m_resCurrent = this.m_lResSelected.get( this.m_lResSelected.size()-1 );
			else
				this.m_resCurrent = null;
		}

		this.m_linkCurrent = null;
		this.selectConnectedLinkages();
	}

	/**
	 * Add the given Residue to the residue selection.
	 * If there are Residue(s) between the Residue and previously selected one, select them, too.
	 * @param res Residue to add
	 */
	public void addSelectionPathTo(Residue res) {
		if ( res == null )
			return;

		if (this.m_resCurrent == null) {
			this.m_lResSelected.add(res);
			if ( this.bboxManager != null )
				this.m_lResSelected.addAll(this.bboxManager.getLinkedResidues(res));
		} else {
			for (Residue r : ResidueOperationUtils.getPath(this.m_resCurrent, res)) {
				if ( this.m_lResSelected.contains(r) )
					continue;
				this.m_lResSelected.add(r);
				if ( this.bboxManager != null )
					this.m_lResSelected.addAll(this.bboxManager.getLinkedResidues(res));
			}
		}
		this.m_resCurrent = res;

		this.m_linkCurrent = null;
		this.selectConnectedLinkages();
	}

	private void selectConnectedLinkages() {
		this.m_lLinkSelected.clear();
		for ( Residue res1 : this.m_lResSelected ) {
			for ( Residue res2 : this.m_lResSelected ) {
				if ( res1.equals(res2) )
					continue;
				if ( res2.getParent() == null || !res2.getParent().equals(res1) )
					continue;
				this.m_lLinkSelected.add( res2.getParentLinkage() );
			}
		}
	}

	/**
	 * Selects all residues and linkages in the glycan
	 */
	public void selectAllResidues() {
		this.initSelection();
		this.m_lResSelected.addAll(this.m_glycan.getAllResidues());
		this.selectConnectedLinkages();
	}

	/**
	 * Selects all glycans and residues when "Ctrl+A" is pressed.
	 */
	@Override
	public void selectAll() {
		this.selectAllResidues();
	}

	//------------
	// Rendering glycan image

	@Override
	public void updateView(boolean force) {
		this.redrawGlycan(force);
	}

	public void redrawGlycan(boolean force) {
		// Show database ID
		if ( this.m_bws.getGraphicOptions().SHOW_ID )
			this.showDatabaseID();
		else if (this.m_txtDatabaseID != null && !this.m_txtDatabaseID.isDisposed() )
			this.m_txtDatabaseID.getParent().dispose();

		GlycanSelectionState gState = new GlycanSelectionState(this.m_glycan);
		if ( !m_lResSelected.isEmpty() ) {
			gState.getSelectedResidues().addAll(m_lResSelected);
		} else if ( m_resCurrent != null ) {
			gState.getSelectedResidues().add(m_resCurrent);
		}
		if ( !m_lLinkSelected.isEmpty() ) {
			gState.getSelectedLinkages().addAll(m_lLinkSelected);
		} else if ( m_linkCurrent != null ) {
			gState.getSelectedLinkages().add(m_linkCurrent);
		}

		if ( force || this.isChanged(gState) )
			this.redrawGlycan(gState.getSelectedResidues(), gState.getSelectedLinkages());
		this.m_gStateOld = gState;
	}

	protected boolean isChanged(GlycanSelectionState gStateNew) {
		if ( this.m_gStateOld == null )
			return true;
		return this.m_gStateOld.equals(gStateNew);
	}

	public void redrawGlycan(HashSet<Residue> setSelectedResidues, HashSet<Linkage> setSelectedLinkages) {
		// Dispose old image
		if ( this.m_label.getImage() != null )
			this.m_label.getImage().dispose();

		this.posManager = new PositionManager();
		this.bboxManager = new BBoxManager();
		Image img = drawGlycanImage(setSelectedResidues, setSelectedLinkages, this.posManager, this.bboxManager);
		this.m_label.setImage(img);
		this.m_label.pack();
	}

	private Image drawGlycanImage(HashSet<Residue> setSelectedResidue, HashSet<Linkage> setSelectedLinkage,
			PositionManager posManager , BBoxManager bboxManager) {

		GlycanRendererSWT gr = this.m_bws.getGlycanRenderer();
		boolean show_mass = gr.getGraphicOptions().SHOW_MASSES;
		boolean show_redend = gr.getGraphicOptions().SHOW_REDEND;

		int l = gr.getGraphicOptions().MARGIN_LEFT / 2 + 3 ;
		int t = 10;
		int r = 5;
		int b = 5;

		Rectangle bound = gr.computeBoundingBoxes(this.m_glycan, l, t, show_mass, show_redend, posManager, bboxManager);

		Image img = new Image(this.m_label.getDisplay(), bound.width+l+r, bound.height+t+b);
		GC gc = new GC(img);
		gc.setAntialias(SWT.ON);
		// Clear background
		gc.setBackground(this.m_label.getBackground());
		gc.fillRectangle(img.getBounds());
		// Draw selected marker
		if ( this.isSelected() ) {
			Color colSelected = new Color(this.m_label.getDisplay(), SWTColors.GRAY, 0);
			gc.setBackground( colSelected );
			gc.fillRectangle(0, bound.y, 5, bound.height);
			colSelected.dispose();
		}
		Color colorOld = gr.getBackgroundColor();
		gr.setBackgroundColor(this.m_label.getBackground());
		gr.paint(gc, this.m_glycan, setSelectedResidue, setSelectedLinkage, show_mass, show_redend, posManager, bboxManager);
		gr.setBackgroundColor(colorOld);
		gc.dispose();

		return img;
	}

	protected boolean isSelected() {
		return ( this.m_resCurrent != null || this.m_linkCurrent != null );
	}

}
