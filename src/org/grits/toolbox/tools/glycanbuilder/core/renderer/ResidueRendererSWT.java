/*
*   EuroCarbDB, a framework for carbohydrate bioinformatics
*
*   Copyright (c) 2006-2009, Eurocarb project, or third-party contributors as
*   indicated by the @author tags or express copyright attribution
*   statements applied by the authors.  
*
*   This copyrighted material is made available to anyone wishing to use, modify,
*   copy, or redistribute it subject to the terms and conditions of the GNU
*   Lesser General Public License, as published by the Free Software Foundation.
*   A copy of this license accompanies this distribution in the file LICENSE.txt.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*   or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
*   for more details.
*
*   Last commit: $Rev: 1930 $ by $Author: david@nixbioinf.org $ on $Date:: 2010-07-29 #$  
*/

package org.grits.toolbox.tools.glycanbuilder.core.renderer;

import static org.grits.toolbox.tools.glycanbuilder.core.renderer.utils.Geometry.angle;
import static org.grits.toolbox.tools.glycanbuilder.core.renderer.utils.Geometry.center;
import static org.grits.toolbox.tools.glycanbuilder.core.renderer.utils.Geometry.isDown;
import static org.grits.toolbox.tools.glycanbuilder.core.renderer.utils.Geometry.isLeft;
import static org.grits.toolbox.tools.glycanbuilder.core.renderer.utils.Geometry.isUp;
import static org.grits.toolbox.tools.glycanbuilder.core.renderer.utils.Geometry.midx;
import static org.grits.toolbox.tools.glycanbuilder.core.renderer.utils.Geometry.midy;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.Transform;
import org.eurocarbdb.application.glycanbuilder.ResAngle;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.eurocarbdb.application.glycanbuilder.ResidueStyle;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.eurocarbdb.application.glycanbuilder.TextUtils;
import org.grits.toolbox.tools.glycanbuilder.core.config.GraphicOptionsSWT;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.style.ResidueStyleDictionary;
import org.grits.toolbox.tools.glycanbuilder.core.renderer.utils.TextShapeUtils;

/**
 * Objects of this class are used to create a graphical representation of a
 * {@link Residue} object given the current graphic options
 * ({@link GraphicOptionsSWT}. The rules to draw the residue in the different
 * notations are stored in the {@link ResidueStyleDictionary}.
 * 
 * @author Alessio Ceroni (a.ceroni@imperial.ac.uk)
 * @author Masaaki Matsubara (matsubara@uga.edu)
 */

public class ResidueRendererSWT extends SWTRenderer {

	protected ResidueStyleDictionary theResidueStyleDictionary;

	protected GraphicOptionsSWT theGraphicOptions;

	/**
	 * Empty constructor.
	 */
	public ResidueRendererSWT(Device device) {
		super(device);
		this.theResidueStyleDictionary = new ResidueStyleDictionary();
		this.theGraphicOptions = new GraphicOptionsSWT();

	}

	/**
	 * Create a new residue renderer copying the style dictionary and graphic
	 * options from the <code>src</code> object.
	 */
	public ResidueRendererSWT(Device device, GlycanRendererSWT src) {
		super(device);
		theResidueStyleDictionary = src.getResidueStyleDictionary();
		theGraphicOptions = src.getGraphicOptions();
	}

	/**
	 * Return the graphic options used by this object.
	 */
	public GraphicOptionsSWT getGraphicOptions() {
		return theGraphicOptions;
	}

	/**
	 * Set the graphic options used by this object.
	 */
	public void setGraphicOptions(GraphicOptionsSWT opt) {
		theGraphicOptions = opt;
	}

	/**
	 * Return the residue style dictionary used by this object.
	 */
	public ResidueStyleDictionary getResidueStyleDictionary() {
		return theResidueStyleDictionary;
	}

	/**
	 * Set the residue style dictionary used by this object.
	 */
	public void setResidueStyleDictionary(ResidueStyleDictionary residueStyleDictionary) {
		theResidueStyleDictionary = residueStyleDictionary;
	}

	// --- Data access

	/**
	 * Return a graphical representation of a residue type as an image of
	 * <code>max_y_size</code> height.
	 */
	public Image getImage(ResidueType type, int max_y_size) {
		int orientation = theGraphicOptions.ORIENTATION;
		theGraphicOptions.ORIENTATION = GraphicOptionsSWT.RL;

		// compute bounding box
		Residue node = new Residue(type);
		Rectangle bbox = computeBoundingBox(node, false, 4, 4, new ResAngle(), max_y_size - 8, max_y_size - 8);

		// Create an image that supports transparent pixels
		Image img = new Image(this.m_device, bbox.width + 8, bbox.height + 8);

		// create a graphic context
		GC gc = new GC(img);
		gc.setAntialias(SWT.ON);
		gc.setBackground(this.getBackgroundColor());
		gc.fillRectangle(0, 0, bbox.width+8, bbox.height+8);

		// paint the residue
		paint(gc, node, false, false, null, bbox, null, new ResAngle());
		gc.dispose();

		theGraphicOptions.ORIENTATION = orientation;

		return img;
		// return Toolkit.getDefaultToolkit().createImage(img.getSource());
	}

	// -----------
	// Bounding box

	/**
	 * Return the text to be written in the residue representation given the residue
	 * style in the current notation.
	 */
	public String getText(Residue node) {
		if (node == null)
			return "";

		ResidueType type = node.getType();
		ResidueStyle style = theResidueStyleDictionary.getStyle(node);
		String text = style.getText();

		return (text != null) ? text : type.getResidueName();
	}

	/**
	 * Return the text to be written in the residue representation given the residue
	 * style in the current notation.
	 * 
	 * @param on_border
	 *            <code>true</code> if the residue is displayed on the border of its
	 *            parent, used for substitutions and modifications
	 */
	public String getText(Residue node, boolean on_border) {
		// special cases
		if (node == null)
			return "";
		if (on_border && node.isSpecial() && !node.isLCleavage())
			return "*";

		// get text
		String text = null;
		if (on_border && node.isLCleavage())
			text = getText(node.getCleavedResidue());
		else
			text = getText(node);

		// add linkage
		if (on_border && !node.getParentLinkage().hasUncertainParentPositions() && theGraphicOptions.SHOW_INFO)
			text = node.getParentLinkage().getParentPositionsString() + text;

		// add brackets for cleavages
		if (on_border && node.isLCleavage())
			text = "(" + text + ")";

		return text;
	}

	protected Rectangle computeBoundingBox(Residue node, boolean on_border, int x, int y, ResAngle orientation,
			int node_size, int max_y_size) {

		// get style
		ResidueStyle style = theResidueStyleDictionary.getStyle(node);
		String shape = style.getShape();

		// compute dimensions
		if (max_y_size < node_size)
			node_size = max_y_size;

		Rectangle dim;
		if (shape == null || on_border) {
			String text = getText(node, on_border);

			int font_size = theGraphicOptions.NODE_FONT_SIZE;
			int x_size = TextShapeUtils.textBounds(this.m_device, text, theGraphicOptions.NODE_FONT_FACE,
					font_size).width;

			if (x_size > node_size)
				dim = new Rectangle(0, 0, x_size, node_size);
			else
				dim = new Rectangle(0, 0, node_size, node_size);

			orientation = theGraphicOptions.getOrientationAngle();
		} else if (shape.equals("startrep") || shape.equals("endrep")) {
			int size = Math.min(node_size * 2, max_y_size);
			double linkInfoSize = TextShapeUtils.getFontSizePixel(theGraphicOptions.LINKAGE_INFO_SIZE);
			int font_size = (int)Math.round(linkInfoSize) + 1;

			dim = new Rectangle(0, 0, size / 2, size + 2 * font_size);
		} else if (shape.equals("point"))
			dim = new Rectangle(0, 0, 1, 1);
		else
			dim = new Rectangle(0, 0, node_size, node_size);

		// return bounding box
		if (orientation.equals(0) || orientation.equals(180))
			return new Rectangle(x, y, dim.width, dim.height);
		return new Rectangle(x, y, dim.height, dim.width);
	}

