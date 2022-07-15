package org.eclipse.swt.snipets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class TestStackLayout {

	static int pageNum = -1;

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		Rectangle clientArea = shell.getClientArea();
		shell.setBounds(clientArea.x + 10, clientArea.y + 10, 300, 200);
		// create the composite that the pages will share
		final Composite contentPanel = new Composite(shell, SWT.BORDER);
		contentPanel.setBounds(clientArea.x + 100, clientArea.y + 10, 190, 90);
		final StackLayout layout = new StackLayout();
		contentPanel.setLayout(layout);

		// create the first page's content
		final Composite page0 = new Composite(contentPanel, SWT.NONE);
		page0.setLayout(new RowLayout());
		Label label = new Label(page0, SWT.NONE);
		label.setText("Label on page 1");
		label.pack();

		// create the second page's content
		final Composite page1 = new Composite(contentPanel, SWT.NONE);
		page1.setLayout(new RowLayout());
		Button button = new Button(page1, SWT.NONE);
		button.setText("Button on page 2");
		button.pack();

		// create the button that will switch between the pages
		Button pageButton = new Button(shell, SWT.PUSH);
		pageButton.setText("Push");
		pageButton.setBounds(clientArea.x + 10, clientArea.y + 10, 80, 25);
		pageButton.addListener(SWT.Selection, event -> {
			pageNum = ++pageNum % 2;
			layout.topControl = pageNum == 0 ? page0 : page1;
			contentPanel.layout();
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
