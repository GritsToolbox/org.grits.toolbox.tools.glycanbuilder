package org.grits.toolbox.tools.glycanbuilder.widgets.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

/**
 * Utility class containing methods to facilitate the used of the mouse
 * functions
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 */
public class MouseUtils {

	private MouseUtils() {
	};

	/**
	 * Return {@code true} if the combination of mouse and keyboard buttons
	 * used to select objects has been pressed (left click)
	 * 
	 * @param e
	 *            the {@link MouseEvent} information sent to the mouse events
	 *            listener
	 */
	static public boolean isSelectTrigger(MouseEvent e) {
		return (e.button == 1 && (e.stateMask & SWT.MODIFIER_MASK) == 0);
	}

	/**
	 * Return {@code true} if the combination of mouse and keyboard buttons
	 * used to select additional objects has been pressed (ctrl + left click)
	 * 
	 * @param e
	 *            the {@link MouseEvent} information sent to the mouse events
	 *            listener
	 */
	static public boolean isAddSelectTrigger(MouseEvent e) {
//		return (e.button == 1 &&
//				((e.stateMask & SWT.MODIFIER_MASK) == SWT.MOD1 || (e.stateMask & SWT.MODIFIER_MASK) == SWT.MOD4 ));
		return (e.button == 1 && ((e.stateMask & SWT.MODIFIER_MASK) == SWT.MOD1) );
	}

	/**
	 * Return {@code true} if the combination of mouse and keyboard buttons
	 * used to select all objects in a range has been pressed (shift + left click)
	 * 
	 * @param e
	 *            the {@link MouseEvent} information sent to the mouse events
	 *            listener
	 */
	static public boolean isSelectAllTrigger(MouseEvent e) {
		return (e.button == 1 && (e.stateMask & SWT.MODIFIER_MASK) == SWT.MOD2);
	}
}
