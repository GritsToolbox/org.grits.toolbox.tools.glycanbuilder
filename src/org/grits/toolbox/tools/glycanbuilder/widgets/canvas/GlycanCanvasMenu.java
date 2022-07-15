package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eurocarbdb.application.glycanbuilder.CoreType;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.eurocarbdb.application.glycanbuilder.TerminalType;
import org.grits.toolbox.tools.glycanbuilder.core.io.GlycanIOUtils;
import org.grits.toolbox.tools.glycanbuilder.core.io.SequenceFormat;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.CoreDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.ResidueDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.TerminalDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.utils.ResidueOperationUtils;
import org.grits.toolbox.tools.glycanbuilder.widgets.dialog.RepititionPropertyDialog;
import org.grits.toolbox.tools.glycanbuilder.widgets.dialog.ResiduePropertyDialog;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.ClipUtils;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.GeneralIconProvider;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.GlycanIconProvider;

/**
 * A class providing a static method for adding right-click menus to GlycanCanvasInterface
 * which is an interface of GlycanCanvas and GlycanLabel classes.
 * Cut, Copy, Paste and Delete menus are added first as common menus,
 * then the menus for adding a residue or glycan are continued.
 * The menus for changing residue property and mass options are added if user select a glycan.
 * At the last, save menus for sequences and images are added.
 * 
 * @see GlycanCanvasInterface
 * @author Masaaki Matsubara (matsubara@uga.edu)
 */
public class GlycanCanvasMenu {

	/**
	 * Sets Menus for GlycanCanvasInterface.
	 * @param canvas GlycanCanvasInterface to be added the menu
	 */
	public static void setMenu(GlycanCanvasInterface canvas) {
		Control control = canvas.getControl();
		// Remove old menu
		disposeMenu(control.getMenu());

		// Set Menu
		Menu menu = new Menu(control);

		addCommonMenuItems(canvas, menu);

		// Separator
		new MenuItem(menu, SWT.SEPARATOR);

		if ( canvas instanceof GlycanLabel ) {
			GlycanLabel label = (GlycanLabel)canvas;
			if ( label.getCurrentResidue() != null )
				addResidueMenuItems(label, menu);
			else if ( label.getCurrentLinkage() != null )
				addLinkageMenuItems(label, menu);
			else
				addGlycanMenuItems(label, menu);
			if ( label.canAddBracket() )
				addBracketMenuItem(label, menu);
		} else
			addGlycanMenuItems(canvas, menu);

		if ( canvas instanceof GlycanLabel ) {
			// Separator
			new MenuItem(menu, SWT.SEPARATOR);

			addResiduePropertyMenu((GlycanLabel)canvas, menu);
			addMassOptionsMenu((GlycanLabel)canvas, menu);
		}

		// Separator
		new MenuItem(menu, SWT.SEPARATOR);
		addSaveMenuItems(canvas, menu);

		control.setMenu(menu);
	}

	private static void disposeMenu(Menu menu) {
		if ( menu == null )
			return;
		if ( menu.isDisposed() )
			return;
		// Dispose current menus
//		for ( MenuItem item0 : menu.getItems() ) {
//			if ( item0.getMenu() != null )
//				for ( MenuItem item1 : item0.getMenu().getItems() )
//					item1.dispose();
//			item0.dispose();
//		}
		menu.dispose();
	}

