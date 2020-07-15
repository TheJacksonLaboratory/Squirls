package org.monarchinitiative.squirls.core.scoring.calculators;

import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import org.monarchinitiative.squirls.core.reference.allele.AlleleGenerator;
import org.monarchinitiative.squirls.core.scoring.calculators.ic.SplicingInformationContentCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ielis.hyperutil.reference.fasta.SequenceInterval;

/**
 * This class calculates <code>wt_ri_donor</code> feature - individual information of the <em>wt/ref</em> allele of the
 * donor site.
 */
public class WtRiDonor extends BaseFeatureCalculator {

    private static final Logger LOGGER = LoggerFactory.getLogger(WtRiDonor.class);

    public WtRiDonor(SplicingInformationContentCalculator calculator, AlleleGenerator generator) {
        super(calculator, generator);
    }

    @Override
    public double score(GenomePosition anchor, GenomeVariant variant, SequenceInterval sequence) {
        final String donorSiteSnippet = generator.getDonorSiteSnippet(anchor, sequence);

        if (donorSiteSnippet == null) {
            LOGGER.debug("Unable to create wt/alt snippets for variant `{}` using sequence `{}`", variant, sequence.getInterval());
            return Double.NaN;
        }

        return calculator.getSpliceDonorScore(donorSiteSnippet);
    }
}
