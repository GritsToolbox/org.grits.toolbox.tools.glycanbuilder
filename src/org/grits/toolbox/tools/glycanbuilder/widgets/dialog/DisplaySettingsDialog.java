package org.grits.toolbox.tools.glycanbuilder.widgets.dialog;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;

public class DisplaySettingsDialog extends TitleAreaDialog {

	private GraphicOptionsSWT m_options;

	private Combo m_cmbDisplay;
	private Button m_btnShowInfo;
	private Spinner m_spnNodeSize;
	private Spinner m_spnNodeFontSize;
	private Combo m_cmbNodeFontFace;
	private Spinner m_spnCompositionFontSize;
	private Combo m_cmbCompositionFontFace;
	private Spinner m_spnLinkageInfoSize;
	private Combo m_cmbLinkageInfoFontFace;
	private Spinner m_spnNodeSpace;
	private Spinner m_spnNodeSubSpace;
	private Spinner m_spnStructuresSpace;
	private Spinner m_spnMassTextSpace;
	private Spinner m_spnMassTextSize;
	private Combo m_cmbMassTextFontFace;

	public DisplaySettingsDialog(Shell parentShell, GraphicOptionsSWT options) {
		super(parentShell);
		this.m_options = options;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Display settings");
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		GridLayout layout = new GridLayout(2, true);
		container.setLayout(layout);

		createSettingsControls(container);
		setSettingsParams( this.m_options );

		return area;
	}

