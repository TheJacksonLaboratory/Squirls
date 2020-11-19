package org.monarchinitiative.squirls.core.model;

import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * POJO for transcript data used within 3S codebase.
 */
public class SplicingTranscript {

    public static final String EXON_REGION_CODE = "ex";
    public static final String INTRON_REGION_CODE = "ir";

    private static final SplicingTranscript DEFAULT = SplicingTranscript.builder().build();

    private final GenomeInterval txRegionCoordinates;

    private final String accessionId;

    private final List<SplicingExon> exons;

    private final List<SplicingIntron> introns;

    private SplicingTranscript(Builder builder) {
        txRegionCoordinates = builder.coordinates;
        exons = List.copyOf(builder.exons);
        introns = List.copyOf(builder.introns);
        accessionId = builder.accessionId;
        if (builder.check)
            check();
    }

    public static SplicingTranscript getDefaultInstance() {
        return DEFAULT;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void check() {
        if (!exons.stream().allMatch(e -> e.getInterval().getStrand().equals(txRegionCoordinates.getStrand()))) {
            throw new IllegalArgumentException("All exons are not on transcript's strand");
        }

        if (!introns.stream().allMatch(i -> i.getInterval().getStrand().equals(txRegionCoordinates.getStrand()))) {
            throw new IllegalArgumentException("All introns are not on transcript's strand");
        }

        for (int i = 0; i < exons.size(); i++) {
            GenomeInterval current = exons.get(i).getInterval();
            if (current.getGenomeBeginPos().isGeq(current.getGenomeEndPos())) {
                throw new IllegalArgumentException("Invalid exon " + i + ": begin is not upstream from end");
            }

            if (i > 0) {
                GenomeInterval previous = exons.get(i - 1).getInterval();
                if (previous.getGenomeEndPos().isGeq(current.getGenomeBeginPos())) {
                    throw new IllegalArgumentException("Inconsistent exon " + (i - 1) + ',' + i + " order: " + previous + ", " + current);
                }
            }
        }

        for (int i = 0; i < introns.size(); i++) {
            GenomeInterval current = introns.get(i).getInterval();
            if (current.getGenomeBeginPos().isGeq(current.getGenomeEndPos())) {
                throw new IllegalArgumentException("Invalid intron " + i + ": begin is not upstream from end");
            }

            if (i > 0) {
                GenomeInterval previous = introns.get(i - 1).getInterval();
                if (previous.getGenomeEndPos().isGeq(current.getGenomeBeginPos())) {
                    throw new IllegalArgumentException("Inconsistent exon " + (i - 1) + ',' + i + " order: " + previous + ", " + current);
                }
            }
        }
    }

    public GenomeInterval getTxRegionCoordinates() {
        return txRegionCoordinates;
    }

    public List<SplicingExon> getExons() {
        return exons;
    }

    public List<SplicingIntron> getIntrons() {
        return introns;
    }

    public String getAccessionId() {
        return accessionId;
    }

    public Strand getStrand() {
        return txRegionCoordinates.getStrand();
    }

    public int getChr() {
        return txRegionCoordinates.getChr();
    }

    public String getChrName() {
        return txRegionCoordinates.getRefDict().getContigIDToName().get(txRegionCoordinates.getChr());
    }

    public int getTxBegin() {
        return txRegionCoordinates.getBeginPos();
    }


    public int getTxEnd() {
        return txRegionCoordinates.getEndPos();
    }

    public int getTxLength() {
        return txRegionCoordinates.length();
    }

    @Override
    public String toString() {
        return "SplicingTranscript{" +
                "txRegionCoordinates=" + txRegionCoordinates +
                ", accessionId='" + accessionId + '\'' +
                ", exons=" + exons +
                ", introns=" + introns +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SplicingTranscript that = (SplicingTranscript) o;
        return Objects.equals(txRegionCoordinates, that.txRegionCoordinates) &&
                Objects.equals(accessionId, that.accessionId) &&
                Objects.equals(exons, that.exons) &&
                Objects.equals(introns, that.introns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(txRegionCoordinates, accessionId, exons, introns);
    }

    public static final class Builder {

        private final List<SplicingExon> exons = new ArrayList<>();
        private final List<SplicingIntron> introns = new ArrayList<>();
        private GenomeInterval coordinates;
        private String accessionId = "";
        private boolean check = false;

        private Builder() {
            // private no-op
        }

        public Builder setAccessionId(String accessionId) {
            this.accessionId = accessionId;
            return this;
        }

        public Builder setCoordinates(GenomeInterval coordinates) {
            this.coordinates = coordinates;
            return this;
        }

        public Builder addExon(SplicingExon exon) {
            this.exons.add(exon);
            return this;
        }

        public Builder addAllExons(Collection<SplicingExon> exons) {
            this.exons.addAll(exons);
            return this;
        }

        public Builder addIntron(SplicingIntron intron) {
            this.introns.add(intron);
            return this;
        }

        public Builder addAllIntrons(Collection<SplicingIntron> introns) {
            this.introns.addAll(introns);
            return this;
        }

        public Builder check(boolean check) {
            this.check = check;
            return this;
        }

        public SplicingTranscript build() {
            return new SplicingTranscript(this);
        }
    }
}
