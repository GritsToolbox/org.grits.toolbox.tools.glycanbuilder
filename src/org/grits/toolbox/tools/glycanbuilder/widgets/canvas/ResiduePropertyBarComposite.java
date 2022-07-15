package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.eurocarbdb.application.glycanbuilder.Linkage;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.MultiSelectionCombo;

public class ResiduePropertyBarComposite extends Composite {

	private GlycanCanvasComposite m_canvas;

	private Combo m_cmbAnom;
	private Combo m_cmbAnomPos;
	private MultiSelectionCombo m_mcmbPos;
	private Combo m_cmbChiral;
	private Combo m_cmbRing;
	private Button m_btnHasSecond;
	private Combo m_cmbAnomPos2;
	private MultiSelectionCombo m_mcmbPos2;

	public ResiduePropertyBarComposite(Composite parent, GlycanCanvasComposite canvas) {
		super(parent, SWT.NONE);

//		GridLayout layout = new GridLayout(13, false);
		RowLayout layout = new RowLayout();
		layout.center = true;
		layout.fill = true;
		layout.wrap = true;
//		layout.pack = false;
		layout.spacing = 10;
		this.setLayout(layout);

		createControls();
		setDefault();

		this.m_canvas = canvas;
		addMouseListnerToCanvas();
	}

	public void createControls() {
		for ( Control ctl : this.getChildren() )
			ctl.dispose();

		CLabel lbl = new CLabel(this, SWT.CENTER);
		lbl.setText("Linkage");

		m_cmbAnom = new Combo(this, SWT.NONE);

		m_cmbAnomPos = new Combo(this, SWT.NONE);

		lbl = new CLabel(this, SWT.CENTER);
		lbl.setText("->");

		m_mcmbPos = new MultiSelectionCombo(this, SWT.BORDER);

		lbl = new CLabel(this, SWT.CENTER);
		lbl.setText("Chirality");

		m_cmbChiral = new Combo(this, SWT.NONE);

		lbl = new CLabel(this, SWT.CENTER);
		lbl.setText("Ring");

		m_cmbRing = new Combo(this, SWT.NONE);

		m_btnHasSecond = new Button(this, SWT.CHECK | SWT.LEFT);
		m_btnHasSecond.setText("2nd bond");

		m_cmbAnomPos2 = new Combo(this, SWT.NONE);

		lbl = new CLabel(this, SWT.CENTER);
		lbl.setText("->");

		m_mcmbPos2 = new MultiSelectionCombo(this, SWT.BORDER);
	}

	public void setDefault() {
		m_cmbAnom.setItems(new String[] {"?", "a", "b" });
		m_cmbAnom.select(0);
		m_cmbAnom.setEnabled(false);

		this.setDefaultPos(m_mcmbPos);
		m_mcmbPos.setEnabled(false);

		m_cmbAnomPos.setItems(new String[] {"?", "1", "2", "3"});
		m_cmbAnomPos.select(1);
		m_cmbAnomPos.setEnabled(false);

		m_cmbChiral.setItems(new String[] {"?", "D", "L" });
		m_cmbChiral.select(0);
		m_cmbChiral.setEnabled(false);

		m_cmbRing.setItems(new String[] {"?", "p", "f", "o" });
		m_cmbRing.select(0);
		m_cmbRing.setEnabled(false);

		m_btnHasSecond.setEnabled(false);

		m_cmbAnomPos2.setItems(new String[] {"?", "1", "2", "3"});
		m_cmbAnomPos2.select(0);
		m_cmbAnomPos2.setEnabled(false);

		this.setDefaultPos(m_mcmbPos2);
		m_mcmbPos2.setEnabled(false);
	}

	private void setDefaultPos(MultiSelectionCombo mcmb) {
		mcmb.setItems(new String[] {"?", "1", "2", "3", "4", "5", "6"});
		mcmb.select(0);
	}

