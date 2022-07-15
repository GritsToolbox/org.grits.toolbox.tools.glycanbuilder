package org.grits.toolbox.tools.glycanbuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.tools.glycanbuilder.widgets.GlycanBuilder;

public class TestGlycanBuilder {

	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setText("GlycanBuilder");
		shell.setImage(new Image( display, "icons/logo.png" ));
//		shell.setLayout(new GridLayout(1, false));
		shell.setLayout(new FillLayout());
		shell.setBackgroundMode(SWT.INHERIT_FORCE);

		GlycanBuilder builder = new GlycanBuilder(shell);
//		BuilderWorkspaceSWT bws = builder.getGlycanCanvasWithToolBar().getGlycanCanvas().getBuilderWorkspace();
//
//		GlycanDatabase gdb = GlycanDatabaseUtils.getGlycanDatabase("All-Glycan.xml");
//		List<Glycan> lGlycans = new ArrayList<>();
//		try {
//			for ( String sequence : GlycanDatabaseUtils.extractGlycanSequences(gdb) ) {
//				Glycan glycan = GWSParser.fromString(sequence, bws.getDefaultMassOptions());
//				lGlycans.add(glycan);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		builder.getGlycanCanvasWithToolBar().getGlycanCanvas().addGlycans(lGlycans);

		shell.pack();
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		display.dispose();

	}

}
