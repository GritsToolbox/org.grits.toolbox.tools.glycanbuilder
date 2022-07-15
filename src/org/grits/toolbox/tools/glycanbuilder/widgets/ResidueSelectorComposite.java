package org.grits.toolbox.tools.glycanbuilder.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.eurocarbdb.application.glycanbuilder.ResidueDictionary;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.ResidueRendererSWT;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;

public class ResidueSelectorComposite extends Composite {

	private List<Residue> m_lResidues;
	public ResidueSelectorComposite(Composite parent) {
		super(parent, SWT.NONE);

		this.m_lResidues = new ArrayList<>();

		BuilderWorkspaceSWT bw = new BuilderWorkspaceSWT(parent.getDisplay());
		ResidueRendererSWT rr = bw.getGlycanRenderer().getResidueRenderer();
		bw.setNotation(GraphicOptionsSWT.NOTATION_SNFG);
		this.setLayout(new GridLayout(1, false));
		for ( String sup : ResidueDictionary.getSuperclasses() ) {
			Group g = new Group(this, SWT.NONE);
			g.setText(sup);

			g.setLayout(new GridLayout(8, false));

			for ( ResidueType res : ResidueDictionary.getResidues(sup) ) {
				Button btn = new Button(g, SWT.PUSH);
				rr.setBackgroundColor(btn.getBackground());
				btn.setText( res.getName() );
				btn.setImage( rr.getImage(res, 36) );
				btn.setToolTipText(res.getDescription());
			}
			g.pack();
		}
	}

	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setText("Residue selector");
		shell.setLayout(new FillLayout());
		shell.setBackgroundMode(SWT.INHERIT_FORCE);

		new org.eurocarbdb.application.glycanbuilder.BuilderWorkspace(new GlycanRendererAWT());

		ScrolledComposite sc = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		ResidueSelectorComposite resSelector = new ResidueSelectorComposite(sc);

		sc.setContent(resSelector);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		resSelector.pack();
		sc.setMinSize(resSelector.getBounds().width,resSelector.getBounds().height);

//		shell.pack();
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		resSelector.dispose();
		display.dispose();
	}
}
