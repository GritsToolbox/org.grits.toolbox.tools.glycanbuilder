package org.grits.toolbox.tools.glycanbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class FirstSWTApplication {

	public static void main(String[] args) {
		Display display = new Display();

		Shell shell = new Shell(display);

		shell.setText("Canvas Example");
		shell.setLayout(new FillLayout());
		Rectangle bounds = shell.getClientArea();

		final Image image1 = new Image(shell.getDisplay(), 120, 140);
		final Image image2 = new Image(display, 120, 140);
		GC gc1 = new GC(image1);
		GC gc2 = new GC(image2);

		final int[] t_lFillColors = {
				SWT.COLOR_RED, SWT.COLOR_BLUE, SWT.COLOR_GREEN,
				SWT.COLOR_CYAN, SWT.COLOR_MAGENTA, SWT.COLOR_YELLOW
			};
		List<Rectangle> t_lRects = new ArrayList<>();
		for ( int i=0; i<6; i++ ) {
			Rectangle rect = new Rectangle(10, 10+20*i, 100, 10 );
			gc1.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc1.setBackground(display.getSystemColor(t_lFillColors[i]));
			gc1.fillRectangle(rect);
			gc1.drawRectangle(rect);
			gc2.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			gc2.setBackground(display.getSystemColor(t_lFillColors[i]));
			gc2.fillRectangle(rect);
			gc2.drawRectangle(rect);
		}
		gc1.dispose();
		gc2.dispose();
		final Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.drawImage(image1, 0, 0);
				e.gc.drawImage(image2, 150, 0);

				e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));

				Path p = new Path(display);
//				p.moveTo(75, 225);
				p.addArc(0, 150, 150, 150, 0, 120);
				p.lineTo(75, 225);
				p.close();
				e.gc.drawPath(p);
				e.gc.fillPath(p);
			}
		});
		

		shell.open();
		while ( !shell.isDisposed() ) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
