package org.eclipse.swt.snipets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestRegion {

	public static void main(String[] a) {
		Display display = new Display();
		final Shell shell = new Shell(display, SWT.NO_TRIM);

		Region region = new Region();
		region.add(createCircle(50, 50, 50));
		region.subtract(createCircle(50, 50, 20));
		shell.setSize(region.getBounds().width, region.getBounds().height);
		shell.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		shell.setRegion(region);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	static int[] createCircle(int xOffset, int yOffset, int radius) {
		int[] circlePoints = new int[10 * radius];
		for (int loopIndex = 0; loopIndex < 2 * radius + 1; loopIndex++) {
			int xCurrent = loopIndex - radius;
			int yCurrent = (int) Math.sqrt(radius * radius - xCurrent * xCurrent);
			int doubleLoopIndex = 2 * loopIndex;

			circlePoints[doubleLoopIndex] = xCurrent + xOffset;
			circlePoints[doubleLoopIndex + 1] = yCurrent + yOffset;
			circlePoints[10 * radius - doubleLoopIndex - 2] = xCurrent + xOffset;
			circlePoints[10 * radius - doubleLoopIndex - 1] = -yCurrent + yOffset;
		}

		return circlePoints;
	}
}
