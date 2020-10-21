package org.monarchinitiative.squirls.cli.visualization.panel;

import org.monarchinitiative.squirls.cli.visualization.*;
import org.monarchinitiative.squirls.core.SplicingPredictionData;
import org.monarchinitiative.squirls.core.data.ic.SplicingPwmData;
import org.monarchinitiative.vmvt.core.VmvtGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

/**
 * This graphics generator makes graphics for the splice variant. The graphics generation is delegated to the
 * appropriate method of {@link AbstractGraphicsGenerator}.
 */
public class PanelGraphicsGenerator extends AbstractGraphicsGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PanelGraphicsGenerator.class);

    private final TemplateEngine templateEngine;

    public PanelGraphicsGenerator(VmvtGenerator vmvtGenerator,
                                  SplicingPwmData splicingPwmData,
                                  VisualizationContextSelector contextSelector) {
        super(vmvtGenerator, splicingPwmData, contextSelector);

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(true);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public String generateGraphics(VisualizedVariant data) {
        /*
        To generate graphics, we first determine graphics type (visualization context).
        Then, we select the relevant parts of the splicing data.
        Finally, we process the data using appropriate template and return HTML
         */

        final SplicingPredictionData prediction = data.getPrimaryPrediction();
        final Map<String, Double> featureMap = prediction.getFeatureMap();

        // 0 - select what visualization context and template name
        final String templateName;
        final String graphics;
        final VisualizationContext visualizationContext;
        try {
            visualizationContext = contextSelector.selectContext(featureMap);
            switch (visualizationContext) {
                case CANONICAL_DONOR:
                    templateName = "donor";
                    graphics = makeCanonicalDonorContextGraphics(prediction);
                    break;
                case CRYPTIC_DONOR:
                    templateName = "donor";
                    graphics = makeCrypticDonorContextGraphics(prediction);
                    break;
                case CANONICAL_ACCEPTOR:
                    templateName = "acceptor";
                    graphics = makeCanonicalAcceptorContextGraphics(prediction);
                    break;
                case CRYPTIC_ACCEPTOR:
                    templateName = "acceptor";
                    graphics = makeCrypticAcceptorContextGraphics(prediction);
                    break;
                default:
                    LOGGER.warn("Unable to generate graphics for context {}", visualizationContext.getTitle());
                    return EMPTY_SVG_IMAGE;
            }
        } catch (MissingFeatureException e) {
            LOGGER.warn("Missing feature "); // TODO: 15. 10. 2020 add info
            return EMPTY_SVG_IMAGE;
        }

        // 1 - prepare context for the template
        final Context context = new Context();
        context.setVariable("features", featureMap);
        context.setVariable("variantData", data);
        context.setVariable("annotations", data.getAnnotations());
        context.setVariable("graphics", graphics);

        return templateEngine.process(templateName, context);
    }
}