	// ----------
	// Painting

	static private int sat(int v, int t) {
		if (v > t)
			return t;
		return v;
	}

	static private int sig(int v) {
		return 128 + v / 2;
	}

	/**
	 * Draw a residue on a graphic context using the specified bounding box.
	 * 
	 * @param gc
	 *            the graphic context
	 * @param node
	 *            the residue to be drawn
	 * @param selected
	 *            <code>true</code> if the residue should be shown as selected
	 * @param on_border
	 *            <code>true</code> if the residue should be drawn on the border of
	 *            its parent
	 * @param par_bbox
	 *            the bounding box of the parent residue
	 * @param cur_bbox
	 *            the bounding box of the current residue
	 * @param sup_bbox
	 *            the bounding box used to decide the spatial orientation of the
	 *            residue
	 * @param orientation
	 *            the orientation of the residue
	 */
	public void paint(GC gc, Residue node, boolean selected, boolean on_border, Rectangle par_bbox, Rectangle cur_bbox,
			Rectangle sup_bbox, ResAngle orientation) {
		paint(gc, node, selected, true, on_border, par_bbox, cur_bbox, sup_bbox, orientation);
	}

	/**
	 * Draw a residue on a graphic context using the specified bounding box.
	 * 
	 * @param gc
	 *            the graphic context
	 * @param node
	 *            the residue to be drawn
	 * @param selected
	 *            <code>true</code> if the residue should be shown as selected
	 * @param active
	 *            <code>true</code> if the residue should be shown as active
	 * @param on_border
	 *            <code>true</code> if the residue should be drawn on the border of
	 *            its parent
	 * @param par_bbox
	 *            the bounding box of the parent residue
	 * @param cur_bbox
	 *            the bounding box of the current residue
	 * @param sup_bbox
	 *            the bounding box used to decide the spatial orientation of the
	 *            residue
	 * @param orientation
	 *            the orientation of the residue
	 */
	public void paint(GC gc, Residue node, boolean selected, boolean active, boolean on_border, Rectangle par_bbox,
			Rectangle cur_bbox, Rectangle sup_bbox, ResAngle orientation) {
		if (node == null)
			return;

		ResidueStyle style = theResidueStyleDictionary.getStyle(node);

		// draw shape
		Path shape = createShape(gc.getDevice(), node, par_bbox, cur_bbox, sup_bbox, orientation);
		Path text_shape = createTextShape(gc.getDevice(), node, par_bbox, cur_bbox, sup_bbox, orientation);
		Path fill_shape = createFillShape(gc.getDevice(), node, cur_bbox);

		RGB shape_rgb = new RGB( style.getShapeColor().getRed(), style.getShapeColor().getGreen(), style.getShapeColor().getBlue() );
		RGB fill_rgb = new RGB( style.getFillColor().getRed(), style.getFillColor().getGreen(), style.getFillColor().getBlue() );
		RGB text_rgb = new RGB( style.getTextColor().getRed(), style.getTextColor().getGreen(), style.getTextColor().getBlue() );
		if (selected)
			fill_rgb = new RGB(sig(fill_rgb.red), sig(fill_rgb.green), sig(fill_rgb.blue));
		if (!active) {
			shape_rgb = new RGB(sig(shape_rgb.red), sig(shape_rgb.green), sig(shape_rgb.blue));
			fill_rgb = new RGB(sig(fill_rgb.red), sig(fill_rgb.green), sig(fill_rgb.blue));
			text_rgb = new RGB(sig(text_rgb.red), sig(text_rgb.green), sig(text_rgb.blue));
		}
		Color shape_color = new Color(gc.getDevice(), shape_rgb);
		Color fill_color = new Color(gc.getDevice(), fill_rgb);
		Color text_color = new Color(gc.getDevice(), text_rgb);

		if (shape != null && !on_border) {
			if (fill_shape != null) {

				// Translate clipping to fill shape
				Transform tr = new Transform(gc.getDevice());
				tr.translate(1, 1);
				gc.setTransform(tr);

				Region old_clip = new Region();
				gc.getClipping(old_clip);

				gc.setClipping(shape);

				gc.setBackground((style.isFillNegative()) ? fill_color : getColorWhite());
				gc.fillPath(shape);

				gc.setBackground((style.isFillNegative()) ? getColorWhite() : fill_color);
				gc.fillPath(fill_shape);

				tr.translate(-1, -1);
				gc.setTransform(tr);

				gc.setForeground(shape_color);
				gc.drawPath(fill_shape);

				tr.translate(1, 1);
				gc.setTransform(tr);

				gc.setClipping(old_clip);
				old_clip.dispose();

				tr.translate(-1, -1);
				gc.setTransform(tr);
				tr.dispose();

			}

			// draw contour
			gc.setLineAttributes((selected) ? new LineAttributes(2) : new LineAttributes(1));
			gc.setForeground(shape_color);
			gc.drawPath(shape);
			gc.setLineAttributes(new LineAttributes(1));
		} else if (selected) {
			// draw selected contour for empty shape
			gc.setLineAttributes(new LineAttributes(2.f, SWT.CAP_FLAT, SWT.JOIN_ROUND, SWT.LINE_CUSTOM,
					new float[] { 5.f, 5.f }, 0.f, 1.f));
			gc.setForeground(shape_color);
			gc.drawRectangle(cur_bbox);
			gc.setLineAttributes(new LineAttributes(1));
		}

		// add text shape
		if (text_shape != null) {
			gc.setBackground(shape_color);
			gc.fillPath(text_shape);
		}

		// draw text
		if (shape == null || on_border || style.getText() != null) {
			if (shape == null || on_border)
				orientation = theGraphicOptions.getOrientationAngle();
			else if (style.getText() != null)
				orientation = new ResAngle(0);

			String text = getText(node, on_border);

			int font_size = theGraphicOptions.NODE_FONT_SIZE;
			int x_size = TextShapeUtils.textBounds(gc.getDevice(), text, theGraphicOptions.NODE_FONT_FACE,
					font_size).width;
			if (shape != null)
				font_size = sat(8 * font_size * cur_bbox.width / x_size / 10, font_size);

			Font new_font = new Font(gc.getDevice(), theGraphicOptions.NODE_FONT_FACE, font_size, SWT.NORMAL);
			Font old_font = gc.getFont();
			gc.setFont(new_font);

			// compute bounding rect
			Rectangle text_bound = TextShapeUtils.textBounds(new_font, text);
			// remove leading space from height
			int l = gc.getFontMetrics().getLeading();
			text_bound.height -= l*2;

			// draw text
			gc.setForeground(text_color);

			if (orientation.equals(0) || orientation.equals(180)) {
				Rectangle text_rect = new Rectangle(midx(cur_bbox) - text_bound.width / 2,
						midy(cur_bbox) - text_bound.height / 2, text_bound.width, text_bound.height);
				if (shape == null || fill_shape == null) {
					gc.setBackground(this.getBackgroundColor());
					gc.fillRectangle(text_rect);
				}
				gc.drawText(text, (int) text_rect.x, (int) text_rect.y-l, true);
			} else {
				Rectangle text_rect = new Rectangle(midx(cur_bbox) - text_bound.height / 2,
						midy(cur_bbox) - text_bound.width / 2, text_bound.height, text_bound.width);
				if (shape == null || fill_shape == null) {
					gc.setBackground(this.getBackgroundColor());
					gc.fillRectangle(text_rect);
				}

				// rotate image
				Transform transform = new Transform(gc.getDevice());
				transform.rotate(-90);
				gc.setTransform(transform);

				gc.drawString(text, -(int) (text_rect.y-l + text_rect.height), (int) text_rect.x, true);

				transform.rotate(90);
				gc.setTransform(transform);

				transform.dispose();
			}

			gc.setFont(old_font);

			new_font.dispose();
		}

		// Dispose
		if ( shape != null )
			shape.dispose();
		if ( text_shape != null )
			text_shape.dispose();
		if ( fill_shape != null )
			fill_shape.dispose();
		shape_color.dispose();
		fill_color.dispose();
		text_color.dispose();
	}

