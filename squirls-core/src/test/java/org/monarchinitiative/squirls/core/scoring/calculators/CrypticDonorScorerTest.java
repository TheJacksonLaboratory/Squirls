package org.monarchinitiative.squirls.core.scoring.calculators;

import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

class CrypticDonorScorerTest extends CalculatorTestBase {

    private CrypticDonor scorer;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        scorer = new CrypticDonor(calculator, generator);
    }

    @Test
    void snpInDonor() {
        GenomeVariant variant = new GenomeVariant(new GenomePosition(rd, Strand.FWD, 1, 1201), "t", "g");
        final GenomePosition anchor = st.getExons().get(0).getInterval().getGenomeEndPos();

        final double score = scorer.score(anchor, variant, sequenceInterval);

        assertThat(score, is(closeTo(4.6317, EPSILON)));
    }

    @Test
    void snpUpstreamFromDonor() {
        GenomeVariant variant = new GenomeVariant(new GenomePosition(rd, Strand.FWD, 1, 1196), "C", "T");
        final GenomePosition anchor = st.getExons().get(0).getInterval().getGenomeEndPos();

        final double score = scorer.score(anchor, variant, sequenceInterval);

        assertThat(score, is(closeTo(0.3526, EPSILON)));
    }
}