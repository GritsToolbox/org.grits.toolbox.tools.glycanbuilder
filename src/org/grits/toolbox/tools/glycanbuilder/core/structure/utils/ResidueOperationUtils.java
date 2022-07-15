package org.grits.toolbox.tools.glycanbuilder.core.structure.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.eurocarbdb.application.glycanbuilder.CoreType;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.Linkage;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.eurocarbdb.application.glycanbuilder.ResidueDictionary;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.eurocarbdb.application.glycanbuilder.TerminalType;
import org.grits.toolbox.tools.glycanbuilder.widgets.canvas.GlycanCanvasComposite;
import org.grits.toolbox.tools.glycanbuilder.widgets.canvas.GlycanCanvasInterface;
import org.grits.toolbox.tools.glycanbuilder.widgets.canvas.GlycanLabel;

/**
 * Utility class for handling Residues in Glycan.
 * 
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class ResidueOperationUtils {

	/**
	 * Returns a list of Residues between the path from the given Residues {@code a}
	 * to {@code b}.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static List<Residue> getPath(Residue a, Residue b) {

		// get paths from A to root
		Residue nav;
		Stack<Residue> pra = new Stack<Residue>();
		for (nav = a; nav != null; nav = nav.getParent())
			pra.push(nav);

		// get paths from B to root
		Stack<Residue> prb = new Stack<Residue>();
		for (nav = b; nav != null; nav = nav.getParent())
			prb.push(nav);

		// check if root is the same
		if (pra.peek() != prb.peek())
			return new ArrayList<Residue>();

		// remove common steps
		Residue common = null;
		while (!pra.empty() && !prb.empty() && pra.peek() == prb.peek()) {
			common = pra.pop();
			prb.pop();
		}
		pra.push(common);

		// create path
		List<Residue> path = new ArrayList<Residue>();
		for (Iterator<Residue> i = pra.iterator(); i.hasNext();)
			path.add(i.next());
		while (!prb.empty())
			path.add(prb.pop());

		return path;
	}

	/**
	 * Create a list of structures from the selected residues. If two residues in
	 * the list are linked they will be so in the new structures as well.
	 * Disconnected components will constitute separate structures. Residues in
	 * uncertain antenna will be added to all structures created by the residues in
	 * the same original Glycan object.
	 * 
	 * @see Glycan
	 * @see Glycan#addAntenna
	 */
	public static List<Glycan> extractSelection(List<Residue> lResSelected, MassOptions defaultMassOptions) {
		// search roots and antennae
		List<Residue> lRoots = new ArrayList<Residue>();
		List<Residue> lAntennae = new ArrayList<Residue>();
		for (Residue cur : lResSelected) {
			Residue par = cur.getParent();
			if (par == null || !lResSelected.contains(par) || par.isBracket()) {
				if (cur.isAntenna())
					lAntennae.add(cur);
				else if (!cur.isBracket())
					lRoots.add(cur);
			}
		}

		// clone roots
		List<Residue> lClonedRoots = new ArrayList<Residue>();
		for (Residue root : lRoots) {
			Residue rootCloned = cloneStructure(root, lResSelected, true);
			if (!rootCloned.isReducingEnd() || rootCloned.hasChildren()) {
				lClonedRoots.add(rootCloned);
			}
		}

		// create structures
		HashSet<Residue> lAssignedAntennae = new HashSet<Residue>();
		List<Glycan> lClonedGlycans = new ArrayList<Glycan>();
		for (Residue resClonedRoot : lClonedRoots) {
			Glycan glycanCloned = new Glycan(resClonedRoot, false, defaultMassOptions);
			lClonedGlycans.add(glycanCloned);

			// add antennae
			for (Residue resAntennae : lAntennae) {
				lAssignedAntennae.add(resAntennae);
				glycanCloned.addAntenna(cloneStructure(resAntennae, lResSelected, true));
			}
		}

		// create structures from unassigned antennae
		for (Residue resAntenna : lAntennae) {
			if (lAssignedAntennae.contains(resAntenna))
				continue;
			Residue resClonedAntennae = cloneStructure(resAntenna, lResSelected, true);
			lClonedGlycans.add(new Glycan(resClonedAntennae, false, defaultMassOptions));
		}

		// remove unmatched repetitions
		for (Glycan s : lClonedGlycans)
			s.removeUnpairedRepetitions();

		return lClonedGlycans;
	}

	private static Residue cloneStructure(Residue resRoot, List<Residue> lRess, boolean addAttachment) {
		if (resRoot == null)
			return null;

		if (addAttachment && !resRoot.isReducingEnd()
				&& (resRoot.getParent() == null || !resRoot.getParent().isReducingEnd())) {
			Residue resRootCloned = ResidueDictionary.createAttachPoint();
			resRootCloned.addChild(cloneStructure(resRoot, lRess, false), resRoot.getParentLinkage().getBonds());
			return resRootCloned;
		} else {
			// clone this
			Residue resRootCloned = resRoot.cloneResidue();

			// clone children
			for (Linkage link : resRoot.getChildrenLinkages()) {
				Residue resChild = link.getChildResidue();
				if (lRess.contains(resChild))
					resRootCloned.addChild(cloneStructure(resChild, lRess, false), link.getBonds());
			}
			return resRootCloned;
		}
	}

	/**
	 * Adds a Residue with the given Residue as a child of current Residue. Returns
	 * false if no residue is selected.
	 * 
	 * @param res
	 *            Residue to be added
	 * @return true if the addition is successful, otherwise false
	 */
	public static boolean addResidue(Residue resCurrent, Residue resToAdd) {
		if (resCurrent == null)
			return false;
		if (!resCurrent.addChild(resToAdd))
			return false;
		return true;
	}

	/**
	 * Changes the type of current residue to the given one.
	 * 
	 * @param resType
	 *            ResidueType of a Residue to be set
	 * @return true if the change is successful, otherwise false
	 */
	public static boolean changeResidueType(Residue resCurrent, ResidueType resType, boolean isComposition) {
		if (resCurrent == null || (resCurrent.hasParent() && !resType.canHaveParent())
				|| (!resCurrent.hasParent() && !resType.canBeReducingEnd())
				|| (resCurrent.hasChildren() && !resType.canHaveChildren()) || isComposition)
			return false;

		resCurrent.setType(resType);
		return true;
	}

	/**
	 * Removes current Residue from the given Glycan.
	 * 
	 * @return true if the remove is successful, otherwise false
	 */
	public static boolean removeResidue(Residue resCurrent, Glycan glycan) {
		if (resCurrent == null)
			return false;
		if (!glycan.contains(resCurrent))
			return false;

		return glycan.removeResidue(resCurrent);
	}

	public static boolean removeResidues(Glycan glycan, List<Residue> lResToRemove) {
		if (lResToRemove == null || lResToRemove.isEmpty())
			return false;
		return glycan.removeResidues(lResToRemove);
	}

	/**
	 * Merge the specified structures with the glycan object containing
	 * {@code resCurrent}. The root of each structure is added as a child of
	 * {@code resCurrent}, while the uncertain antennae are added to the bracket.
	 * 
	 * @param resCurrent
	 *            Residue to be added the glycans
	 * @param glcyanCurrent
	 *            is the resCurrent is in the composition
	 * @param lGlycanToAdd
	 *            List of Glycans to add
	 * @see Residue#addChild
	 * @see Glycan#addAntenna
	 */
	public static boolean addGlycans(Residue resCurrent, Glycan glycanCurrent, List<Glycan> lGlycanToAdd) {
		if (!canAddGlycansToResidue(resCurrent, glycanCurrent.isComposition(), lGlycanToAdd))
			return false;
		return addGlycansToResidue(resCurrent, glycanCurrent, lGlycanToAdd);
	}

	/**
	 * Return {@code true} if the structures can be merged with the glycan object
	 * containing {@code resCurrent}.
	 * 
	 * @param resCurrent
	 *            Residue to be added the glycans
	 * @param isComposition
	 *            whether the resCurrent is in the composition
	 * @param lGlycanToAdd
	 *            List of Glycans to add
	 * @see #addGlycansToResidue
	 */
	public static boolean canAddGlycansToResidue(Residue resCurrent, boolean isComposition, List<Glycan> lGlycanToAdd) {
		if (resCurrent == null)
			return false;
		if (isComposition)
			return false;

		if (resCurrent.isAntenna() || resCurrent.isBracket()) {
			for (Glycan s : lGlycanToAdd) {
				if (s.isFuzzy())
					return false;
			}
		}

		if (resCurrent.isInRepetition()) {
			for (Glycan s : lGlycanToAdd) {
				if (s.hasRepetition())
					return false;
			}
		}

		return true;
	}

	private static boolean addGlycansToResidue(Residue resCurrent, Glycan glycanCurrent, List<Glycan> lGlycanToAdd) {
		if (lGlycanToAdd == null || lGlycanToAdd.isEmpty())
			return false;

		if (glycanCurrent == null || resCurrent == null || resCurrent.isReducingEnd())
			return false;

		// append roots as child of the current selection
		for (Glycan structure : lGlycanToAdd)
			resCurrent.addChild(structure.getRoot());

		// find non-overlapping set of antennae
		List<Residue> brackets = new LinkedList<Residue>();
		for (Glycan structure : lGlycanToAdd) {
			if (structure.getBracket() == null)
				continue;

			boolean found = false;
			for (Residue res : brackets) {
				if (structure.getBracket().subtreeEquals(res))
					found = true;
			}
			if (!found)
				brackets.add(structure.getBracket());
		}

		// add antennae to current structure
		for (Residue b : brackets) {
			for (Linkage link : b.getChildrenLinkages())
				glycanCurrent.addAntenna(link.getChildResidue(), link.getBonds());
		}

		return true;
	}

	/**
	 * Insert {@code resToInsert} between the {@code current} residue and its
	 * parent.
	 * 
	 * @see Residue#addChild
	 */
	public static Residue insertResidueBefore(Residue resCurrent, boolean isComposition, Residue resToInsert) {
		if (resToInsert == null || resCurrent == null || resCurrent.getParent() == null || isComposition)
			return null;

		// insert node before current selection
		if (!resCurrent.insertParent(resToInsert))
			return null;

		return resToInsert;
	}

	/**
	 * Insert {@code resToInsert} between the {@code resCurrent} residue and its
	 * parent. Do the same to all the residues that are drawn at the same position.
	 * 
	 * @see Residue#addChild
	 */
	public static Residue insertResidueBefore(Residue resCurrent, boolean isComposition, List<Residue> lLinkedRes,
			Residue resToInsert) {
		if (resToInsert == null || resCurrent == null || resCurrent.getParent() == null || isComposition)
			return null;

		// insert node before current selection
		if (!resCurrent.insertParent(resToInsert))
			return null;

		// do the same with linked residues
		if (lLinkedRes != null) {
			for (Residue r : lLinkedRes)
				r.insertParent(resToInsert.cloneResidue());
		}

		return resToInsert;
	}

	private static boolean changeReducingEndTypePVT(Glycan glycan, ResidueType resTypeNew) {
		if (glycan != null)
			return glycan.setReducingEndType(resTypeNew);
		return false;
	}

	/**
	 * Change the reducing end marker for all the specified structures.
	 * 
	 * @see Glycan#setReducingEndType
	 */
	public static boolean changeReducingEndType(List<Glycan> lGlycans, ResidueType resTypeNew) {
		boolean changed = false;
		for (Glycan s : lGlycans)
			changed |= changeReducingEndTypePVT(s, resTypeNew);
		return changed;
	}

	/**
	 * Change the reducing end marker for the {@code currentGlycan}
	 * 
	 * @see Glycan#setReducingEndType
	 */
	public static boolean changeReducingEndType(Glycan currentGlycan, ResidueType resTypeNew) {
		if (changeReducingEndTypePVT(currentGlycan, resTypeNew))
			return true;
		return false;
	}

	/**
	 * Create a repeat block containing the selected nodes.
	 * 
	 * @param resSelectedLast
	 *            the last residue in the repeat block
	 * @param lRepResidues
	 *            the residues in the repeat block
	 * @throws Exception
	 *             if the reapeat unit cannot be created
	 */
	public static boolean createRepetition(Residue resSelectedLast, List<Residue> lRepResidues) throws Exception {
		if (lRepResidues == null || lRepResidues.size() == 0)
			return false;

		// select first and last residue of the repetition,
		// check if the selected residues form a connected component and contain no
		// repetitions

		Residue first = null;
		Residue last = resSelectedLast;
		for (Residue r : lRepResidues) {
			// check content of the repeating unit
			if (r.isReducingEnd())
				throw new Exception("The repeating unit cannot contain the reducing end");
			if (r.isBracket())
				throw new Exception("The repeating unit cannot contain the bracket");
			if (r.isCleavage())
				throw new Exception("The repeating unit cannot contain a cleavage");
			if (r.isRepetition())
				throw new Exception("Repeating units cannot be nested");

			// find the starting point of the repetition and check that all the components
			// are connected
			if (r.getParent() != null && !lRepResidues.contains(r.getParent())) {
				if (first == null)
					first = r;
				else
					throw new Exception("The residue forming the repeating unit must be all linked together.");
			}

			if (resSelectedLast != null)
				continue;
			// first attempt to find the end point of the repetition looking for residues
			// with outbound connections, if more than one throw an exception
			if (r.isSaccharide() && r.getNoChildren() > 0) {
				int links_out = 0;
				for (Linkage l : r.getChildrenLinkages()) {
					if (!lRepResidues.contains(l.getChildResidue()))
						links_out++;
				}
				if (links_out > 0) {
					if (last == null)
						last = r;
					else
						throw new Exception(
								"There are more than one residue in the repeating units with children not in the repeating unit. Check that all the residues in the repeating unit have been selected.");
				}
			}
		}

		if (first == null)
			throw new Exception("No available start point for the repeating unit.");
		if (first.isInRepetition())
			throw new Exception("Repeating units cannot be nested");
		if (first.isAntenna())
			throw new Exception("Repeating units cannot be in an antenna");

		if (last == null) {
			if (lRepResidues.size() == 1)
				last = first;
			else
				return false;
		}

		// create the repetition
		Residue start = ResidueDictionary.createStartRepetition();
		//start.setAnomericState(first.getAnomericState());
		start.setAnomericCarbon(first.getAnomericCarbon());
		start.setParentLinkage(first.getParentLinkage());
		
		first.insertParent(start, first.getParentLinkage().getBonds());
		Residue end = ResidueDictionary.createEndRepetition();
		
		end.setParentLinkage(new Linkage(last, end, new char[] {first.getParentLinkage().getParentPositionsSingle()}));
		
		last.addChild(end, end.getParentLinkage().getBonds());
		for (Iterator<Linkage> il = last.iterator(); il.hasNext();) {
			Linkage child_link = il.next();
			if (child_link.getChildResidue() != end && !lRepResidues.contains(child_link.getChildResidue())) {
				end.getChildrenLinkages().add(child_link);
				child_link.setParentResidue(end);
				il.remove();
			}
		}

		start.setEndRepitionResidue(end);

		return true;
	}

	public static final String ADD = "Add";
	public static final String CHANGE = "Change";
	public static final String INSERT = "Insert";

