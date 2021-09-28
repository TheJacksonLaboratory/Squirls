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

package org.monarchinitiative.squirls.ingest.transcripts;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import org.monarchinitiative.squirls.core.reference.TranscriptModel;
import org.monarchinitiative.svart.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Daniel Danis
 */
public class JannovarDataManager {

    private static final NumberFormat NF = NumberFormat.getInstance();

    private static final Logger LOGGER = LoggerFactory.getLogger(JannovarDataManager.class);

    private final Set<JannovarData> jannovarDataSet;

    private JannovarDataManager(Set<JannovarData> jannovarDataSet) {
        this.jannovarDataSet = Set.copyOf(jannovarDataSet);
    }

    /**
     * @param path to directory with `*.ser` files
     * @return jannovar data manager with loaded jannovar data
     */
    public static JannovarDataManager fromDirectory(Path path) {
        final File[] files = path.toFile().listFiles(name -> name.getName().endsWith(".ser"));
        if (files != null) {
            return fromPaths(Arrays.stream(files).map(File::toPath).toArray(Path[]::new));
        } else {
            LOGGER.warn("Path does not point to directory `{}`", path);
            return new JannovarDataManager(Collections.emptySet());
        }
    }

    /**
     * @param paths pointing to jannovar cache `*.ser` files
     * @return instance with loaded Jannovar data
     */
    public static JannovarDataManager fromPaths(Path... paths) {
        Set<JannovarData> data = Arrays.stream(paths)
                .map(loadJannovarData())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        return new JannovarDataManager(data);
    }

    private static Function<Path, Optional<JannovarData>> loadJannovarData() {
        return path -> {
            try {
                return Optional.ofNullable(new JannovarDataSerializer(path.toString()).load());
            } catch (SerializationException e) {
                LOGGER.warn("Error when deserializing jannovar data at `{}`: {}", path, e.getMessage());
                return Optional.empty();
            }
        };
    }

    /**
     * This function remaps Jannovar's transcript model into Squirls' model.
     */
    private static Function<de.charite.compbio.jannovar.reference.TranscriptModel, Optional<TranscriptModel>> toTranscript(GenomicAssembly assembly) {
        return tx -> {
            GenomeInterval txRegion = tx.getTXRegion();
            GenomeInterval cds = tx.getCDSRegion();
            String contigName = txRegion.getRefDict().getContigIDToName().getOrDefault(tx.getChr(), "");
            Contig contig = assembly.contigByName(contigName);
            String accession = tx.getAccession();
            try {
                if (contig.equals(Contig.unknown())) {
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn("Unknown contig {} for transcript {}", contigName, accession);
                    return Optional.empty();
                }
                Strand strand = tx.getStrand().isForward() ? Strand.POSITIVE : Strand.NEGATIVE;
                CoordinateSystem coordinateSystem = CoordinateSystem.zeroBased(); // always in Jannovar


                org.monarchinitiative.squirls.core.reference.TranscriptModel sqtx;
                int transcriptSupportLevel = remapTxSupportLevel(tx.getTranscriptSupportLevel());
                if (tx.isCoding()) {
                    sqtx = org.monarchinitiative.squirls.core.reference.TranscriptModel.coding(
                            contig, strand, coordinateSystem, txRegion.getBeginPos(), txRegion.getEndPos(),
                            cds.getBeginPos(), cds.getEndPos(),
                            accession, tx.getGeneSymbol(), transcriptSupportLevel,
                            remapExons(tx.getExonRegions(), contig, strand));
                } else {
                    sqtx = org.monarchinitiative.squirls.core.reference.TranscriptModel.noncoding(
                            contig, strand, coordinateSystem, txRegion.getBeginPos(), txRegion.getEndPos(),
                            accession, tx.getGeneSymbol(), transcriptSupportLevel,
                            remapExons(tx.getExonRegions(), contig, strand));
                }

                return Optional.of(sqtx);
            } catch (Exception e) {
                LOGGER.warn("Error remapping transcript {} at `{}:{}-{}({}-{})`: {}", accession,
                        tx.getChr(), NF.format(txRegion.getBeginPos()), NF.format(txRegion.getEndPos()),
                        NF.format(cds.getBeginPos()), NF.format(cds.getEndPos()), e.getMessage());
                return Optional.empty();
            }
        };
    }

    private static int remapTxSupportLevel(int transcriptSupportLevel) {
        /* From Jannovar's docs:
        In the case that the transcript support level is not available, 6 is used as a substitute for transcripts
        that are marked as primary in UCSC, 7 for the longest transcript (in absence of both level and UCSC primary
        marking), and 8 for transcripts that fall neither into pseudo-level 6 and 7. A value of -1 is used for N/A.
        */
        if (transcriptSupportLevel < -1 || transcriptSupportLevel > 8)
            throw new IllegalArgumentException("Illegal TSL=" + transcriptSupportLevel);

        if (transcriptSupportLevel == 6) {
            // Annotated as canonical transcript by UCSC (used in absence of TSL).
            return 1;
        } else if (transcriptSupportLevel == 7) {
            // Longest transcript of a gene (used in absence of any TSL annotation and UCSC annotation of this transcript).
            return 1;
        } else if (transcriptSupportLevel == 8) {
            // Lowest available priority (used in absence of any TSL and UCSC annotation of this transcript).
            return 5;
        } else {
            return transcriptSupportLevel;
        }
    }

    private static List<GenomicRegion> remapExons(List<GenomeInterval> exons, Contig contig, Strand strand) {
        List<GenomicRegion> remapped = new ArrayList<>(exons.size());

        for (GenomeInterval exon : exons) {
            remapped.add(GenomicRegion.of(contig, strand, CoordinateSystem.zeroBased(), Position.of(exon.getBeginPos()), Position.of(exon.getEndPos())));
        }

        return remapped;
    }

    /**
     * Remap all transcripts present in the jannovar databases into Squirls' domain, using provided assembly.
     */
    public Collection<TranscriptModel> getAllTranscriptModels(GenomicAssembly assembly) {
        return jannovarDataSet.parallelStream()
                .map(jd -> jd.getTmByAccession().values())
                .flatMap(Collection::stream)
                .map(toTranscript(assembly))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}