	// ------------
	// Shape

	static private Path createDiamond(Device device, float x, float y, float w, float h, boolean flat) {
		// static private Polygon createDiamond(double x, double y, double w, double h)
		// {
		if ((w % 2) == 1)
			w++;
		if ((h % 2) == 1)
			h++;

		float h0 = h * ((flat)? .8f : 1.0f);
		float y0 = y + ((flat)? h*.1f : 0.0f);
		Path p = new Path(device);
		p.moveTo(x + w / 2, y0);
		p.lineTo(x + w, y0 + h0 / 2);
		p.lineTo(x + w / 2, y0 + h0);
		p.lineTo(x, y0 + h0 / 2);
		p.close();
		// p.add(new int[] { (int) (x + w / 2), (int) (y), (int) (x + w), (int) (y + h /
		// 2), (int) (x + w / 2),
		// (int) (y + h), (int) (x), (int) (y + h / 2) });
		// Polygon p = new Polygon();
		// p.addPoint((int)(x+w/2), (int)(y));
		// p.addPoint((int)(x+w), (int)(y+h/2));
		// p.addPoint((int)(x+w/2), (int)(y+h));
		// p.addPoint((int)(x), (int)(y+h/2));
		return p;
	}

	static private Path createDiamond(Device device, float x, float y, float w, float h) {
		return createDiamond(device, x, y, w, h, false);
	}

	static private Path createFlatDiamond(Device device, float x, float y, float w, float h) {
		return createDiamond(device, x, y, w, h, true);
	}

	static private Path createHatDiamond(Device device, float x, float y, float w, float h) {
		// static private Shape createHatDiamond(double angle, double x, double y,
		// double w, double h) {
		// GeneralPath f = new GeneralPath();
		Path f = new Path(device);

		// append diamond
		Path p1 = createDiamond(device, x, y, w, h);
		f.addPath(p1);
		p1.dispose();
		// f.append(createDiamond(x, y, w, h), false);

		// append hat
		Path p2 = new Path(device);
		p2.moveTo(x - 2, y + h / 2 - 2);
		p2.lineTo(x + w / 2 - 2, y - 2);
		f.addPath(p2);
		p2.dispose();
		// Polygon p = new Polygon();
		// p.addPoint((int) (x - 2), (int) (y + h / 2 - 2));
		// p.addPoint((int) (x + w / 2 - 2), (int) (y - 2));
		// f.append(p, false);

		return f;
	}

	static private Path createRHatDiamond(Device device, float x, float y, float w, float h) {
		// static private ShapeType createRHatDiamond(double angle, double x, double y,
		// double w, double h) {
		Path f = new Path(device);
		// GeneralPath f = new GeneralPath();

		// append diamond
		Path p1 = createDiamond(device, x, y, w, h);
		f.addPath(p1);
		p1.dispose();
		// f.append(createDiamond(x, y, w, h), false);

		// append hat
		Path p2 = new Path(device);
		p2.moveTo(x + w + 2, y + h / 2 - 2);
		p2.lineTo(x + w / 2 + 2, y - 2);
		f.addPath(p2);
		p2.dispose();
		// Polygon p = new Polygon();
		// p.addPoint((int) (x + w + 2), (int) (y + h / 2 - 2));
		// p.addPoint((int) (x + w / 2 + 2), (int) (y - 2));
		// f.append(p, false);

		return f;
	}

	static private Path createRhombus(Device device, float x, float y, float w, float h) {
		// static private Polygon createRhombus(double x, double y, double w, double h)
		// {
		Path p = new Path(device);
		p.moveTo(x + 0.50f * w, y);
		p.lineTo(x + 0.85f * w, y + 0.50f * h);
		p.lineTo(x + 0.50f * w, y + h);
		p.lineTo(x + 0.15f * w, y + 0.50f * h);
		p.close();
		// Polygon p = new Polygon();
		// p.addPoint((int) (x + 0.50 * w), (int) (y));
		// p.addPoint((int) (x + 0.85 * w), (int) (y + 0.50 * h));
		// p.addPoint((int) (x + 0.50 * w), (int) (y + h));
		// p.addPoint((int) (x + 0.15 * w), (int) (y + 0.50 * h));
		return p;
	}

	static private Path createTriangle(Device device, double angle, float x, float y, float w, float h) {
		// static private Polygon createTriangle(double angle, double x, double y,
		// double w, double h) {
		// Polygon p = new Polygon();
		int iAngle = 0;
		if (angle >= -Math.PI / 4. && angle <= Math.PI / 4.) {
			// pointing right
			iAngle = 1;
		} else if (angle >= Math.PI / 4. && angle <= 3. * Math.PI / 4.) {
			// pointing down
			iAngle = 2;
		} else if (angle >= -3. * Math.PI / 4. && angle <= -Math.PI / 4.) {
			// pointing up
			iAngle = 0;
		} else {
			// pointing left
			iAngle = 3;
		}

		return createTopTriangle(device, iAngle, x, y, w, h);
	}

	static private Path createTopTriangle(Device device, int angle, float x, float y, float w, float h) {
		Path p = new Path(device);

		if (angle == 0) {
			// pointing up
			p.moveTo(x + w / 2, y);
			p.lineTo(x + w, y + h);
			p.lineTo(x, y + h);
			// p.addPoint((int) (x + w / 2), (int) (y));
			// p.addPoint((int) (x + w), (int) (y + h));
			// p.addPoint((int) (x), (int) (y + h));
		}
		if (angle == 1) {
			// pointing right
			p.moveTo(x + w, y + h / 2);
			p.lineTo(x, y + h);
			p.lineTo(x, y);
			// p.addPoint((int) (x + w), (int) (y + h / 2));
			// p.addPoint((int) (x), (int) (y + h));
			// p.addPoint((int) (x), (int) (y));
		}
		if (angle == 2) {
			// pointing down
			p.moveTo(x + w / 2, y + h);
			p.lineTo(x, y);
			p.lineTo(x + w, y);
			// p.addPoint((int) (x + w / 2), (int) (y + h));
			// p.addPoint((int) (x), (int) (y));
			// p.addPoint((int) (x + w), (int) (y));
		}
		if (angle == 3) {
			// pointing left
			p.moveTo(x, y + h / 2);
			p.lineTo(x + w, y + h);
			p.lineTo(x + w, y);
			// p.addPoint((int) (x), (int) (y + h / 2));
			// p.addPoint((int) (x + w), (int) (y + h));
			// p.addPoint((int) (x + w), (int) (y));
		}
		p.close();

		return p;
	}

