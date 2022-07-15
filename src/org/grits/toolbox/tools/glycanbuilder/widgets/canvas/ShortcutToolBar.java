package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.eurocarbdb.application.glycanbuilder.ResidueHistory;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.ResidueDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.utils.ResidueOperationUtils;
import org.grits.toolbox.tools.glycanbuilder.widgets.dialog.RepititionPropertyDialog;
import org.grits.toolbox.tools.glycanbuilder.widgets.dialog.ResiduePropertyDialog;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.GeneralIconProvider;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.GlycanIconProvider;

public class ShortcutToolBar implements ResidueHistory.Listener, GlycanIconProvider.IconUpdateListener{

	private GlycanCanvasComposite m_canvas;
	private ResidueHistory m_resHistory;

	private ToolBar m_bar;

	private Map<ResidueType, ToolItem> m_mapResTypeToItem;

	private ToolItem m_itemSeparator1;
	private List<ToolItem> m_lRecentResItems;
	private Map<ResidueType, ToolItem> m_mapRecentResTypeToItem;

	private ToolItem m_itemBracket;
	private ToolItem m_itemRepeat;
	private ToolItem m_itemProperties;
	private ToolItem m_itemOrientation;

	public ShortcutToolBar(Composite parent, GlycanCanvasComposite canvas) {
		this.m_bar = new ToolBar(parent, SWT.WRAP | SWT.FLAT | SWT.RIGHT);
		this.m_canvas = canvas;
		this.m_resHistory = canvas.getBuilderWorkspace().getResidueHistory();
		this.m_mapResTypeToItem = new HashMap<>();
		this.m_lRecentResItems = new ArrayList<>();
		this.m_mapRecentResTypeToItem = new HashMap<>();
		createItems();
		addMouseListnerToCanvas();
		// Add this to ResidueHistory and to GlycanIconProvider as a listener
		canvas.getBuilderWorkspace().getResidueHistory().addHistoryChangedListener(this);
		GlycanIconProvider.addIconUpdateListener(this);

		final ShortcutToolBar toRemove = this;
		this.m_bar.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				GlycanIconProvider.removeIconUpdateListener(toRemove);
			}
		});
	}

	public ToolItem getChangeOrientationItem() {
		return this.m_itemOrientation;
	}

	private void createItems() {

		ToolItem item;

		// Add common residue buttons
		for ( ResidueType resType : ResidueDictionary.directResidues() ) {
			item = new ToolItem(this.m_bar, SWT.PUSH);
			this.m_mapResTypeToItem.put(resType, item);
			item.setToolTipText(resType.getDescription());
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if ( !ResidueOperationUtils.canAddResidueToCanvas(m_canvas, new Residue(resType)) )
						return;
					m_canvas.addResidue(new Residue(resType));
					m_canvas.updateView();
				}
			});
		}

		this.updateIconImages();

		// Add separator
		new ToolItem(this.m_bar, SWT.SEPARATOR);

		// Bracket
		item = new ToolItem(this.m_bar, SWT.PUSH);
//		item.setText("Add bracket");
		item.setImage(GeneralIconProvider.getBracketIcon());
		item.setToolTipText("Add bracket");
		item.setEnabled(false);

		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( m_canvas.getCurrentGlycanLabel() == null )
					return;
				m_canvas.getCurrentGlycanLabel().addBracket();
				((ToolItem)e.getSource()).setEnabled(false);
				m_canvas.updateView();
			}

		});
		this.m_itemBracket = item;

		// Repeating unit
		item = new ToolItem(this.m_bar, SWT.PUSH);
//		item.setText("Add repeating unit");
		item.setImage(GeneralIconProvider.getRepeatIcon());
		item.setToolTipText("Add repeating unit");
		item.setEnabled(false);

		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( m_canvas.getCurrentGlycanLabel() == null )
					return;
				m_canvas.getCurrentGlycanLabel().createRepitition();
				m_canvas.updateView();
			}
		});
		this.m_itemRepeat = item;

		// Residue properties
		item = new ToolItem(this.m_bar, SWT.PUSH);
//		item.setText("Residue properties");
		item.setImage(GeneralIconProvider.getResiduePropertiesIcon());
		item.setToolTipText("Residue properties");
		item.setEnabled(false);

		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( m_canvas.getCurrentGlycanLabel() == null
				  || m_canvas.getCurrentGlycanLabel().getCurrentResidue() == null )
					return;

				Residue resCurrent = m_canvas.getCurrentGlycanLabel().getCurrentResidue();
				// Create dialog
				Dialog dlg;
				if ( resCurrent.isEndRepetition() )
					dlg = new RepititionPropertyDialog( m_bar.getShell(), resCurrent );
				else
					dlg = new ResiduePropertyDialog( m_bar.getShell(), resCurrent );
				dlg.open();
				m_canvas.updateView();
			}
		});
		this.m_itemProperties = item;

		// Change orientation
		item = new ToolItem(this.m_bar, SWT.PUSH);
