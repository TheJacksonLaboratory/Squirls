package org.monarchinitiative.squirls.core.scoring;

public class RichSplicingAnnotator implements SplicingAnnotator {

    /*
     add all features within dense annotator and also
     wt_ri donor
     wt_ri acceptor
     alt_ri_donor_best_window
     alt_ri_acceptor_best_window
     sstrength_diff_donor
     sstrength_diff_acceptor
     */

    @Override
    public <T extends Annotatable> T annotate(T data) {
        // TODO: 14. 7. 2020 implement
        return data;
    }
}
