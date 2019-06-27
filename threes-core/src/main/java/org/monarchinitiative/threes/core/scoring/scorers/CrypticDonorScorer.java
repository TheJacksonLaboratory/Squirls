package org.monarchinitiative.threes.core.scoring.scorers;

import org.monarchinitiative.threes.core.Utils;
import org.monarchinitiative.threes.core.calculators.ic.SplicingInformationContentCalculator;
import org.monarchinitiative.threes.core.model.*;
import org.monarchinitiative.threes.core.reference.allele.AlleleGenerator;

import java.util.function.Function;

/**
 *
 */
public class CrypticDonorScorer implements SplicingScorer {

    private static final int MAX_IN_INTRON = -50;

    private final SplicingInformationContentCalculator icAnnotator;

    private final AlleleGenerator generator;

    private final int donorLength;

    public CrypticDonorScorer(SplicingInformationContentCalculator icAnnotator, AlleleGenerator generator) {
        this.icAnnotator = icAnnotator;
        this.generator = generator;
        this.donorLength = icAnnotator.getSplicingParameters().getDonorLength();
    }

    @Override
    public Function<SplicingTernate, Double> scoringFunction() {
        return t -> {
            final SplicingRegion region = t.getRegion();
            final SequenceInterval sequenceInterval = t.getSequenceInterval();
            final SplicingVariant variant = t.getVariant();

            if (!(region instanceof SplicingIntron)) {
                return Double.NaN;
            }
            final SplicingIntron intron = (SplicingIntron) region;
            // this scorer is applied when variant does not overlap with canonical donor site. Here we ensure that it really
            // does not overlap the donor site
            final AlleleGenerator.Region donor = generator.makeDonorRegion(intron);
            final GenomeCoordinates varCoor = variant.getCoordinates();
            if (donor.overlapsWith(varCoor.getBegin(), varCoor.getEnd())) {
                return Double.NaN;
            }

            final int diff = donor.differenceTo(varCoor.getBegin(), varCoor.getEnd());
            if (diff < MAX_IN_INTRON) {
                // we do not score intronic variant that are too deep in intron
                return Double.NaN;
            }

            String altSnippet = sequenceInterval.getSubsequence(varCoor.getBegin() - donorLength + 1, varCoor.getBegin())
                    + variant.getAlt()
                    + sequenceInterval.getSubsequence(varCoor.getEnd(), varCoor.getEnd() + donorLength - 1);

            double wtCanonicalDonorScore = intron.getDonorScore();

            double altCrypticDonorScore = Utils.slidingWindow(altSnippet, donorLength)
                    .map(icAnnotator::getSpliceDonorScore)
                    .max(Double::compareTo)
                    .orElse(Double.NaN);
            return altCrypticDonorScore - wtCanonicalDonorScore;
        };
    }
}
