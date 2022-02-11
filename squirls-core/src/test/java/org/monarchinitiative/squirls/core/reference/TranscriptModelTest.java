/*
 * SOFTWARE LICENSE AGREEMENT
 * FOR NON-COMMERCIAL USE
 * 	This Software License Agreement (this “Agreement”) is made between you (“You,” “Your,” or “Licensee”) and The
 * 	Jackson Laboratory (“Licensor”). This Agreement grants to You a license to the Licensed Software subject to Your
 * 	acceptance of all the terms and conditions contained in this Agreement. Please read the terms and conditions
 * 	carefully. You accept the terms and conditions set forth herein by using, downloading or opening the software
 *
 * 1. LICENSE
 *
 * 1.1	Grant. Subject to the terms and conditions of this Agreement, Licensor hereby grants to Licensee a worldwide,
 * royalty-free, non-exclusive, non-transferable, non-sublicensable license to download, copy, display, and use the
 * Licensed Software for Non-Commercial purposes only. “Licensed Software” means the current version of the software.
 * “Non-Commercial” means not intended or directed toward commercial advantage or monetary compensation.
 *
 * 1.2	License Limitations. Nothing in this Agreement shall be construed to confer any rights upon Licensee except as
 * expressly granted herein. Licensee may not use or exploit the Licensed Software other than expressly permitted by this
 * Agreement. Licensee may not, nor may Licensee permit any third party, to modify, translate, reverse engineer, decompile,
 * disassemble or create derivative works based on the Licensed Software or any portion thereof. Subject to Section 1.1,
 * Licensee may distribute the Licensed Software to a third party, provided that the recipient agrees to use the Licensed
 * Software on the terms and conditions of this Agreement. Licensee acknowledges that Licensor reserves the right to offer
 * to Licensee or any third party a license for commercial use and distribution of the Licensed Software on terms and
 * conditions different than those contained in this Agreement.
 *
 * 2. OWNERSHIP OF INTELLECTUAL PROPERTY
 *
 * 2.1	Ownership Rights. Except for the limited license rights expressly granted to Licensee under this Agreement, Licensee
 * acknowledges that all right, title and interest in and to the Licensed Software and all intellectual property rights
 * therein shall remain with Licensor or its licensors, as applicable.
 *
 * 3. DISCLAIMER OF WARRANTY AND LIMITATION OF LIABILITY
 *
 * 3.1 	Disclaimer of Warranty. LICENSOR PROVIDES THE LICENSED SOFTWARE ON A NO-FEE BASIS “AS IS” WITHOUT WARRANTY OF
 * ANY KIND, EXPRESS OR IMPLIED. LICENSOR EXPRESSLY DISCLAIMS ALL WARRANTIES OR CONDITIONS OF ANY KIND, INCLUDING ANY
 * WARRANTY OF MERCHANTABILITY, TITLE, SECURITY, ACCURACY, NON-INFRINGEMENT OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * 3,2	Limitation of Liability.  LICENSEE ASSUMES FULL RESPONSIBILITY AND RISK FOR ANY LOSS RESULTING FROM LICENSEE’s
 * DOWNLOADING AND USE OF THE LICENSED SOFTWARE.  IN NO EVENT SHALL LICENSOR BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, ARISING FROM THE LICENSED SOFTWARE OR LICENSEE’S USE OF
 * THE LICENSED SOFTWARE, REGARDLESS OF WHETHER LICENSOR IS ADVISED, OR HAS OTHER REASON TO KNOW, OR IN FACT KNOWS,
 * OF THE POSSIBILITY OF THE FOREGOING.
 *
 * 3.3	Acknowledgement. Without limiting the generality of Section 3.1, Licensee acknowledges that the Licensed Software
 * is provided as an information resource only, and should not be relied on for any diagnostic or treatment purposes.
 *
 * 4. TERM AND TERMINATION
 *
 * 4.1 	Term. This Agreement commences on the date this Agreement is executed and will continue until terminated in
 * accordance with Section 4.2.
 *
 * 4.2	Termination. If Licensee breaches any provision hereunder, or otherwise engages in any unauthorized use of the
 * Licensed Software, Licensor may terminate this Agreement immediately. Licensee may terminate this Agreement at any
 * time upon written notice to Licensor. Upon termination, the license granted hereunder will terminate and Licensee will
 * immediately cease using the Licensed Software and destroy all copies of the Licensed Software in its possession.
 * Licensee will certify in writing that it has complied with the foregoing obligation.
 *
 * 5. MISCELLANEOUS
 *
 * 5.1	Future Updates. Use of the Licensed Software under this Agreement is subject to the terms and conditions contained
 * herein. New or updated software may require additional or revised terms of use. Licensor will provide notice of and
 * make available to Licensee any such revised terms.
 *
 * 5.2	Entire Agreement. This Agreement, including any Attachments hereto, constitutes the sole and entire agreement
 * between the parties as to the subject matter set forth herein and supersedes are previous license agreements,
 * understandings, or arrangements between the parties relating to such subject matter.
 *
 * 5.2 	Governing Law. This Agreement shall be construed, governed, interpreted and applied in accordance with the
 * internal laws of the State of Maine, U.S.A., without regard to conflict of laws principles. The parties agree that
 * any disputes between them may be heard only in the state or federal courts in the State of Maine, and the parties
 * hereby consent to venue and jurisdiction in those courts.
 *
 * version:6-8-18
 *
 * Daniel Danis, Peter N Robinson, 2020
 */

