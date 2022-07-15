package org.grits.toolbox.tools.glycanbuilder.core.structure.document;

import java.util.LinkedList;

/**
 * Class for using undo and redo operation.
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class StateManager {

	private static final int MAXIMUM_NUMBER_OF_STATES = 20;

	private int m_nActions;
	private int m_iCurState;
	private String m_strCurState;
	private LinkedList<String> m_lStates;

	public StateManager() {
		this.reset();
	}

	/**
	 * Reset the state holder. Clear all of the stored information
	 */
	public void reset() {
		this.m_nActions = 0;
		this.m_iCurState = 0;
		this.m_strCurState = "";
		this.m_lStates = new LinkedList<>();
		this.m_lStates.add(this.m_strCurState);
	}

	public String getCurrentState() {
		return this.m_strCurState;
	}

	public void addState(String str) {
		if (str == null)
			return;

		this.m_strCurState = str;

		// Clear following actions
		while (this.m_iCurState < (this.m_lStates.size() - 1))
			this.m_lStates.removeLast();

		// Add new action
		this.m_lStates.addLast(str);

		// Limit the size of the queue
		while (this.m_lStates.size() > MAXIMUM_NUMBER_OF_STATES)
			this.m_lStates.removeFirst();

		// Update indices
		this.m_iCurState = this.m_lStates.size() - 1;
		this.m_nActions++;
//		System.out.println(this);
	}

	/**
	 * Return {@code true} if the state has changed and can be restored
	 */
	public boolean canUndo() {
		return (this.m_iCurState > 0);
	}

	/**
	 * Do the rollback to the previous state
	 */
	public void undo() {
		if (!this.canUndo())
			return;
		this.m_iCurState--;
		this.m_strCurState = this.m_lStates.get(this.m_iCurState);
		this.m_nActions--;
//		System.out.println(this);
	}

	/**
	 * Apply the saved changes if it has been restored to a previous state
	 */
	public boolean canRedo() {
		return (this.m_iCurState < (this.m_lStates.size() - 1));
	}

	/**
	 * Do the rollforward to the next state
	 */
	public void redo() {
		if (!this.canRedo())
			return;
		this.m_iCurState++;
		this.m_strCurState = this.m_lStates.get(this.m_iCurState);
		this.m_nActions++;
//		System.out.println(this);
	}

	public String toString() {
		String str = "[";
		str += " curStateNo="+this.m_iCurState;
		str += " curState="+this.m_strCurState;
		str += " nActions="+this.m_nActions;
		str += " savedStates="+this.m_lStates;
		str += " ]";
		return str;
	}
}
