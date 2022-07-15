package org.grits.toolbox.tools.glycanbuilder.widgets.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.MassUtils;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.grits.toolbox.tools.glycanbuilder.core.structure.dictionary.ResidueDictionary;

public class MassOptionsDialog extends TitleAreaDialog {

	private MassOptions m_massOptions;

	private Combo m_cmbIsotope;
	private Combo m_cmbDeriv;
	private Combo m_cmbRedend;
	private Text m_txtRedendName;
	private Text m_txtRedendMass;
	private Button m_btnNegativeMode;
	private Map<String, Spinner> m_mapIonToSpinner;
	private Map<String, Spinner> m_mapExIonToSpinner;

	public MassOptionsDialog(Shell parentShell, MassOptions massOptions) {
		super(parentShell);
		this.m_massOptions = massOptions;
		if (this.m_massOptions == null)
			this.m_massOptions = new MassOptions();

	}

	@Override
	public void create() {
		super.create();
		setTitle("Mass options");
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(4, true);
		container.setLayout(layout);

		createControls(container);
		setParameters(this.m_massOptions);

		return area;
	}

	private void createControls(Composite parent) {
		Label lbl;
		Combo cmb;

		// Isotope
		lbl = new Label(parent, SWT.NONE);
		lbl.setText("Isotope");

		cmb = new Combo(parent, SWT.READ_ONLY);
		cmb.setEnabled(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(cmb);
		this.m_cmbIsotope = cmb;

		/// empty label (2 x 3 spans)
		lbl = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().span(2, 3).applyTo(lbl);

		// Derivatization
		lbl = new Label(parent, SWT.NONE);
		lbl.setText("Derivatization");

		cmb = new Combo(parent, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(cmb);
		this.m_cmbDeriv = cmb;

		// Reducing end
		lbl = new Label(parent, SWT.NONE);
		lbl.setText("Reducing end");

		cmb = new Combo(parent, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(cmb);
		this.m_cmbRedend = cmb;

		/// empty label (1 x 2 spans)
		lbl = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().span(1, 2).applyTo(lbl);

		/// Reducing end name for "Other"
		lbl = new Label(parent, SWT.NONE);
		lbl.setText("name");

		Text txt = new Text(parent, SWT.BORDER);
		txt.setEnabled(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(txt);
		txt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		this.m_txtRedendName = txt;

		/// Reducing ned mass for "Other"
		lbl = new Label(parent, SWT.NONE);
		lbl.setText("mass");

		txt = new Text(parent, SWT.BORDER);
		txt.setText("0");
		txt.setEnabled(false);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(txt);
		txt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});
		this.m_txtRedendMass = txt;

		/// Add selection listener
		this.m_cmbRedend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( ((Combo)e.widget).getText().equals("Other...") ) {
					m_txtRedendName.setEnabled(true);
					m_txtRedendMass.setEnabled(true);
					validate();
				} else {
					m_txtRedendName.setEnabled(false);
					m_txtRedendMass.setEnabled(false);
					setErrorMessage(null);
				}
			}
		});

		// Horizontal separator label
		lbl = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(4, 1).applyTo(lbl);

		// Negarive mode
//		lbl = new Label(parent, SWT.NONE);
//		lbl.setText("Negative mode");

