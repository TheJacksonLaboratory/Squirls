package org.monarchinitiative.threes.autoconfigure;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.threes.core.scoring.SplicingAnnotator;
import org.monarchinitiative.threes.core.scoring.dense.DenseSplicingAnnotator;
import org.monarchinitiative.threes.core.scoring.sparse.SparseSplicingAnnotator;
import org.springframework.beans.factory.BeanCreationException;
import xyz.ielis.hyperutil.reference.fasta.GenomeSequenceAccessor;
import xyz.ielis.hyperutil.reference.fasta.SingleChromosomeGenomeSequenceAccessor;
import xyz.ielis.hyperutil.reference.fasta.SingleFastaGenomeSequenceAccessor;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ThreesAutoConfigurationTest extends AbstractAutoConfigurationTest {

    @Test
    void testAllPropertiesSupplied() {
        load(ThreesAutoConfiguration.class, "threes.data-directory=" + TEST_DATA,
                "threes.genome-assembly=hg19",
                "threes.data-version=1710");
        Path threesDataDirectory = context.getBean("threesDataDirectory", Path.class);
        assertThat(threesDataDirectory.getFileName(), equalTo(Paths.get("data")));

        String threesGenomeAssembly = context.getBean("threesGenomeAssembly", String.class);
        assertThat(threesGenomeAssembly, is("hg19"));

        String threesDataVersion = context.getBean("threesDataVersion", String.class);
        assertThat(threesDataVersion, is("1710"));

        // default values
        ThreesProperties properties = context.getBean(ThreesProperties.class);
        assertThat(properties.getMaxDistanceExonUpstream(), is(50));
        assertThat(properties.getMaxDistanceExonDownstream(), is(50));
        assertThat(properties.getGenomeSequenceAccessorType(), is("simple"));
        assertThat(properties.getSplicingAnnotatorType(), is("sparse"));

        GenomeSequenceAccessor accessor = context.getBean("genomeSequenceAccessor", GenomeSequenceAccessor.class);
        assertThat(accessor, is(instanceOf(SingleFastaGenomeSequenceAccessor.class)));

        SplicingAnnotator splicingAnnotator = context.getBean("splicingAnnotator", SplicingAnnotator.class);
        assertThat(splicingAnnotator, is(instanceOf(SparseSplicingAnnotator.class)));
    }

    @Test
    void testOptionalProperties() {
        load(ThreesAutoConfiguration.class, "threes.data-directory=" + TEST_DATA,
                "threes.genome-assembly=hg19",
                "threes.data-version=1710",
                "threes.max-distance-exon-upstream=100",
                "threes.max-distance-exon-downstream=200",
                "threes.genome-sequence-accessor-type=chromosome",
                "threes.splicing-annotator-type=dense");

        ThreesProperties properties = context.getBean(ThreesProperties.class);
        assertThat(properties.getMaxDistanceExonUpstream(), is(100));
        assertThat(properties.getMaxDistanceExonDownstream(), is(200));
        assertThat(properties.getGenomeSequenceAccessorType(), is("chromosome"));
        assertThat(properties.getSplicingAnnotatorType(), is("dense"));

        GenomeSequenceAccessor accessor = context.getBean("genomeSequenceAccessor", GenomeSequenceAccessor.class);
        assertThat(accessor, is(instanceOf(SingleChromosomeGenomeSequenceAccessor.class)));

        SplicingAnnotator splicingAnnotator = context.getBean("splicingAnnotator", SplicingAnnotator.class);
        assertThat(splicingAnnotator, is(instanceOf(DenseSplicingAnnotator.class)));
    }

    @Test
    void testMissingDataDirectory() {
        Throwable thrown = assertThrows(BeanCreationException.class, () -> load(ThreesAutoConfiguration.class,
                "threes.genome-assembly=hg19",
                "threes.data-version=1710"));
        assertThat(thrown.getMessage(), containsString("Path to 3S data directory (`--threes.data-directory`) is not specified"));
    }

    @Test
    void testProvidedPathDoesNotPointToDirectory() {
        Throwable thrown = assertThrows(BeanCreationException.class, () -> load(ThreesAutoConfiguration.class,
                "threes.data-directory=" + TEST_DATA + "/rocket",
                "threes.genome-assembly=hg19",
                "threes.data-version=1710"));
        assertThat(thrown.getMessage(), containsString("Path to 3S data directory 'src/test/resources/data/rocket' does not point to real directory"));

    }

    @Test
    void testMissingGenomeAssembly() {
        Throwable thrown = assertThrows(BeanCreationException.class, () -> load(ThreesAutoConfiguration.class,
                "threes.data-directory=" + TEST_DATA,
//                "threes.genome-assembly=hg19",
                "threes.data-version=1710"));
        assertThat(thrown.getMessage(), containsString("Genome assembly (`--threes.genome-assembly`) is not specified"));
    }

    @Test
    void testMissingDataVersion() {
        Throwable thrown = assertThrows(BeanCreationException.class, () -> load(ThreesAutoConfiguration.class,
                "threes.data-directory=" + TEST_DATA,
                "threes.genome-assembly=hg19"
//                "threes.data-version=1710",
        ));
        assertThat(thrown.getMessage(), containsString("Data version (`--threes.data-version`) is not specified"));
    }


}