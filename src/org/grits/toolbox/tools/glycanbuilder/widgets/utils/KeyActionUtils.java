package org.grits.toolbox.tools.glycanbuilder.widgets.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * Utility class containing methods to facilitate the used of the keyboard functions
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class KeyActionUtils {

	private KeyActionUtils() {
	}

	public static boolean isUndoTrigger(KeyEvent e) {
		// Ctrl+Z
		return ((e.stateMask & SWT.MODIFIER_MASK ) == SWT.MOD1 && (char)e.keyCode == 'z' );
	}

	public static boolean isRedoTrigger(KeyEvent e) {
		// Ctrl+Y
		return ((e.stateMask & SWT.MODIFIER_MASK ) == SWT.MOD1 && (char)e.keyCode == 'y' );
	}

	public static boolean isCutTrigger(KeyEvent e) {
		// Ctrl+X
		return ((e.stateMask & SWT.MODIFIER_MASK ) == SWT.MOD1 && (char)e.keyCode == 'x' );
	}

	public static boolean isCopyTrigger(KeyEvent e) {
		// Ctrl+C
		return ((e.stateMask & SWT.MODIFIER_MASK ) == SWT.MOD1 && (char)e.keyCode == 'c' );
	}

	public static boolean isPasteTrigger(KeyEvent e) {
		// Ctrl+Y
		return ((e.stateMask & SWT.MODIFIER_MASK ) == SWT.MOD1 && (char)e.keyCode == 'v' );
	}

	public static boolean isDeleteTrigger(KeyEvent e) {
		// DEL
		return ((e.stateMask & SWT.MODIFIER_MASK ) == 0 && e.keyCode == SWT.DEL );
	}

	public static boolean isSelectAllTrigger(KeyEvent e) {
		// Ctrl+A
		return ((e.stateMask & SWT.MODIFIER_MASK ) == SWT.MOD1 && (char)e.keyCode == 'a' );
	}

	public static boolean isSelectNoneTrigger(KeyEvent e) {
		// ESC
		return ((e.stateMask & SWT.MODIFIER_MASK ) == 0 && e.keyCode == SWT.ESC );
	}

}
