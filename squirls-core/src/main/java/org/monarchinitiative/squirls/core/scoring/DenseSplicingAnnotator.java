package org.monarchinitiative.squirls.core.scoring;

import com.google.common.collect.ComparisonChain;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import org.monarchinitiative.squirls.core.Metadata;
import org.monarchinitiative.squirls.core.data.ic.SplicingPwmData;
import org.monarchinitiative.squirls.core.model.SplicingTranscript;
import org.monarchinitiative.squirls.core.reference.SplicingLocationData;
import org.monarchinitiative.squirls.core.reference.allele.AlleleGenerator;
import org.monarchinitiative.squirls.core.reference.transcript.NaiveSplicingTranscriptLocator;
import org.monarchinitiative.squirls.core.reference.transcript.SplicingTranscriptLocator;
import org.monarchinitiative.squirls.core.scoring.calculators.*;
import org.monarchinitiative.squirls.core.scoring.calculators.conservation.BigWigAccessor;
import org.monarchinitiative.squirls.core.scoring.calculators.ic.SplicingInformationContentCalculator;
import xyz.ielis.hyperutil.reference.fasta.SequenceInterval;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This annotator calculates the following splicing features for each {@link Annotatable}:
 * <ul>
 *     <li><code>canonical_donor</code></li>
 *     <li><code>cryptic_donor</code></li>
 *     <li><code>canonical_acceptor</code></li>
 *     <li><code>cryptic_acceptor</code></li>
 *     <li><code>cryptic_acceptor</code></li>
 *     <li><code>hexamer</code></li>
 *     <li><code>septamer</code></li>
 *     <li><code>phylop</code></li>
 *     <li><code>donor_offset</code></li>
 *     <li><code>acceptor_offset</code></li>
 * </ul>
 */
public class DenseSplicingAnnotator implements SplicingAnnotator {

    private final SplicingTranscriptLocator transcriptLocator;

    private final CanonicalDonor canonicalDonorFeatureCalculator;

    private final CanonicalAcceptor canonicalAcceptorScorer;

    private final CrypticDonor crypticDonorScorer;

    private final CrypticAcceptor crypticAcceptorScorer;

    private final Hexamer hexamer;

    private final Septamer septamer;

    private final BigWig phyloPFeatureCalculator;

    /**
     * Create the annotator.
     *
     * @param splicingPwmData splice site data
     * @param hexamerMap      map of hexamer scores from the ESRSeq method
     * @param septamerMap     map of septamer scores from the SMS method
     * @param bigWigAccessor  the accessor that allows to get PhyloP scores from a bigwig file
     */
    public DenseSplicingAnnotator(SplicingPwmData splicingPwmData,
                                  Map<String, Double> hexamerMap,
                                  Map<String, Double> septamerMap,
                                  BigWigAccessor bigWigAccessor) {
        SplicingInformationContentCalculator calculator = new SplicingInformationContentCalculator(splicingPwmData);
        AlleleGenerator generator = new AlleleGenerator(calculator.getSplicingParameters());

        transcriptLocator = new NaiveSplicingTranscriptLocator(calculator.getSplicingParameters());

        canonicalDonorFeatureCalculator = new CanonicalDonor(calculator, generator);
        canonicalAcceptorScorer = new CanonicalAcceptor(calculator, generator);
        crypticDonorScorer = new CrypticDonor(calculator, generator);
        crypticAcceptorScorer = new CrypticAcceptor(calculator, generator);

        hexamer = new Hexamer(hexamerMap);
        septamer = new Septamer(septamerMap);

        phyloPFeatureCalculator = new BigWig(bigWigAccessor);
    }

