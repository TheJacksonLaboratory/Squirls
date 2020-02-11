package org.monarchinitiative.threes.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.data.ReferenceDictionaryBuilder;
import org.monarchinitiative.threes.core.calculators.ic.SplicingInformationContentCalculator;
import org.monarchinitiative.threes.core.calculators.sms.SMSCalculator;
import org.monarchinitiative.threes.core.data.ic.InputStreamBasedPositionalWeightMatrixParser;
import org.monarchinitiative.threes.core.data.ic.SplicingPositionalWeightMatrixParser;
import org.monarchinitiative.threes.core.data.ic.SplicingPwmData;
import org.monarchinitiative.threes.core.data.sms.FileSMSParser;
import org.monarchinitiative.threes.core.data.sms.SMSParser;
import org.monarchinitiative.threes.core.model.SplicingParameters;
import org.monarchinitiative.threes.core.reference.allele.AlleleGenerator;
import org.monarchinitiative.threes.core.scoring.dense.DenseSplicingAnnotator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
@Configuration
public class TestDataSourceConfig {

    /**
     * @return in-memory database for testing
     */
    @Bean
    public DataSource dataSource() {
        String jdbcUrl = "jdbc:h2:mem:threes";
        final HikariConfig config = new HikariConfig();
        config.setUsername("sa");
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(jdbcUrl);

        return new HikariDataSource(config);
    }

    @Bean
    public SplicingParameters splicingParameters() {
        return PojosForTesting.makeSplicingParameters();
    }

    @Bean
    public ReferenceDictionary referenceDictionary() {
        ReferenceDictionaryBuilder builder = new ReferenceDictionaryBuilder();

        builder.putContigID("chr1", 1);
        builder.putContigName(1, "chr1");
        builder.putContigLength(1, 10_000);

        builder.putContigID("chr2", 2);
        builder.putContigName(2, "chr2");
        builder.putContigLength(2, 100_000);

        builder.putContigID("chr3", 3);
        builder.putContigName(3, "chr3");
        builder.putContigLength(3, 200_000);
        return builder.build();
    }

    @Bean
    public SplicingInformationContentCalculator splicingInformationContentCalculator(SplicingPwmData splicingPwmData) {
        return new SplicingInformationContentCalculator(splicingPwmData);
    }

    @Bean
    public SplicingPwmData splicingPwmData(SplicingPositionalWeightMatrixParser splicingPositionalWeightMatrixParser) {
        return splicingPositionalWeightMatrixParser.getSplicingPwmData();
    }

    @Bean
    public SplicingPositionalWeightMatrixParser splicingPositionalWeightMatrixParser() throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(TestDataSourceConfig.class.getResource("data/ic/spliceSites.yaml").getPath()))) {
            return new InputStreamBasedPositionalWeightMatrixParser(inputStream);
        }
    }

    @Bean
    public AlleleGenerator alleleGenerator(SplicingParameters splicingParameters) {
        return new AlleleGenerator(splicingParameters);
    }

    @Bean
    public DenseSplicingAnnotator denseSplicingEvaluator(SplicingPwmData splicingPwmData) {
        return new DenseSplicingAnnotator(splicingPwmData);
    }

    @Bean
    public SMSCalculator smsCalculator(SMSParser smsParser) {
        return new SMSCalculator(smsParser.getSeptamerMap());
    }

    @Bean
    public SMSParser smsParser() throws IOException {
        Path path = Paths.get(TestDataSourceConfig.class.getResource("data/sms/septamer-scores.tsv").getPath());
        return new FileSMSParser(path);
    }
}