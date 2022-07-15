package org.grits.toolbox.tools.glycanbuilder.widgets.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.grits.toolbox.tools.glycanbuilder.core.io.SequenceFormat;

public class ImportTextComposite {

	private FormToolkit m_toolkit;

	private Combo m_cmbFormat;
	private Text m_txtSequence;
	private Button m_btnImport;
	private Button m_btnClear;

	private List<SelectionListener> m_lImportListeners;

	private String m_strSequence;
	private SequenceFormat m_format;

	public ImportTextComposite(Composite parent, FormToolkit toolkit) {
		this.m_lImportListeners = new ArrayList<>();
		this.m_toolkit = toolkit;
		createControls(parent);
	}

	public ImportTextComposite(Composite parent) {
		this.m_lImportListeners = new ArrayList<>();
		this.m_toolkit = new FormToolkit(parent.getDisplay());
		createControls(parent);
	}

	public String getSequence() {
		return this.m_strSequence;
	}

	public SequenceFormat getSequenceFormat() {
		return this.m_format;
	}

	private void createControls(Composite parent) {
		parent.setLayout(new GridLayout(2, true));

		// Create controls
		this.m_toolkit.createLabel(parent, "String encoded");

		// Combo for format selection
		this.m_cmbFormat = new Combo(parent, SWT.READ_ONLY);
		this.m_cmbFormat.setItems(SequenceFormat.getNames());
		this.m_cmbFormat.select(0); // GWS
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(this.m_cmbFormat);

		// Text for sequence
		this.m_txtSequence = this.createText(parent, null, "Enter a glycan sequence", SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 80).span(2, 1).applyTo(this.m_txtSequence);

		// Button for import
		this.m_btnImport = this.m_toolkit.createButton(parent, "Import", SWT.FLAT);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(this.m_btnImport);
		this.m_btnImport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setValues();
			}
		});

		// Button for clear
		this.m_btnClear = this.m_toolkit.createButton(parent, "Clear", SWT.FLAT);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(this.m_btnClear);
		this.m_btnClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearText();
			}
		});
	}

	private Text createText(Composite parent, String value, String message, int style) {
		style |= SWT.MULTI | SWT.WRAP | SWT.V_SCROLL;
		Text txt = this.m_toolkit.createText(parent, value, style);
		txt.setMessage(message);
		txt.setToolTipText(message);

		// Add scroll bar listener
		Listener scrollBarListener = new Listener () {
			@Override
			public void handleEvent(Event event) {
				Text t = (Text)event.widget;
				if ( t.getVerticalBar() != null ) {
					Rectangle r1 = t.getClientArea();
					Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
					Point p = t.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
					t.getVerticalBar().setVisible(r2.height <= p.y);
				}
				if (event.type == SWT.Modify) {
					t.getParent().layout(true);
					t.showSelection();
				}
			}
		};
		txt.addListener(SWT.Resize, scrollBarListener);
		txt.addListener(SWT.Modify, scrollBarListener);
		return txt;
	}

	public void addImportListener(SelectionListener l) {
		if ( this.m_lImportListeners.contains(l) )
			return;
		this.m_lImportListeners.add(l);
		this.m_btnImport.addSelectionListener(l);
	}

	public void removeImportListener(SelectionListener l) {
		if ( !this.m_lImportListeners.contains(l) )
			return;
		this.m_lImportListeners.remove(l);
		this.m_btnImport.removeSelectionListener(l);
	}

	public void clearImportListeners() {
		for ( SelectionListener l : this.m_lImportListeners ) {
			this.m_btnImport.removeSelectionListener(l);
		}
	}

	private void setValues() {
		m_format = SequenceFormat.values()[m_cmbFormat.getSelectionIndex()];
		m_strSequence = m_txtSequence.getText();
	}

	public void clearText() {
		this.m_txtSequence.setText("");
	}
}
