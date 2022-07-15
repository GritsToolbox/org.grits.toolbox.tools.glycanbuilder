package org.grits.toolbox.tools.glycanbuilder.core.structure.mass;

import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.Linkage;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.MassUtils;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.eurocarbdb.application.glycanbuilder.ResidueType;

/**
 * A class for calculating glycan mass. There are the same methods in Glycan class
 * but it can not consider a monosaccharide mass having unknown molecular composition.
 * @author Masaaki Matsubara (matsubara@uga.edu)
 *
 */
public class GlycanMassCalculator {

	private Glycan glycan;
	private MassOptions mass_options;

	public GlycanMassCalculator(Glycan glycan) {
		this.glycan = glycan;
		this.mass_options = glycan.getMassOptions();
	}

	/**
	 * Compute the mass-to-charge ratio given the current mass settings.
	 */
	public double computeMZ() {
		double mass = computeMass();
		return glycan.getMassOptions().ION_CLOUD.and(glycan.getMassOptions().NEUTRAL_EXCHANGES).computeMZ(mass);
	}

	/**
	 * Compute the mass of the molecule given the current mass settings.
	 */
	public double computeMass() {
		if (glycan.hasRepetition() && (glycan.areAllRepetitionsConstant(glycan.getRoot()) == false
				|| glycan.areAllRepetitionsConstant(glycan.getBracket()) == false)) {
			return -1.;
		}
		if ( containsUnknownMass() )
			return -1.;
		return computeMass(glycan.getRoot(), 1) + computeMass(glycan.getBracket(), 1);
	}

	private boolean containsUnknownMass() {
		return containsUnknownMass(glycan.getRoot()) ||
				( glycan.getBracket() != null && containsUnknownMass(glycan.getBracket()) );
	}

	private boolean containsUnknownMass(Residue node) {
		if (node == null || node.getTypeName().equals("Sugar"))
			return true;

		if ( !node.isRepetition() && !node.isReducingEnd() && node.getType().getComposition().equals("?") )
			return true;

		for (Linkage l : node.getChildrenLinkages()) {
			if ( containsUnknownMass(l.getChildResidue()) )
				return true;
		}

		return false;
	}

	private double computeMass(Residue node, double multipler) {
		if (node == null || node.getTypeName().equals("Sugar"))
			return 0.;

		if (node.isStartRepetition()) {
			Residue end = node.getEndRepitionResidue();
			if (end.getMaxRepetitions() == end.getMinRepetitions()) {
				multipler = end.getMaxRepetitions();
			} else {
				// will throw something here!
			}
		} else if (node.isEndRepetition()) {
			multipler = 1;
		}

		ResidueType type = node.getType();
		int no_bonds = node.getNoBonds();

		// repetition notes
		// we assume a signal bond between the end of one block and the start of another

		double mass = 0.;

		if (!node.isRepetition() || checkCompositionResidue(node)) {
			mass = type.getMass();
		}

		// modify for alditol
		if (node.isReducingEnd() && node.getType().makesAlditol())
			mass += 2 * MassUtils.hydrogen.getMass();

		if (node.isBracket() && !glycan.isComposition()) {
			int no_linked_labiles = Math.min(glycan.countLabilePositions(), glycan.countDetachedLabiles());
			mass -= (no_bonds - no_linked_labiles) * substitutionMass();
		} else if (node.isCleavage() && !node.isRingFragment()) {
			// cleavages have no derivatization
			if (node.isReducingEnd() && !node.hasChildren()) {
				// fix for composition
				// mass += MassOptions.H2O;
				mass += substitutionMass();
			}
		} else {
			if (node.isRepetition() == false) {
				// add groups
				if (isDropped(type))
					mass -= (type.getMass() - MassUtils.water.getMass() - substitutionMass());
				else
					mass += ((noSubstitutions(type) - no_bonds) * substitutionMass());
			}
		}

		mass = mass * multipler;

		if (node.getParent() != null) {
			if (node.getParent().isStartRepetition()) {
				Residue startRepResidue = node.getParent();
				int noBonds = startRepResidue.getLinkageAt(0).getNoBonds(); // B
				int repetitions = startRepResidue.getEndRepitionResidue().getMaxRepetitions(); // n

				mass += (repetitions - 1) * (noBonds - 1) * MassUtils.water.getMass();
				mass += (repetitions - 2) * (noBonds - 1) * substitutionMass();
			}
		}

		// add children
		for (Linkage l : node.getChildrenLinkages()) {
			if (l.getChildResidue().isRepetition()) {
				if (l.getChildResidue().isEndRepetition()) {
					// Correct for the number of bonds the final residue actually has
					mass += (no_bonds - (l.getChildResidue().getNoBonds() + no_bonds - 2)) * substitutionMass();
				}
			}
//			if (isDehydrationBond(l)) {
				mass -= MassUtils.water.getMass() * l.getNoBonds() * multipler; // remove a water molecule for each bond
//			}

//			if (!l.getChildResidue().getType().getComposition().contains("O")) {
//				if (l.getParentLinkageType().equals(LinkageType.H_AT_OH)
//						|| l.getParentLinkageType().equals(LinkageType.H_LOSE)) {
//					mass += MassUtils.water.getMass() * l.getNoBonds() * multipler;
//					mass -= MassUtils.hydrogen.getMass() * l.getNoBonds() * multipler * 2;
//				}
//			}
			mass += computeMass(l.getChildResidue(), multipler);
		}

		return mass;
	}

