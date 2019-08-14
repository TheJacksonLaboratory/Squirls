package org.monarchinitiative.threes.core.model;

/**
 * POJO for grouping together a nucleotide sequence and coordinate system.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public class SequenceInterval {

    private final GenomeCoordinates coordinates;

    private final String sequence;

    private SequenceInterval(GenomeCoordinates coordinates, String sequence) {
        this.coordinates = coordinates;
        this.sequence = sequence;

        // sanity checks
        if (coordinates.getLength() != sequence.length()) {
            throw new IllegalArgumentException(String.format("Sequence with length %d for coordinates %d", sequence.length(), coordinates.getLength()));
        }
    }

    /**
     * Convert nucleotide sequence to reverse complement.
     *
     * @param sequence of nucleotides, only {a,c,g,t,n,A,C,G,T,N} permitted
     * @return reverse complement of given <code>sequence</code>
     * @throws IllegalArgumentException if there is an unpermitted character present
     */
    public static String reverseComplement(String sequence) {
        char[] oldSeq = sequence.toCharArray();
        char[] newSeq = new char[oldSeq.length];
        int idx = oldSeq.length - 1;
        for (int i = 0; i < oldSeq.length; i++) {
            if (oldSeq[i] == 'A') {
                newSeq[idx - i] = 'T';
            } else if (oldSeq[i] == 'a') {
                newSeq[idx - i] = 't';
            } else if (oldSeq[i] == 'T') {
                newSeq[idx - i] = 'A';
            } else if (oldSeq[i] == 't') {
                newSeq[idx - i] = 'a';
            } else if (oldSeq[i] == 'C') {
                newSeq[idx - i] = 'G';
            } else if (oldSeq[i] == 'c') {
                newSeq[idx - i] = 'g';
            } else if (oldSeq[i] == 'G') {
                newSeq[idx - i] = 'C';
            } else if (oldSeq[i] == 'g') {
                newSeq[idx - i] = 'c';
            } else if (oldSeq[i] == 'N') {
                newSeq[idx - i] = 'N';
            } else if (oldSeq[i] == 'n') {
                newSeq[idx - i] = 'n';
            } else
                throw new IllegalArgumentException(String.format("Illegal nucleotide %s in sequence %s",
                        oldSeq[i], sequence));
        }
        return new String(newSeq);
    }

    public static SequenceInterval of(GenomeCoordinates coordinates, String sequence) {
        return new SequenceInterval(coordinates, sequence);
    }

    public GenomeCoordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return String.format("%s:[%d-%d]%s %s",
                coordinates.getContig(), coordinates.getBegin(), coordinates.getEnd(), coordinates.getStrand() ? "+" : "-", sequence);
    }

    public String getSequence() {
        return sequence;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SequenceInterval)) return false;

        SequenceInterval that = (SequenceInterval) o;

        if (coordinates != null ? !coordinates.equals(that.coordinates) : that.coordinates != null) return false;
        return sequence != null ? sequence.equals(that.sequence) : that.sequence == null;

    }

    @Override
    public int hashCode() {
        int result = coordinates != null ? coordinates.hashCode() : 0;
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        return result;
    }

    /**
     * Essentially, substring from the sequence is returned.
     *
     * @param begin 0-based exclusive local begin coordinate
     * @param end   0-based inclusive local begin coordinate
     * @return String with local sequence
     * @throws IndexOutOfBoundsException if <code>begin < 0</code>, <code>begin > end</code>, and <code>end > seq.length</code>
     */
    public String getLocalSequence(int begin, int end) {
        return sequence.substring(begin, end);
    }


    /**
     * Get sequence present between given begin and end coordinates.
     * <p>
     * If this sequence interval represents regino chr1:10-20, then {@code begin}=10 and {@code end}=11 will return the \
     * first base.
     *
     * @param begin 0-based exclusive begin coordinate
     * @param end   0-based inclusive end coordinate
     * @return String subsequence
     * @throws IndexOutOfBoundsException if the {@code begin} and {@code end} coordinates are not represented
     */
    public String getSubsequence(int begin, int end) {
        return getLocalSequence(begin - coordinates.getBegin(), end - coordinates.getBegin());
    }

}
