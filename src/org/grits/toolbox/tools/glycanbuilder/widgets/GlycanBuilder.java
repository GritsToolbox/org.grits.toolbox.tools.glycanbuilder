package org.grits.toolbox.tools.glycanbuilder.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.tools.glycanbuilder.widgets.canvas.GlycanCanvasWithToolBarComposite;
import org.grits.toolbox.tools.glycanbuilder.widgets.forms.GlycanBuilderMultiForm;

/**
 * A class for adding a sash form containing GlycanCanvasWithToolBarComposite and GlycanBuilderMultiform.
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class GlycanBuilder {

	private GlycanCanvasWithToolBarComposite m_canvas;
	private GlycanBuilderMultiForm m_multiForm;

	public GlycanBuilder(Composite parent) {
		parent.setLayout(new FillLayout());

		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayout(new FillLayout());

		this.m_canvas = new GlycanCanvasWithToolBarComposite(sash, SWT.BORDER);

		this.m_multiForm = new GlycanBuilderMultiForm(sash, this.m_canvas);

		sash.setWeights(new int[] {70, 30});
	}

	public GlycanCanvasWithToolBarComposite getGlycanCanvasWithToolBar() {
		return this.m_canvas;
	}

	public GlycanBuilderMultiForm getBuilderMultiForm() {
		return this.m_multiForm;
	}
}
