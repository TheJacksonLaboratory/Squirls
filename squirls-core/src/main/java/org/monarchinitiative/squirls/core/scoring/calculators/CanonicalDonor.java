package org.monarchinitiative.squirls.core.scoring.calculators;

import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import org.monarchinitiative.squirls.core.reference.allele.AlleleGenerator;
import org.monarchinitiative.squirls.core.scoring.calculators.ic.SplicingInformationContentCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ielis.hyperutil.reference.fasta.SequenceInterval;

public class CanonicalDonor extends BaseFeatureCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanonicalDonor.class);

    public CanonicalDonor(SplicingInformationContentCalculator annotator, AlleleGenerator generator) {
        super(annotator, generator);
    }

    @Override
    public double score(GenomePosition anchor, GenomeVariant variant, SequenceInterval sequence) {
        final GenomeInterval donorRegion = generator.makeDonorInterval(anchor);

        if (!donorRegion.overlapsWith(variant.getGenomeInterval())) {
            // shortcut - if variant does not affect the donor site
            return 0;
        }

        final String donorSiteSnippet = generator.getDonorSiteSnippet(anchor, sequence);
        final String donorSiteWithAltAllele = generator.getDonorSiteWithAltAllele(anchor, variant, sequence);

        if (donorSiteSnippet == null || donorSiteWithAltAllele == null) {
            LOGGER.debug("Unable to create wt/alt snippets for variant `{}` using interval `{}`", variant, sequence.getInterval());
            return Double.NaN;
        }

        final double refScore = calculator.getSpliceDonorScore(donorSiteSnippet);
        final double altScore = calculator.getSpliceDonorScore(donorSiteWithAltAllele);

        return refScore - altScore;
    }
}
