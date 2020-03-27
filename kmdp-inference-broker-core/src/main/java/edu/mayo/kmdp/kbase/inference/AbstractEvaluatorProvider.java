package edu.mayo.kmdp.kbase.inference;

import edu.mayo.kmdp.SurrogateHelper;
import edu.mayo.kmdp.id.helper.DatatypeHelper;
import edu.mayo.kmdp.inference.v4.server.InferenceApiInternal;
import edu.mayo.kmdp.knowledgebase.v4.server.KnowledgeBaseApiInternal;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.metadata.surrogate.Representation;
import edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.omg.spec.api4kp._1_0.id.KeyIdentifier;
import org.omg.spec.api4kp._1_0.id.ResourceIdentifier;

public abstract class AbstractEvaluatorProvider
    implements Function<KnowledgeAsset, Optional<InferenceApiInternal._infer>> {

  protected KnowledgeBaseApiInternal kbase;

  protected Map<KeyIdentifier,InferenceApiInternal._infer> evaluators = new HashMap<>();

  protected AbstractEvaluatorProvider(KnowledgeBaseApiInternal kbaseManager) {
    this.kbase = kbaseManager;
  }

  @Override
  public Optional<InferenceApiInternal._infer> apply(KnowledgeAsset knowledgeAsset) {
    ResourceIdentifier vid = getAssetId(knowledgeAsset);
    return Optional.ofNullable(
        evaluators.computeIfAbsent(
            vid.asKey(),
            k -> tryGetEvaluator(knowledgeAsset)));
  }

  private InferenceApiInternal._infer tryGetEvaluator(KnowledgeAsset knowledgeAsset) {
    if (!supportsIndividualAsset(knowledgeAsset) && !supportsRepresentation(knowledgeAsset)) {
      return null;
    }
    return getEvaluator(knowledgeAsset);
  }

  protected abstract InferenceApiInternal._infer getEvaluator(KnowledgeAsset knowledgeAsset);

  protected boolean supportsRepresentation(KnowledgeAsset knowledgeAsset) {
    return knowledgeAsset == null;
  }

  protected boolean supportsIndividualAsset(KnowledgeAsset knowledgeAsset) {
    return knowledgeAsset == null;
  }

  protected ResourceIdentifier getAssetId(KnowledgeAsset asset) {
    return DatatypeHelper.toSemanticIdentifier(asset.getAssetId());
  }

  protected Optional<KnowledgeRepresentationLanguage> detectLanguage(KnowledgeAsset asset) {
    return Optional.ofNullable(SurrogateHelper.canonicalRepresentationOf(asset))
        .map(Representation::getLanguage);
  }

}
