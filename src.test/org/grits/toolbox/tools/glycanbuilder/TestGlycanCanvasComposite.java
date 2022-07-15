package org.grits.toolbox.tools.glycanbuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eurocarbdb.application.glycanbuilder.FileUtils;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT;
import org.grits.toolbox.tools.glycanbuilder.widgets.canvas.GlycanCanvasWithToolBarComposite;

public class TestGlycanCanvasComposite {

	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setText("Draw Glycan");
//		shell.setLayout(new GridLayout(1, false));
		shell.setLayout(new FillLayout());
		shell.setBackgroundMode(SWT.INHERIT_FORCE);

		List<String> t_lGWBSeqs = new ArrayList<>();
//		// Test Pens
//		t_lGWBSeqs.add("freeEnd--??1D-Pen,p--??1D-Ara,p--??1D-Lyx,p--??1D-Xyl,p--??1D-Rib,p$MONO,perMe,0,0,freeEnd");
//		// Test Hexs
//		t_lGWBSeqs.add("freeEnd--??1D-Hex,p--??1D-Glc,p--??1D-Man,p--??1D-Gal,p--??1D-Gul,p--??1D-Alt,p--??1D-All,p--??1D-Tal,p--??1D-Ido,p$MONO,perMe,0,0,freeEnd");
//		// Test HexNs
//		t_lGWBSeqs.add("freeEnd--??1D-HexN,p--??1D-GlcN,p--??1D-ManN,p--??1D-GalN,p--??1D-GulN,p--??1D-AltN,p--??1D-AllN,p--??1D-TalN,p--??1D-IdoN,p$MONO,perMe,0,0,freeEnd");
//		// Test HexNAcs
//		t_lGWBSeqs.add("freeEnd--??1D-HexNAc,p--??1D-GlcNAc,p--??1D-ManNAc,p--??1D-GalNAc,p--??1D-GulNAc,p--??1D-AltNAc,p--??1D-AllNAc,p--??1D-TalNAc,p--??1D-IdoNAc,p$MONO,perMe,0,0,freeEnd");
//		// Test HexAs
//		t_lGWBSeqs.add("freeEnd--??1D-dHexA,p--??1D-HexA,p--??1D-GlcA,p--??1D-ManA,p--??1D-GalA,p--??1D-GulA,p--??1D-AltA,p--??1D-AllA,p--??1D-TalA,p--??1L-IdoA,p$MONO,perMe,0,0,freeEnd");
//		// Test dHexs 1
//		t_lGWBSeqs.add("freeEnd--??1D-dHex,p--??1D-Qui,p--??1D-Rha,p--??1D-dAlt,p--??1D-dTal,p--??1D-Fuc,p$MONO,perMe,0,0,freeEnd");
//		// Test dHexs 2
//		t_lGWBSeqs.add("freeEnd--??1D-ddHex,p--??1D-Oli,p--??1D-Tyv,p--??1D-Abe,p--??1D-Par,p--??1D-Dig,p--??1D-Col,p$MONO,perMe,0,0,freeEnd");
//		// Test dHexNAcs
//		t_lGWBSeqs.add("freeEnd--??1D-dHexNAc,p--??1D-QuiNAc,p--??1D-RhaNAc,p--??1D-FucNAc,p$MONO,perMe,0,0,freeEnd");
//		// Test Non (Sia can not be specified because it does not have cirtain structural information)
//		t_lGWBSeqs.add("freeEnd--??2D-Non,p--??2D-Kdn,p--??2D-NeuAc,p--??2D-NeuGc,p--??2D-Neu,p$MONO,perMe,0,0,freeEnd");
////		t_lGWBSeqs.add("freeEnd--??2D-Non,p--??2D-Kdn,p--??2D-NeuAc,p--??2D-NeuGc,p--??2D-Neu,p--??2D-Sia,p$MONO,perMe,0,0,freeEnd");
//		// Others (L-gro-D-manHep and D-gro-D-manHep can not be parsed? Hyphens should be removed from these.)
//		t_lGWBSeqs.add("freeEnd--??1D-Hep,p--??1D-Bac,p--??1D-Kdo,p--??1D-Dha,p--??1D-MurNAc,p--??1D-MurNGc,p--??1D-Mur,p--??1D-Unknown,p$MONO,perMe,0,0,freeEnd");
////		t_lGWBSeqs.add("freeEnd--??1D-Hep,p--??1D-Bac,p--??1D-L-gro-D-manHep,p--??1D-Kdo,p--??1D-Dha,p--??1D-D-gro-D-manHep,p--??1D-MurNAc,p--??1D-MurNGc,p--??1D-Mur,p--??1D-Unknown,p$MONO,perMe,0,0,freeEnd");
//		// Ketoses
//		t_lGWBSeqs.add("freeEnd--??1D-Ketose,p--??1D-Api,p--??1D-Fru,p--??1D-Tag,p--??1D-Sor,p--??1D-Psi,p$MONO,perMe,0,0,freeEnd");
//		// aGal
//		t_lGWBSeqs.add("freeEnd--?a1D-Gal,p$MONO,perMe,0,0,freeEnd");
//		// GD3
//		t_lGWBSeqs.add("freeEnd--?b1D-Glc,p--4b1D-Gal,p--3a2D-NeuAc,p--8a2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
//		// GD1a
//		t_lGWBSeqs.add("freeEnd/#bcleavage--?b1D-Glc,p--4b1D-Gal,p(--4b1D-GalNAc,p--3b1D-Gal,p--3a2D-NeuAc,p)--3a2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
//		// GD1b
//		t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p)--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
//		// GD1c
//		t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
//		// GM1b_GalNAc
//		t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
//		// GM1b_GlcNAc
//		t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GlcNAc,p--??1D-Gal,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");