	static private Path createStar(Device device, double x, double y, double w, double h, int points) {
		// static private Polygon createStar(double x, double y, double w, double h, int
		// points) {
		double rx = w / 2.;
		double ry = h / 2.;
		double cx = x + w / 2.;
		double cy = y + h / 2.;

		double step = Math.PI / (double) points;
		double nstep = Math.PI / 2. - 2. * step;

		double mrx = rx / (Math.cos(step) + Math.sin(step) / Math.tan(nstep));
		double mry = ry / (Math.cos(step) + Math.sin(step) / Math.tan(nstep));

		Path p = new Path(device);
		// Polygon p = new Polygon();
		// move to first point
		p.moveTo((int) cx, (int) y);
		for (int i = 1; i <= 2 * points; i++) {
			if ((i % 2) == 0)
				p.lineTo((int) (cx + rx * Math.cos(i * step - Math.PI / 2.)),
						(int) (cy + ry * Math.sin(i * step - Math.PI / 2.)));
			else
				p.lineTo((int) (cx + mrx * Math.cos(i * step - Math.PI / 2.)),
						(int) (cy + mry * Math.sin(i * step - Math.PI / 2.)));
			// if ((i % 2) == 0) {
			// p.addPoint((int) (cx + rx * Math.cos(i * step - Math.PI / 2.)),
			// (int) (cy + ry * Math.sin(i * step - Math.PI / 2.)));
			// else
			// p.addPoint((int) (cx + mrx * Math.cos(i * step - Math.PI / 2.)),
			// (int) (cy + mry * Math.sin(i * step - Math.PI / 2.)));
		}
		p.close();
		return p;
	}

	static private Path createPolygon(Device device, double x, double y, double w, double h, int points, boolean flat) {
		double rx = w / 2.;
		double ry = h / (flat ? 2.5 : 2.);
		double cx = x + w / 2.;
		double cy = y + h / 2.;

		double step = Math.PI / (points / 2.);
		Path p = new Path(device);
		double rotate = 0.;
		if (points % 2 == 1) {
			rotate = -Math.PI / 2.;
			// move to first point
			p.moveTo((float) cx, (float) y);
		} else
			p.moveTo((float) (x + w), (float) cy);
		for (int i = 1; i < points; i++) {
			p.lineTo((float) (cx + rx * Math.cos(i * step + rotate)), (float) (cy + ry * Math.sin(i * step + rotate)));
		}
		p.close();
		return p;
	}

	static private Path createRegularPolygon(Device device, double x, double y, double w, double h, int points) {
		return createPolygon(device, x, y, w, h, points, false);
	}

	static private Path createPentagon(Device device, double x, double y, double w, double h) {
		// static private Polygon createPentagon(double x, double y, double w, double h)
		// {
		return createRegularPolygon(device, x, y, w, h, 5);
	}

	static private Path createHexagon(Device device, double x, double y, double w, double h) {
		// static private Polygon createHexagon(double x, double y, double w, double h)
		// {
		return createRegularPolygon(device, x, y, w, h, 6);
	}

	static private Path createHeptagon(Device device, double x, double y, double w, double h) {
		// static private Polygon createHeptagon(double x, double y, double w, double h)
		// {
		return createRegularPolygon(device, x, y, w, h, 7);
	}

	static private Path createFlatHexagon(Device device, double x, double y, double w, double h) {
		return createPolygon(device, x, y, w, h, 6, true);
	}

//	static private Path createLine(Device device, double angle, double x, double y, double w, double h) {
//		// static private Shape createLine(double angle, double x, double y, double w,
//		// double h) {
//
//		double rx = w / 2.;
//		double ry = h / 2.;
//		double cx = x + w / 2.;
//		double cy = y + h / 2.;
//
//		Path p = new Path(device);
//		// Polygon p = new Polygon();
//
//		double x1 = cx + rx * Math.cos(angle - Math.PI / 2.);
//		double y1 = cy + ry * Math.sin(angle - Math.PI / 2.);
//		p.moveTo((int) x1, (int) y1);
//		// p.addPoint((int) x1, (int) y1);
//
//		double x2 = cx + rx * Math.cos(angle + Math.PI / 2.);
//		double y2 = cy + ry * Math.sin(angle + Math.PI / 2.);
//		p.lineTo((int) x2, (int) y2);
//		// p.addPoint((int) x2, (int) y2);
//
//		return p;
//	}

	static private Path createCleavage(Device device, double angle, double x, double y, double w, double h,
			boolean has_oxygen) {
		// static private Shape createCleavage(double angle, double x, double y, double
		// w, double h, boolean has_oxygen) {

		// GeneralPath f = new GeneralPath();
		Path f = new Path(device);

		double rx = w / 2.;
		double ry = h / 2.;
		double cx = x + w / 2.;
		double cy = y + h / 2.;

		// create cut
		double x1 = cx + rx * Math.cos(angle + Math.PI / 2.);
		double y1 = cy + ry * Math.sin(angle + Math.PI / 2.);
		double x2 = cx + rx * Math.cos(angle - Math.PI / 2.);
		double y2 = cy + ry * Math.sin(angle - Math.PI / 2.);
		double x3 = x2 + rx * Math.cos(angle);
		double y3 = y2 + ry * Math.sin(angle);

		Path p = new Path(device);
		// Polygon p = new Polygon();
		p.moveTo((int) x1, (int) y1);
		p.lineTo((int) x2, (int) y2);
		p.lineTo((int) x3, (int) y3);
		p.lineTo((int) x2, (int) y2);
		f.addPath(p);
		p.dispose();
		// p.addPoint((int) x1, (int) y1);
		// p.addPoint((int) x2, (int) y2);
		// p.addPoint((int) x3, (int) y3);
		// p.addPoint((int) x2, (int) y2);
		// f.append(p, false);

		if (has_oxygen) {
			// create oxygen
			double ox = cx + rx * Math.cos(angle);
			double oy = cy + ry * Math.sin(angle);
			Path o = new Path(device);
			o.addArc((int) (ox - rx / 3.), (int) (oy - ry / 3.), (int) (rx / 1.5), (int) (ry / 1.5), 0, 360);
			o.close();
			f.addPath(o);
			o.dispose();
			// Shape o = new Ellipse2D.Double(ox - rx / 3., oy - ry / 3., rx / 1.5, ry /
			// 1.5);
			// f.append(o, false);
		}

		return f;
	}

