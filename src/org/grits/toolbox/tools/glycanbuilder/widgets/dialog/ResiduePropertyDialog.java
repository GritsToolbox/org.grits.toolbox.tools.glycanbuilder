package org.grits.toolbox.tools.glycanbuilder.widgets.dialog;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eurocarbdb.application.glycanbuilder.Linkage;
import org.eurocarbdb.application.glycanbuilder.Residue;

public class ResiduePropertyDialog extends TitleAreaDialog {

	private Residue m_res;

	private List m_lsPos;
	private Combo m_cmbAnom;
	private Combo m_cmbAnomPos;
	private Combo m_cmbChiral;
	private Combo m_cmbRing;
	private Button m_btnHasSecond;
	private List m_lsPos2;
	private Combo m_cmbAnomPos2;

	public ResiduePropertyDialog(Shell parentShell, Residue res) {
		super(parentShell);
		this.m_res = res;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Residue properties");
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout layout = new GridLayout(2, true);
		container.setLayout(layout);

		createPropertyControls(container);
		setResidueParams(this.m_res);

		area.pack();
		return area;
	}

	private void createPropertyControls(Composite container) {
		Label lblLink = new Label(container, SWT.NONE);
		lblLink.setText("Linkage position");

		m_lsPos = new List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		m_lsPos.setItems( new String[] {"?", "1", "2", "3", "4", "5", "6"} );
		m_lsPos.setEnabled(false);
		this.setGridData(m_lsPos);

		Label lblAnomPos = new Label(container, SWT.NONE);
		lblAnomPos.setText("Anomeric carbon");

		m_cmbAnomPos = new Combo(container, SWT.NONE);
		m_cmbAnomPos.setItems(new String[] {"?", "1", "2", "3"});
		m_cmbAnomPos.select(1);
		m_cmbAnomPos.setEnabled(false);
		this.setGridData(m_cmbAnomPos);

		Label lblAnom = new Label(container, SWT.NONE);
		lblAnom.setText("Anomeric state");

		m_cmbAnom = new Combo(container, SWT.NONE);
		m_cmbAnom.setItems(new String[] {"?", "a", "b" });
		m_cmbAnom.select(0);
		m_cmbAnom.setEnabled(false);
		this.setGridData(m_cmbAnom);

		Label lblChiral = new Label(container, SWT.NONE);
		lblChiral.setText("Chirality");

		m_cmbChiral = new Combo(container, SWT.NONE);
		m_cmbChiral.setItems(new String[] {"?", "D", "L" });
		m_cmbChiral.select(0);
		m_cmbChiral.setEnabled(false);
		this.setGridData(m_cmbChiral);

		Label lblRing = new Label(container, SWT.NONE);
		lblRing.setText("Ring size");

		m_cmbRing = new Combo(container, SWT.NONE);
		m_cmbRing.setItems(new String[] {"?", "p", "f", "o" });
		m_cmbRing.select(0);
		m_cmbRing.setEnabled(false);
		this.setGridData(m_cmbRing);

		m_btnHasSecond = new Button(container, SWT.CHECK | SWT.LEFT);
		m_btnHasSecond.setText("Second bond");
		m_btnHasSecond.setEnabled(false);
		m_btnHasSecond.setLayoutData( new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1) );
		Label lblPos2 = new Label(container, SWT.NONE);
		lblPos2.setText("Parent position");

		m_lsPos2 = new List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		m_lsPos2.setItems( new String[] {"?", "1", "2", "3", "4", "5", "6"} );
		m_lsPos2.setEnabled(false);
		this.setGridData(m_lsPos2);

		Label lblAnomPos2 = new Label(container, SWT.NONE);
		lblAnomPos2.setText("Child position");

		m_cmbAnomPos2 = new Combo(container, SWT.NONE);
		m_cmbAnomPos2.setItems(new String[] {"?", "1", "2", "3"});
		m_cmbAnomPos2.select(0);
		m_cmbAnomPos2.setEnabled(false);
		this.setGridData(m_cmbAnomPos2);

