package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

public class GlycanCanvasWithToolBarComposite extends Composite {

	private GlycanCanvasComposite m_canvas;
	private ShortcutToolBar m_toolBar;
	private ResiduePropertyBarComposite m_resPropBar;
	
	public GlycanCanvasWithToolBarComposite(Composite parent, int style) {
		super(parent, style);

		this.setLayout(new GridLayout(1, false));
		this.setBackgroundMode(SWT.INHERIT_FORCE);

		// Create canvas composite
		this.m_canvas = new GlycanCanvasComposite(this);
		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
		gd.heightHint = 300;
		this.m_canvas.setLayoutData(gd);

		// Create shortcut tool bar
		this.m_toolBar = new ShortcutToolBar(this, this.m_canvas);
		this.m_toolBar.getToolBar().moveAbove(this.m_canvas);
		gd = new GridData(GridData.FILL, GridData.FILL, true, false);
		this.m_toolBar.getToolBar().setLayoutData(gd);

		// Create residue property bar composite
		this.m_resPropBar = new ResiduePropertyBarComposite(this, this.m_canvas);
		gd = new GridData(GridData.FILL, GridData.FILL, true, false);
		this.m_resPropBar.setLayoutData(gd);

		parent.layout();

		// Add selection listener to ShortcutToolBar items
		for ( ToolItem item : this.m_toolBar.getToolBar().getItems() ) {
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// Update current residue in ResiduePropertyBarComposite
					m_resPropBar.updateCurrentResidue();
				}
			});
		}
	}

	public GlycanCanvasComposite getGlycanCanvas() {
		return this.m_canvas;
	}

	public ShortcutToolBar getShortcutToolBar() {
		return this.m_toolBar;
	}

	public ResiduePropertyBarComposite getResiduPropertyBar() {
		return this.m_resPropBar;
	}

	public void updateView() {
		this.updateView(true);
	}

	public void updateView(boolean force) {
		this.m_canvas.updateView(force);
		this.m_resPropBar.updateCurrentResidue();
		this.m_toolBar.updateButtonState();
	}
}