	private void createSettingsControls(Composite container) {
		String[] availableFonts = GraphicOptionsSWT.getAllFontFaces().toArray(new String[0]);

		Label lbl;
		lbl = new Label(container, SWT.NONE);
		lbl.setText("Display type");

		m_cmbDisplay = getNewCombo(container, GraphicOptionsSWT.DISPLAYS, 0);
		m_cmbDisplay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String strDisplay = ((Combo)e.widget).getText();
				m_options.setDisplay(strDisplay);
				setSettingsParams( m_options );
			}
		});

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Show linkage info");

		m_btnShowInfo = new Button(container, SWT.CHECK);
		m_btnShowInfo.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Residue size");

		m_spnNodeSize = getNewSpinner(container, 1, 100, 1, 1);
		m_spnNodeSize.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Residue text size");

		m_spnNodeFontSize = getNewSpinner(container, 1, 40, 1, 1);
		m_spnNodeFontSize.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Residue text font");

		m_cmbNodeFontFace = getNewCombo(container, availableFonts, 0);
		m_cmbNodeFontFace.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Composition text size");

		m_spnCompositionFontSize = getNewSpinner(container, 1, 40, 1, 1);
		m_spnCompositionFontSize.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Composition text font");

		m_cmbCompositionFontFace = getNewCombo(container, availableFonts, 0);
		m_cmbCompositionFontFace.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Linkage info text size");

		m_spnLinkageInfoSize = getNewSpinner(container, 1, 40, 1, 1);
		m_spnLinkageInfoSize.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Linkage info text font");

		m_cmbLinkageInfoFontFace = getNewCombo(container, availableFonts, 0);
		m_cmbLinkageInfoFontFace.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Space between residues");

		m_spnNodeSpace = getNewSpinner(container, 1, 100, 1, 1);
		m_spnNodeSpace.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Space between border residues");

		m_spnNodeSubSpace = getNewSpinner(container, 1, 40, 1, 1);
		m_spnNodeSubSpace.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Space between structures");

		m_spnStructuresSpace = getNewSpinner(container, 1, 100, 1, 1);
		m_spnStructuresSpace.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Space before mass text");

		m_spnMassTextSpace = getNewSpinner(container, 1, 100, 1, 1);
		m_spnMassTextSpace.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Mass text size");

		m_spnMassTextSize = getNewSpinner(container, 1, 40, 1, 1);
		m_spnMassTextSize.addSelectionListener( getNewSelectionAdapterForCustom() );

		lbl = new Label(container, SWT.NONE);
		lbl.setText("Mass text font");

		m_cmbMassTextFontFace = getNewCombo(container, availableFonts, 0);
		m_cmbMassTextFontFace.addSelectionListener( getNewSelectionAdapterForCustom() );
	}

	private Combo getNewCombo(Composite container, String[] items, int selection) {
		Combo cmb = new Combo(container, SWT.NONE);
		cmb.setItems(items);
		cmb.select(selection);
		cmb.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		return cmb;
	}

	private Spinner getNewSpinner(Composite container, int min, int max, int increment, int selection) {
		Spinner spn = new Spinner(container, SWT.BORDER);
		spn.setMinimum(min);
		spn.setMaximum(max);
		spn.setIncrement(increment);
		spn.setSelection(selection);
		spn.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		return spn;
	}

	private SelectionListener getNewSelectionAdapterForCustom() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Change selection to custom
				m_cmbDisplay.select(3); // GraphicOptions.DISPLAY_CUSTOM
				saveCustom();
			}
		};
	}

	private void setSettingsParams(GraphicOptionsSWT options) {
		List<String> allFontFaces = GraphicOptionsSWT.getAllFontFaces();

		double oldScale = options.SCALE;

		options.adjustSize(false);
		options.setScale(1.);

		m_cmbDisplay.select( Arrays.asList(GraphicOptionsSWT.DISPLAYS).indexOf(options.DISPLAY) );

		m_btnShowInfo.setSelection(options.SHOW_INFO);

		m_spnNodeSize.setSelection(options.NODE_SIZE);
		m_spnNodeFontSize.setSelection(options.NODE_FONT_SIZE);
		m_cmbNodeFontFace.select( allFontFaces.indexOf(options.NODE_FONT_FACE) );

		m_spnCompositionFontSize.setSelection(options.COMPOSITION_FONT_SIZE);
		m_cmbCompositionFontFace.select( allFontFaces.indexOf(options.COMPOSITION_FONT_FACE) );

		m_spnLinkageInfoSize.setSelection(options.LINKAGE_INFO_SIZE);
		m_cmbLinkageInfoFontFace.select( allFontFaces.indexOf(options.LINKAGE_INFO_FONT_FACE) );

		m_spnNodeSpace.setSelection(options.NODE_SPACE);
		m_spnNodeSubSpace.setSelection(options.NODE_SUB_SPACE);
		m_spnStructuresSpace.setSelection(options.STRUCTURES_SPACE);

		m_spnMassTextSpace.setSelection(options.MASS_TEXT_SPACE);
		m_spnMassTextSize.setSelection(options.MASS_TEXT_SIZE);
		m_cmbMassTextFontFace.select( allFontFaces.indexOf(options.MASS_TEXT_FONT_FACE) );

		options.adjustSize(true);
		options.setScale(oldScale);
	}

	private void saveCustom() {
		m_options.SHOW_INFO_CUSTOM = m_btnShowInfo.getSelection();

		m_options.NODE_SIZE_CUSTOM      = m_spnNodeSize.getSelection();
		m_options.NODE_FONT_SIZE_CUSTOM = m_spnNodeFontSize.getSelection();
		m_options.NODE_FONT_FACE_CUSTOM = m_cmbNodeFontFace.getText();

		m_options.COMPOSITION_FONT_SIZE_CUSTOM = m_spnCompositionFontSize.getSelection();
		m_options.COMPOSITION_FONT_FACE_CUSTOM = m_cmbCompositionFontFace.getText();

		m_options.LINKAGE_INFO_SIZE_CUSTOM      = m_spnLinkageInfoSize.getSelection();
		m_options.LINKAGE_INFO_FONT_FACE_CUSTOM = m_cmbLinkageInfoFontFace.getText();

		m_options.NODE_SPACE_CUSTOM       = m_spnNodeSpace.getSelection();
		m_options.NODE_SUB_SPACE_CUSTOM   = m_spnNodeSubSpace.getSelection();
		m_options.STRUCTURES_SPACE_CUSTOM = m_spnStructuresSpace.getSelection();

		m_options.MASS_TEXT_SPACE_CUSTOM     = m_spnMassTextSpace.getSelection();
		m_options.MASS_TEXT_SIZE_CUSTOM      = m_spnMassTextSize.getSelection();
		m_options.MASS_TEXT_FONT_FACE_CUSTOM = m_cmbMassTextFontFace.getText();

		m_options.setDisplay( m_cmbDisplay.getText() );
	}

}
