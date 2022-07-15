package org.eclipse.swt.snipets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestForcusListener {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(3, true));
		shell.setText("One Potato, Two Potato");
		// Create the focus listener
		FocusListener listener = new FocusListener() {
			public void focusGained(FocusEvent event) {
				Button button = (Button) event.getSource();
				button.setText("I'm It!");
			}

			public void focusLost(FocusEvent event) {
				Button button = (Button) event.getSource();
				button.setText("Pick Me!");
			}
		};

		// Create the buttons and add the listener to each one
		for (int i = 0; i < 6; i++) {
			Button button = new Button(shell, SWT.PUSH);
			button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			button.setText("Pick Me!");
			button.addFocusListener(listener);
		}

		// Display the window
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
