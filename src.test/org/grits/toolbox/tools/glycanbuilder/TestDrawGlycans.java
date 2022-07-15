package org.grits.toolbox.tools.glycanbuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eurocarbdb.application.glycanbuilder.BuilderWorkspace;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.Linkage;
import org.eurocarbdb.application.glycanbuilder.PositionManager;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.io.parser.GWSParser;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.BBoxManager;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.GlycanRendererSWT;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.SWTColors;

public class TestDrawGlycans {

	private static org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT bw;

	private static Label lblStatusLine;

	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setText("Draw Glycan");
		shell.setLayout(new FillLayout());
		shell.setBackgroundMode(SWT.INHERIT_FORCE);

		ScrolledComposite sc = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite parent = new Composite(sc, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		parent.setLayout(gridLayout);

		sc.setContent(parent);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		List<String> t_lGWBSeqs = new ArrayList<>();
		// Test Pens
		t_lGWBSeqs.add("freeEnd--??1D-Pen,p--??1D-Ara,p--??1D-Lyx,p--??1D-Xyl,p--??1D-Rib,p$MONO,perMe,0,0,freeEnd");
		// Test Hexs
		t_lGWBSeqs.add("freeEnd--??1D-Hex,p--??1D-Glc,p--??1D-Man,p--??1D-Gal,p--??1D-Gul,p--??1D-Alt,p--??1D-All,p--??1D-Tal,p--??1D-Ido,p$MONO,perMe,0,0,freeEnd");
		// Test HexNs
		t_lGWBSeqs.add("freeEnd--??1D-HexN,p--??1D-GlcN,p--??1D-ManN,p--??1D-GalN,p--??1D-GulN,p--??1D-AltN,p--??1D-AllN,p--??1D-TalN,p--??1D-IdoN,p$MONO,perMe,0,0,freeEnd");
		// Test HexNAcs
		t_lGWBSeqs.add("freeEnd--??1D-HexNAc,p--??1D-GlcNAc,p--??1D-ManNAc,p--??1D-GalNAc,p--??1D-GulNAc,p--??1D-AltNAc,p--??1D-AllNAc,p--??1D-TalNAc,p--??1D-IdoNAc,p$MONO,perMe,0,0,freeEnd");
		// Test HexAs
		t_lGWBSeqs.add("freeEnd--??1D-dHexA,p--??1D-HexA,p--??1D-GlcA,p--??1D-ManA,p--??1D-GalA,p--??1D-GulA,p--??1D-AltA,p--??1D-AllA,p--??1D-TalA,p--??1L-IdoA,p$MONO,perMe,0,0,freeEnd");
		// Test dHexs 1
		t_lGWBSeqs.add("freeEnd--??1D-dHex,p--??1D-Qui,p--??1D-Rha,p--??1D-dAlt,p--??1D-dTal,p--??1D-Fuc,p$MONO,perMe,0,0,freeEnd");
		// Test dHexs 2
		t_lGWBSeqs.add("freeEnd--??1D-ddHex,p--??1D-Oli,p--??1D-Tyv,p--??1D-Abe,p--??1D-Par,p--??1D-Dig,p--??1D-Col,p$MONO,perMe,0,0,freeEnd");
		// Test dHexNAcs
		t_lGWBSeqs.add("freeEnd--??1D-dHexNAc,p--??1D-QuiNAc,p--??1D-RhaNAc,p--??1D-FucNAc,p$MONO,perMe,0,0,freeEnd");
		// Test Non (Sia can not be specified because it does not have cirtain structural information)
		t_lGWBSeqs.add("freeEnd--??2D-Non,p--??2D-Kdn,p--??2D-NeuAc,p--??2D-NeuGc,p--??2D-Neu,p$MONO,perMe,0,0,freeEnd");
//		t_lGWBSeqs.add("freeEnd--??2D-Non,p--??2D-Kdn,p--??2D-NeuAc,p--??2D-NeuGc,p--??2D-Neu,p--??2D-Sia,p$MONO,perMe,0,0,freeEnd");
		// Others (L-gro-D-manHep and D-gro-D-manHep can not be parsed? Hyphens should be removed from these.)
		t_lGWBSeqs.add("freeEnd--??1D-Hep,p--??1D-Bac,p--??1D-Kdo,p--??1D-Dha,p--??1D-MurNAc,p--??1D-MurNGc,p--??1D-Mur,p--??1D-Unknown,p$MONO,perMe,0,0,freeEnd");
//		t_lGWBSeqs.add("freeEnd--??1D-Hep,p--??1D-Bac,p--??1D-L-gro-D-manHep,p--??1D-Kdo,p--??1D-Dha,p--??1D-D-gro-D-manHep,p--??1D-MurNAc,p--??1D-MurNGc,p--??1D-Mur,p--??1D-Unknown,p$MONO,perMe,0,0,freeEnd");
		// Ketoses
		t_lGWBSeqs.add("freeEnd--??1D-Ketose,p--??1D-Api,p--??1D-Fru,p--??1D-Tag,p--??1D-Sor,p--??1D-Psi,p$MONO,perMe,0,0,freeEnd");
		// aGal
		t_lGWBSeqs.add("freeEnd--?a1D-Gal,p$MONO,perMe,0,0,freeEnd");
		// GD3
		t_lGWBSeqs.add("freeEnd--?b1D-Glc,p--4b1D-Gal,p--3a2D-NeuAc,p--8a2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
		// GD1a
		t_lGWBSeqs.add("freeEnd/#bcleavage--?b1D-Glc,p--4b1D-Gal,p(--4b1D-GalNAc,p--3b1D-Gal,p--3a2D-NeuAc,p)--3a2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
		// GD1b
		t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p(--??1D-GalNAc,p--??1D-Gal,p)--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
		// GD1c
		t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
		// GM1b_GalNAc
		t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GalNAc,p--??1D-Gal,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");
		// GM1b_GlcNAc
		t_lGWBSeqs.add("freeEnd--??1D-Glc,p--??1D-Gal,p--??1D-GlcNAc,p--??1D-Gal,p--??2D-NeuAc,p$MONO,perMe,0,0,freeEnd");

		new BuilderWorkspace(new GlycanRendererAWT());
		final List<Glycan> structures = new ArrayList<>();
		for ( String t_strGWBSeq : t_lGWBSeqs ) {
			Glycan g = Glycan.fromString(t_strGWBSeq);
			structures.add( g );
		}

		bw = new org.grits.toolbox.tools.glycanbuilder.core.workspace.BuilderWorkspaceSWT(parent.getDisplay());

		GlycanRendererSWT gr = bw.getGlycanRenderer();
//		gr.setBackgroundColor(shell.getBackground());

		List<Label> labels = new ArrayList<Label>();
		boolean show_mass = true;
		boolean show_redend = true;
		for ( Glycan structure : structures ) {
			labels.add( getGlycanLabel(parent, structure, gr, show_mass, show_redend, GraphicOptionsSWT.NOTATION_CFG) );
			labels.add( getGlycanLabel(parent, structure, gr, show_mass, show_redend, GraphicOptionsSWT.NOTATION_SNFG) );
			labels.add( getGlycanLabel(parent, structure, gr, show_mass, show_redend, GraphicOptionsSWT.NOTATION_UOXFCOL) );
		}

		parent.pack();
		for ( Label label : labels ) {
			Rectangle rect = label.getBounds();
			System.out.println(rect.x+","+rect.y+","+rect.width+","+rect.height);
		}

		sc.setMinSize(parent.getBounds().width,parent.getBounds().height);

		shell.pack();
		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		display.dispose();

	}

	static Label getGlycanLabel(Composite parent, Glycan structure, GlycanRendererSWT gr, boolean show_mass, boolean show_redend, String notation) {
		Label label = new Label(parent, SWT.NONE);

		Menu popupMenu = new Menu(label);
		MenuItem addItem = new MenuItem(popupMenu, SWT.CASCADE);
		addItem.setText("Add");
		MenuItem editItem = new MenuItem(popupMenu, SWT.CASCADE);
		editItem.setText("Edit");
		MenuItem deleteItem = new MenuItem(popupMenu, SWT.CASCADE);
		deleteItem.setText("Del");
		label.setMenu(popupMenu);

		PositionManager posManager = new PositionManager();
		BBoxManager bboxManager = new BBoxManager();
		
		Image img = drawGlycanImage(parent, structure, gr, show_mass, show_redend, notation,
				null, null, posManager, bboxManager);
		label.setImage(img);

		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				System.out.println(GWSParser.toString(structure));
				Point p = new Point(e.x, e.y);
				bw.setNotation(notation);
				HashSet<Residue> setSelectedResidues = new HashSet<>();
				HashSet<Linkage> setSelectedLinkages = new HashSet<>();
				if ( e.button == 1 ) { // left click
					Residue res = bboxManager.getNodeAtPoint(p);
					if ( res != null ) {
						setSelectedResidues.add(res);
						System.out.println( GWSParser.writeResidueType(res) );
						// Add a residue to the clicked residue
						try {
							res.addChild( GWSParser.readSubtree("?1D-Hex,p--??1D-HexNAc,p", false) );
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					else {
						Linkage link = bboxManager.getLinkageAtPoint(p);
						if ( link != null )
							setSelectedLinkages.add(link);
					}
				} else if ( e.button == 3 ) { // right click
					setSelectedResidues.addAll(structure.getAllResidues());
				}
				Image img = drawGlycanImage(parent, structure, gr, show_mass, show_redend, notation,
						setSelectedResidues, setSelectedLinkages, posManager, bboxManager);
				Label l = (Label)e.getSource();
				l.setImage(img);
				parent.pack();
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		return label;
	}

	private static Image drawGlycanImage(Composite parent, Glycan structure, GlycanRendererSWT gr,
			boolean show_mass, boolean show_redend, String notation,
			HashSet<Residue> setSelectedResidue, HashSet<Linkage> setSelectedLinkage,
			PositionManager posManager , BBoxManager bboxManager) {
		bw.setNotation(notation);

		int iSelectMargin = 5;
		Rectangle bound = gr.computeBoundingBoxes(structure, 5+iSelectMargin, 5, show_mass, show_redend, posManager, bboxManager);
		bound.x = 0;
		bound.y = 0;
		bound.width  += 10 + iSelectMargin;
		bound.height += 10;

		Image img = new Image(parent.getDisplay(), bound.width, bound.height);
		GC gc = new GC(img);
		// Clear background
		gc.setBackground(parent.getBackground());
		gc.fillRectangle(img.getBounds());
		if ( setSelectedResidue != null || setSelectedLinkage != null ) {
			gc.setBackground( new Color(parent.getDisplay(), SWTColors.CYAN) );
			gc.fillRectangle(0, 0, iSelectMargin, bound.height);
		}
		gr.paint(gc, structure, setSelectedResidue, setSelectedLinkage, show_mass, show_redend, posManager, bboxManager);
		gc.dispose();

		return img;
	}
}