//	public static boolean canAddCoreStructureToCanvas(GlycanCanvasInterface canvas, CoreType coreType) {
//		try {
//			return canAddResidueToCanvas(canvas, coreType.newCore());
//		} catch (Exception e) {
//		}
//		return false;
//	}
//
//	public static boolean canAddTerminalToCanvas(GlycanCanvasInterface canvas, TerminalType termType) {
//		try {
//			return canAddResidueToCanvas(canvas, termType.newTerminal());
//		} catch (Exception e) {
//		}
//		return false;
//	}
//
	public static boolean canAddResidueToCanvas(GlycanCanvasInterface canvas, Residue resToAdd) {
		GlycanLabel label = null;
		if ( canvas instanceof GlycanLabel ) {
			label = (GlycanLabel)canvas;
		} else if ( canvas instanceof GlycanCanvasComposite ) {
			label = ((GlycanCanvasComposite)canvas).getCurrentGlycanLabel();
		}
		if ( label == null || label.getCurrentResidue() == null )
			return true;

		return ResidueOperationUtils.canModifyResidue(
				label.getCurrentResidue(), resToAdd, ResidueOperationUtils.ADD
			);
	}

	public static boolean canAddCoreStructure(Residue res, CoreType coreType) {
		try {
			return canModifyResidue(res, coreType.newCore(), ADD);
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean canAddTerminal(Residue res, TerminalType termType) {
		try {
			return canModifyResidue(res, termType.newTerminal(), ADD);
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean canAddResidue(Residue res, ResidueType resType) {
		return canModifyResidue(res, new Residue(resType), ADD);
	}

	public static boolean canModifyResidueToLabel(GlycanLabel label, Residue resToAdd, String mode) {
		return ( ResidueOperationUtils.canModifyResidue(label.getCurrentResidue(), resToAdd, mode)
			  || ResidueOperationUtils.canInsertResidue(label.getCurrentLinkage(), resToAdd, mode) );
	}

	public static boolean canModifyResidue(Residue resCurrent, Residue resToAdd, String mode) {
		if ( resCurrent == null || resToAdd == null )
			return false;

		// For add
		if ( mode.equals(ADD) ) {
			if ( !canAddChild(resCurrent, resToAdd) )
				return false;
		// For insert or change
		} else if ( mode.equals(INSERT) || mode.equals(CHANGE) ) {
			Residue resParent = resCurrent.getParent();
			if ( !canAddChild(resParent, resToAdd) )
				return false;
			if ( mode.equals(INSERT) ) {
				if ( !canAddChild(resToAdd, resCurrent) )
					return false;
			} else if ( mode.equals(CHANGE) ) {
				for ( Linkage linChild : resCurrent.getChildrenLinkages() ) {
					Residue resChild = linChild.getChildResidue();
					if ( !canAddChild(resToAdd, resChild) )
						return false;
				}
			}
		}

		return true;
	}

	public static boolean canInsertResidue(Linkage linCurrent, Residue resToAdd, String mode) {
		if ( linCurrent == null || resToAdd == null )
			return false;

		if ( !mode.equals(INSERT) )
			return false;

		if ( !canAddChild(linCurrent.getParentResidue(), resToAdd) )
			return false;
		if ( !canAddChild(resToAdd, linCurrent.getChildResidue()) )
			return false;

		return true;
	}

	public static boolean canAddChild(Residue resParent, Residue resChild) {
		if ( resParent == null )
			return false;

		if ( !resParent.canAddChild(resChild) )
			return false;

		// Retrieve first child
		Residue resChildChild = resChild;
		while ( resChildChild.isAttachPoint() ) {
			if( !resChildChild.hasChildren() )
				return false;
			resChildChild = resChildChild.getChildrenLinkages().get(0).getChildResidue();
		}
		while ( resChildChild.isReducingEnd() && !resChildChild.canHaveParent() )
			resChildChild = resChildChild.firstChild();

		if ( !resParent.canHaveChildren() || !resChildChild.canHaveParent() )
			return false;

		if ( !resParent.isSaccharide() && !resChildChild.isSaccharide() )
			return false;

		return true;
	}


	// /**
	// * Merge the structures formed by the residues in <code>tocopy</code> with the
	// * glycan object containing <code>current</code>.
	// *
	// * @see #addStructures
	// * @see #extractView
	// */
	// public void copyResidues(Residue current, HashSet<Residue> tocopy) {
	// // copy structures
	// List<Glycan> cloned_structures = extractView(tocopy);
	//
	// // paste structures
	// if (canAddStructures(current, cloned_structures) && addStructuresPVT(current,
	// cloned_structures))
	// fireDocumentChanged();
	// }
	//
	// /**
	// * Merge the structures formed by the residues in <code>tocopy</code> with the
	// * glycan object containing <code>current</code>. Do the same with all linked
	// * residues.
	// *
	// * @see #addStructures
	// * @see #extractView
	// */
	// public void copyResidues(Residue current, List<Residue> linked,
	// HashSet<Residue> tocopy) {
	// // copy structures
	// List<Glycan> cloned_structures = extractView(tocopy);
	//
	// // paste structures
	// if (canAddStructures(current, cloned_structures) && addStructuresPVT(current,
	// cloned_structures)) {
	// if (linked != null) {
	// for (Residue r : linked)
	// addStructuresPVT(r, extractView(tocopy));
	// }
	// fireDocumentChanged();
	// }
	// }
	//
	// /**
	// * Merge the structures formed by the residues in <code>tocopy</code> with the
	// * glycan object containing <code>current</code>. Remove the residues from
	// their
	// * containing structures after that.
	// *
	// * @see #addStructures
	// * @see #extractView
	// */
	// public void moveResidues(Residue current, HashSet<Residue> tomove) {
	// // copy structures
	// List<Glycan> cloned_structures = extractView(tomove);
	//
	// // paste structures
	// if (canAddStructures(current, cloned_structures) && addStructuresPVT(current,
	// cloned_structures)) {
	// // remove residues
	// removeResiduesPVT(tomove);
	//
	// fireDocumentChanged();
	// }
	// }
	//
	// /**
	// * Merge the structures formed by the residues in <code>tocopy</code> with the
	// * glycan object containing <code>current</code>. Do the same for all linked
	// * residues. Remove the residues from their containing structures after that.
	// *
	// * @see #addStructures
	// * @see #extractView
	// */
	// public void moveResidues(Residue current, List<Residue> linked,
	// HashSet<Residue> tomove) {
	// // copy structures
	// List<Glycan> cloned_structures = extractView(tomove);
	//
	// // paste structures
	// if (canAddStructures(current, cloned_structures) && addStructuresPVT(current,
	// cloned_structures)) {
	// if (linked != null) {
	// for (Residue r : linked)
	// addStructuresPVT(r, extractView(tomove));
	// }
	//
	// // remove residues
	// removeResiduesPVT(tomove);
	//
	// fireDocumentChanged();
	// }
	// }
	//
	// private boolean removeResiduePVT(Residue toremove) {
	// if (toremove == null)
	// return false;
	//
	// for (int i = 0; i < m_lGlycans.size(); i++) {
	// Glycan structure = m_lGlycans.get(i);
	// if (structure.removeResidue(toremove)) {
	// if (structure.isEmpty())
	// m_lGlycans.remove(i);
	// else {
	// List<Glycan> new_structures = structure.splitMultipleRoots();
	// for (Iterator<Glycan> l = new_structures.iterator(); l.hasNext();)
	// m_lGlycans.add(1 + i++, l.next());
	// }
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// /**
	// * Remove a residue from is containing structure.
	// */
	// public boolean removeResidue(Residue toremove) {
	// if (removeResiduePVT(toremove)) {
	// fireDocumentChanged();
	// return true;
	// }
	// return false;
	// }
	//
	// private boolean removeResiduesPVT(Collection<Residue> toremove) {
	// if (toremove == null)
	// return false;
	//
	// boolean removed = false;
	// for (int i = 0; i < m_lGlycans.size(); i++) {
	// Glycan structure = m_lGlycans.get(i);
	// if (structure.removeResidues(toremove)) {
	// if (structure.isEmpty()) {
	// m_lGlycans.remove(i);
	// i--;
	// } else {
	// List<Glycan> new_structures = structure.splitMultipleRoots();
	// for (Iterator<Glycan> l = new_structures.iterator(); l.hasNext();)
	// m_lGlycans.add(1 + i++, l.next());
	// }
	// removed = true;
	// }
	// }
	// return removed;
	// }

	// /**
	// * Remove the residues from their containing structure.
	// */
	// public void removeResidues(Collection<Residue> toremove) {
	// if (removeResiduesPVT(toremove))
	// fireDocumentChanged();
	// }

	// protected boolean swap(Residue node1, Residue node2) {
	// if (node1 == null || node2 == null)
	// return false;
	// if (node1.getParent() != node2.getParent())
	// return false;
	//
	// Residue parent = node1.getParent();
	// if (parent.swapChildren(node1, node2)) {
	// fireDocumentChanged();
	// return true;
	// }
	// return false;
	// }


}