		m_btnHasSecond.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( ((Button)e.widget).getSelection()
				&& ( m_res != null && m_res.getParentLinkage() != null ) ) {
					m_cmbAnomPos2.setEnabled(true);
					m_lsPos2.setEnabled(true);
					return;
				}
				m_cmbAnomPos2.setEnabled(false);
				m_lsPos2.setEnabled(false);

			}
		});
	}

	private void setGridData(Control control) {
		GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false);
		gd.heightHint = control.computeSize(SWT.DEFAULT, 40).y;
		control.setLayoutData(gd);
	}

	private void setResidueParams(Residue res) {
		if ( res.isSaccharide() ) {
			m_cmbAnom.setEnabled(true);
			m_cmbAnom.select( this.getComboIndex(m_cmbAnom, res.getAnomericState()) );

			m_cmbAnomPos.setEnabled(true);
			m_cmbAnomPos.select( this.getComboIndex(m_cmbAnomPos, res.getAnomericCarbon()) );

			m_cmbChiral.setEnabled(true);
			m_cmbChiral.select( this.getComboIndex(m_cmbChiral, res.getChirality()) );

			m_cmbRing.setEnabled(true);
			m_cmbRing.select( this.getComboIndex(m_cmbRing, res.getRingSize()) );
		}

		// For parent linkages
		if ( res.getParentLinkage() == null )
			return;
		Linkage linkParent = res.getParentLinkage();

		m_btnHasSecond.setEnabled(true);
		if ( linkParent.hasMultipleBonds() )
			m_btnHasSecond.setSelection(true);

		char[] possDef = linkParent.getParentResidue().getType().getLinkagePositions();
		if ( linkParent.getParentResidue().isBracket() ) 
			possDef = new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'N'};
		if ( possDef.length != 0 ) {
			// Reset positions
			ArrayList<String> lDefPoss = new ArrayList<>();
			lDefPoss.add("?");
			for ( char posNew : possDef ) {
				if (posNew == ' ')
					continue;
				lDefPoss.add(posNew+"");
			}
			m_lsPos.setItems(lDefPoss.toArray(new String[0]));
			m_lsPos2.setItems(lDefPoss.toArray(new String[0]));

			// Set selections
			m_lsPos.setEnabled(true);
			char[] poss = linkParent.glycosidicBond().getParentPositions();
			m_lsPos.select( this.getListIndeces(m_lsPos, poss) );

			// Set selections if multiple bonds
			if ( linkParent.hasMultipleBonds() ) {
				poss = linkParent.getBonds().get(0).getParentPositions();
				m_lsPos2.select( this.getListIndeces(m_lsPos2, poss) );
			}
		}

		if ( m_btnHasSecond.getSelection() ) {
			m_lsPos2.setEnabled(true);
			m_cmbAnomPos2.setEnabled(true);
		}
		m_cmbAnomPos2.select( this.getComboIndex(m_cmbAnomPos2, linkParent.getBonds().get(0).getChildPosition()) );

	}

	private int[] getListIndeces(List list, char[] values) {
		ArrayList<Integer> lInxs = new ArrayList<>();
		for ( int i=0; i<list.getItemCount(); i++ ) {
			String item = list.getItems()[i];
			for ( char value : values )
				if ( item.equals(value+"") )
					lInxs.add(i);
		}
		int[] inxs = new int[lInxs.size()];
		for ( int i=0; i<lInxs.size(); i++ )
			inxs[i] = ((ArrayList<Integer>) lInxs).get(i);
		if ( inxs.length == 0 )
			return new int[] {0};
		return inxs;
	}

	private int getComboIndex(Combo cmb, char value) {
		int inx = 0;
		for ( String item : cmb.getItems() ) {
			if ( !item.equals(value+"") )
				continue;
			inx = cmb.indexOf(value+"");
		}
		return inx;
	}

	private char[] getSelectedPositions(List list) {
		String[] selected = list.getSelection();
		if ( selected.length == 0 )
			return new char[] {'?'};
		char[] poss = new char[selected.length];
		for (int i=0; i<selected.length; i++)
			poss[i] = selected[i].charAt(0);
		return poss;
	}

	private void save() {
		Linkage linkParent = this.m_res.getParentLinkage();

		this.m_res.setAnomericState(m_cmbAnom.getText().charAt(0));

		this.m_res.setAnomericCarbon(m_cmbAnomPos.getText().charAt(0));

		this.m_res.setChirality(m_cmbChiral.getText().charAt(0));

		this.m_res.setRingSize(m_cmbRing.getText().charAt(0));

		if ( linkParent == null )
			return;
		char[] poss = getSelectedPositions(this.m_lsPos);

		if ( !m_btnHasSecond.getSelection() ) {
			linkParent.setLinkagePositions(poss);
			return;
		}
		char[] poss2 = getSelectedPositions(this.m_lsPos2);
		linkParent.setLinkagePositions(poss, poss2, m_cmbAnomPos2.getText().charAt(0));
	}

	@Override
	protected void okPressed() {
		save();
		super.okPressed();
	}
}
