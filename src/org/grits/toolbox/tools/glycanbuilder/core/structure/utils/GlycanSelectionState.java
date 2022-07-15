package org.grits.toolbox.tools.glycanbuilder.core.structure.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.Linkage;
import org.eurocarbdb.application.glycanbuilder.Residue;

public class GlycanSelectionState {

	private Glycan m_glycan;
	private HashSet<Residue> m_setSelectedResidues;
	private HashSet<Linkage> m_setSelectedLinkages;

	public GlycanSelectionState(Glycan glycan) {
		this.m_glycan = glycan;
		this.m_setSelectedResidues = new HashSet<>();
		this.m_setSelectedLinkages = new HashSet<>();
	}

	public void addResidue(Residue res) {
		if ( !this.m_glycan.contains(res) )
			return;
		this.m_setSelectedResidues.add(res);
	}

	public void addLinkage(Linkage lin) {
		if ( !this.m_glycan.contains(lin.getParentResidue())
		  || !this.m_glycan.contains(lin.getChildResidue()) )
			return;
		this.m_setSelectedLinkages.add(lin);
	}

	public HashSet<Residue> getSelectedResidues() {
		return this.m_setSelectedResidues;
	}

	public HashSet<Linkage> getSelectedLinkages() {
		return this.m_setSelectedLinkages;
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null || ! (obj instanceof GlycanSelectionState) )
			return false;

		GlycanSelectionState other = (GlycanSelectionState)obj;
		// Compare glycan
		if ( !this.m_glycan.equalsStructure(other.m_glycan) )
			return true;
		// Compare residue selection
		if ( (  this.m_setSelectedResidues.isEmpty() && !other.m_setSelectedResidues.isEmpty() )
		  || ( !this.m_setSelectedResidues.isEmpty() &&  other.m_setSelectedResidues.isEmpty() ) )
			return true;
		if ( !this.m_setSelectedResidues.isEmpty() && !other.m_setSelectedResidues.isEmpty() ) {
			if ( this.m_setSelectedResidues.size() != other.m_setSelectedResidues.size() )
				return true;
			TreeSet<Integer> setResPos1 = new TreeSet<>();
			for ( Residue res : this.m_setSelectedResidues )
				setResPos1.add( getResidueIndex(this.m_glycan, res) );
			TreeSet<Integer> setResPos2 = new TreeSet<>();
			for ( Residue res : other.m_setSelectedResidues )
				setResPos2.add( getResidueIndex(other.m_glycan, res) );
			if ( !setResPos1.toString().equals(setResPos2.toString()) )
				return true;
		}
		// Compare linkage selection
		if ( (  this.m_setSelectedLinkages.isEmpty() && !other.m_setSelectedLinkages.isEmpty() )
		  || ( !this.m_setSelectedLinkages.isEmpty() &&  other.m_setSelectedLinkages.isEmpty() ) )
			return true;
		if ( !this.m_setSelectedLinkages.isEmpty() && !other.m_setSelectedLinkages.isEmpty() ) {
			if ( this.m_setSelectedLinkages.size() != other.m_setSelectedLinkages.size() )
				return true;
			TreeSet<Integer> setOldResPos = new TreeSet<>();
			for ( Linkage lin : this.m_setSelectedLinkages )
				setOldResPos.add( getResidueIndex(this.m_glycan, lin.getChildResidue()) );
			TreeSet<Integer> setResPos = new TreeSet<>();
			for ( Linkage lin : other.m_setSelectedLinkages )
				setResPos.add( getResidueIndex(other.m_glycan, lin.getChildResidue()) );
			if ( !setOldResPos.toString().equals(setResPos.toString()) )
				return true;
		}
		return false;
	}

	/**
	 * Get residue index calculated with breadth-first search
	 * @param glycan Glycan containing {@code res}
	 * @param res Residue to be caluclated the index number
	 * @return The index number of the given residue in the given glycan
	 *  ({@code -1} if the given residue is not contained in the given glycan)
	 */
	private static int getResidueIndex(Glycan glycan, Residue res) {
		if ( !glycan.contains(res) )
			return -1;
		Residue parent = glycan.getRoot();
		int iIndex = 1;
		LinkedList<Residue> lChildren = new LinkedList<>();
		lChildren.add(parent);
		while ( !lChildren.isEmpty() ) {
			parent = lChildren.removeFirst();
			if ( parent.equals(res) )
				break;
			for ( int i=0; i<parent.getNoChildren(); i++ )
				lChildren.add( parent.getChildAt(i) );
			iIndex++;
		}
		return iIndex;
	}
}
