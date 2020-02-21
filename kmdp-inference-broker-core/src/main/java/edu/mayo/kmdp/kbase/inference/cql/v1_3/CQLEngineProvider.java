package edu.mayo.kmdp.kbase.inference.cql.v1_3;

import edu.mayo.kmdp.id.helper.DatatypeHelper;
import edu.mayo.kmdp.inference.v3.server.InferenceApiInternal._infer;
import edu.mayo.kmdp.kbase.inference.AbstractEvaluatorProvider;
import edu.mayo.kmdp.knowledgebase.v3.server.KnowledgeBaseApiInternal;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries;
import java.util.UUID;
import javax.inject.Named;
import org.omg.spec.api4kp._1_0.services.KPServer;
import org.springframework.beans.factory.annotation.Autowired;

@Named
public class CQLEngineProvider
    extends AbstractEvaluatorProvider {

  public CQLEngineProvider(
      @KPServer
      @Autowired
      KnowledgeBaseApiInternal kbaseManager) {
    super(kbaseManager);
  }

  @Override
  protected _infer getEvaluator(KnowledgeAsset knowledgeAsset) {
    return kbase.initKnowledgeBase(knowledgeAsset)
        .map(DatatypeHelper::deRef)
        .flatMap(kbId -> kbase.getKnowledgeBase(UUID.fromString(kbId.getTag()),kbId.getVersion()))
        .map(CQLEvaluator::new)
        .orElseThrow(UnsupportedOperationException::new);
  }

  @Override
  protected boolean supportsRepresentation(KnowledgeAsset knowledgeAsset) {
    return detectLanguage(knowledgeAsset)
        .map(lang -> lang.asEnum() == KnowledgeRepresentationLanguageSeries.HL7_CQL)
        .orElse(false);
  }
}
