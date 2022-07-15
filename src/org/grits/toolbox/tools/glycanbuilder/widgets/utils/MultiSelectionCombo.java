package org.grits.toolbox.tools.glycanbuilder.widgets.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MultiSelectionCombo extends Composite {

	private Shell shell = null;
	private List list = null;

	private Text txtCurrentSelection = null;
	private Button btnArrow = null;

	private String[] textItems = null;
	private int[] currentSelection = null;

	private ArrayList<MouseListener> lMouseListener;

	public MultiSelectionCombo(Composite parent, String[] items, int[] selection, int style) {
		super(parent, style);
		currentSelection = selection;
		textItems = items;
		init();
	}

	public MultiSelectionCombo(Composite parent, int style) {
		this(parent, new String[0], new int[0], style);
	}

	private void init() {
		GridLayout layout = new GridLayout(2, false);
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		setLayout(layout);
		txtCurrentSelection = new Text(this, SWT.READ_ONLY);
		txtCurrentSelection.setEditable(false);
		txtCurrentSelection.setLayoutData(new GridData(GridData.FILL_BOTH));

		displayText();

		btnArrow = new Button(this, SWT.ARROW | SWT.DOWN);
		btnArrow.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent event) {
				super.mouseDown(event);
				initFloatShell();
			}

		});

		lMouseListener = new ArrayList<>();

		txtCurrentSelection.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	private void initFloatShell() {
		Point p = txtCurrentSelection.getParent().toDisplay(txtCurrentSelection.getLocation());
		Point size = txtCurrentSelection.getSize();
		if ( size.x < 50 )
			size.x = 50;
		Rectangle shellRect = new Rectangle(p.x, p.y + size.y, size.x, 0);
		shell = new Shell(MultiSelectionCombo.this.getShell(), SWT.NO_TRIM);

		GridLayout gl = new GridLayout();
		gl.marginBottom = 0;
		gl.marginTop = 0;
		gl.marginRight = 0;
		gl.marginLeft = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		shell.setLayout(gl);

		list = new List(shell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		for (String value : textItems) {
			list.add(value);
		}

		list.setSelection(currentSelection);

		GridData gd = new GridData(GridData.FILL_BOTH);
		list.setLayoutData(gd);
		size = list.computeSize(shellRect.width, 60);

		shell.setSize( size );
		shell.setLocation(shellRect.x, p.y - size.y );

		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent event) {
				super.mouseUp(event);
				currentSelection = list.getSelectionIndices();
				if ((event.stateMask & SWT.CTRL) == 0) {
					shell.dispose();
					displayText();
				}
			}
		});

		for ( MouseListener listener : lMouseListener ) {
			list.addMouseListener(listener);
		}

		shell.addShellListener(new ShellAdapter() {

			public void shellDeactivated(ShellEvent arg0) {
				if (shell != null && !shell.isDisposed()) {
					currentSelection = list.getSelectionIndices();
					displayText();
					shell.dispose();
				}
			}
		});
		shell.open();
	}

	private void displayText() {
		if (currentSelection != null && currentSelection.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < currentSelection.length; i++) {
				if (i > 0)
					sb.append(", ");
				sb.append(textItems[currentSelection[i]]);
			}
			txtCurrentSelection.setText(sb.toString());
		} else {
			txtCurrentSelection.setText("---");
		}
		this.getParent().layout();

	}

	public String[] getItems() {
		return this.textItems;
	}

	public void setItems(String[] items) {
		this.textItems = items;
	}

	public void add(String item) {
		String[] textItemsNew = new String[this.textItems.length+1];
		for ( int i=0; i<textItemsNew.length-1; i++ )
			textItemsNew[i] = this.textItems[i];
		textItemsNew[textItemsNew.length-1] = item;
		this.textItems = textItemsNew;
	}

	public int getItemCount() {
		return this.textItems.length;
	}

	public int[] getSelectionIndices() {
		return this.currentSelection;
	}

	/**
	 * Returns selected texts.
	 * @return String[] containing selected texts
	 */
	public String[] getSelections() {
		String[] selections = new String[this.currentSelection.length];
		int i=0;
		for ( int item : this.currentSelection )
			selections[i++] = this.textItems[item];
		return selections;
	}

	public void setSelectionIndices(int[] selection) {
		this.currentSelection = selection;
		displayText();
	}

	public void select(int selection) {
		this.currentSelection = new int[] {selection};
		displayText();
	}

	public void addMouseListener(MouseListener listener) {
		this.lMouseListener.add(listener);
	}

	public void removeMouseListener(MouseListener listener) {
		this.lMouseListener.remove(listener);
	}

	public void resetMouseListener() {
		this.lMouseListener = new ArrayList<>();
	}

	public void removeAll() {
		this.textItems = new String[0];
		this.currentSelection = new int[0];
		this.lMouseListener = new ArrayList<>();
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.txtCurrentSelection.setEnabled(enabled);
		this.btnArrow.setEnabled(enabled);
		if ( enabled )
			txtCurrentSelection.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		else
			txtCurrentSelection.setBackground(this.getBackground());
	}

	// Main method to showcase MultiSelectionCombo
	// (can be removed from productive code)
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		shell.setText("MultiSelectionCombo Demo");

		// Items and pre-selected items in combo box
		String[] items = new String[] { "Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Iota",
				"Kappa" };
		int[] selection = new int[] { 0, 2 };

		// Create MultiSelectCombo box
		final MultiSelectionCombo combo = new MultiSelectionCombo(shell, items, selection, SWT.BORDER);
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		((GridData) combo.getLayoutData()).widthHint = 300;

		// Add button to print current selection on console
		Button button = new Button(shell, SWT.NONE);
		button.setText("What is selected?");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println("Selected items: " + Arrays.toString(combo.getSelectionIndices()));
			}
		});

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

}