	static private Path createCrossRingCleavage(Device device, double angle, double x, double y, double w, double h,
			int first_pos, int last_pos) {
		// static private Shape createCrossRingCleavage(double angle, double x, double
		// y, double w, double h,
		// int first_pos, int last_pos) {

		Path c = new Path(device);
		// GeneralPath c = new GeneralPath();

		// add hexagon
		Path p = createHexagon(device, x + 1, y + 1, w - 2, h - 2);
		c.addPath(p);
		p.dispose();
		// c.append(createHexagon(x + 1, y + 1, w - 2, h - 2), false);
		// return c;

		// add line
		double rx = w / 2.;
		double ry = h / 2.;
		double cx = x + w / 2.;
		double cy = y + h / 2.;

		Path p1 = new Path(device);
		p1.moveTo((float) cx, (float) cy);
		p1.lineTo((float) (cx + 1.2 * rx * Math.cos(angle + first_pos * Math.PI / 3 - Math.PI / 6)),
				(float) (cy + 1.2 * ry * Math.sin(angle + first_pos * Math.PI / 3 - Math.PI / 6)));
		c.addPath(p1);
		p1.dispose();
		// Polygon p1 = new Polygon();
		// p1.addPoint((int) cx, (int) cy);
		// p1.addPoint((int) (cx + 1.2 * rx * Math.cos(angle + first_pos * Math.PI / 3 -
		// Math.PI / 6)),
		// (int) (cy + 1.2 * ry * Math.sin(angle + first_pos * Math.PI / 3 - Math.PI /
		// 6)));
		// c.append(p1, false);

		Path p2 = new Path(device);
		p2.moveTo((float) cx, (float) cy);
		p2.lineTo((float) (cx + 1.2 * rx * Math.cos(angle + last_pos * Math.PI / 3 - Math.PI / 6)),
				(float) (cy + 1.2 * ry * Math.sin(angle + last_pos * Math.PI / 3 - Math.PI / 6)));
		c.addPath(p2);
		p2.dispose();
		// Polygon p2 = new Polygon();
		// p2.addPoint((int) cx, (int) cy);
		// p2.addPoint((int) (cx + 1.2 * rx * Math.cos(angle + last_pos * Math.PI / 3 -
		// Math.PI / 6)),
		// (int) (cy + 1.2 * ry * Math.sin(angle + last_pos * Math.PI / 3 - Math.PI /
		// 6)));
		// c.append(p2, false);
		return c;

		/*
		 * double rx = w/2.; double ry = h/2.; double cx = x+w/2.; double cy = y+h/2.;
		 * 
		 * // add half hexagon
		 * 
		 * double step = Math.PI/3.; Polygon p = new Polygon();
		 * p.addPoint((int)(cx+0.866*rx*Math.cos(angle-Math.PI/2)),(int)(cy+0.866*ry*
		 * Math.sin(angle-Math.PI/2)));
		 * p.addPoint((int)(cx+rx*Math.cos(angle-Math.PI/3)),(int)(cy+ry*Math.sin(angle-
		 * Math.PI/3)));
		 * p.addPoint((int)(cx+rx*Math.cos(angle)),(int)(cy+ry*Math.sin(angle)));
		 * p.addPoint((int)(cx+rx*Math.cos(angle+Math.PI/3)),(int)(cy+ry*Math.sin(angle+
		 * Math.PI/3)));
		 * p.addPoint((int)(cx+0.866*rx*rx*Math.cos(angle+Math.PI/2)),(int)(cy+0.866*ry*
		 * Math.sin(angle+Math.PI/2))); c.append(p,false);
		 */

		// add pos
		/*
		 * AffineTransform t = new AffineTransform(); int fs = (int)(h/3);
		 * 
		 * double tx1 = cx + rx*Math.cos(angle+Math.PI/2) + fs*Math.cos(angle+Math.PI);
		 * double ty1 = cy + ry*Math.sin(angle+Math.PI/2) + fs*Math.sin(angle+Math.PI);
		 * t.setToTranslation(tx1,ty1);
		 * c.append(t.createTransformedShape(getTextShape("" + first_pos,
		 * theGraphicOptions.LINKAGE_INFO_FONT_FACE, fs)),false);
		 * 
		 * double tx2 = cx + rx*Math.cos(angle-Math.PI/2) + fs*Math.cos(angle+Math.PI) +
		 * fs*Math.cos(angle+Math.PI/2); double ty2 = cy + ry*Math.sin(angle-Math.PI/2)
		 * + fs*Math.sin(angle+Math.PI) + fs*Math.sin(angle+Math.PI/2);
		 * t.setToTranslation(tx2,ty2);
		 * c.append(t.createTransformedShape(getTextShape("" + last_pos,
		 * theGraphicOptions.LINKAGE_INFO_FONT_FACE, fs)),false);
		 */
	}

	static private Path createEnd(Device device, double angle, double x, double y, double w, double h) {
		// static private Shape createEnd(double angle, double x, double y, double w,
		// double h) {
		double rx = w / 2.;
		double ry = h / 2.;
		double cx = x + w / 2.;
		double cy = y + h / 2.;

		// start point
		double x1 = cx + rx * Math.cos(angle - Math.PI / 2.);
		double y1 = cy + ry * Math.sin(angle - Math.PI / 2.);

		// end point
		double x2 = cx + rx * Math.cos(angle + Math.PI / 2.);
		double y2 = cy + ry * Math.sin(angle + Math.PI / 2.);

		double curve = 0.75;
		// double curve = 0.5;
		// ctrl point 1
		double cx1 = cx + curve * rx * Math.cos(angle - Math.PI / 2.);
		double cy1 = cy + curve * ry * Math.sin(angle - Math.PI / 2.);
		double tx1 = cx1 + curve * rx * Math.cos(angle - Math.PI);
		double ty1 = cy1 + curve * ry * Math.sin(angle - Math.PI);

		// ctrl point 2
		double cx2 = cx + curve * rx * Math.cos(angle + Math.PI / 2.);
		double cy2 = cy + curve * ry * Math.sin(angle + Math.PI / 2.);
		double tx2 = cx2 + curve * rx * Math.cos(angle);
		double ty2 = cy2 + curve * ry * Math.sin(angle);

		Path p = new Path(device);
		p.moveTo((float) x1, (float) y1);
		p.cubicTo((float) tx1, (float) ty1, (float) tx2, (float) ty2, (float) x2, (float) y2);
		return p;
		/*
		 * Polygon p = new Polygon(); p.addPoint((int)x1,(int)y1);
		 * p.addPoint((int)x2,(int)y2); return p;
		 */
		// return new CubicCurve2D.Double(x1, y1, tx1, ty1, tx2, ty2, x2, y2);
	}