	private boolean checkCompositionResidue(Residue node) {
		if (glycan.isComposition()) {
			// if(node.isReducingEnd()) return false;
			if (node.isBracket())
				return false;
			return true;
		}

		return false;
	}

	private double substitutionMass() {
		if (mass_options.DERIVATIZATION.equals(MassOptions.PERMETHYLATED))
			return (MassUtils.methyl.getMass() - MassUtils.hydrogen.getMass());
		if (mass_options.DERIVATIZATION.equals(MassOptions.PERDMETHYLATED))
			return (MassUtils.dmethyl.getMass() - MassUtils.hydrogen.getMass());
		if (mass_options.DERIVATIZATION.equals(MassOptions.PERACETYLATED))
			return (MassUtils.acetyl.getMass() - MassUtils.hydrogen.getMass());
		if (mass_options.DERIVATIZATION.equals(MassOptions.PERDACETYLATED))
			return (MassUtils.dacetyl.getMass() - MassUtils.hydrogen.getMass());
		if (mass_options.DERIVATIZATION.equals(MassOptions.HEAVYPERMETHYLATION))
			return (MassUtils.heavyMethyl.getMass() - MassUtils.hydrogen.getMass());
		return 0.;
	}

	private boolean isDropped(ResidueType type) {
		if (type.isDroppedWithMethylation() && (mass_options.DERIVATIZATION.equals(MassOptions.PERMETHYLATED)
				|| mass_options.DERIVATIZATION.equals(MassOptions.PERDMETHYLATED)
				|| mass_options.DERIVATIZATION.equals(MassOptions.HEAVYPERMETHYLATION)))
			return true;
		if (type.isDroppedWithAcetylation() && (mass_options.DERIVATIZATION.equals(MassOptions.PERACETYLATED)
				|| mass_options.DERIVATIZATION.equals(MassOptions.PERACETYLATED)))
			return true;
		return false;
	}

	private int noSubstitutions(ResidueType type) {
		if (mass_options.DERIVATIZATION.equals(MassOptions.PERMETHYLATED)
				|| mass_options.DERIVATIZATION.equals(MassOptions.PERDMETHYLATED)
				|| mass_options.DERIVATIZATION.equals(MassOptions.HEAVYPERMETHYLATION))
			return type.getNoMethyls();
		if (mass_options.DERIVATIZATION.equals(MassOptions.PERACETYLATED)
				|| mass_options.DERIVATIZATION.equals(MassOptions.PERDACETYLATED))
			return type.getNoAcetyls();
		return 0;
	}

}
