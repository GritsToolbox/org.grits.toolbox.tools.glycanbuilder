package org.grits.toolbox.tools.glycanbuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.SpinnerCombo;

public class TestCombo {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Test Combo");
		shell.setLayout(new RowLayout());
		Combo combo = new Combo(shell, SWT.NONE);
		combo.setItems("A-1", "B-1", "C-1");
		Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		text.setText("some text");
		combo.addListener(SWT.DefaultSelection, e -> System.out.println(e.widget + " - Default Selection"));
		text.addListener(SWT.DefaultSelection, e -> System.out.println(e.widget + " - Default Selection"));

		SpinnerCombo cmbSpn = new SpinnerCombo(shell, SWT.READ_ONLY);
		cmbSpn.setValues(1, 1, 100);
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}