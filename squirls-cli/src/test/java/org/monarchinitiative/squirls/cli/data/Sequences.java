package org.monarchinitiative.squirls.cli.data;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import xyz.ielis.hyperutil.reference.fasta.SequenceInterval;

class Sequences {

    /**
     * A real sequence from interval `>chr9:136,224,501-136,224,800` (1-based coordinates) on hg19. The interval contains
     * exon 3 of <em>SURF2</em>.
     */
    private static final String SURF2_EXON3 = "TGTCCAGGGATGAGTCCAAGACACAGCCACCAGTCTGAATCCTTGCTGTGAACTGTCCCT" +
            "ACAAATTTGGTCTCTCTGCTCTGTAGGCACCAGTTGTTCTGCAAACTCACCCTGCGGCAC" +
            "ATCAACAAGTGCCCAGAACACGTGCTGAGGCACACCCAGGGCCGGCGGTACCAGCGAGCT" +
            "CTGTGTAAATGTAAGTCCCAGTGGACCCCCATCAGTGCATCGCCATCTGAGTGCATGCCC" +
            "GCCTTGCCCCAGATGGAGCGTGCTTGAAGGCAGGTCGTCCTTCAGCGATCCGTGTTGATG";
    /**
     * A real sequence from interval `>chr1:21,894,401-21,895,000` (1-based coordinates) on hg19. The interval contains
     * exon 7 of <em>ALPL</em>.
     */
    private static final String ALPL_EXON7 = "GTCCCAATAGACTCGTGATTTCATCTCCCCACGCTGGACAAGTAAGGCCCAGAGATGACT" +
            "GAGGCCTGGCTCACCAGGCTGGGAAAGTGTCCACACCATCTCCAGGGACTCCAGGAGTCC" +
            "AGGTTCCAAGCCGAGGTCACTGGGGCTTCTGGGCATCTTGGAACCCTGCAGAAGTGATGG" +
            "CTCCTGTCTCTTTTAGGTGATCATGGGGGGTGGCCGGAAATACATGTACCCCAAGAATAA" +
            "AACTGATGTGGAGTATGAGAGTGACGAGAAAGCCAGGGGCACGAGGCTGGACGGCCTGGA" +
            "CCTCGTTGACACCTGGAAGAGCTTCAAACCGAGATACAAGGTAGCCTGTGCTGGGGCCAT" +
            "GTGGCTGCAGAGGTGGCCTGTGATGGGGAGAGGCTGTGTGACCCCTGCTCTGAAGTTCTG" +
            "TTGTCCTCTGTAGAAAGGGCATCGGAAATCTTTCCTCCATGGGCTCAAATGGCCTAAGAG" +
            "ATATACAAGCAGTAGATAGTCAGTGACTAATCATTTCCTTCCCTTGGAGGGGATACCAGA" +
            "GAGAGGCGGATGGTGAGGAGGAGGCTGGGCCAGGGAATAACCTGCGTGGACATTGAGTCA";

    /**
     * A real sequence from interval `>chr16:2110401-2111000` (1-based coordinates) on hg19. The interval contains
     * exon 11 of <em>TSC2</em>.
     */
    private static final String TSC2_EXON11 = "ACCGGGTGCCCAGGATTCAGTTGCTGGTCTGTCCGAGTCAGGGACTTTGCAGGCAGGCAT" +
            "GGGGGTGGGGCCCGTCTGGGTCCTGACTGTGCTGGAGCATGTAGAAACCCCTCCTGGGCG" +
            "CCCCACCTGCTGTTTCTGCGGCCCCTGATAAACGTGTGGTGGGCACTGCGCGCTCAGGCG" +
            "TGCTACTCTCGGTCCCAAGGGTGACTGGGAGGGCGTCCCACAGCAAGCAAGCAGCTCTGA" +
            "CCCTGTGTGCTGGCCGGGCTCGTGTTCCAGGCCATGGCATGTCCGAACGAGGTGGTGTCC" +
            "TATGAGATCGTCCTGTCCATCACCAGGCTCATCAAGAAGTATAGGAAGGAGCTCCAGGTG" +
            "GTGGCGTGGGACATTCTGCTGAACATCATCGAACGGCTCCTTCAGCAGCTCCAGGTGGGG" +
            "TGGGGGCAGGAGCTCCGGGGAGCACCGGGAACCCAGACAGGCAGGCTCGGCCCACTCAGA" +
            "AGATGGTACCTTGGGCCCCATCTCTGGGGGTCCCGCAGAGACTGCCAGAACCGTGTTCTC" +
            "TGGTGATTCGCAGTGGCGCTCATCCACCTTCCACCGGAGACAGGTCTGATTTTTCCAGAC";

