package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.grits.toolbox.tools.glycanbuilder.core.structure.document.GlycanInfo;
import org.grits.toolbox.tools.glycanbuilder.core.structure.utils.GlycanSelectionState;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.MouseUtils;

/**
 * {@link GlycanLabel} for using as a component of GlycanCanvasComposite.
 * @see GlycanCanvasComposite
 * @author Masaaki Matsubara
 *
 */
public class GlycanLabelForCanvas extends GlycanLabel {

	private GlycanCanvasComposite m_parentCanvas;

	private boolean m_bWasSelected;

	/**
	 * Constructor as a child of GlycanCanvasComposite
	 * @param parent GlycanCanvasComposite
	 * @param glycan
	 */
	public GlycanLabelForCanvas(GlycanCanvasComposite parent, GlycanInfo glycan) {
		super(parent.getControl(), parent.getBuilderWorkspace());
		this.m_parentCanvas = parent;
		setGlycan(glycan);
	}

	@Override
	protected void setDefaultListeners() {
		GlycanCanvasListenerProvider.addListenersToCanvas(this);
	}

	public boolean hasUniqueID() {
		return !this.getBuilderWorkspace().getGlycanDocument().isDuplicatedID(this.getGlycanInfo());
	}

	@Override
	public void performResidueSelection(MouseEvent e) {
		if (MouseUtils.isSelectTrigger(e)) {
			this.m_parentCanvas.setSelection(this);
		} else if (MouseUtils.isAddSelectTrigger(e)) {
			this.m_parentCanvas.addOrRemoveSelection(this);
		} else if (MouseUtils.isSelectAllTrigger(e)) {
			this.m_parentCanvas.addSelection(this);
		}

		// Do super
		super.performResidueSelection(e);
	}

	@Override
	public void selectAll() {
		this.m_parentCanvas.selectAll();
	}

	@Override
	public List<Glycan> getAllGlycans() {
		return this.m_parentCanvas.getAllGlycans();
	}

	@Override
	public List<Glycan> getSelectedGlycans() {
		return this.m_parentCanvas.getSelectedGlycans();
	}

	@Override
	public boolean hasSelection() {
		return this.m_parentCanvas.hasSelection();
	}

	@Override
	public boolean addGlycan(Glycan glycan) {
		return this.m_parentCanvas.addGlycan(glycan);
	}

	@Override
	public boolean addResidue(Residue res) {
		return this.m_parentCanvas.addResidue(res);
	}

	@Override
	protected boolean isSelected() {
		if ( this.m_parentCanvas.getSelectedGlycanLabels().contains(this) )
			return true;
//		GlycanLabelForCanvas current = this.m_parentCanvas.getCurrentGlycanLabel();
//		if ( current != null && current.equals(this) )
//			return true;
		return super.isSelected();
	}

//	@Override
//	public void loadDocument() {
//		this.m_parentCanvas.loadDocument();
//	}
//
//	@Override
//	public void saveDocument() {
//		this.m_parentCanvas.saveDocument();
//	}
//
	@Override
	public void undo() {
		this.m_parentCanvas.undo();
	}

	@Override
	public void redo() {
		this.m_parentCanvas.redo();
	}

	@Override
	public boolean canCut() {
		return this.m_parentCanvas.canCut();
	}

	@Override
	public boolean canCopy() {
		return this.m_parentCanvas.canCopy();
	}

	@Override
	public boolean canPaste() {
		return this.m_parentCanvas.canPaste();
	}

	@Override
	public boolean canDelete() {
		return this.m_parentCanvas.canDelete();
	}

	@Override
	public void cut() {
		this.m_parentCanvas.cut();
	}

	@Override
	public void copy() {
		this.m_parentCanvas.copy();
	}

	@Override
	public void paste() {
		this.m_parentCanvas.paste();
	}

	@Override
	public void delete() {
		this.m_parentCanvas.delete();
	}

	@Override
	public void updateView(boolean force) {
		this.m_parentCanvas.updateView(force);
	}

	@Override
	protected boolean isChanged(GlycanSelectionState gStateNew) {
		if ( super.isChanged(gStateNew) )
			return true;
		boolean isSelected = this.m_parentCanvas.getSelectedGlycanLabels().contains(this);
		if ( this.m_bWasSelected != isSelected ) {
			this.m_bWasSelected = isSelected;
			return true;
		}
		return false;
	}
}