	private static void addCommonMenuItems(GlycanCanvasInterface canvas, Menu menu) {
		MenuItem item;

		// Cut
		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Cut\tCtrl+X");
		item.setImage(GeneralIconProvider.getCutIcon());
		item.setEnabled(canvas.canCut());
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.cut();
				canvas.updateView(true);
			}
		});

		// Copy
		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Copy\tCtrl+C");
		item.setImage(GeneralIconProvider.getCopyIcon());
		item.setEnabled(canvas.canCopy());
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.copy();
				canvas.updateView(true);
			}
		});

		// Paste
		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Paste\tCtrl+V");
		item.setImage(GeneralIconProvider.getPasteIcon());
		item.setEnabled(canvas.canPaste());
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.paste();
				canvas.updateView(true);
			}
		});

		// Delete
		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Delete\tDelete");
		item.setImage(GeneralIconProvider.getDeleteIcon());
		item.setEnabled(canvas.canDelete());
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.delete();
				canvas.updateView(true);
			}
		});
	}

	private static void addGlycanMenuItems(GlycanCanvasInterface canvas, Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Add structure");
		addCoreGlycanMenu(canvas, item);

		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Add terminal structure");
		addTerminalMenu(canvas, item);
	}

	/**
	 * Adds sub Menu, which adds core glycans to canvas, to the given MenuItem.
	 * @param parentItem MenuItem to be added Menu
	 */
	private static void addCoreGlycanMenu(GlycanCanvasInterface canvas, MenuItem parentItem) {
		// Create sub menu for superclasses
		Menu menuSub = new Menu(parentItem);
		for ( String sup : CoreDictionary.getSuperclasses() ) {
			MenuItem itemSup = new MenuItem(menuSub, SWT.CASCADE);
			itemSup.setText(sup);

			// Create sub menus for core glycans grouped with superclass
			Menu menuSup = new Menu(itemSup);
			for ( CoreType coreType : CoreDictionary.getCores(sup) ) {
				
				MenuItem itemCore = new MenuItem(menuSup, SWT.CASCADE);
				itemCore.setText( coreType.getDescription() );
				itemCore.setImage( GlycanIconProvider.getGlycanCoreIcon(coreType) );
				itemCore.setToolTipText(coreType.getDescription());

				final Glycan g = Glycan.fromString(coreType.getStructure());
				itemCore.addSelectionListener( new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						canvas.addGlycan(g);
						canvas.updateView(true);
					}
				});
			}

			itemSup.setMenu(menuSup);
		}

		parentItem.setMenu(menuSub);
	}

	/**
	 * Adds sub Menu, which adds terminal glycans to canvas, to the given MenuItem.
	 * @param parentItem MenuItem to be added Menu
	 */
	private static void addTerminalMenu(GlycanCanvasInterface canvas, MenuItem parentItem) {
		// Create sub menu for superclasses
		Menu menuSub = new Menu(parentItem);
		for ( String sup : TerminalDictionary.getSuperclasses() ) {
			MenuItem itemSup = new MenuItem(menuSub, SWT.CASCADE);
			itemSup.setText(sup);

			// Create sub menus for core glycans grouped with superclass
			Menu menuSup = new Menu(itemSup);
			for ( TerminalType termType : TerminalDictionary.getTerminals(sup) ) {
				if ( !canAddTerminal(canvas, termType) )
					continue;
				MenuItem itemCore = new MenuItem(menuSup, SWT.CASCADE);
				itemCore.setText( termType.getDescription() );
				itemCore.setImage( GlycanIconProvider.getGlycanTerminalIcon(termType) );
				itemCore.setToolTipText(termType.getDescription());

				itemCore.addSelectionListener( new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							canvas.addResidue(termType.newTerminal());
							canvas.updateView(true);
						} catch (Exception e1) {
						}
					}
				});
			}

			if ( menuSup.getItemCount() == 0 ) {
				menuSup.dispose();
				itemSup.dispose();
				continue;
			}
			itemSup.setMenu(menuSup);
		}
		if ( menuSub.getItemCount() == 0 ) {
			menuSub.dispose();
			parentItem.dispose();
			return;
		}
		parentItem.setMenu(menuSub);
	}

	private static boolean canAddTerminal(GlycanCanvasInterface canvas, TerminalType termType) {
		try {
			Residue root = termType.newTerminal();
			GlycanLabel label = null;
			if ( canvas instanceof GlycanLabel ) {
				label = (GlycanLabel)canvas;
			} else if ( canvas instanceof GlycanCanvasComposite ) {
				label = ((GlycanCanvasComposite)canvas).getCurrentGlycanLabel();
			}
			if ( label == null )
				return true;
			if ( label.getCurrentResidue() != null ) {
				return ResidueOperationUtils.canModifyResidue(
						label.getCurrentResidue(), root, ResidueOperationUtils.ADD
					);
			}
		} catch (Exception e) {
		}
		return false;
	}

	private static void addResidueMenuItems(GlycanLabel label, Menu menu) {
		MenuItem item;
		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Add residue");
		addResidueAdditionMenu(label, item, ResidueOperationUtils.ADD);

		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Add terminal");
		addTerminalMenu(label, item);

		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Insert residue before");
		addResidueAdditionMenu(label, item, ResidueOperationUtils.INSERT);

		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Change residue type");
		addResidueAdditionMenu(label, item, ResidueOperationUtils.CHANGE);

		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Add repeating unit");
		item.setEnabled(label.hasSelectedResidues());
		item.setImage( GeneralIconProvider.getRepeatIcon() );
		item.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				label.createRepitition();
				label.updateView(true);
			}
		});
	}

	private static void addResidueAdditionMenu(GlycanLabel label, MenuItem parentItem, String mode) {
		if ( !mode.equals(ResidueOperationUtils.ADD)
		  && !mode.equals(ResidueOperationUtils.INSERT)
		  && !mode.equals(ResidueOperationUtils.CHANGE) )
			return;

		// Create sub menu for superclasses of residues
		Menu menuSub = new Menu(parentItem);
		for ( String sup : ResidueDictionary.getSuperclasses() ) {
			// Ignore reducing end
			if ( sup.equals("Reducing end") )
				continue;
			MenuItem itemSup = new MenuItem(menuSub, SWT.CASCADE);
			itemSup.setText(sup);

			// Create sub menus for residues grouped with superclass
			Menu menuSup = new Menu(itemSup);
			for ( ResidueType resType : ResidueDictionary.getResidues(sup) ) {
				if ( !ResidueOperationUtils.canModifyResidueToLabel(label, new Residue(resType), mode) )
					continue;

				MenuItem itemRes = new MenuItem(menuSup, SWT.CASCADE);
				itemRes.setText( resType.getName() );
				itemRes.setImage( GlycanIconProvider.getResidueIcon(resType) );
				itemRes.setToolTipText(resType.getDescription());

				itemRes.addSelectionListener( new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						if ( mode.equals(ResidueOperationUtils.ADD) ) { // Add residue
							label.addResidue(new Residue(resType));
						}
						else if ( mode.equals(ResidueOperationUtils.INSERT) ) { // Insert residue before
							if ( label.getCurrentResidue() != null )
								label.insertResidueBefore(new Residue(resType));
							else if ( label.getCurrentLinkage() != null )
								label.insertResidue(new Residue(resType));
						}
						else if ( mode.equals(ResidueOperationUtils.CHANGE) ) { // Change residue type
							label.changeResidueType(resType);
						}
						label.getBuilderWorkspace().getResidueHistory().add(resType);
						label.updateView(true);
					}
				});
			}
			if ( menuSup.getItemCount() == 0 ) {
				menuSup.dispose();
				itemSup.dispose();
				continue;
			}
			itemSup.setMenu(menuSup);
		}
		if ( menuSub.getItemCount() == 0 ) {
			menuSub.dispose();
			parentItem.dispose();
			return;
		}
		parentItem.setMenu(menuSub);
	}

	private static void addLinkageMenuItems(GlycanLabel label, Menu menu) {
		MenuItem item;

		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Insert residue");
		addResidueAdditionMenu(label, item, ResidueOperationUtils.INSERT);

//		MenuItem itemEdit = new MenuItem(menu, SWT.CASCADE);
//		itemEdit.setText("Edit linkage");
//		MenuItem deleteLink = new MenuItem(menu, SWT.CASCADE);
//		deleteLink.setText("Delete residue");
	}

	private static void addBracketMenuItem(GlycanLabel label, Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Add bracket");
		item.setEnabled(label.canAddBracket());
		item.setImage( GeneralIconProvider.getBracketIcon() );
		item.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				label.addBracket();
				label.updateView(true);
			}
		});
	}

	private static void addResiduePropertyMenu(GlycanLabel label, Menu menu) {
		if ( label.getCurrentResidue() == null )
			return;
		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Residue properties");
		item.setImage(GeneralIconProvider.getResiduePropertiesIcon());
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Residue res = label.getCurrentResidue();
				if ( res == null )
					return;
				// Create dialog
				Dialog dlg;
				if ( res.isEndRepetition() )
					dlg = new RepititionPropertyDialog( menu.getShell(), res );
				else
					dlg = new ResiduePropertyDialog( menu.getShell(), res );
				dlg.open();
				label.updateView(true);
			}
		});
	}

	private static void addMassOptionsMenu(GlycanLabel label, Menu menu) {
		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				label.changeReducingEndType();
				label.redrawGlycan(true);
			}
		};

		MenuItem item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Change reducing end type");
		item.addSelectionListener(listener);

		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Mass options of selected structure");
		item.addSelectionListener(listener);
	}

	private static void addSaveMenuItems(GlycanCanvasInterface canvas, Menu menu) {
		MenuItem item;

//		item = new MenuItem(menu, SWT.CASCADE);
//		item.setText("Import");

		addCopyToClipboardMenu(canvas, menu);

		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Save image");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Extract glycans
				List<Glycan> lGlycans;
				if ( canvas.hasSelection() )
					lGlycans = canvas.getSelectedGlycans();
				else
					lGlycans = canvas.getAllGlycans();
				if ( lGlycans.isEmpty() ) {
					MessageBox box = new MessageBox(canvas.getControl().getShell(), SWT.OK|SWT.ICON_WARNING);
					box.setMessage("No glycans in this canvas.");
					box.open();
					return;
				}

				GlycanIOUtils.saveGlycanImage(lGlycans, canvas.getBuilderWorkspace(), null);
			}
		});
	}

	private static void addCopyToClipboardMenu(GlycanCanvasInterface canvas, Menu menu) {
		MenuItem item;

		// Add parent menu item
		item = new MenuItem(menu, SWT.CASCADE);
		item.setText("Copy to clipboad");
		Menu menuExport = new Menu(item);
		item.setMenu(menuExport);

		// Add GWS menu
		item = new MenuItem(menuExport, SWT.CASCADE);
		item.setText("GWS");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				copySelectionToClipboard(canvas);
				ClipUtils.copyGWSToClipboard();
				MessageBox box = new MessageBox(canvas.getControl().getShell(), SWT.OK|SWT.ICON_INFORMATION);
				box.setMessage("GWS sequence is set to clipboad successfuly.");
				box.open();
			}
		});

		// Add GlycoCT menu
		item = new MenuItem(menuExport, SWT.CASCADE);
		item.setText("GlycoCT");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Glycan> lGlycans = getGlycansFromCanvas(canvas);
				List<String> lGlycoCTs = GlycanIOUtils.convertGlycanSecuenceWithCheck(lGlycans, SequenceFormat.GCT_C, false);
				if ( lGlycoCTs == null || lGlycoCTs.isEmpty() )
					return;

				ClipUtils.copyTextToClipboad(lGlycoCTs.get(0));
				MessageBox box = new MessageBox(canvas.getControl().getShell(), SWT.OK|SWT.ICON_INFORMATION);
				box.setMessage("GlycoCT{condensed} sequence of the first strcuture is set to clipboad.");
				box.open();
			}
		});
	}

	private static void copySelectionToClipboard(GlycanCanvasInterface canvas) {
		if ( canvas.hasSelection() ) {
			canvas.copy();
			return;
		}
		// Copy all if no selection
		canvas.selectAll();
		canvas.copy();
		canvas.resetSelection();
	}

	private static List<Glycan> getGlycansFromCanvas(GlycanCanvasInterface canvas) {
		if ( canvas.hasSelection() )
			return canvas.getSelectedGlycans();
		// Copy all if no selection
		return canvas.getAllGlycans();
	}
}