	static private Path createBracket(Device device, double angle, double x, double y, double w, double h) {
		// static private Shape createBracket(double angle, double x, double y, double
		// w, double h) {

		double rx = w / 2.;
		double ry = h / 2.;
		double cx = x + w / 2.;
		double cy = y + h / 2.;

		// first start point
		double x11 = cx + rx * Math.cos(angle - Math.PI / 2.) + 0.2 * rx * Math.cos(angle);
		double y11 = cy + ry * Math.sin(angle - Math.PI / 2.) + 0.2 * ry * Math.sin(angle);

		// first ctrl point 1
		double tx11 = cx + 0.9 * rx * Math.cos(angle - Math.PI / 2.) + 0.2 * rx * Math.cos(angle - Math.PI);
		double ty11 = cy + 0.9 * ry * Math.sin(angle - Math.PI / 2.) + 0.2 * ry * Math.sin(angle - Math.PI);

		// first ctrl point 2;
		double tx21 = cx + 0.1 * rx * Math.cos(angle - Math.PI / 2.) + 0.2 * rx * Math.cos(angle);
		double ty21 = cy + 0.1 * ry * Math.sin(angle - Math.PI / 2.) + 0.2 * ry * Math.sin(angle);

		// first end point
		double x21 = cx + 0.2 * rx * Math.cos(angle - Math.PI);
		double y21 = cy + 0.2 * ry * Math.sin(angle - Math.PI);

		// first shape
		Path s1 = new Path(device);
		s1.moveTo((float) x11, (float) y11);
		s1.cubicTo((float) tx11, (float) ty11, (float) tx21, (float) ty21, (float) x21, (float) y21);
		// Shape s1 = new CubicCurve2D.Double(x11, y11, tx11, ty11, tx21, ty21, x21,
		// y21);

		// second start point
		double x12 = cx + rx * Math.cos(angle + Math.PI / 2.) + 0.2 * rx * Math.cos(angle);
		double y12 = cy + ry * Math.sin(angle + Math.PI / 2.) + 0.2 * ry * Math.sin(angle);

		// second ctrl point 1
		double tx12 = cx + 0.9 * rx * Math.cos(angle + Math.PI / 2.) + 0.2 * rx * Math.cos(angle - Math.PI);
		double ty12 = cy + 0.9 * ry * Math.sin(angle + Math.PI / 2.) + 0.2 * ry * Math.sin(angle - Math.PI);

		// second ctrl point 2;
		double tx22 = cx + 0.1 * rx * Math.cos(angle + Math.PI / 2.) + 0.2 * rx * Math.cos(angle);
		double ty22 = cy + 0.1 * ry * Math.sin(angle + Math.PI / 2.) + 0.2 * ry * Math.sin(angle);

		// second end point
		double x22 = cx + 0.2 * rx * Math.cos(angle - Math.PI);
		double y22 = cy + 0.2 * ry * Math.sin(angle - Math.PI);

		// second shape
		Path s2 = new Path(device);
		s2.moveTo((float) x12, (float) y12);
		s2.cubicTo((float) tx12, (float) ty12, (float) tx22, (float) ty22, (float) x22, (float) y22);
		// ShapeType s2 = new CubicCurve2D.Double(x12, y12, tx12, ty12, tx22, ty22, x22,
		// y22);

		// generate bracket
		Path b = new Path(device);
		b.addPath(s1);
		b.addPath(s2);
		s1.dispose();
		s2.dispose();
		// GeneralPath b = new GeneralPath();
		// b.append(s1, false);
		// b.append(s2, false);
		return b;
	}

	private Path createRepetition(Device device, double angle, double x, double y, double w, double h) {
		// private Shape createRepetition(double angle, double x, double y, double w,
		// double h) {

		double r = Math.min(w, h);
		double cx = x + w / 2.;
		double cy = y + h / 2.;

		// -----
		// create shape
		Path p = new Path(device);
		// Polygon p = new Polygon();

		// first point
		double x1 = cx + r * Math.cos(angle - Math.PI / 2.) + r / 4. * Math.cos(angle + Math.PI);
		double y1 = cy + r * Math.sin(angle - Math.PI / 2.) + r / 4. * Math.sin(angle + Math.PI);
		p.moveTo((float) x1, (float) y1);
		// p.addPoint((int) x1, (int) y1);

		// second point
		double x2 = cx + r * Math.cos(angle - Math.PI / 2.);
		double y2 = cy + r * Math.sin(angle - Math.PI / 2.);
		p.lineTo((float) x2, (float) y2);
		// p.addPoint((int) x2, (int) y2);

		// third point
		double x3 = cx + r * Math.cos(angle + Math.PI / 2.);
		double y3 = cy + r * Math.sin(angle + Math.PI / 2.);
		p.lineTo((float) x3, (float) y3);
		// p.addPoint((int) x3, (int) y3);

		// fourth point
		double x4 = cx + r * Math.cos(angle + Math.PI / 2.) + r / 4. * Math.cos(angle + Math.PI);
		double y4 = cy + r * Math.sin(angle + Math.PI / 2.) + r / 4. * Math.sin(angle + Math.PI);
		p.lineTo((float) x4, (float) y4);
		// p.addPoint((int) x4, (int) y4);

		// close shape
		p.lineTo((float) x3, (float) y3);
		p.lineTo((float) x2, (float) y2);
		p.close();
		// p.addPoint((int) x3, (int) y3);
		// p.addPoint((int) x2, (int) y2);

		return p;
	}