    static int getOffset(Stream<GenomePosition> positions, GenomeInterval variant) {
        final Comparator<GenomePosition> findClosestExonIntronBorder = (left, right) -> ComparisonChain.start()
                .compare(Math.abs(left.differenceTo(variant)), Math.abs(right.differenceTo(variant)))
                .result();

        final GenomePosition closestPosition = positions.min(findClosestExonIntronBorder).orElseThrow();
        final int diff = closestPosition.differenceTo(variant);
        if (diff > 0) {
            // variant is upstream from the border position
            return -diff;
        } else if (diff < 0) {
            // variant is downstream from the border position
            return -diff + 1;
        } else {
            /*
            Due to representation of exon|Intron / intron|Exon boundary as a GenomePosition that represents position of
            the capital E/I character above, we need to distinguish when variant interval denotes
              - a deletion of the boundary, or
              - SNP at +1 position.

            The code below handles these situations.
            */
            if (variant.contains(closestPosition) && variant.length() > 1) {
                // deletion of the boundary
                return 0;
            } else {
                // SNP at +1 position
                return 1;
            }
        }
    }

    @Override
    public <T extends Annotatable> T annotate(T data) {
        final GenomeVariant variant = data.getVariant();
        final SplicingTranscript transcript = data.getTranscript();
        final SequenceInterval sequence = data.getSequence();

        final GenomeVariant variantOnStrand = variant.withStrand(transcript.getStrand());
        final SplicingLocationData locationData = transcriptLocator.locate(variant, transcript);

        final Optional<GenomePosition> donorBoundary = locationData.getDonorBoundary();
        final Optional<GenomePosition> acceptorBoundary = locationData.getAcceptorBoundary();


        if (donorBoundary.isPresent()) {
            // `canonical_donor` feature
            final double canonicalDonor = canonicalDonorFeatureCalculator.score(donorBoundary.get(), variantOnStrand, sequence);
            data.putFeature("canonical_donor", canonicalDonor);

            // `cryptic_donor` feature
            final double crypticDonor = crypticDonorScorer.score(donorBoundary.get(), variantOnStrand, sequence);
            data.putFeature("cryptic_donor", crypticDonor);
        } else {
            data.putFeature("canonical_donor", 0.);
            data.putFeature("cryptic_donor", 0.);
        }

        if (acceptorBoundary.isPresent()) {
            // `canonical_acceptor` feature
            final double canonicalAcceptor = canonicalAcceptorScorer.score(acceptorBoundary.get(), variantOnStrand, sequence);
            data.putFeature("canonical_acceptor", canonicalAcceptor);

            // `cryptic_acceptor` feature
            final double crypticAcceptor = crypticAcceptorScorer.score(acceptorBoundary.get(), variantOnStrand, sequence);
            data.putFeature("cryptic_acceptor", crypticAcceptor);
        } else {
            data.putFeature("canonical_acceptor", 0.);
            data.putFeature("cryptic_acceptor", 0.);
        }

        final double hexamerScore = hexamer.score(null, variant, sequence);
        data.putFeature("hexamer", hexamerScore);
        final double septamerScore = septamer.score(null, variant, sequence);
        data.putFeature("septamer", septamerScore);

        final double phylopScore = phyloPFeatureCalculator.score(null, variant, sequence);
        data.putFeature("phylop", phylopScore);

        final int donorOffset = getOffset(transcript.getExons().stream()
                        .map(e -> e.getInterval().getGenomeEndPos()),
                variantOnStrand.getGenomeInterval());
        data.putFeature("donor_offset", (double) donorOffset);

        final int acceptorOffset = getOffset(transcript.getExons().stream()
                        .map(e -> e.getInterval().getGenomeBeginPos()),
                variantOnStrand.getGenomeInterval());
        data.putFeature("acceptor_offset", (double) acceptorOffset);

        final Metadata.Builder metadataBuilder = Metadata.builder()
                .meanPhyloPScore(phylopScore);

        locationData.getDonorBoundary().ifPresent(boundary -> metadataBuilder.putDonorCoordinate(transcript.getAccessionId(), boundary));
        locationData.getAcceptorBoundary().ifPresent(boundary -> metadataBuilder.putAcceptorCoordinate(transcript.getAccessionId(), boundary));

        data.setMetadata(metadataBuilder.build());

        return data;
    }
}
