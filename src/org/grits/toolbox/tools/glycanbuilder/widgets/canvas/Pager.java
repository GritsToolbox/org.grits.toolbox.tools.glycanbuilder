package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.SpinnerCombo;

public class Pager {

	private Composite m_compositePager;

	private Label m_lblPageDescription;
	private Spinner m_spnNumElementsPerPage;
//	private Spinner m_spnCurrentPage;
	private SpinnerCombo m_spnCmbCurrentPage;
	private Label m_lblTotalPages;

	private int m_nTotalElements;
	private int m_nElementsPerPage;
	private int m_iCurrentPage;

	private List<SelectionListener> m_lListeners;

	public Pager() {
		this.m_nTotalElements = 1;
		this.m_nElementsPerPage = 20;
		this.m_iCurrentPage = 1;

		this.m_lListeners = new ArrayList<>();
	}

	public void setNumberOfTotalElements(int value) {
		this.m_nTotalElements = value;
	}

	public int getNumberOfTotalElements() {
		return this.m_nTotalElements;
	}

	public void setNumberOfElementsPerPage(int value) {
		this.m_nElementsPerPage = value;
	}

	public int getNumberOfElementsPerPage() {
		return this.m_nElementsPerPage;
	}

	public void setCurrentPageNumber(int value) {
		this.m_iCurrentPage = value;
	}

	public int getCurrentPageNumber() {
		return this.m_iCurrentPage;
	}

	public void addSelectionListener(SelectionListener listener) {
		if ( this.m_lListeners.contains(listener) )
			return;
		this.m_lListeners.add(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		if ( !this.m_lListeners.contains(listener) )
			return;
		this.m_lListeners.remove(listener);
	}

	protected void createControls(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));

		RowLayout layout = new RowLayout();
		layout.marginWidth = 10;
		layout.center = true;
		layout.fill = true;
		layout.wrap = true;
//		layout.pack = false;
		layout.spacing = 0;
		container.setLayout(layout);

		Label lbl = new Label(container, SWT.CENTER);
		lbl.setText("1-1 of 1");
		this.m_lblPageDescription = lbl;

		// Empty label for space
		lbl = new Label(container, SWT.CENTER);
		lbl.setLayoutData(new RowData(50, SWT.DEFAULT));

		lbl = new Label(container, SWT.CENTER);
		lbl.setText("Pages show at most");
		Spinner spn;
		spn = new Spinner(container, SWT.READ_ONLY);
		spn.setValues(this.m_nElementsPerPage, 10, 50, 0, 1, 10);
		spn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_nElementsPerPage = ((Spinner)e.widget).getSelection();
			}
		});
		for ( SelectionListener l : this.m_lListeners )
			spn.addSelectionListener(l);
		this.m_spnNumElementsPerPage = spn;

		// Empty label for space
		lbl = new Label(container, SWT.NONE);
		lbl.setLayoutData(new RowData(10, SWT.DEFAULT));

		lbl = new Label(container, SWT.CENTER);
		lbl.setText("Page #");
		SpinnerCombo spnCmb = new SpinnerCombo(container, SWT.READ_ONLY);
		spnCmb.setValues(this.m_iCurrentPage, 1, 1);
		spnCmb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_iCurrentPage = spnCmb.getSelection();
			}
		});
		for ( SelectionListener l : this.m_lListeners )
			spnCmb.addSelectionListener(l);
		this.m_spnCmbCurrentPage = spnCmb;

		lbl = new Label(container, SWT.CENTER);
		lbl.setText("/ 1");
		this.m_lblTotalPages = lbl;

		this.m_compositePager = container;

		parent.layout();
	}

	public void updatePager(Composite parent) {
		if ( this.m_compositePager == null || this.m_compositePager.isDisposed() )
			createControls(parent);
		else {
			parent = this.m_compositePager.getParent();
			this.m_spnNumElementsPerPage.setSelection(this.m_nElementsPerPage);
//			this.m_spnCurrentPage.setSelection(this.m_iCurrentPage);
			this.m_spnCmbCurrentPage.setSelection(this.m_iCurrentPage);
		}
		this.m_lblPageDescription.setText(toString());
		// Calculate total number of pages
		int nPages = 1;
		if ( this.m_nTotalElements > this.m_nElementsPerPage ) {
			nPages = this.m_nTotalElements / this.m_nElementsPerPage;
			if ( this.m_nTotalElements%this.m_nElementsPerPage != 0 )
				nPages++;
		}
		this.m_spnCmbCurrentPage.setMaximum(nPages);
//		this.m_spnCurrentPage.setMaximum(nPages);
		this.m_lblTotalPages.setText("/ "+nPages);
		this.m_compositePager.pack();
		parent.layout();
	}

	public boolean isDisposed() {
		return ( this.m_compositePager == null || this.m_compositePager.isDisposed() );
	}

	public void dispose() {
		this.m_compositePager.dispose();
		this.m_iCurrentPage = 1;
		this.m_nTotalElements = 1;
	}

	@Override
	public String toString() {
		if ( this.m_nTotalElements == 0 )
			return "";
		int iBegin = (this.m_iCurrentPage-1) * this.m_nElementsPerPage + 1;
		int iEnd = Math.min( this.m_iCurrentPage * this.m_nElementsPerPage, this.m_nTotalElements);
		return iBegin+"-"+iEnd+" of "+this.m_nTotalElements;
	}
}