	private Path createShape(Device device, Residue node, Rectangle par_bbox, Rectangle cur_bbox, Rectangle sup_bbox,
			ResAngle orientation) {
		// private Shape createShape(Residue node, Rectangle par_bbox, Rectangle
		// cur_bbox, Rectangle sup_bbox,
		// ResAngle orientation) {

		ResidueStyle style = theResidueStyleDictionary.getStyle(node);
		String shape = style.getShape();

		if (shape == null || shape.equals("none") || shape.equals("-"))
			return null;

		float x = (float) cur_bbox.x;
		float y = (float) cur_bbox.y;
		float w = (float) cur_bbox.width;
		float h = (float) cur_bbox.height;
		// double x = (double) cur_bbox.getX();
		// double y = (double) cur_bbox.getY();
		// double w = (double) cur_bbox.getWidth();
		// double h = (double) cur_bbox.getHeight();

		// non-oriented shapes
		if (shape.equals("point")) {
			Path path = new Path(device);
			path.addRectangle(x + w / 2.f, y + h / 2.f, 0, 0);
			path.close();
			// return new Rectangle2D.Double(x + w / 2., y + h / 2., 0, 0);
			return path;
		}
		if (shape.equals("square")) {
			Path path = new Path(device);
			path.addRectangle(x, y, w, h);
			path.close();
			return path;
			// return new Rectangle2D.Double(x, y, w, h);
		}
		if (shape.equals("circle")) {
			Path path = new Path(device);
			path.addArc(x, y, w, h, 0, 360);
			path.close();
			return path;
			// return new Ellipse2D.Double(x, y, w, h);
		}
		if (shape.equals("diamond"))
			return createDiamond(device, x, y, w, h);
		if (shape.equals("flatdiamond"))
			return createFlatDiamond(device, x, y, w, h);
		if (shape.equals("rhombus"))
			return createRhombus(device, x, y, w, h);
		if (shape.equals("star"))
			return createStar(device, x, y, w, h, 5);
		if (shape.equals("sixstar"))
			return createStar(device, x, y, w, h, 6);
		if (shape.equals("sevenstar"))
			return createStar(device, x, y, w, h, 7);
		if (shape.equals("pentagon"))
			return createPentagon(device, x, y, w, h);
		if (shape.equals("hexagon"))
			return createHexagon(device, x, y, w, h);
		if (shape.equals("flathexagon"))
			return createFlatHexagon(device, x, y, w, h);
		if (shape.equals("heptagon"))
			return createHeptagon(device, x, y, w, h);
		if (shape.equals("flatsquare")) {
			Path path = new Path(device);
			path.addRectangle(x, y+h*.25f, w, h*.5f);
			path.close();
			return path;
//			return new Rectangle.Double(x,y+h*.25,w,h*.5);
		}


		Point pp = (par_bbox != null) ? center(par_bbox) : center(cur_bbox);
		Point pc = center(cur_bbox);
		Point ps = (sup_bbox != null) ? center(sup_bbox) : center(cur_bbox);

		// partially oriented shapes
		if (shape.equals("triangle")) {
//			if (this.theGraphicOptions.NOTATION.equals(GraphicOptionsSWT.NOTATION_SNFG)) {
//				if (node.getWasSticky()) {
//					if (orientation.getIntAngle() == 180)
//						return createTopTriangle(device, theGraphicOptions.ORIENTATION, x, y, w, h);
//					return createTriangle(device, angle(pp, ps), x, y, w, h);
//				}
//				return createTopTriangle(device, theGraphicOptions.ORIENTATION, x, y, w, h);
//			}
			return createTriangle(device, angle(pp, ps), x, y, w, h);
		}
		if (shape.equals("hatdiamond"))
			return createHatDiamond(device, x, y, w, h);
		if (shape.equals("rhatdiamond"))
			return createRHatDiamond(device, x, y, w, h);

		if (shape.equals("bracket"))
			return createBracket(device, orientation.opposite().getAngle(), x, y, w, h);
		if (shape.equals("startrep"))
			return createRepetition(device, orientation.opposite().getAngle(), x, y, w, h);
		if (shape.equals("endrep"))
			return createRepetition(device, orientation.getAngle(), x, y, w, h);

		// totally oriented shapes
		if (shape.startsWith("acleavage")) {
			Vector<String> tokens = TextUtils.tokenize(shape, "_");
			int first_pos = Integer.parseInt(tokens.elementAt(1));
			int last_pos = Integer.parseInt(tokens.elementAt(2));
			return createCrossRingCleavage(device, angle(pc, ps), x, y, w, h, first_pos, last_pos);
		}
		if (shape.equals("bcleavage"))
			return createCleavage(device, angle(ps, pc), x, y, w, h, false);
		if (shape.equals("ccleavage"))
			return createCleavage(device, angle(ps, pc), x, y, w, h, true);

		if (shape.startsWith("xcleavage")) {
			Vector<String> tokens = TextUtils.tokenize(shape, "_");
			int first_pos = Integer.parseInt(tokens.elementAt(1));
			int last_pos = Integer.parseInt(tokens.elementAt(2));
			return createCrossRingCleavage(device, angle(pp, pc), x, y, w, h, first_pos, last_pos);
		}
		if (shape.equals("ycleavage"))
			return createCleavage(device, angle(pp, pc), x, y, w, h, true);
		if (shape.equals("zcleavage"))
			return createCleavage(device, angle(pp, pc), x, y, w, h, false);

		if (shape.equals("end"))
			return createEnd(device, angle(pp, ps), x, y, w, h);

		Path p = new Path(device);
		p.addRectangle(cur_bbox.x, cur_bbox.y, cur_bbox.width, cur_bbox.height);
		return p;
	}

	private Path createRepetitionText(Device device, double angle, double x, double y, double w, double h, int min,
			int max) {
		// private Shape createRepetitionText(double angle, double x, double y, double
		// w, double h, int min, int max) {

		double r = Math.min(w, h);
		double cx = x + w / 2.;
		double cy = y + h / 2.;

		double x2 = cx + r * Math.cos(angle - Math.PI / 2.);
		double y2 = cy + r * Math.sin(angle - Math.PI / 2.);
		double x3 = cx + r * Math.cos(angle + Math.PI / 2.);
		double y3 = cy + r * Math.sin(angle + Math.PI / 2.);

		Path ret = new Path(device);
		// GeneralPath ret = new GeneralPath();

		// --------
		// add min repetition
		if (min >= 0 || max >= 0) {
			String text = (min >= 0) ? "" + min : "0";
			Rectangle tb = TextShapeUtils.textBounds(device, text, theGraphicOptions.LINKAGE_INFO_FONT_FACE,
					theGraphicOptions.LINKAGE_INFO_SIZE);

			double dist = (isUp(angle) || isDown(angle)) ? tb.width / 2 + 4 : tb.height / 2 + 4;
			double xmin, ymin;
			if (isLeft(angle) || isUp(angle)) {
				xmin = x2 + dist * Math.cos(angle - Math.PI / 2.) - tb.width / 2.;
				ymin = y2 + dist * Math.sin(angle - Math.PI / 2.) - tb.height / 2.;
			} else {
				xmin = x3 + dist * Math.cos(angle + Math.PI / 2.) - tb.width / 2.;
				ymin = y3 + dist * Math.sin(angle + Math.PI / 2.) - tb.height / 2.;
			}

			Path pText = TextShapeUtils.getTextPath(device, xmin, ymin, text, theGraphicOptions.LINKAGE_INFO_FONT_FACE,
					theGraphicOptions.LINKAGE_INFO_SIZE);
			ret.addPath(pText);
			pText.dispose();
			// ret.append(getTextShape(xmin, ymin, text,
			// theGraphicOptions.LINKAGE_INFO_FONT_FACE,
			// theGraphicOptions.LINKAGE_INFO_SIZE), false);
		}

		// --------
		// add max repetition
		if (min >= 0 || max >= 0) {
			String text = (max >= 0) ? "" + max : "+inf";
			Rectangle tb = TextShapeUtils.textBounds(device, text, theGraphicOptions.LINKAGE_INFO_FONT_FACE,
					theGraphicOptions.LINKAGE_INFO_SIZE);
			// Dimension tb = textBounds(text, theGraphicOptions.LINKAGE_INFO_FONT_FACE,
			// theGraphicOptions.LINKAGE_INFO_SIZE);

			double dist = (isUp(angle) || isDown(angle)) ? tb.width / 2 + 4 : tb.height / 2 + 4;
			double xmax, ymax;
			if (isLeft(angle) || isUp(angle)) {
				xmax = x3 + dist * Math.cos(angle + Math.PI / 2.) - tb.width / 2.;
				ymax = y3 + dist * Math.sin(angle + Math.PI / 2.) - tb.height / 2.;
			} else {
				xmax = x2 + dist * Math.cos(angle - Math.PI / 2.) - tb.width / 2.;
				ymax = y2 + dist * Math.sin(angle - Math.PI / 2.) - tb.height / 2.;
			}

			Path pText = TextShapeUtils.getTextPath(device, xmax, ymax, text, theGraphicOptions.LINKAGE_INFO_FONT_FACE,
					theGraphicOptions.LINKAGE_INFO_SIZE);
			ret.addPath(pText);
			pText.dispose();
			// ret.append(getTextShape(xmax, ymax, text,
			// theGraphicOptions.LINKAGE_INFO_FONT_FACE,
			// theGraphicOptions.LINKAGE_INFO_SIZE), false);
		}

		return ret;
	}

