package org.monarchinitiative.threes.ingest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * These tests serve to generate small databases for testing in user's home directory.
 */
@Disabled("This test is run only to generate small database for testing of other programs")
class FakeUpDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(FakeUpDatabase.class);

    private static final Path BUILD_DIR = Paths.get(System.getProperty("user.home"));

    private static final Path HG19_JANNOVAR_DB_DIR = Paths.get(FakeUpDatabase.class.getResource("transcripts/hg19").getPath());

    private static final Path HG38_JANNOVAR_DB_DIR = Paths.get(FakeUpDatabase.class.getResource("transcripts/hg38").getPath());

    private static final Path SPLICING_IC_MATRIX_PATH = Paths.get(FakeUpDatabase.class.getResource("spliceSites.yaml").getPath());

    @Test
    void makeHg19Database() throws Exception {
        URL genomeUrl = new URL("http://hgdownload.soe.ucsc.edu/goldenPath/hg19/bigZips/chromFa.tar.gz");
        ThreesDataBuilder.buildDatabase(BUILD_DIR, genomeUrl, HG19_JANNOVAR_DB_DIR, SPLICING_IC_MATRIX_PATH, "1710_hg19");
    }

    @Test
    void makeHg38Database() throws Exception {
        URL genomeUrl = new URL("http://hgdownload.soe.ucsc.edu/goldenPath/hg38/bigZips/hg38.chromFa.tar.gz");
        ThreesDataBuilder.buildDatabase(BUILD_DIR, genomeUrl, HG38_JANNOVAR_DB_DIR, SPLICING_IC_MATRIX_PATH, "1710_hg38");
    }
}