		Button btn = new Button(parent, SWT.CHECK);
		btn.setText("Negative mode");
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).applyTo(btn);
		this.m_btnNegativeMode = btn;

		/// empty label (2 x 2 spans)
		lbl = new Label(parent, SWT.NONE);
		GridDataFactory.fillDefaults().span(2, 2).applyTo(lbl);

		// Ions
		this.m_mapIonToSpinner = new HashMap<>();
		this.m_mapExIonToSpinner = new HashMap<>();

		// H
		createIonSpinnerControl(parent, MassOptions.ION_H);

		// Na
		createIonSpinnerControl(parent, MassOptions.ION_NA);

		// Li
		createIonSpinnerControl(parent, MassOptions.ION_LI);

		// K
		createIonSpinnerControl(parent, MassOptions.ION_K);

		// Horizontal separator label
		lbl = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(4, 1).applyTo(lbl);

		// Cl
		createIonSpinnerControl(parent, MassOptions.ION_CL);

		// H2PO4
		createIonSpinnerControl(parent, MassOptions.ION_H2PO4);
	}

	private Spinner getDefaultSpinner(Composite parent) {
		Spinner spn = new Spinner(parent, SWT.BORDER);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(spn);
		return spn;
	}

	private void createIonSpinnerControl(Composite parent, String strIon) {
		// Ion
		Label lbl = new Label(parent, SWT.NONE);
		lbl.setText("# " + strIon + " ions");

		Spinner spn = getDefaultSpinner(parent);
		this.m_mapIonToSpinner.put(strIon, spn);

		// No ex ions for "H"
		if (strIon.equals(MassOptions.ION_H))
			return;

		// Ex ion
		lbl = new Label(parent, SWT.NONE);
		lbl.setText("ex. " + strIon + " ions");

		spn = getDefaultSpinner(parent);
		this.m_mapExIonToSpinner.put(strIon, spn);
	}

	private void setParameters(MassOptions massOptions) {
		this.m_cmbIsotope.setItems(new String[] { /*"---",*/ MassOptions.ISOTOPE_MONO, MassOptions.ISOTOPE_AVG });

		this.m_cmbDeriv.setItems(new String[] { /*"---",*/ MassOptions.NO_DERIVATIZATION, MassOptions.PERMETHYLATED,
				MassOptions.PERDMETHYLATED, MassOptions.PERACETYLATED, MassOptions.PERDACETYLATED });

		List<String> lRedends = new ArrayList<>();
//		lRedends.add("---");
		lRedends.addAll(ResidueDictionary.getReducingEndsString());
		lRedends.add("Other...");
		this.m_cmbRedend.setItems(lRedends.toArray(new String[0]));

		for (String strIon : this.m_mapIonToSpinner.keySet()) {
			Spinner spn = this.m_mapIonToSpinner.get(strIon);
			spn.setMinimum(0);
			spn.setMaximum(10);
			spn.setSelection(0);
		}

		for (String strExIon : this.m_mapExIonToSpinner.keySet()) {
			Spinner spn = this.m_mapExIonToSpinner.get(strExIon);
			spn.setMinimum(0);
			spn.setMaximum(50);
			spn.setSelection(0);
		}

		// For sets of reducing end
		// set selections
		this.m_cmbIsotope.setText(massOptions.ISOTOPE);
		this.m_cmbDeriv.setText(massOptions.DERIVATIZATION);

		if (massOptions.REDUCING_END_TYPE == null) {
			this.m_cmbRedend.select(0);
			this.m_txtRedendName.setText("");
			this.m_txtRedendMass.setText("0");
		} else if (massOptions.REDUCING_END_TYPE.isCustomType()) {
			this.m_cmbRedend.setText("Other...");
			this.m_txtRedendName.setEnabled(true);
			this.m_txtRedendName.setText(massOptions.REDUCING_END_TYPE.getResidueName());
			this.m_txtRedendMass.setEnabled(true);
			// Remove water mass
			double mass = massOptions.REDUCING_END_TYPE.getResidueMassMain()-MassUtils.water.getMainMass();
			this.m_txtRedendMass.setText("" + mass);
		} else {
			this.m_cmbRedend.setText(massOptions.REDUCING_END_TYPE.getName());
			this.m_txtRedendName.setText("");
			this.m_txtRedendMass.setText("0");
		}

		this.m_btnNegativeMode.setSelection(massOptions.ION_CLOUD.isNegative());

		for ( String strIon : this.m_mapIonToSpinner.keySet() ) {
			Spinner spnIon = this.m_mapIonToSpinner.get(strIon);
			int value = massOptions.ION_CLOUD.get(strIon);
			spnIon.setSelection(value);
		}

		for ( String strExIon : this.m_mapExIonToSpinner.keySet() ) {
			Spinner spnIon = this.m_mapExIonToSpinner.get(strExIon);
			int value = massOptions.NEUTRAL_EXCHANGES.get(strExIon);
			spnIon.setSelection(value);
		}
	}

	private void save() {
		this.m_massOptions.ISOTOPE = this.m_cmbIsotope.getText();
		this.m_massOptions.DERIVATIZATION = this.m_cmbDeriv.getText();

		ResidueType resTypeRedend;
		if ( this.m_cmbRedend.getText().equals("Other...") ) {
			resTypeRedend = ResidueType.createOtherReducingEnd(
					this.m_txtRedendName.getText(),
					Double.valueOf( this.m_txtRedendMass.getText() )
				);
			
		} else {
			resTypeRedend = ResidueDictionary.findResidueType(this.m_cmbRedend.getText());
		}
		this.m_massOptions.REDUCING_END_TYPE = resTypeRedend;

		int multiplier = (this.m_btnNegativeMode.getSelection())? -1 : 1;
		for ( String strIon : this.m_mapIonToSpinner.keySet() ) {
			int nIon = this.m_mapIonToSpinner.get(strIon).getSelection();
			if ( !strIon.equals(MassOptions.ION_CL) && !strIon.equals(MassOptions.ION_H2PO4) )
				nIon *= multiplier;
			this.m_massOptions.ION_CLOUD.set(strIon,nIon);
		}
		int nExchange = 0;
		for ( String strIon : this.m_mapExIonToSpinner.keySet() ) {
			int nIon = this.m_mapExIonToSpinner.get(strIon).getSelection();
			this.m_massOptions.NEUTRAL_EXCHANGES.set(strIon,nIon);
			nExchange += nIon;
		}
		this.m_massOptions.NEUTRAL_EXCHANGES.set(MassOptions.ION_H, -nExchange);
		
	}

	private static final int MAX_REDEND_NAME_LENGTH = 20;

	private boolean validate() {
		this.setErrorMessage(null);
		if ( !this.m_cmbRedend.getText().equals("Other...") ) {
			return true;
		}

//		if ( ResidueDictionary.findResidueType(this.m_txtRedendName.getText())!=null ) {
//			this.setErrorMessage("The name specified for the reducing end is already existing.");
//			return false;
//		}
		// For symbol restrictions in reducing end name
		Character[] symbols = {'#','.','_'};
		List<Character> lAllowedSymbols = Arrays.asList(symbols); 
		String strRedend = this.m_txtRedendName.getText();
		for ( int i=0; i<strRedend.length(); i++ ) {
			char c = strRedend.charAt(i);
			if ( Character.isAlphabetic(c) )
				continue;
			if ( Character.isDigit(c) )
				continue;
			if ( lAllowedSymbols.contains(Character.valueOf(c)) )
				continue;
			this.setErrorMessage("The symbol \""+c+"\" cannot be used for reducing end name.");
			return false;
		}
		// For length of reducing end name
		if ( strRedend.length() > MAX_REDEND_NAME_LENGTH ) {
			this.setErrorMessage("The reducing end name is too long.");
		}

		// For value of reducing end mass
		try {
			Double.valueOf(this.m_txtRedendMass.getText());
		} catch (NumberFormatException e) {
			this.setErrorMessage("The mass value specified for the reducing end is not valid.");
			return false;
		}
		return true;
	}

	@Override
	protected void okPressed() {
		if ( !validate() ) {
			MessageBox box = new MessageBox(this.getShell(), SWT.OK|SWT.ERROR);
			box.setMessage(this.getErrorMessage());
			return;
		}
		save();
		super.okPressed();
	}

}