	private Path createTextShape(Device device, Residue node, Rectangle par_bbox, Rectangle cur_bbox,
			Rectangle sup_bbox, ResAngle orientation) {
		// private Shape createTextShape(Residue node, Rectangle par_bbox, Rectangle
		// cur_bbox, Rectangle sup_bbox,
		// ResAngle orientation) {

		ResidueStyle style = theResidueStyleDictionary.getStyle(node);
		String shape = style.getShape();

		if (shape == null || shape.equals("none") || shape.equals("-"))
			return null;

		double x = (double) cur_bbox.x;
		double y = (double) cur_bbox.y;
		double w = (double) cur_bbox.width;
		double h = (double) cur_bbox.height;
		// double x = (double) cur_bbox.getX();
		// double y = (double) cur_bbox.getY();
		// double w = (double) cur_bbox.getWidth();
		// double h = (double) cur_bbox.getHeight();

		if (shape.equals("endrep"))
			return createRepetitionText(device, orientation.getAngle(), x, y, w, h, node.getMinRepetitions(),
					node.getMaxRepetitions());

		return null;
	}

	// --------------
	// Fill

	static private Path createRectangle(Device device, float x, float y, float w, float h) {
		Path p = new Path(device);
		p.addRectangle(x, y, w, h);
		p.close();
		return p;
	}

	static private Path createTriangle(Device device, float x1, float y1, float x2, float y2, float x3, float y3) {
		// static private Shape createTriangle(double x1, double y1, double x2, double
		// y2, double x3, double y3) {
		Path p = new Path(device);
		p.moveTo(x1, y1);
		p.lineTo(x2, y2);
		p.lineTo(x3, y3);
		p.close();
		// Polygon p = new Polygon();
		// p.addPoint((int) x1, (int) y1);
		// p.addPoint((int) x2, (int) y2);
		// p.addPoint((int) x3, (int) y3);
		return p;
	}

	static private Path createHalf(Device device, float rx, float ry, float cx, float cy) {
		double step = Math.PI / 2.5;
		Path p = new Path(device);
		// Polygon p = new Polygon();
		p.moveTo((float) ((cx - .5) + rx * Math.cos(5 * step - Math.PI / 2.)),
				(float) (cy + ry * Math.sin(5 * step - Math.PI / 2.)));
		p.lineTo((float) ((cx - .5) + rx * Math.cos(4 * step - Math.PI / 2.)),
				(float) (cy + ry * Math.sin(4 * step - Math.PI / 2.)));
		p.lineTo((float) ((cx - .5) + rx * Math.cos(3 * step - Math.PI / 2.)),
				(float) (cy + ry * Math.sin(3 * step - Math.PI / 2.)));
		p.lineTo((float) ((cx - .5) + rx * Math.cos(2.5 * step - Math.PI / 2.)),
				(float) (cy + ry * Math.sin(3 * step - Math.PI / 2.)));

		// p.addPoint((int) ((cx - .5) + rx * Math.cos(5 * step - Math.PI / 2.)),
		// (int) (cy + ry * Math.sin(5 * step - Math.PI / 2.)));
		// p.addPoint((int) ((cx - .5) + rx * Math.cos(4 * step - Math.PI / 2.)),
		// (int) (cy + ry * Math.sin(4 * step - Math.PI / 2.)));
		// p.addPoint((int) ((cx - .5) + rx * Math.cos(3 * step - Math.PI / 2.)),
		// (int) (cy + ry * Math.sin(3 * step - Math.PI / 2.)));
		// p.addPoint((int) ((cx - .5) + rx * Math.cos(2.5 * step - Math.PI / 2.)),
		// (int) (cy + ry * Math.sin(3 * step - Math.PI / 2.)));
		p.close();
		return p;
	}

	static private Path createCheckered(Device device, float x, float y, float w, float h) {
		// static private ShapeType createCheckered(double x, double y, double w, double
		// h) {
		Path c = new Path(device);
		Path p1 = new Path(device);
		p1.addRectangle(x + w / 2.f, y, w / 2.f, h / 2.f);
		p1.close();
		c.addPath(p1);
		p1.dispose();
		Path p2 = new Path(device);
		p2.addRectangle(x, y + h / 2.f, w / 2.f, h / 2.f);
		p2.close();
		c.addPath(p2);
		p2.dispose();
		return c;

	}

	static private Path createArc(Device device, double x, double y, double w, double h, int start_pos, int end_pos) {
		Path p = new Path(device);
		p.addArc((float) (x - 0.5 * w), (float) (y - 0.5 * h), (float) (2 * w), (float) (2 * h),
				(float) (-end_pos * 60. + 30.), (float) (-((start_pos - end_pos + 6) % 6) * 60.));
		p.lineTo((float) (x + 0.5 * w), (float) (y + 0.5 * h));
		p.close();
		return p;
	}

	private Path createFillShape(Device device, Residue node, Rectangle cur_bbox) {
		// private Shape createFillShape(Residue node, Rectangle cur_bbox) {

		ResidueStyle style = theResidueStyleDictionary.getStyle(node);
		String fillstyle = style.getFillStyle();

		float x = (float) cur_bbox.x;
		float y = (float) cur_bbox.y;
		float w = (float) cur_bbox.width;
		float h = (float) cur_bbox.height;

		if (fillstyle.equals("empty"))
			return null;
		if (fillstyle.equals("full")) {
			return createRectangle(device, cur_bbox.x, cur_bbox.y, cur_bbox.width, cur_bbox.height);
			// return cur_bbox;
		}
		if (fillstyle.equals("half"))
			return createHalf(device, w / 2.f, h / 2.f, x + w / 2.f, y + h / 2.f);

		if (fillstyle.equals("left")) {
			return createRectangle(device, x, y, w / 2.f, h);
			// return new Rectangle2D.Double(x, y, w / 2., h);
		}
		if (fillstyle.equals("top")) {
			return createRectangle(device, x, y, w, h / 2.f);
			// return new Rectangle2D.Double(x, y, w, h / 2.);
		}
		if (fillstyle.equals("right")) {
			return createRectangle(device, x + w / 2.f, y, w / 2.f, h);
			// return new Rectangle2D.Double(x + w / 2., y, w / 2., h);
		}
		if (fillstyle.equals("bottom")) {
			return createRectangle(device, x, y + h / 2.f, w, h / 2.f);
			// return new Rectangle2D.Double(x, y + h / 2., w, h / 2.);
		}

		if (fillstyle.equals("topleft"))
			return createTriangle(device, x, y, x + w, y, x, y + h);
		if (fillstyle.equals("topright"))
			return createTriangle(device, x, y, x + w, y, x + w, y + h);
		if (fillstyle.equals("bottomright"))
			return createTriangle(device, x + w, y, x + w, y + h, x, y + h);
		if (fillstyle.equals("bottomleft"))
			return createTriangle(device, x, y, x + w, y + h, x, y + h);

		float cx = x + w / 2.f;
		float cy = y + h / 2.f;
		float rx = w / 6.f;
		float ry = h / 6.f;
		if (fillstyle.equals("circle")) {
			Path p = new Path(device);
			p.addArc(cx - rx, cy - ry, 2.f * rx, 2.f * ry, 0, 360);
			p.close();
			return p;
		}
		if (fillstyle.equals("checkered"))
			return createCheckered(device, x, y, w, h);
		if (fillstyle.startsWith("arc")) {
			Vector<String> tokens = TextUtils.tokenize(fillstyle, "_");
			int first_pos = Integer.parseInt(tokens.elementAt(1));
			int last_pos = Integer.parseInt(tokens.elementAt(2));
			return createArc(device, x, y, w, h, first_pos, last_pos);
		}

		return null;
	}

}