		System.out.println( FileUtils.getRootDir() );

		new BuilderWorkspaceSWT(display);
		final List<Glycan> structures = new ArrayList<>();
		for ( String t_strGWBSeq : t_lGWBSeqs ) {
			Glycan g = Glycan.fromString(t_strGWBSeq);
			structures.add(g);
		}
//		for ( CoreType t_ct : CoreDictionary.getCores()) {
//			Glycan g = Glycan.fromString(t_ct.getStructure());
//			structures.add(g);
//		}

		GlycanCanvasWithToolBarComposite canvasBar = new GlycanCanvasWithToolBarComposite(shell, SWT.NONE);

//		GlycanCanvasComposite canvas = new GlycanCanvasComposite(shell);
//		GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
////		gd.widthHint = 600;
//		gd.heightHint = 300;
//		canvas.setLayoutData(gd);
//		canvas.setBuilderWorkspace(bws);
//		canvas.getBuilderWorkspace().setNotation(GraphicOptions.NOTATION_SNFG);
//		canvas.getBuilderWorkspace().getGraphicOptions().SHOW_MASSES = true;
//		canvas.getBuilderWorkspace().getGraphicOptions().SHOW_REDEND = true;
//
//		for ( Glycan structure : structures )
//			canvas.addGlycan(structure);
//
//		ShortcutToolBar sc = new ShortcutToolBar(shell, canvas);
//		sc.getToolBar().moveAbove(canvas);
//		gd = new GridData(GridData.FILL, GridData.FILL, true, false);
////		gd.widthHint = 600;
//		sc.getToolBar().setLayoutData(gd);
//
//		ResiduePropertyBarComposite tail = new ResiduePropertyBarComposite(shell, canvas);
//		gd = new GridData(GridData.FILL, GridData.FILL, true, false);
////		gd.widthHint = 600;
//		tail.setLayoutData(gd);


		shell.pack();
//		System.out.println(canvas.getClientArea());
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		canvasBar.dispose();
		display.dispose();

	}

}
