package org.grits.toolbox.tools.glycanbuilder.widgets.utils;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

/**
 * This class is a wrapper class for CCombo to use like Spinner
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class SpinnerCombo {

	private CCombo m_cmb;
	private int m_iMax;
	private int m_iMin;
	private int m_iSelection;

	public SpinnerCombo(Composite parent, int style) {
		this.m_cmb = new CCombo(parent, style);

		this.m_iMax = 1;
		this.m_iMin = 1;
		this.m_iSelection = -1;

		this.m_cmb.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CCombo cmb = (CCombo)e.widget;
				if ( cmb.getSelectionIndex() == -1 ) {
					m_iSelection = -1;
					return;
				}
				m_iSelection = cmb.getSelectionIndex() + m_iMin;
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				verifySelection( (CCombo)e.widget );
			}
		});
		this.m_cmb.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}
			@Override
			public void focusLost(FocusEvent e) {
				verifySelection( (CCombo)e.widget );
			}
		});
	}

	private void verifySelection(CCombo cmb) {
		int iNewSelection = m_iSelection;
		try {
			iNewSelection = Integer.parseInt(cmb.getText());
		} catch (NumberFormatException e1) {
		}
		setSelection(iNewSelection);
	}

	public boolean isDisposed() {
		return this.m_cmb.isDisposed();
	}

	/**
	 * 
	 * @param listener
	 * @see CCombo#addSelectionListener(SelectionListener)
	 */
	public void addSelectionListener(SelectionListener listener) {
		this.m_cmb.addSelectionListener(listener);
	}

	/**
	 * 
	 * @param listener
	 * @see CCombo#removeSelectionListener(SelectionListener)
	 */
	public void removeSelectionListener(SelectionListener listener) {
		this.m_cmb.removeSelectionListener(listener);
	}

	/**
	 * Sets the receiver's selection, minimum value, maximum value all at once. 
	 * Note: This is similar to setting the values individually using the appropriate methods, but may be implemented in a more efficient fashion on some platforms.
	 * @param selection the new selection value
	 * @param minimum the new minimum value
	 * @param maximum the new maximum value
	 */
	public void setValues(int selection, int minimum, int maximum) {
		if (maximum < minimum) return;
		this.m_iMin = minimum;
		this.m_iMax = maximum;
		this.m_iSelection = selection;
		this.updateItems();
		this.selectItem();
	}

	public void setMaximum(int value) {
		if (value < this.m_iMin) return;
		this.m_iMax = value;
		this.updateItems();
		this.selectItem();
	}

	public int getMaximum() {
		return this.m_iMax;
	}

	public void setMinimum(int value) {
		if (value > this.m_iMax) return;
		this.m_iMin = value;
		this.updateItems();
		this.selectItem();
	}

	public int getMinimum() {
		return this.m_iMin;
	}

	public void setSelection(int value) {
		this.m_iSelection = value;
		this.selectItem();
	}

	public int getSelection() {
		return this.m_iSelection;
	}

	private void updateItems() {
		int nItems = this.m_iMax - this.m_iMin + 1;
		String[] items = new String[nItems];
		for ( int i = 0; i < nItems; i++ ) {
			items[i] = ""+(this.m_iMin + i);
		}
		this.m_cmb.setItems(items);
	}

	private void selectItem() {
		this.m_iSelection = Math.min( Math.max(this.m_iSelection, this.m_iMin), this.m_iMax );
		this.m_cmb.select(this.m_iSelection - this.m_iMin);
	}
}
