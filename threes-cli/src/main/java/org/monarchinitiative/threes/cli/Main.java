package org.monarchinitiative.threes.cli;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;
import org.monarchinitiative.threes.cli.cmd.Command;
import org.monarchinitiative.threes.cli.cmd.GenerateConfigCommand;
import org.monarchinitiative.threes.cli.cmd.annotate_csv.AnnotateCsvCommand;
import org.monarchinitiative.threes.cli.cmd.annotate_pos.AnnotatePosCommand;
import org.monarchinitiative.threes.cli.cmd.annotate_vcf.AnnotateVcfCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@SpringBootApplication
public class Main {

    private static final String EPILOG =
            "            ____\n" +
                    "      _,.-'`_ o `;__,\n" +
                    "       _.-'` '---'  '";

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        /*
         1. define CLI interface
         */
        ArgumentParser parser = ArgumentParsers.newFor("java -jar threes-cli.jar").build();
        parser.description("Code for splicing calculations:");
        parser.defaultHelp(true);
        parser.epilog(EPILOG);

        /*
        This CLI has 2 command groups: {generate-config, run}
        - a) - generate-config - generate a config file for commands from the `run` group
        - b) - run - commands that do useful things, e.g. annotate a single variant, a VCF file, etc.
         */
        Subparsers mainSubparsers = parser.addSubparsers();

        // a) - `generate-config`
        GenerateConfigCommand.setupSubparsers(mainSubparsers);

        // b) - `run` command group
        final Subparser runParser = mainSubparsers.addParser("run")
                .setDefault("cmd", "run")
                .help("run a command");
        final Subparsers runCommandGroupSubparsers = runParser.addSubparsers();
        AnnotatePosCommand.setupSubparsers(runCommandGroupSubparsers);
        AnnotateVcfCommand.setupSubparsers(runCommandGroupSubparsers);
        AnnotateCsvCommand.setupSubparsers(runCommandGroupSubparsers);

        // - we require 3S properties to be provided
        runParser.addArgument("-c", "--config")
                .required(true)
                .metavar("/path/to/application.yml")
                .help("path to configuration file generated by `generate-config` command");

        /*
         2. Parse the command line arguments
         */
        Namespace namespace = null;
        List<String> unknownArgsList = new ArrayList<>();
        try {
            namespace = parser.parseKnownArgs(args, unknownArgsList);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        /*
         3. run the command
         */
        String cmdName = namespace.get("cmd");
        if (cmdName.equals("generate-config")) {
            final GenerateConfigCommand cmd = new GenerateConfigCommand();
            cmd.run(namespace);
        } else {
            String configPath = namespace.getString("config");
            LOGGER.info("Reading 3S configuration from `{}`", configPath);
            if (!unknownArgsList.isEmpty()) {
                LOGGER.info("Passing the following args to Spring: '{}'",
                        String.join(", ", unknownArgsList));
            }

            // bootstrap Spring application context
            ConfigurableApplicationContext appContext = new SpringApplicationBuilder(Main.class)
                    .properties(String.format("spring.config.location=%s", configPath))
                    .run(args);

            // get the selected command and run it
            Command command;

            switch (cmdName) {
                case "annotate-pos":
                    command = appContext.getBean(AnnotatePosCommand.class);
                    break;
                case "annotate-vcf":
                    command = appContext.getBean(AnnotateVcfCommand.class);
                    break;
                case "annotate-csv":
                    command = appContext.getBean(AnnotateCsvCommand.class);
                    break;
                default:
                    LOGGER.warn("Unknown command '{}'", cmdName);
                    System.exit(1);
                    return; // unreachable, but still required
            }
            command.run(namespace);
        }
        LOGGER.info("Done!");
    }

}