    /**
     * A real sequence from interval `chrX:107849601-107850400` (1-based coordinates) on hg19. The interval contains
     * exon 29 of <em>COL4A5</em>.
     */
    private static final String COL4A5_EXON29 = "tacaaaaaccacaatgttgttatcatacttagtaaaattaaaaataattgtttagtatta" +
            "tctaatactcagcctataatcatatttccCTACAAGGGTAGTCTGTTCTTTAATCTCATG" +
            "CTCTCTCAGTTTTTTTTTTTTATCCTGAAACCAGTCCAAATCATCTAATTTGTTTTACTA" +
            "AACCCTGTTTCCAATCCTTCCATATGTTTTCTCCAACATACCAATTACTTTTTATTTAAT" +
            "TATCTTCTCCTCTCCCCCCATGGAAGGAAAAGTATTGTCTTGTATTTTCGGCATTAAATT" +
            "CTCTGTGGCAAACAATAAGGACAGAAAAGTCATGGGAGTTTTTGTTGTGTTTTGTCATGT" +
            "GTATGCTCAAGGGTGAACCAGGATTTGCATTACCTGGGCCACCTGGGCCACCAGGACTTC" +
            "CAGGTTTCAAAGGAGCACTTGGTCCAAAAGGTGATCGTGGTTTCCCAGGACCTCCGGGTC" +
            "CTCCAGGACGCACTGGCTTAGATGGGCTCCCTGGACCAAAAGGTATGGAGGCTGTCACTG" +
            "CATCTCAACTTGCTTTTAACTTTTATAGAAATTGACACCTTTGGGAAAGTTTATGGGTGA" +
            "TAAGCCCAATTAACTGGAAACCCTATGCTGCCATTCTGTTACTGCTACACATTTTTTTCA" +
            "ATCCTTCATTGATTATTTTTGTCTTTTATTTCATTGAATAGATAATAAAATCATggcccg" +
            "gcacggtggctcactcctgtaatcccagcactttgggaggccaaggtgggcggatcacct" +
            "gaggtctggaattcaagacc";

    /**
     * A real sequence from interval `>chr19:39075401-39075900` (1-based coordinates) on hg19. The interval contains
     * exon 102 of <em>RYR1</em>.
     */
    private static final String RYR1_EXON102 = "cacagccctgaccatttctggctgttggtccctgtctgatgccgtatctgtgagcccttt" +
            "gagggcagggcccagggctgtctcagtcgttaccatgtcttcagccctgcctatcccggg" +
            "gccttggctggtactcagtgaatgtcgaatgaatgagtgaCCAGTGTGCTCCCCTCCCTC" +
            "AGTGTTACCTGTTTCACATGTACGTGGGTGTCCGGGCTGGCGGAGGCATTGGGGACGAGA" +
            "TCGAGGACCCCGCGGGTGACGAATACGAGCTCTACAGGGTGGTCTTCGACATCACCTTCT" +
            "TCTTCTTCGTCATCGTCATCCTGTTGGCCATCATCCAGGGTCAGTGCTGGGAGTGGGCGC" +
            "TCAGGGCCCGGAGGcaggctagctccatggctaagaatgcaggcccaggatccagtcggc" +
            "ctgcattcataccccatctctacctctcgctactgtgagaccttgggcaagtcacctctc" +
            "ggggcctccgtttctccatc";

    private Sequences() {
        // no-op
    }

    /**
     * Get sequence corresponding to region `>chr9:136,224,501-136,224,800` (1-based coordinates) on hg19. This is the
     * region containing exon 3 of SURF2 gene.
     *
     * @param rd {@link ReferenceDictionary} to use
     * @return the sequence interval
     */
    static SequenceInterval getSurf2Exon3Sequence(ReferenceDictionary rd) {
        return SequenceInterval.builder()
                .sequence(SURF2_EXON3)
                .interval(new GenomeInterval(rd, Strand.FWD,
                        rd.getContigNameToID().get("chr9"), 136_224_501, 136_224_800, PositionType.ONE_BASED))
                .build();
    }

    /**
     * Get sequence corresponding to region `>chr1:21,894,401-21,895,000` (1-based coordinates) on hg19. The interval contains
     * exon 7 of <em>ALPL</em>.
     *
     * @param rd {@link ReferenceDictionary} to use
     * @return the sequence interval
     */
    static SequenceInterval getAlplExon7Sequence(ReferenceDictionary rd) {
        return SequenceInterval.builder()
                .sequence(ALPL_EXON7)
                .interval(new GenomeInterval(rd, Strand.FWD,
                        rd.getContigNameToID().get("chr1"), 21894401, 21895000, PositionType.ONE_BASED))
                .build();
    }

    /**
     * A real sequence from interval `>chr16:2110401-2111000` (1-based coordinates) on hg19. The interval contains
     * exon 11 of <em>TSC2</em>.
     *
     * @param rd {@link ReferenceDictionary} to use
     * @return the sequence interval
     */
    static SequenceInterval getTsc2Exon11Sequence(ReferenceDictionary rd) {
        return SequenceInterval.builder()
                .sequence(TSC2_EXON11)
                .interval(new GenomeInterval(rd, Strand.FWD,
                        rd.getContigNameToID().get("chr16"), 2_110_401, 2_111_000, PositionType.ONE_BASED))
                .build();
    }

    /**
     * A real sequence from interval `>chrX:107849601-107850400` (1-based coordinates) on hg19. The interval contains
     * exon 29 of <em>COL4A5</em>.
     *
     * @param rd {@link ReferenceDictionary} to use
     * @return the sequence interval
     */
    static SequenceInterval getCol4a5Exon29Sequence(ReferenceDictionary rd) {
        return SequenceInterval.builder()
                .sequence(COL4A5_EXON29)
                .interval(new GenomeInterval(rd, Strand.FWD,
                        rd.getContigNameToID().get("chr16"), 107_849_601, 107_850_400, PositionType.ONE_BASED))
                .build();
    }

    /**
     * A real sequence from interval `>chr19:39075401-39075900` (1-based coordinates) on hg19. The interval contains
     * exon 102 of <em>RYR1</em>.
     *
     * @param rd {@link ReferenceDictionary} to use
     * @return the sequence interval
     */
    static SequenceInterval getRyr1Exon102Sequence(ReferenceDictionary rd) {
        return SequenceInterval.builder()
                .sequence(RYR1_EXON102)
                .interval(new GenomeInterval(rd, Strand.FWD,
                        rd.getContigNameToID().get("chr19"), 39_075_401, 39_075_900, PositionType.ONE_BASED))
                .build();
    }

}
