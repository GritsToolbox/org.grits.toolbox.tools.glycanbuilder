package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanDocument;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanInfo;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.ClipUtils;

/**
 * Composite class for aligning GlycanLabelForCanvas.
 * GlycanLabelForCanvas class is a content of this.
 * 
 * @see GlycanLabelForCanvas
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class GlycanCanvasComposite extends Composite implements GlycanCanvasInterface {

	private ScrolledComposite m_compositeCanvasParent;
	private Composite m_compositeCanvas;
	private Pager m_pager;
	private boolean m_bShowPager;

	private BuilderWorkspaceSWT m_bws;
	private GlycanDocument m_doc;

	private GlycanLabelForCanvas m_lblCurrent;
	private List<GlycanLabelForCanvas> m_lLabels;
	private List<GlycanLabelForCanvas> m_lSelected;

	private List<MouseListener> m_lMouseListeners;

	public GlycanCanvasComposite(Composite parent) {
		super(parent, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.setLayout(layout);
		this.setBackground( this.getDisplay().getSystemColor(SWT.COLOR_WHITE) );

		this.m_bws = new BuilderWorkspaceSWT(parent.getDisplay());
		this.m_bws.setNotation(GraphicOptionsSWT.NOTATION_SNFG);
		this.m_bws.getGraphicOptions().SHOW_MASSES = true;
		this.m_bws.getGraphicOptions().SHOW_REDEND = true;
		this.m_doc = this.m_bws.getGlycanDocument();

		// Pager settings
		this.m_pager = new Pager();
		this.m_pager.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadDocument();
				updateView(false);
			}
		});
		this.m_bShowPager = false;

		initCanvas();
		initLabels();
		this.m_lMouseListeners = new ArrayList<>();
	}

	/**
	 * Initialize canvas layout and mouse listener
	 */
	protected void initCanvas() {
		this.m_compositeCanvasParent = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		this.m_compositeCanvasParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.m_compositeCanvasParent.setExpandHorizontal(true);
		this.m_compositeCanvasParent.setExpandVertical(true);

		this.m_compositeCanvas = new Composite(this.m_compositeCanvasParent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = Math.max(0, this.m_bws.getGraphicOptions().MARGIN_TOP - 10);
		layout.marginBottom = Math.max(0, this.m_bws.getGraphicOptions().MARGIN_BOTTOM - 5);
		layout.marginLeft = Math.max(0, this.m_bws.getGraphicOptions().MARGIN_LEFT / 2 - 3); // Adjust margin for selected labels
		layout.marginRight = Math.max(0, this.m_bws.getGraphicOptions().MARGIN_RIGHT - 5);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 1;
		layout.verticalSpacing = this.m_bws.getGraphicOptions().MARGIN_TOP;
		this.m_compositeCanvas.setLayout(layout);

		GlycanCanvasListenerProvider.addListenersToCanvas(this);

		this.m_compositeCanvasParent.setContent(m_compositeCanvas);
	}

	public void showPager(boolean bShowPager) {
		this.m_bShowPager = bShowPager;
		this.loadDocument();
	}

	/// 

	/**
	 * Initialize labels
	 */
	private void initLabels() {
		this.initSelection();
		if ( this.m_lLabels != null )
			for ( GlycanLabelForCanvas label : this.m_lLabels )
				label.dispose();
		this.m_lLabels = new ArrayList<>();
	}

	/**
	 * Initialize the selection (selected and current labels)
	 */
	public void initSelection() {
		this.m_lSelected = new ArrayList<>();
		this.m_lblCurrent = null;
	}

	/**
	 * Initialize the selection and all labels
	 */
	public void initAllSelection() {
		this.initSelection();
		for ( GlycanLabelForCanvas label : this.m_lLabels )
			label.initSelection();
	}

	public void dispose() {
		if ( this.isDisposed() )
			return;
		this.m_compositeCanvas.dispose();
		super.dispose();
		this.m_bws = null;
		if ( this.m_lLabels != null )
			for ( GlycanLabelForCanvas l : this.m_lLabels )
				l.dispose();
		this.m_lLabels = null;
		this.m_lblCurrent = null;
	}

	/**
	 * Returns Composite for glycan canvas aligning labels of Glycans.
	 * @return Composite for glycan canvas
	 */
	@Override
	public Composite getControl() {
		return this.m_compositeCanvas;
	}

	public List<GlycanLabelForCanvas> getAllGlycanLabels() {
		// Returns copy of the list
		List<GlycanLabelForCanvas> copy = new ArrayList<>();
		copy.addAll(this.m_lLabels);
		return copy;
	}

	public List<GlycanLabelForCanvas> getSelectedGlycanLabels() {
		// Returns copy of the list
		List<GlycanLabelForCanvas> copy = new ArrayList<>();
		copy.addAll(this.m_lSelected);
		return this.m_lSelected;
	}

	/**
	 * Color glycan database IDs duplicating the other ID.
	 * @return {@code true} if there is colored ID
	 */
	public boolean colorDuplicatedIDs() {
		boolean bHasDuplicatedID = false;
		for ( GlycanLabelForCanvas l : this.getAllGlycanLabels() ) {
			boolean bHasUniqueID = l.hasUniqueID();
			l.colorDatabaseID(!bHasUniqueID);
			bHasDuplicatedID |= !bHasUniqueID;
		}
		return bHasDuplicatedID;
	}

	/**
	 * Add the given MouseListener to both canvas and glycan labels.
	 * @param listener MouseListner to be added to canvas and glycan labels.
	 */
	public void addCanvasMouseListner(MouseListener listener) {
		if ( listener == null )
			return;
		this.m_lMouseListeners.add(listener);
		this.m_compositeCanvas.addMouseListener(listener);
		for ( GlycanLabelForCanvas lbl : this.m_lLabels )
			lbl.getControl().addMouseListener(listener);
	}

	public void removeCanvasMouseListener(MouseListener listener) {
		if ( listener == null || !this.m_lMouseListeners.contains(listener) )
			return;
		this.m_lMouseListeners.remove(listener);
		this.m_compositeCanvas.removeMouseListener(listener);
		for ( GlycanLabelForCanvas lbl : this.m_lLabels )
			lbl.getControl().removeMouseListener(listener);
	}

	/**
	 * Returns BuilderWorkspaceSWT for drawing glycans.
	 * @return BuilderWorkspaceSWT for drawing glycans
	 */
	public BuilderWorkspaceSWT getBuilderWorkspace() {
		return this.m_bws;
	}

	/// Residue selection methods
	@Override
	public void performResidueSelection(MouseEvent e) {
		if ( e.button == 3 )
			return;
		this.initAllSelection();
	}

	public void setSelection(GlycanLabelForCanvas label) {
		this.initAllSelection();
		this.addSelection(label);
	}

	public void addOrRemoveSelection(GlycanLabelForCanvas label) {
		if ( !this.m_lSelected.contains(label) ) {
			this.addSelection(label);
		} else if ( !label.hasSelectedResidues() ) {
			this.m_lSelected.remove(label);
			if ( !this.m_lSelected.isEmpty() )
				this.m_lblCurrent = this.m_lSelected.get( this.m_lSelected.size()-1 );
			else
				this.m_lblCurrent = null;
		}
	}

	public void addSelection(GlycanLabelForCanvas label) {
		if ( this.m_lSelected.contains(label) )
			return;
		this.m_lSelected.add(label);
		this.m_lblCurrent = label;
	}

	@Override
	public void selectAll() {
		this.initSelection();
		for ( GlycanLabelForCanvas label : this.m_lLabels ) {
			this.m_lSelected.add(label);
			label.selectAllResidues();
		}
	}

	@Override
	public void resetSelection() {
		this.initAllSelection();
	}

	@Override
	public boolean hasSelection() {
		for ( GlycanLabelForCanvas l : this.getAllGlycanLabels() ) {
			if ( l.hasSelectedResidues() )
				return true;
		}
		if ( !this.getSelectedGlycanLabels().isEmpty() )
			return true;
		return false;
	}

	@Override
	public List<Glycan> getAllGlycans() {
		List<Glycan> t_lGlycan = new ArrayList<>();
		for ( GlycanLabelForCanvas lbl : this.m_lLabels )
			t_lGlycan.add(lbl.getGlycan());
		return t_lGlycan;
	}

	@Override
	public List<Glycan> getSelectedGlycans() {
		List<Glycan> lClonedGlycans = new ArrayList<>();
		for ( GlycanLabelForCanvas label : this.m_lSelected ) {
			if ( !label.hasSelectedResidues() ) {
				lClonedGlycans.add(label.getGlycan().clone());
				continue;
			}
			lClonedGlycans.addAll( label.extractSelection() );
		}
		return lClonedGlycans;
	}

	/**
	 * Adds glycan with the given Residue as a root.
	 * @param resRoot Residue to be a root of glycan
	 */
	public boolean addGlycan(Residue resRoot) {
		Glycan gNew = new Glycan(resRoot, true, this.m_bws.getDefaultMassOptions());
		this.addGlycan(gNew);
		// Set root residue as selection
		if ( this.m_lblCurrent != null )
			this.m_lblCurrent.setSelection( this.m_lblCurrent.getGlycan().getRoot(false) );
		return true;
	}

	/**
	 * Adds glycan.
	 * @param glycan Glycan
	 * @returns true if the glycan can be added successfully
	 */
	public boolean addGlycan(Glycan glycan) {
		if ( glycan == null )
			return false;
		this.m_doc.addStructure( this.correctGlycan(glycan) );
		this.loadDocument();
		return true;
	}

	public boolean addGlycans(List<Glycan> lGlycans) {
		if ( lGlycans == null )
			return false;
		List<GlycanInfo> toAdd = new ArrayList<>();
		for ( Glycan glycan : lGlycans ) {
			if ( glycan == null )
				continue;
			toAdd.add( this.correctGlycan(glycan) );
		}
		this.m_doc.addStructures(toAdd);
		this.loadDocument();
		return true;
	}

	private GlycanInfo correctGlycan(Glycan glycan) {
		if ( glycan == null )
			return null;
		if ( !glycan.getRoot().isReducingEnd() )
			glycan = new Glycan(glycan.getRoot(), true, glycan.getMassOptions());
		return new GlycanInfo(glycan);
	}

	protected void addGlycanLabel(GlycanInfo glycan) {
		GlycanLabelForCanvas label = this.getNewGlycanLabel(glycan);

		for ( MouseListener listener : this.m_lMouseListeners )
			label.getControl().addMouseListener(listener);
		this.m_lLabels.add(label);
		this.m_lblCurrent = label;
	}

	protected GlycanLabelForCanvas getNewGlycanLabel(GlycanInfo glycan) {
		return new GlycanLabelForCanvas(this, glycan);
	}

	/**
	 * Adds the given Residue as a child of current Residue or as a root Residue of new Glycan.
	 * @param res Residue to be a child of current Residue or a root Residue of new Glycan
	 * @return true if the given Residue is added as a child of current Residue, false if as a root Residue of new Glycan
	 */
	public boolean addResidue(Residue res) {
		// Add selected residue to current residue
		if (this.m_lblCurrent != null && this.m_lblCurrent.addResidueToCurrentResidue(res) ) {
			return true;
		}

		// Otherwise add glycan having the residue as a root
		this.addGlycan(res);
		return true;
	}

	/**
	 * Sets current GlycanLabelForCanvas.
	 * @param label GlycanLabelForCanvas
	 */
	public void setCurrentGlycanLabel(GlycanLabelForCanvas label) {
		if ( label != null && !this.m_lLabels.contains(label) )
			this.m_lLabels.add(label);
		this.m_lblCurrent = label;
	}

	/**
	 * Returns current GlycanLabelForCanvas.
	 * @return GlycanLabelForCanvas
	 */
	public GlycanLabelForCanvas getCurrentGlycanLabel() {
		return m_lblCurrent;
	}

	/**
	 * Removes the given GlycanLabelForCanvas from the list.
	 * @param label GlycanLabelForCanvas to be removed
	 */
	public void removeGlycanlabel(GlycanLabelForCanvas label) {
		if ( !this.m_lLabels.contains(label) )
			return;
		this.m_lLabels.remove(label);
		if ( this.m_lSelected.contains(label) )
			this.m_lSelected.remove(label);
		if ( this.m_lblCurrent != null && this.m_lblCurrent.equals(label) )
			this.m_lblCurrent = null;
		label.dispose();
	}

	//-----------------
	// For GlycanDocument

	public void loadDocument() {
		int nStructure = this.m_doc.getStructures().size();
		if ( !this.m_bShowPager && nStructure == 0 ) {
			// Clear labels
			this.initLabels();
			if ( !this.m_pager.isDisposed() )
				this.m_pager.dispose();
			return;
		}
		this.initSelection();
		// Extract glycans to show based on page number
		this.m_pager.setNumberOfTotalElements(nStructure);
		List<GlycanInfo> lGlycansToShow = new ArrayList<>();
		if ( this.m_bShowPager || nStructure > this.m_pager.getNumberOfElementsPerPage() ) {
			this.m_pager.updatePager(this);
			int iCurrentPage = this.m_pager.getCurrentPageNumber();
			int fromIndex = (iCurrentPage-1) * this.m_pager.getNumberOfElementsPerPage();
			int toIndex = Math.min( iCurrentPage * this.m_pager.getNumberOfElementsPerPage(), nStructure);
			lGlycansToShow.addAll(this.m_doc.getStructures().subList(fromIndex, toIndex));
		} else {
			if ( !this.m_pager.isDisposed() ) {
				this.m_pager.dispose();
				this.layout();
			}
			lGlycansToShow = this.m_doc.getStructures();
		}
		// Show glycans
		int i=0;
		for ( GlycanInfo glycan : lGlycansToShow ) {
			if ( i < this.m_lLabels.size() )
				// Overwrite glycan to exist label
				this.m_lLabels.get(i).setGlycan(glycan);
			else
				this.addGlycanLabel(glycan);
			i++;
		}
		// Trim extra labels
		while ( this.m_lLabels.size() > i )
			this.removeGlycanlabel(this.m_lLabels.get(this.m_lLabels.size()-1));
	}

	public void saveDocument() {
		this.m_doc.saveState();
	}

	public void resetDocument() {
		this.m_doc.reset();
		this.loadDocument();
	}

	//------------
	// Implementation of interface

	@Override
	public void undo() {
		if ( this.m_doc.undo() )
			loadDocument();
	}

	@Override
	public void redo() {
		if ( this.m_doc.redo() )
			loadDocument();
	}

	@Override
	public boolean canCut() {
		return this.hasSelection();
	}

	@Override
	public boolean canCopy() {
		return this.hasSelection();
	}

	@Override
	public boolean canPaste() {
		return ClipUtils.canGetGlycanFromClipboard();
	}

	@Override
	public boolean canDelete() {
		return this.hasSelection();
	}

	@Override
	public void cut() {
		this.copy();
		this.delete();
	}

	@Override
	public void copy() {
		ClipUtils.copyToClipboard(this.getSelectedGlycans(), this.m_bws);
	}

	@Override
	public void paste() {
		// Load glycans from clipboard
		List<Glycan> lGlycans = ClipUtils.getGlycansFromClipboard(this.m_bws.getDefaultMassOptions());
		if ( lGlycans.isEmpty() )
			return;
		// Try to add the glycans from clipboard to the current residue
		if ( this.m_lblCurrent == null || !this.m_lblCurrent.addGlycansToCurrentResidue(lGlycans) ) {
			// Add as new glycans
			this.addGlycans(lGlycans);
		}
	}

	@Override
	public void delete() {
		// Check labels having selected residues
		boolean hasSelection = false;
		for ( GlycanLabelForCanvas l : this.getAllGlycanLabels() ) {
			if ( l.hasSelectedResidues() ) {
				hasSelection = true;
				break;
			}
		}
		List<GlycanInfo> lToBeRemoved = new ArrayList<>();
		for ( GlycanLabelForCanvas l : this.getAllGlycanLabels() ) {
			// Remove selected labels when every labels has no selection
			if ( !hasSelection && this.getSelectedGlycanLabels().contains(l) ) {
				lToBeRemoved.add(l.getGlycanInfo());
				continue;
			}
			if ( !l.hasSelectedResidues() )
				continue;
			l.removeResidues();
			if ( l.getGlycan().isEmpty() )
				lToBeRemoved.add(l.getGlycanInfo());
		}
		this.m_doc.removeStructures(lToBeRemoved);
		this.loadDocument();
	}

	//----------
	// Rendering glycan image

	@Override
	public void updateView(boolean force) {
		this.redrawGlycans(force);
		this.resetSize();
		// Focus to the canvas composite to use key binding
		this.m_compositeCanvas.forceFocus();
	}

	public void updateView() {
		this.updateView(true);
	}

	/**
	 * Redraw all glycans.
	 */
	private void redrawGlycans(boolean force) {
		for ( GlycanLabelForCanvas l : this.m_lLabels )
			l.redrawGlycan(force);
	}

	/**
	 * Resets minimum client area size.
	 */
	private void resetSize() {
		// The preferred size must be set to ScrolledComposite before set to canvas composite, otherwise no scroll bar
		Point size = m_compositeCanvas.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		this.m_compositeCanvasParent.setMinSize(size);
		m_compositeCanvas.setSize(size);
	}
}
