package org.monarchinitiative.squirls.core.scoring;

import org.monarchinitiative.squirls.core.data.ic.SplicingPwmData;
import org.monarchinitiative.squirls.core.reference.allele.AlleleGenerator;
import org.monarchinitiative.squirls.core.reference.transcript.NaiveSplicingTranscriptLocator;
import org.monarchinitiative.squirls.core.reference.transcript.SplicingTranscriptLocator;
import org.monarchinitiative.squirls.core.scoring.calculators.ExclusionZoneFeatureCalculator;
import org.monarchinitiative.squirls.core.scoring.calculators.FeatureCalculator;
import org.monarchinitiative.squirls.core.scoring.calculators.PptIsTruncated;
import org.monarchinitiative.squirls.core.scoring.calculators.PyrimidineToPurineAtMinusThree;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This {@link SplicingAnnotator} implementation is used for evaluation of <em>AGEZ</em> features based on
 * <a href="https://pubmed.ncbi.nlm.nih.gov/32126153/">this paper</a>.
 * <p>
 * In addition to
 */
public class AGEZSplicingAnnotator extends AbstractSplicingAnnotator {

    public AGEZSplicingAnnotator(SplicingPwmData splicingPwmData,
                                 Map<String, Double> hexamerMap,
                                 Map<String, Double> septamerMap) {
        super(new NaiveSplicingTranscriptLocator(splicingPwmData.getParameters()),
                Stream.of(
                        makeCalculatorMap(splicingPwmData).entrySet(), // agez calculators
                        RichSplicingAnnotator.makeCalculatorMap(splicingPwmData).entrySet(), // rich
                        DenseSplicingAnnotator.makeDenseCalculatorMap(splicingPwmData, hexamerMap, septamerMap).entrySet()) // dense
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    static Map<String, FeatureCalculator> makeCalculatorMap(SplicingPwmData splicingPwmData) {
        SplicingTranscriptLocator locator = new NaiveSplicingTranscriptLocator(splicingPwmData.getParameters());
        AlleleGenerator generator = new AlleleGenerator(splicingPwmData.getParameters());
        // TODO - consider externalizing the AGEZ region definitions
        return Map.of(
                "creates_ag_in_agez", ExclusionZoneFeatureCalculator.makeAgCalculator(locator, ExclusionZoneFeatureCalculator.AGEZ_BEGIN, ExclusionZoneFeatureCalculator.AGEZ_END),
                "ppt_is_truncated", new PptIsTruncated(locator, ExclusionZoneFeatureCalculator.AGEZ_BEGIN, ExclusionZoneFeatureCalculator.AGEZ_END),
                "yag_at_acceptor_minus_three", new PyrimidineToPurineAtMinusThree(locator, generator),
                "creates_yag_in_agez", ExclusionZoneFeatureCalculator.makeYagCalculator(locator, ExclusionZoneFeatureCalculator.AGEZ_BEGIN, ExclusionZoneFeatureCalculator.AGEZ_END)
        );
    }
}