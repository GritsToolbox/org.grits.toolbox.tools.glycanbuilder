package org.grits.toolbox.tools.glycanbuilder.widgets.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eurocarbdb.application.glycanbuilder.CoreType;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.eurocarbdb.application.glycanbuilder.TerminalType;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.CoreDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.ResidueDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.TerminalDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.structure.utils.ResidueOperationUtils;
import org.grits.toolbox.tools.glycanbuilder.widgets.canvas.GlycanCanvasWithToolBarComposite;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.GlycanIconProvider;

public class AddStructureForm extends FormAbstract implements GlycanIconProvider.IconUpdateListener {

	private Map<ResidueType, Button> m_mapResTypeToButton;
	private Map<CoreType, Button> m_mapCoreTypeToButton;
	private Map<TerminalType, Button> m_mapTerminalTypeToButton;
	private Button m_btnChangeRedEnd;

	public AddStructureForm(MultiPageFormAbstract parent, String tabText) {
		super(parent, tabText);
		this.m_mapResTypeToButton = new HashMap<>();
		this.m_mapCoreTypeToButton = new HashMap<>();
		this.m_mapTerminalTypeToButton = new HashMap<>();
		createControl();

		// Add this to GlycanIconProvider as a listener
		GlycanIconProvider.addIconUpdateListener(this);
		final AddStructureForm toRemove = this;
		this.getForm().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				GlycanIconProvider.removeIconUpdateListener(toRemove);
			}
		});

		// Update icons
		residueIconUpdated();
		glycanIconUpdated();
	}

	private void createControl() {
		getForm().setText(Messages.getString("AddStructure.title")); //$NON-NLS-1$

		Composite sectionClient;

		// Core glycan
		sectionClient = this.createNewSection(Messages.getString("AddStructure.core")); //$NON-NLS-1$
		for ( String sup : CoreDictionary.getSuperclasses() ) {
			
			ExpandableComposite group = this.createExpandableComposite(sectionClient);
			group.setText(sup);
			for ( CoreType type : CoreDictionary.getCores(sup) ) {
				Button btn = this.createIconButton(
						(Composite)group.getClient(), null, SWT.PUSH | SWT.CENTER);
				btn.setToolTipText(type.getDescription());
				this.m_mapCoreTypeToButton.put(type, btn);
			}
		}

		// Residue
		sectionClient = this.createNewSection(Messages.getString("AddStructure.residue_ms")); //$NON-NLS-1$
		this.createResidueSection(sectionClient, true);
		sectionClient = this.createNewSection(Messages.getString("AddStructure.residue_other")); //$NON-NLS-1$
		this.createResidueSection(sectionClient, false);

		// Terminal
		sectionClient = this.createNewSection(Messages.getString("AddStructure.terminal")); //$NON-NLS-1$
		for ( String sup : TerminalDictionary.getSuperclasses() ) {
			ExpandableComposite group = this.createExpandableComposite(sectionClient);
			group.setText(sup);
			for ( TerminalType type : TerminalDictionary.getTerminals(sup) ) {
				Button btn = this.createIconButton(
						(Composite)group.getClient(), null, SWT.PUSH | SWT.CENTER);
				btn.setToolTipText(type.getDescription());
				this.m_mapTerminalTypeToButton.put(type, btn);
			}
		}

		// Change reducing end type
		Composite comp = getToolkit().createComposite(getForm().getBody());
		comp.setLayout(new GridLayout(1, false));
		Button btn = getToolkit().createButton(comp,
				Messages.getString("AddStructure.reducing_end"), //$NON-NLS-1$
				SWT.PUSH | SWT.WRAP
			);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 50;
		btn.setLayoutData(data);
		this.m_btnChangeRedEnd = btn;
	}

	private void createResidueSection(Composite sectionClient, boolean isSaccharide) {
		for ( String sup : ResidueDictionary.getSuperclasses() ) {
			// Ignore reducing end
			if ( sup.equals("Reducing end") )
				continue;
			ExpandableComposite group = this.createExpandableComposite(sectionClient);
			group.setText(sup);
			boolean hasContents = false;
			for ( ResidueType type : ResidueDictionary.getResidues(sup) ) {
				if ( type.isSaccharide() != isSaccharide )
					continue;
				Button btn = this.createIconButton(
						(Composite)group.getClient(), null, SWT.PUSH | SWT.CENTER);
				btn.setToolTipText(type.getDescription());
				this.m_mapResTypeToButton.put(type, btn);
				hasContents = true;
			}
			if ( !hasContents )
				group.dispose();
		}
	}

	private Button createIconButton(Composite parent, String text, int style) {
		Button btn = getToolkit().createButton(parent, text, style);
		ColumnLayoutData data = new ColumnLayoutData();
		data.horizontalAlignment = ColumnLayoutData.LEFT;
		btn.setLayoutData(data);
		btn.setBackground( Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND) );
		return btn;
	}

	public void addSelectionListenersForCanvas(GlycanCanvasWithToolBarComposite canvas) {
		SelectionAdapter listenerUpdateView = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.updateView();
			}
		};
		
		for (final ResidueType resType : this.m_mapResTypeToButton.keySet()) {
			Button btn = this.m_mapResTypeToButton.get(resType);
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if ( !ResidueOperationUtils.canAddResidueToCanvas(canvas.getGlycanCanvas(), new Residue(resType)) ) {
						openErrorDialog();
						return;
					}
					canvas.getGlycanCanvas().addResidue(new Residue(resType));
					canvas.getGlycanCanvas().getBuilderWorkspace().getResidueHistory().add(resType);
				}
			});
			btn.addSelectionListener(listenerUpdateView);
		}
		for (final CoreType coreType : this.m_mapCoreTypeToButton.keySet()) {
			Button btn = this.m_mapCoreTypeToButton.get(coreType);
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						Residue root = coreType.newCore();
						if ( !ResidueOperationUtils.canAddResidueToCanvas(canvas.getGlycanCanvas(), root) )
							throw new Exception();
						canvas.getGlycanCanvas().addResidue(root);
						canvas.getGlycanCanvas().getCurrentGlycanLabel().resetSelection();
					} catch (Exception e1) {
						openErrorDialog();
					}
				}
			});
			btn.addSelectionListener(listenerUpdateView);
		}
		for (final TerminalType termType : this.m_mapTerminalTypeToButton.keySet()) {
			Button btn = this.m_mapTerminalTypeToButton.get(termType);
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						Residue root = termType.newTerminal();
						if ( !ResidueOperationUtils.canAddResidueToCanvas(canvas.getGlycanCanvas(), root) )
							throw new Exception();
						canvas.getGlycanCanvas().addResidue(termType.newTerminal());
					} catch (Exception e1) {
						openErrorDialog();
					}
				}
			});
			btn.addSelectionListener(listenerUpdateView);
		}

		this.m_btnChangeRedEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( canvas.getGlycanCanvas().getCurrentGlycanLabel() == null )
					return;
				canvas.getGlycanCanvas().getCurrentGlycanLabel().changeReducingEndType();
			}
		});
		this.m_btnChangeRedEnd.addSelectionListener(listenerUpdateView);
	}

	private void openErrorDialog() {
		MessageBox box = new MessageBox(getForm().getShell(), SWT.OK|SWT.ICON_ERROR);
		box.setMessage("The structure can not be added.");
		box.open();
	}

	@Override
	public void residueIconUpdated() {
		for ( ResidueType type : ResidueDictionary.allResidues() ) {
			Button btn = this.m_mapResTypeToButton.get(type);
			if ( btn != null )
				btn.setImage(GlycanIconProvider.getResidueIcon(type));
		}
		getForm().layout();
	}

	@Override
	public void glycanIconUpdated() {
		Map<String, List<Button>> mapSuperToButtons = new HashMap<>();
		for ( CoreType type : CoreDictionary.getCores() ) {
			Button btn = this.m_mapCoreTypeToButton.get(type);
			if ( btn != null )
				btn.setImage(GlycanIconProvider.getGlycanCoreIcon(type));
			if ( mapSuperToButtons.get(type.getSuperclass()) == null )
				mapSuperToButtons.put(type.getSuperclass(), new ArrayList<>());
			mapSuperToButtons.get(type.getSuperclass()).add(btn);
		}
		this.equalHight(mapSuperToButtons);
		mapSuperToButtons.clear();
		for ( TerminalType type : TerminalDictionary.getTerminals() ) {
			Button btn = this.m_mapTerminalTypeToButton.get(type);
			if ( btn != null )
				btn.setImage(GlycanIconProvider.getGlycanTerminalIcon(type));
			if ( mapSuperToButtons.get(type.getSuperclass()) == null )
				mapSuperToButtons.put(type.getSuperclass(), new ArrayList<>());
			mapSuperToButtons.get(type.getSuperclass()).add(btn);
		}
		this.equalHight(mapSuperToButtons);
		getForm().layout();
	}

	private void equalHight(Map<String, List<Button>> mapSuperToButtons) {
		for ( String strSuper : mapSuperToButtons.keySet() ) {
			List<Button> lButtons = mapSuperToButtons.get(strSuper);
			int iMaxHeight = 0;
			for ( Button btn : lButtons ) {
				Point size = btn.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				if ( iMaxHeight < size.y )
					iMaxHeight = size.y;
			}
			ColumnLayoutData data;
			for ( Button btn : lButtons ) {
				data = new ColumnLayoutData();
				data.horizontalAlignment = ColumnLayoutData.LEFT;
				data.heightHint = iMaxHeight;
				btn.setLayoutData(data);
			}
		}
	}

	public void updateIconImages() {
		residueIconUpdated();
		glycanIconUpdated();
	}
}
