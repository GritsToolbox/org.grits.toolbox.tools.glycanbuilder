package org.grits.toolbox.tools.glycanbuilder.widgets.canvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.grits.toolbox.tools.glycanbuilder.widgets.utils.KeyActionUtils;

public class GlycanCanvasListenerProvider {

	public static void addListenersToCanvas(GlycanCanvasInterface canvas) {
		canvas.getControl().addMouseListener( new GlycanCanvasMouseListener(canvas) );
		// Add key listener for adding receiver of keyboard actions
		canvas.getControl().addKeyListener( new GlycanCanvasKeyListener(canvas) );
	}

	private static class GlycanCanvasMouseListener implements MouseListener {

		private GlycanCanvasInterface m_canvas;

		private GlycanCanvasMouseListener(GlycanCanvasInterface canvas) {
			this.m_canvas = canvas;
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseDown(MouseEvent e) {
			// Select residue or linkage
			m_canvas.performResidueSelection(e);
			m_canvas.updateView(false);

			// Sets Menu based on the selected part
			GlycanCanvasMenu.setMenu(m_canvas);
			// Opens pop-up menu for Mac
			if ( SWT.MOD4 != 0 && (
				(e.button == 1 && (e.stateMask & SWT.MODIFIER_MASK) == SWT.MOD4)  // Ctrl + left click
				|| e.button == 3 // right click
			))
				m_canvas.getControl().getMenu().setVisible(true);

			// Forces focus to the clicked Control to use key listener
			((Control) e.widget).forceFocus();

		}

		@Override
		public void mouseUp(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}

	private static class GlycanCanvasKeyListener implements KeyListener {

		private GlycanCanvasInterface m_canvas;

		private GlycanCanvasKeyListener(GlycanCanvasInterface canvas) {
			this.m_canvas = canvas;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if ( KeyActionUtils.isUndoTrigger(e) ) {
				m_canvas.undo();
			} else if ( KeyActionUtils.isRedoTrigger(e) ) {
				m_canvas.redo();
			} else if ( KeyActionUtils.isCopyTrigger(e) ) {
				if ( m_canvas.canCopy() )
					m_canvas.copy();
			} else if ( KeyActionUtils.isCutTrigger(e) ) {
				if ( m_canvas.canCut() )
					m_canvas.cut();
			} else if ( KeyActionUtils.isPasteTrigger(e) ) {
				if ( m_canvas.canPaste() )
					m_canvas.paste();
			} else if ( KeyActionUtils.isDeleteTrigger(e) ) {
				if ( m_canvas.canDelete() )
					m_canvas.delete();
			} else if ( KeyActionUtils.isSelectAllTrigger(e) ) {
				m_canvas.selectAll();
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			m_canvas.updateView(true);
		}

	}

}
