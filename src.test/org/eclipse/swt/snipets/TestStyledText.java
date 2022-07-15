package org.eclipse.swt.snipets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestStyledText {

	public static void main(String[] args) {
		// create the widget's shell
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		shell.setSize(200, 100);
		Display display = shell.getDisplay();
		// create the styled text widget
		StyledText widget = new StyledText(shell, SWT.BORDER);
		widget.setText("This is the StyledText widget.");

		// create the Colors
		Color orange = new Color(display, 255, 127, 0);
		Color lime = new Color(display, 127, 255, 127);

		// make "This" bold and orange
		StyleRange styleRange = new StyleRange();
		styleRange.start = 0;
		styleRange.length = 4;
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = orange;
		widget.setStyleRange(styleRange);

		// make "StyledText" bold and lime
		styleRange = new StyleRange();
		styleRange.start = 12;
		styleRange.length = 10;
		styleRange.fontStyle = SWT.BOLD;
		styleRange.foreground = lime;
		widget.setStyleRange(styleRange);

//		styleRange = new StyleRange(12, 10, null, null, SWT.NORMAL);		
//		widget.setStyleRange(styleRange);	// set the bold, lime colored text back to normal
//		lime.dispose();				// lime is no longer used by the widget so it can be disposed

		shell.open();
		while (!shell.isDisposed())
		if (!display.readAndDispatch()) display.sleep();

	}

}
