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

package org.monarchinitiative.squirls.io.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.squirls.core.reference.TranscriptModel;
import org.monarchinitiative.squirls.io.TestDataSourceConfig;
import org.monarchinitiative.variant.api.*;
import org.monarchinitiative.variant.api.impl.DefaultGenomicAssembly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(classes = TestDataSourceConfig.class)
public class TranscriptModelServiceDbTest {

    private static final Contig one = Contig.of(1, "1", SequenceRole.ASSEMBLED_MOLECULE, "1", AssignedMoleculeType.CHROMOSOME, 10_000, "CM000663.1", "NC_000001.10", "chr1");
    private static final Contig two = Contig.of(2, "2", SequenceRole.ASSEMBLED_MOLECULE, "2", AssignedMoleculeType.CHROMOSOME, 20_000, "CM000664.1", "NC_000002.11", "chr2");
    private static final GenomicAssembly genomicAssembly = DefaultGenomicAssembly.builder().name("Chewie").organismName("Wookie")
            .taxId("9999").submitter("Han Solo").date("2231-01-05").genBankAccession("GBW1").refSeqAccession("RSW1")
            .contigs(List.of(Contig.unknown(), one, two)).build();

    @Autowired
    public DataSource dataSource;

    private TranscriptModelServiceDb instance;

    @BeforeEach
    public void setUp() throws Exception {
        instance = new TranscriptModelServiceDb(dataSource, genomicAssembly);
    }

    @Test
    @Sql("transcripts_create_tables.sql")
    public void insertCodingTranscript() {
        TranscriptModel coding = TranscriptModelBuilder.builder()
                .contig(one).strand(Strand.POSITIVE).start(100).end(900)
                .cdsStart(200).cdsEnd(800)
                .accessionId("NM_000001.2")
                .hgvsSymbol("GENE")
                .setExon(0, Strand.POSITIVE, 100, 300)
                .setExon(1, Strand.POSITIVE, 400, 600)
                .setExon(2, Strand.POSITIVE, 700, 900)
                .build();
        int updated = instance.insertTranscript(coding);
        assertThat(updated, equalTo(4));
    }

    @Test
    @Sql("transcripts_create_tables.sql")
    public void insertNoncodingTranscript() {
        TranscriptModel noncoding = TranscriptModelBuilder.builder()
                .contig(one).strand(Strand.POSITIVE).start(100).end(900)
                .cdsStart(-1).cdsEnd(-1)
                .accessionId("NM_000001.2")
                .hgvsSymbol("GENE")
                .setExon(0, Strand.POSITIVE, 100, 300)
                .setExon(1, Strand.POSITIVE, 400, 600)
                .setExon(2, Strand.POSITIVE, 700, 900)
                .build();
        int updated = instance.insertTranscript(noncoding);
        assertThat(updated, equalTo(4));


    }

    @Test
    @Sql({"transcripts_create_tables.sql", "transcripts_insert.sql"})
    public void getTranscriptAccessionIds() {
        List<String> accessionIds = instance.getTranscriptAccessionIds();

        assertThat(accessionIds, hasSize(5));
        assertThat(accessionIds, hasItems("NM_000001.1", "NM_000002.1", "NM_000003.1", "NM_000004.1", "NM_000005.1"));
    }

    @Test
    @Sql({"transcripts_create_tables.sql", "transcripts_insert.sql"})
    public void fetchTranscripts() {
        GenomicRegion query = GenomicRegion.zeroBased(one, 100, 900);
        List<TranscriptModel> models = instance.getOverlapping(query);
        assertThat(models, hasSize(2));

        TranscriptModel first = models.get(0);
        assertThat(first.contigName(), equalTo("1"));
        assertThat(first.start(), equalTo(100));
        assertThat(first.end(), equalTo(900));
        assertThat(first.strand(), equalTo(Strand.POSITIVE));
        assertThat(first.coordinateSystem(), equalTo(CoordinateSystem.zeroBased()));
        assertThat(first.isCoding(), equalTo(true));
        assertThat(first.cdsStartPosition().pos(), equalTo(200));
        assertThat(first.cdsEndPosition().pos(), equalTo(800));

        assertThat(first.accessionId(), equalTo("NM_000001.1"));
        assertThat(first.hgvsSymbol(), equalTo("JOE"));

        assertThat(first.exonCount(), equalTo(3));
        assertThat(first.intronCount(), equalTo(2));
    }

    @Test
    @Sql({"transcripts_create_tables.sql", "transcripts_insert.sql"})
    public void fetchNonCodingTranscript() {
        GenomicRegion query = GenomicRegion.zeroBased(two, 1300, 1500);
        List<TranscriptModel> models = instance.getOverlapping(query);
        assertThat(models, hasSize(1));

        TranscriptModel tx = models.get(0);

        assertThat(tx.contigName(), equalTo("2"));
        assertThat(tx.start(), equalTo(1000));
        assertThat(tx.end(), equalTo(2000));
        assertThat(tx.strand(), equalTo(Strand.POSITIVE));
        assertThat(tx.coordinateSystem(), equalTo(CoordinateSystem.zeroBased()));
        assertThat(tx.isCoding(), equalTo(false));
        assertThat(tx.cdsStartPosition(), is(nullValue(Position.class)));
        assertThat(tx.cdsEndPosition(), is(nullValue(Position.class)));

        assertThat(tx.accessionId(), equalTo("NM_000005.1"));
        assertThat(tx.hgvsSymbol(), equalTo("JESSE"));
        assertThat(tx.exonCount(), equalTo(2));
    }

    @ParameterizedTest
    @CsvSource({
            "NM_000001.1,   true",
            "NM_000002.1,   true",
            "NM_000002.2,   false"})
    @Sql({"transcripts_create_tables.sql", "transcripts_insert.sql"})
    public void fetchTranscriptByAccession(String accession, boolean expected) {
        Optional<TranscriptModel> tx = instance.getByAccession(accession);
        assertThat(tx.isPresent(), equalTo(expected));
    }

}