package edu.mayo.kmdp.kbase.inference.dmn.v1_1;

import edu.mayo.kmdp.inference.v4.server.InferenceApiInternal._infer;
import edu.mayo.kmdp.kbase.inference.AbstractEvaluatorProvider;
import edu.mayo.kmdp.kbase.inference.dmn.SemanticDMNEvaluator;
import edu.mayo.kmdp.knowledgebase.v4.server.KnowledgeBaseApiInternal;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries;
import javax.inject.Named;
import org.omg.spec.api4kp._1_0.services.KPServer;
import org.springframework.beans.factory.annotation.Autowired;

@Named
public class DMNEngineProvider
    extends AbstractEvaluatorProvider {

  public DMNEngineProvider(
      @KPServer
      @Autowired
      KnowledgeBaseApiInternal kbaseManager) {
    super(kbaseManager);
  }

  @Override
  protected _infer getEvaluator(KnowledgeAsset knowledgeAsset) {
    return kbase.initKnowledgeBase(knowledgeAsset)
        .flatMap(kbId -> kbase.getKnowledgeBase(kbId.getUuid(),kbId.getVersionTag()))
        .map(SemanticDMNEvaluator::new)
        .orElseThrow(UnsupportedOperationException::new);
  }

  protected boolean supportsRepresentation(KnowledgeAsset knowledgeAsset) {
    return detectLanguage(knowledgeAsset)
        .map(lang -> lang.asEnum() == KnowledgeRepresentationLanguageSeries.DMN_1_1)
        .orElse(false);
  }
}