//		item.setText("Change orientation");
		item.setImage( GeneralIconProvider.getOrientationIcon(
				m_canvas.getBuilderWorkspace().getGraphicOptions().ORIENTATION
			));
		item.setToolTipText("Change orientation");

		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int orien = m_canvas.getBuilderWorkspace().getGraphicOptions().ORIENTATION;
				if ( orien == GraphicOptionsSWT.RL )
					orien = GraphicOptionsSWT.BT;
				else if ( orien == GraphicOptionsSWT.BT )
					orien = GraphicOptionsSWT.LR;
				else if ( orien == GraphicOptionsSWT.LR )
					orien = GraphicOptionsSWT.TB;
				else if ( orien == GraphicOptionsSWT.TB )
					orien = GraphicOptionsSWT.RL;
				m_canvas.getBuilderWorkspace().getGraphicOptions().ORIENTATION = orien;
				updateOrientationIcon();
				m_canvas.updateView();
			}
		});
		this.m_itemOrientation = item;
	}

	private void addMouseListnerToCanvas() {
		this.m_canvas.addCanvasMouseListner(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateButtonState();
			}
		});
	}

	public void updateResentResidues() {
		// Dispose and clear old residues
		for ( ToolItem item : this.m_lRecentResItems ) {
			item.dispose();
		}
		this.m_lRecentResItems.clear();
		// Dispose separator if no resent residue
		if ( this.m_resHistory.size() == 0 ) {
			if ( this.m_itemSeparator1 != null )
				this.m_itemSeparator1.dispose();
			this.m_bar.layout();
			return;
		}
		// Add recent residues at next of direct residues
		int index = ResidueDictionary.directResidues().size();
		if ( this.m_itemSeparator1 == null )
			this.m_itemSeparator1 = new ToolItem(this.m_bar, SWT.SEPARATOR, index);
		index++;
		for ( String strRes : this.m_resHistory.getRecentResidues() ) {
			ResidueType resType = ResidueDictionary.findResidueType(strRes);
			ToolItem item = new ToolItem(this.m_bar, SWT.PUSH, index++);
			item.setToolTipText(resType.getDescription());
			item.setImage(GlycanIconProvider.getResidueIcon(resType));
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if ( !ResidueOperationUtils.canAddResidueToCanvas(m_canvas, new Residue(resType)) )
						return;
					m_canvas.addResidue(new Residue(resType));
					m_canvas.updateView();
				}
			});

			this.m_lRecentResItems.add(item);
			this.m_mapRecentResTypeToItem.put(resType, item);
		}
		this.m_bar.layout();
	}


	/**
	 * Enables the buttons when they are available.
	 */
	public void updateButtonState() {
		this.m_itemBracket.setEnabled(false);
		this.m_itemProperties.setEnabled(false);
		this.m_itemRepeat.setEnabled(false);

		if ( this.m_canvas.getCurrentGlycanLabel() == null )
			return;
		if ( this.m_canvas.getCurrentGlycanLabel().hasSelectedResidues() )
			this.m_itemRepeat.setEnabled(true);
		if ( this.m_canvas.getCurrentGlycanLabel().canAddBracket() )
			this.m_itemBracket.setEnabled(true);
		if ( this.m_canvas.getCurrentGlycanLabel().getCurrentResidue() == null )
			return;
		this.m_itemProperties.setEnabled(true);
	}

	/**
	 * Updates orientation icon to current state.
	 */
	public void updateOrientationIcon() {
		this.m_itemOrientation.setImage(GeneralIconProvider.getOrientationIcon(
				this.m_canvas.getBuilderWorkspace().getGraphicOptions().ORIENTATION
			));
	}

	/**
	 * Updates residue icon images when the notation has changed.
	 */
	public void updateIconImages() {
		for ( ResidueType resType : ResidueDictionary.directResidues() ) {
			ToolItem item = this.m_mapResTypeToItem.get(resType);
			item.setImage(GlycanIconProvider.getResidueIcon(resType));
		}
		if ( !this.m_lRecentResItems.isEmpty() ) {
			List<ResidueType> toRemove = new ArrayList<>();
			for ( ResidueType resType : this.m_mapRecentResTypeToItem.keySet() ) {
				ToolItem item = this.m_mapRecentResTypeToItem.get(resType);
				if ( item.isDisposed() ) {
					toRemove.add(resType);
					continue;
				}
				item.setImage(GlycanIconProvider.getResidueIcon(resType));
			}
			for ( ResidueType resType : toRemove )
				this.m_mapRecentResTypeToItem.remove(resType);
		}
		this.m_bar.layout();
	}

	/**
	 * Returns the ToolBar.
	 * @return ToolBar of the shortcuts
	 */
	public ToolBar getToolBar() {
		return this.m_bar;
	}

	/**
	 * Disposes the ToolBar.
	 */
	public void dispose() {
		this.m_bar.dispose();
	}

	@Override
	public void residueHistoryChanged() {
		updateResentResidues();
	}

	@Override
	public void residueIconUpdated() {
		updateIconImages();
	}

	@Override
	public void glycanIconUpdated() {
		// Do nothing
	}
}
