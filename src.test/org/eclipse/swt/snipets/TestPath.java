package org.eclipse.swt.snipets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestPath {

	static int imageNumber;

	public static void main(String[] a) {
		Display display = new Display();
		final Shell shell = new Shell(display);

		shell.setText("Canvas Example");
		shell.setLayout(new FillLayout());

		List<Image> t_lImages = new ArrayList<>();
		int nImage = 12;
		for (int i = 0; i <= nImage; i++) {
			Image image = new Image(display, 100, 100);
			GC gc = new GC(image);
			drawPie(gc, image, 5, 5, 90, 90, 100/nImage*i, true);
			gc.dispose();
			t_lImages.add(image);
		}

		final Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				int x = 0, y = 0;
				for ( int i=0;i<t_lImages.size(); i++ ) {
					Image image = t_lImages.get(i);
					x = 100*(i%5);
					y = 100*(int)(i/5);
					e.gc.drawImage(image, x, y);
				}
				Image image = new Image(display, 100, 100);
				GC gc = new GC(image);
				Path p = new Path(display);
				p.addArc(5, 5, 90, 90, 0, 360);
				p.close();
				gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawPath(p);
				gc.dispose();
				e.gc.drawImage(image, x+100, y);
			}
		});

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	public static void drawPie(GC gc, Image image, int x, int y, int width, int height, int percent,
			boolean draw_border) {
		Rectangle image_size = image.getBounds();

		int width_pad = (width - image_size.width) / 2;
		int height_pad = (height - image_size.height) / 2;

		int angle = (percent * 360) / 100;
		if (angle < 4) {
			angle = 0; // workaround fillArc rendering bug
		}

		Region old_clipping = new Region();

		gc.getClipping(old_clipping);

		Path path_done = new Path(gc.getDevice());

		path_done.addArc(x, y, width, height, 90, -angle);
		path_done.lineTo(x + width / 2, y + height / 2);
		path_done.close();

		gc.setClipping(path_done);

		gc.drawImage(image, x + width_pad, y + height_pad + 1);

		Path path_undone = new Path(gc.getDevice());

		path_undone.addArc(x, y, width, height, 90 - angle, angle - 360);
		path_undone.lineTo(x + width / 2, y + height / 2);
		path_undone.close();

		gc.setClipping(path_undone);

		gc.setAlpha(75);
		gc.drawImage(image, x + width_pad, y + height_pad + 1);
		gc.setAlpha(255);

		gc.setClipping(old_clipping);

		if (draw_border) {
			gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_YELLOW));
			gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
			if (percent == 100) {
				gc.fillOval(x, y, width - 1, height - 1);
				gc.drawOval(x, y, width - 1, height - 1);
			} else if (angle > 0) {
				gc.fillPath(path_done);
				gc.drawPath(path_done);
				gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
				gc.fillPath(path_undone);
				gc.drawPath(path_undone);
			}
		}

		path_done.dispose();
		path_undone.dispose();
		old_clipping.dispose();

	}
}
