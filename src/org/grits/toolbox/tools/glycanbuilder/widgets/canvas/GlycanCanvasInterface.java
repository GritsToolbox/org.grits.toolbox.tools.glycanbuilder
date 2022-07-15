package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Control;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;

/**
 * An interface for classes to edit glycan and its component.
 * @see GlycanLabel
 * @see GlycanCanvasComposite
 * @author Masaaki Matsubara
 *
 */
public interface GlycanCanvasInterface {

	// Widget control
	/**
	 * Returns Control of the glycan canvas.
	 * @return Control of the glycan canvas
	 */
	public Control getControl();

	// Residue selection

	/**
	 * Perform the selection of residues (or linkages).
	 * @param e MouseEvent
	 */
	public void performResidueSelection(MouseEvent e);

	/**
	 * Returns the canvas has selected glycan and/or residues.
	 * @return true if the canvas has selection
	 */
	public boolean hasSelection();

	/**
	 * Selects all glycans and their residues.
	 */
	public void selectAll();

	/**
	 * Resets all selection.
	 */
	public void resetSelection();

	/**
	 * Returns all glycans in the canvas.
	 * @return List of all Glycans
	 */
	public List<Glycan> getAllGlycans();

	/**
	 * Returns selected glycans in the canvas.
	 * If some residues in a glycan are partially selected they will be extracted.
	 * @return List of selected Glycans
	 */
	public List<Glycan> getSelectedGlycans();

	// Undo and redo

	/**
	 * Restore the state after a change
	 */
	public void undo();

	/**
	 * Apply again the changes after the state of the underlying document has
	 * been restored
	 */
	public void redo();

	// Common edit commands
	/**
	 * Returns whether cut command can be performed.
	 * @return true if cut command can be performed, otherwise false
	 */
	public boolean canCut();
	/**
	 * Returns whether copy command can be performed.
	 * @return true if copy command can be performed, otherwise false
	 */
	public boolean canCopy();
	/**
	 * Returns whether paste command can be performed.
	 * @return true if paste command can be performed, otherwise false
	 */
	public boolean canPaste();
	/**
	 * Returns whether delete command can be performed.
	 * @return true if delete command can be performed, otherwise false
	 */
	public boolean canDelete();
	/**
	 * Performs cut command
	 */
	public void cut();
	/**
	 * Performs copy command
	 */
	public void copy();
	/**
	 * Performs paste command
	 */
	public void paste();
	/**
	 * Performs delete command
	 */
	public void delete();

	// Edit glycan
	/**
	 * Adds the given Glycan as a new glycan to the canvas.
	 * @param g Glycan to be added
	 * @return true if the addition is succeed, otherwise false
	 */
	public boolean addGlycan(Glycan g);

	/**
	 * Adds the given Residue to the current residue or a new Glycan with the given residue as a root residue.
	 * @param res Residue to be added
	 * @return true if the addition is succeed, otherwise false
	 */
	public boolean addResidue(Residue res);

	/**
	 * Returns BuilderWorkspaceSWT to use glycan rendering.
	 * @return BuilderWorkspaceSWT
	 */
	public BuilderWorkspaceSWT getBuilderWorkspace();

	/**
	 * Update view to reflect current selection
	 * @param force Force updating view anyway
	 */
	public void updateView(boolean force);
}
