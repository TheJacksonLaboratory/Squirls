package org.monarchinitiative.squirls.cli.cmd.analyze_vcf;

import de.charite.compbio.jannovar.annotation.VariantAnnotator;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.squirls.cli.TestDataSourceConfig;
import org.monarchinitiative.squirls.cli.cmd.analyze_vcf.visualization.simple.SimpleSplicingVariantGraphicsGenerator;
import org.monarchinitiative.squirls.cli.data.VariantsForTesting;
import org.monarchinitiative.squirls.core.data.ic.SplicingPwmData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(classes = TestDataSourceConfig.class)
class HtmlResultWriterTest {

    // TODO: 3. 6. 2020 remove
    private static final Path OUTPATH = Paths.get("target/SQUIRLS.html");
    @Autowired
    public SplicingPwmData splicingPwmData;
    @Autowired
    private JannovarData jannovarData;
    private SimpleSplicingVariantGraphicsGenerator graphicsGenerator;

    private VariantAnnotator annotator;

    private HtmlResultWriter writer;

    @BeforeEach
    void setUp() {
        annotator = new VariantAnnotator(jannovarData.getRefDict(), jannovarData.getChromosomes(), new AnnotationBuilderOptions());
        writer = new HtmlResultWriter();
        graphicsGenerator = new SimpleSplicingVariantGraphicsGenerator(splicingPwmData);
    }

    /**
     * This test does not currently test anything. It writes HTML file to {@link #OUTPATH}.
     *
     * @throws Exception if anything fails
     */
    @Test
    void writeResults() throws Exception {
        final ReferenceDictionary rd = jannovarData.getRefDict();
        Set<SplicingVariantAlleleEvaluation> variantData = Set.of(
                // TODO: 7. 7. 2020 consider removing
//                VariantsForTesting.SURF2DonorExon3Plus4Evaluation(rd, annotator),
//                VariantsForTesting.SURF2Exon3AcceptorMinus2Evaluation(rd, annotator),
                // donor
                VariantsForTesting.BRCA2DonorExon15plus2QUID(rd, annotator),
                VariantsForTesting.ALPLDonorExon7Minus2(rd, annotator),
                VariantsForTesting.HBBcodingExon1UpstreamCrypticInCanonical(rd, annotator),
                VariantsForTesting.HBBcodingExon1UpstreamCryptic(rd, annotator),
                // acceptor
                VariantsForTesting.VWFAcceptorExon26minus2QUID(rd, annotator),
                VariantsForTesting.TSC2AcceptorExon11Minus3(rd, annotator),
                VariantsForTesting.COL4A5AcceptorExon11Minus8(rd, annotator),
                VariantsForTesting.RYR1codingExon102crypticAcceptor(rd, annotator),
                // SRE
                VariantsForTesting.NF1codingExon9coding_SRE(rd, annotator)
        );

        AnalysisResults results = AnalysisResults.builder()
                .addAllSampleNames(List.of("Sample_192"))
                .analysisStats(AnalysisStats.builder()
                        .allVariants(100)
                        .alleleCount(120)
                        .annotatedAlleleCount(115)
                        .pathogenicAlleleCount(2)
                        .build())
                .settingsData(SettingsData.builder()
                        .inputPath("path/to/Sample_192.vcf")
                        .threshold(VariantsForTesting.FAKE_THRESHOLD)
                        .transcriptDb("refseq")
                        .build())
                .variantData(variantData)
                .build();
        try (OutputStream os = Files.newOutputStream(OUTPATH)) {
            writer.writeResults(os, results);
        }
    }

    /**
     * This test does not currently test anything. It writes HTML file to {@link #OUTPATH}.
     *
     * @throws Exception bla
     */
    @Test
    @Disabled
    // TODO - remove or make a real test
    void writeResultsRealGraphics() throws Exception {
        final ReferenceDictionary rd = jannovarData.getRefDict();
        final Set<SplicingVariantAlleleEvaluation> variantData = Set.of(
                // donor
                VariantsForTesting.BRCA2DonorExon15plus2QUID(rd, annotator),
                VariantsForTesting.ALPLDonorExon7Minus2(rd, annotator),
                VariantsForTesting.HBBcodingExon1UpstreamCrypticInCanonical(rd, annotator),
                VariantsForTesting.HBBcodingExon1UpstreamCryptic(rd, annotator),
                // acceptor
                VariantsForTesting.VWFAcceptorExon26minus2QUID(rd, annotator),
                VariantsForTesting.TSC2AcceptorExon11Minus3(rd, annotator),
                VariantsForTesting.COL4A5AcceptorExon11Minus8(rd, annotator),
                VariantsForTesting.RYR1codingExon102crypticAcceptor(rd, annotator),
                // SRE
                VariantsForTesting.NF1codingExon9coding_SRE(rd, annotator)
        );

        final Set<SplicingVariantAlleleEvaluation> variantGraphicsData = new HashSet<>();
        for (SplicingVariantAlleleEvaluation vd : variantData) {
            vd.setGraphics(graphicsGenerator.generateGraphics(vd));
            variantGraphicsData.add(vd);
        }

        AnalysisResults results = AnalysisResults.builder()
                .addAllSampleNames(List.of("Sample_192"))
                .analysisStats(AnalysisStats.builder()
                        .allVariants(100)
                        .alleleCount(120)
                        .annotatedAlleleCount(115)
                        .pathogenicAlleleCount(2)
                        .build())
                .settingsData(SettingsData.builder()
                        .inputPath("path/to/Sample_192.vcf")
                        .threshold(VariantsForTesting.FAKE_THRESHOLD)
                        .transcriptDb("refseq")
                        .build())
                .variantData(variantGraphicsData)
                .build();
        try (OutputStream os = Files.newOutputStream(OUTPATH)) {
            writer.writeResults(os, results);
        }
    }
}