package org.monarchinitiative.squirls.core.reference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.svart.*;
import org.monarchinitiative.svart.assembly.AssignedMoleculeType;
import org.monarchinitiative.svart.assembly.SequenceRole;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TranscriptModelTest {

    private static final Contig CONTIG = Contig.of(1, "1", SequenceRole.ASSEMBLED_MOLECULE, "1", AssignedMoleculeType.CHROMOSOME, 500, "", "", "");

    @Test
    public void properties() {
        TranscriptModel tx = TranscriptModel.coding(CONTIG, Strand.POSITIVE, Coordinates.of(CoordinateSystem.zeroBased(), 100, 200),
                110, 190, "NM_123456.3", "GENE",
                List.of(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 100, 130),
                        GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 150, 170),
                        GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 180, 200)));

        assertThat(tx.start(), equalTo(100));
        assertThat(tx.end(), equalTo(200));
        assertThat(tx.cdsStart().get(), equalTo(110));
        assertThat(tx.cdsEnd().get(), equalTo(190));

        assertThat(tx.accessionId(), equalTo("NM_123456.3"));
        assertThat(tx.hgvsSymbol(), equalTo("GENE"));
        assertThat(tx.isCoding(), equalTo(true));
        assertThat(tx.length(), equalTo(100));

        List<GenomicRegion> exons = tx.exons();
        assertThat(exons.get(0), equalTo(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 100, 130)));
        assertThat(exons.get(1), equalTo(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 150, 170)));
        assertThat(exons.get(2), equalTo(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 180, 200)));
    }

    @ParameterizedTest
    @CsvSource({"POSITIVE, NEGATIVE",
            "NEGATIVE, POSITIVE"})
    public void withStrand(Strand strand, Strand oppositeStrand) {
        TranscriptModel instance = TranscriptModel.coding(CONTIG, strand, Coordinates.of(CoordinateSystem.zeroBased(), 100, 200),
                110, 190, "NM_123456.3", "GENE",
                List.of(GenomicRegion.of(CONTIG, strand, CoordinateSystem.zeroBased(), 100, 130),
                        GenomicRegion.of(CONTIG, strand, CoordinateSystem.zeroBased(), 150, 170),
                        GenomicRegion.of(CONTIG, strand, CoordinateSystem.zeroBased(), 180, 200)));

        assertThat(instance.withStrand(strand), sameInstance(instance));

        TranscriptModel tx = instance.withStrand(oppositeStrand);

        assertThat(tx.start(), equalTo(300));
        assertThat(tx.end(), equalTo(400));
        assertThat(tx.cdsStart().get(), equalTo(310));
        assertThat(tx.cdsEnd().get(), equalTo(390));
        assertThat(tx.length(), equalTo(100));
        assertThat(tx.strand(), equalTo(oppositeStrand));

        assertThat(tx.accessionId(), equalTo("NM_123456.3"));
        assertThat(tx.hgvsSymbol(), equalTo("GENE"));
        assertThat(tx.isCoding(), equalTo(true));

        assertThat(tx.exonCount(), equalTo(3));
        List<GenomicRegion> exons = tx.exons();
        assertThat(exons.get(0), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 300, 320)));
        assertThat(exons.get(1), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 330, 350)));
        assertThat(exons.get(2), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 370, 400)));

        assertThat(tx.intronCount(), equalTo(2));
        List<GenomicRegion> introns = tx.introns();
        assertThat(introns.get(0), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 320, 330)));
        assertThat(introns.get(1), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 350, 370)));
    }

    @ParameterizedTest
    @CsvSource({"POSITIVE, NEGATIVE",
            "NEGATIVE, POSITIVE"})
    public void withStrand_noncodingTx(Strand strand, Strand oppositeStrand) {
        TranscriptModel instance = TranscriptModel.noncoding(CONTIG, strand, Coordinates.of(CoordinateSystem.zeroBased(), 100, 200),
                "NM_123456.3", "GENE",
                List.of(GenomicRegion.of(CONTIG, strand, CoordinateSystem.zeroBased(), 100, 130),
                        GenomicRegion.of(CONTIG, strand, CoordinateSystem.zeroBased(), 150, 170),
                        GenomicRegion.of(CONTIG, strand, CoordinateSystem.zeroBased(), 180, 200)));

        assertThat(instance.withStrand(strand), sameInstance(instance));

        TranscriptModel tx = instance.withStrand(oppositeStrand);

        assertThat(tx.start(), equalTo(300));
        assertThat(tx.end(), equalTo(400));
        assertThat(tx.cdsStart().isEmpty(), is(true));
        assertThat(tx.cdsEnd().isEmpty(), is(true));
        assertThat(tx.length(), equalTo(100));
        assertThat(tx.strand(), equalTo(oppositeStrand));

        assertThat(tx.accessionId(), equalTo("NM_123456.3"));
        assertThat(tx.hgvsSymbol(), equalTo("GENE"));
        assertThat(tx.isCoding(), equalTo(false));

        assertThat(tx.exonCount(), equalTo(3));
        List<GenomicRegion> exons = tx.exons();
        assertThat(exons.get(0), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 300, 320)));
        assertThat(exons.get(1), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 330, 350)));
        assertThat(exons.get(2), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 370, 400)));

        assertThat(tx.intronCount(), equalTo(2));
        List<GenomicRegion> introns = tx.introns();
        assertThat(introns.get(0), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 320, 330)));
        assertThat(introns.get(1), equalTo(GenomicRegion.of(CONTIG, oppositeStrand, CoordinateSystem.zeroBased(), 350, 370)));
    }

    @Test
    public void withCoordinateSystem() {
        TranscriptModel instance = TranscriptModel.coding(CONTIG, Strand.POSITIVE, Coordinates.of(CoordinateSystem.zeroBased(), 100, 200),
                110, 190, "NM_123456.3", "GENE",
                List.of(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 100, 130),
                        GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 150, 170),
                        GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 180, 200)));
        assertThat(instance.withCoordinateSystem(CoordinateSystem.zeroBased()), sameInstance(instance));

        TranscriptModel tx = instance.withCoordinateSystem(CoordinateSystem.oneBased());

        assertThat(tx.start(), equalTo(101));
        assertThat(tx.end(), equalTo(200));
        assertThat(tx.cdsStart().get(), equalTo(111));
        assertThat(tx.cdsEnd().get(), equalTo(190));

        assertThat(tx.accessionId(), equalTo("NM_123456.3"));
        assertThat(tx.hgvsSymbol(), equalTo("GENE"));
        assertThat(tx.isCoding(), equalTo(true));
        assertThat(tx.length(), equalTo(100));

        List<GenomicRegion> exons = tx.exons();
        assertThat(exons.get(0), equalTo(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.oneBased(), 101, 130)));
        assertThat(exons.get(1), equalTo(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.oneBased(), 151, 170)));
        assertThat(exons.get(2), equalTo(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.oneBased(), 181, 200)));
    }

    @Test
    public void withCoordinateSystem_noncodingTx() {
        TranscriptModel instance = TranscriptModel.noncoding(CONTIG, Strand.POSITIVE, Coordinates.of(CoordinateSystem.zeroBased(), 100, 200),
                "NM_123456.3", "GENE",
                List.of(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 100, 130),
                        GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 150, 170),
                        GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 180, 200)));

        assertThat(instance.withCoordinateSystem(CoordinateSystem.zeroBased()), sameInstance(instance));

        TranscriptModel tx = instance.withCoordinateSystem(CoordinateSystem.oneBased());

        assertThat(tx.start(), equalTo(101));
        assertThat(tx.end(), equalTo(200));
        assertThat(tx.cdsStart().isEmpty(), is(true));
        assertThat(tx.cdsEnd().isEmpty(), is(true));

        assertThat(tx.accessionId(), equalTo("NM_123456.3"));
        assertThat(tx.hgvsSymbol(), equalTo("GENE"));
        assertThat(tx.isCoding(), equalTo(false));
        assertThat(tx.length(), equalTo(100));

        List<GenomicRegion> exons = tx.exons();
        assertThat(exons.get(0), equalTo(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.oneBased(), 101, 130)));
        assertThat(exons.get(1), equalTo(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.oneBased(), 151, 170)));
        assertThat(exons.get(2), equalTo(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.oneBased(), 181, 200)));
    }

    @Test
    public void introns() {
        TranscriptModel instance = TranscriptModel.coding(CONTIG, Strand.POSITIVE, Coordinates.of(CoordinateSystem.zeroBased(), 100, 200),
                110, 190, "NM_123456.3", "GENE",
                List.of(GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 100, 130),
                        GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 150, 170),
                        GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 180, 200)));

        assertThat(instance.intronCount(), equalTo(2));
        assertThat(instance.introns(), hasItems(
                GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 130, 150),
                GenomicRegion.of(CONTIG, Strand.POSITIVE, CoordinateSystem.zeroBased(), 170, 180)));

        TranscriptModel onNegative = instance.withStrand(Strand.NEGATIVE);
        assertThat(onNegative.intronCount(), equalTo(2));
        assertThat(onNegative.introns(), hasItems(
                GenomicRegion.of(CONTIG, Strand.NEGATIVE, CoordinateSystem.zeroBased(), 320, 330),
                GenomicRegion.of(CONTIG, Strand.NEGATIVE, CoordinateSystem.zeroBased(), 350, 370)));
    }
}