	public void setResidue(final Residue res) {
		setDefault();

		SelectionAdapter selectionListenerSave = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				save(res);
				m_canvas.getCurrentGlycanLabel().saveDocument();
				m_canvas.updateView();
			}
		};
		MouseAdapter mouseListenerSave = new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				save(res);
				m_canvas.getCurrentGlycanLabel().saveDocument();
				m_canvas.updateView();
			}
		};

		final Linkage linkParent = res.getParentLinkage();

		if ( res.isSaccharide() ) {
			// Anomer symbol
			m_cmbAnom.setEnabled(true);
			m_cmbAnom.select( this.getComboIndex(m_cmbAnom, res.getAnomericState()) );
			removeOldSelectionListeners(m_cmbAnom);
			m_cmbAnom.addSelectionListener(selectionListenerSave);

			// Anomeric position
			m_cmbAnomPos.setEnabled(true);
			m_cmbAnomPos.select( this.getComboIndex(m_cmbAnomPos, res.getAnomericCarbon()) );
			removeOldSelectionListeners(m_cmbAnomPos);
			m_cmbAnomPos.addSelectionListener(selectionListenerSave);

			// Chiral
			m_cmbChiral.setEnabled(true);
			m_cmbChiral.select( this.getComboIndex(m_cmbChiral, res.getChirality()) );
			removeOldSelectionListeners(m_cmbChiral);
			m_cmbChiral.addSelectionListener(selectionListenerSave);

			// Ring
			m_cmbRing.setEnabled(true);
			m_cmbRing.select( this.getComboIndex(m_cmbRing, res.getRingSize()) );
			removeOldSelectionListeners(m_cmbRing);
			m_cmbRing.addSelectionListener(selectionListenerSave);

		}

		removeOldSelectionListeners(m_btnHasSecond);
		m_btnHasSecond.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( ((Button)e.widget).getSelection() ) {
					m_cmbAnomPos2.setEnabled(true);
					m_mcmbPos2.setEnabled(true);
				} else {
					m_cmbAnomPos2.setEnabled(false);
					m_mcmbPos2.setEnabled(false);
				}
			}
		});
		m_btnHasSecond.addSelectionListener(selectionListenerSave);

		if ( linkParent == null || linkParent.getParentResidue().isReducingEnd() ) {
			setDefaultPos(m_mcmbPos);
			setDefaultPos(m_mcmbPos2);
			m_mcmbPos.setEnabled(false);
			m_mcmbPos2.setEnabled(false);
			m_btnHasSecond.setEnabled(false);
			return;
		}

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
			m_mcmbPos.removeAll();
			m_mcmbPos.setItems(lDefPoss.toArray(new String[0]));
			m_mcmbPos2.removeAll();
			m_mcmbPos2.setItems(lDefPoss.toArray(new String[0]));

			// Set selections
			m_mcmbPos.setEnabled(true);
			char[] poss = linkParent.glycosidicBond().getParentPositions();
			m_mcmbPos.setSelectionIndices( this.getMultiComboIndex(m_mcmbPos, poss) );
			m_mcmbPos.resetMouseListener();
			m_mcmbPos.addMouseListener(mouseListenerSave);

			// Set selections
			if ( linkParent.hasMultipleBonds() ) {
				poss = linkParent.getBonds().get(0).getParentPositions();
				m_mcmbPos2.setSelectionIndices( this.getMultiComboIndex(m_mcmbPos2, poss) );
			}
			m_mcmbPos2.resetMouseListener();
			m_mcmbPos2.addMouseListener(mouseListenerSave);
		}
		else {
			setDefaultPos(m_mcmbPos);
			setDefaultPos(m_mcmbPos2);
			m_mcmbPos.setEnabled(false);
			m_mcmbPos2.setEnabled(false);
			m_btnHasSecond.setEnabled(false);
			return;
		}


		if ( m_btnHasSecond.getSelection() ) {
			m_mcmbPos2.setEnabled(true);
			m_cmbAnomPos2.setEnabled(true);
		}
		m_cmbAnomPos2.select( this.getComboIndex(m_cmbAnomPos2, linkParent.getBonds().get(0).getChildPosition()) );
		removeOldSelectionListeners(m_cmbAnomPos2);
		m_cmbAnomPos2.addSelectionListener(selectionListenerSave);
	}

	public void updateCurrentResidue() {
		if (m_canvas.getCurrentGlycanLabel() != null
		 && m_canvas.getCurrentGlycanLabel().getCurrentResidue() != null )
			setResidue(m_canvas.getCurrentGlycanLabel().getCurrentResidue());
		else
			setDefault();
	}

	private void addMouseListnerToCanvas() {
		this.m_canvas.addCanvasMouseListner(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				updateCurrentResidue();
			}
		});
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

	private int[] getMultiComboIndex(MultiSelectionCombo cmb, char[] values) {
		List<Integer> lInxs = new ArrayList<>();
		for ( int i=0; i<cmb.getItemCount(); i++ ) {
			String item = cmb.getItems()[i];
			for ( char value : values )
				if ( item.equals(value+"") )
					lInxs.add(i);
		}
		int[] inxs = new int[lInxs.size()];
		for ( int i=0; i<lInxs.size(); i++ )
			inxs[i] = lInxs.get(i);
		if ( inxs.length == 0 ) {
			cmb.setSelectionIndices(new int[] {0});
			return cmb.getSelectionIndices();
		}
		return inxs;
	}

	private char[] getSelectedPositions(MultiSelectionCombo cmb) {
		String[] texts = cmb.getSelections();
		char[] poss = new char[texts.length];
		for ( int i=0; i<texts.length; i++ )
			poss[i] = texts[i].charAt(0);
		if ( poss.length == 0 ) {
			cmb.setSelectionIndices(new int[] {0});
			return new char[] {'?'};
		}
		return poss;
	}

	private void save(Residue res) {
		Linkage linkParent = res.getParentLinkage();

		res.setAnomericState(m_cmbAnom.getText().charAt(0));

		res.setAnomericCarbon(m_cmbAnomPos.getText().charAt(0));

		res.setChirality(m_cmbChiral.getText().charAt(0));

		res.setRingSize(m_cmbRing.getText().charAt(0));

		if ( linkParent == null )
			return;
		char[] poss = getSelectedPositions(this.m_mcmbPos);

		if ( !m_btnHasSecond.getSelection() ) {
			linkParent.setLinkagePositions(poss);
			return;
		}
		char[] poss2 = getSelectedPositions(this.m_mcmbPos2);
		linkParent.setLinkagePositions(poss, poss2, m_cmbAnomPos2.getText().charAt(0));
	}

	private void removeOldSelectionListeners(Widget widget) {
		for ( Listener listener : widget.getListeners(SWT.Selection) )
			widget.removeListener(SWT.Selection, listener);
	}
}
