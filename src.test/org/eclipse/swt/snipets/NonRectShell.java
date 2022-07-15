package org.eclipse.swt.snipets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;


public class NonRectShell {
	private int POINTS = 11;
	private int offsetX = 0;
	private int offsetY = 0;

	NonRectShell(){
		Display display = new Display();
		final Point center = new Point(0, 0);
		final int[] radial = new int[POINTS*2];
		final Color color = new Color(display, new RGB(64,196,255));

		final Shell shell = new Shell(display, SWT.NO_TRIM);
		shell.setBackground(color);
		shell.setSize(200, 200);

		// Calculate array of coordinate for region
		Rectangle bounds = shell.getClientArea();
		center.x = bounds.x + bounds.width/2;
		center.y = bounds.y + bounds.height/2;
		int pos = 0;
		for (int i =0; i< POINTS; ++i){
			double r = Math.PI * 2 *pos/POINTS;
			radial[i*2] = (int)((1+Math.cos(r)) * center.x);
			radial[i*2+1] = (int)((1+Math.sin(r)) * center.x);
			pos = (pos + POINTS / 2) % POINTS;
		}

		// Create label for displaying on the shell
		Label label = new Label(shell, SWT.CENTER);
		label.setBackground(color);
		label.setText("Weired program");
		// Calculate automatically the label size to minimize
		label.pack();

		// Create right click menu
		Menu menu = new Menu(shell, SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Close the window");
		item.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				// Close the window (shell)
				shell.dispose();
			}
		});

		shell.setMenu(menu);

		shell.addMouseListener(new MouseAdapter(){
			public void mouseDown(MouseEvent e) {
				if(e.button ==1){
					offsetX = e.x;
					offsetY = e.y;
				}
			}
		});

		shell.addMouseMoveListener(new MouseMoveListener(){
			public void mouseMove(MouseEvent e){
				if((e.stateMask & SWT.BUTTON1) !=0) {
					Point pt = shell.toDisplay(e.x, e.y);
					pt.x -= offsetX;
					pt.y -= offsetY;
					shell.setLocation(pt);
				}
			}
		});

		shell.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				shell.dispose();
			}
		});

		// Create region
		Region region = new Region(display);
		region.add(radial);
		region.add(new Rectangle(-5,-5, 150, 25));
		// Set region to shell
		shell.setRegion(region);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {

				display.sleep();
			}
		}

		// Dispose to release the resource
		color.dispose(); 

		display.dispose();
	}

	public static void main(String[] args) {
		new NonRectShell();
	}
}