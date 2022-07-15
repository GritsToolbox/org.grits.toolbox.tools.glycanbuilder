package org.grits.toolbox.tools.glycanbuilder.core.renderer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public abstract class SWTRenderer {

	protected Device m_device;
	private Color m_bgColor;

	private static Color COLOR_WHITE;
	private static Color COLOR_BLACK;

	public SWTRenderer(Device device) {
		this.m_device = device;
		if ( device.isDisposed() )
			this.m_device = Display.getDefault();
		try {
			this.m_bgColor = new Color(this.m_device, this.m_device.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND).getRGB(), 0);
		} catch (SWTException e) {
			// Access display thread safely
			((Display)this.m_device).asyncExec(new Runnable() {
				@Override
				public void run() {
					m_bgColor = new Color(m_device, m_device.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND).getRGB(), 0);
				}
			});
		}
	}

	public void setBackgroundColor(Color bgColor) {
//		this.bgColor.dispose();
//		this.bgColor = new Color(bgColor.getDevice(), bgColor.getRGB(), 0);
		this.m_bgColor = bgColor;
	}

	public Color getBackgroundColor() {
		return this.m_bgColor;
//		return new Color(this.bgColor.getDevice(), bgColor.getRGB(), 0);
	}

	protected Color getColorWhite() {
		if ( COLOR_WHITE == null || COLOR_WHITE.isDisposed() )
			COLOR_WHITE = new Color(this.m_device, SWTColors.WHITE);
		return COLOR_WHITE;
	}

	protected Color getColorBlack() {
		if ( COLOR_BLACK == null || COLOR_WHITE.isDisposed() )
			COLOR_BLACK = new Color(this.m_device, SWTColors.BLACK);
		return COLOR_BLACK;
	}

	protected static Rectangle copy(Rectangle rect) {
		return new Rectangle(rect.x, rect.y, rect.width, rect.height);
	}
}
