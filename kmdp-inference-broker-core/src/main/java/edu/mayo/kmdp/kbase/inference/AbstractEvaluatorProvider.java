package edu.mayo.kmdp.kbase.inference;

import static org.omg.spec.api4kp._20200801.surrogate.SurrogateHelper.canonicalRepresentationOf;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.omg.spec.api4kp._20200801.api.inference.v4.server.InferenceApiInternal;
import org.omg.spec.api4kp._20200801.api.knowledgebase.v4.server.KnowledgeBaseApiInternal;
import org.omg.spec.api4kp._20200801.id.KeyIdentifier;
import org.omg.spec.api4kp._20200801.id.ResourceIdentifier;
import org.omg.spec.api4kp._20200801.services.SyntacticRepresentation;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeAsset;
import org.omg.spec.api4kp._20200801.taxonomy.krlanguage.KnowledgeRepresentationLanguage;

public abstract class AbstractEvaluatorProvider
    implements Function<KnowledgeAsset, Optional<InferenceApiInternal._infer>> {

  protected KnowledgeBaseApiInternal kbase;

  protected Map<KeyIdentifier,InferenceApiInternal._infer> evaluators = new HashMap<>();

  protected AbstractEvaluatorProvider(KnowledgeBaseApiInternal kbaseManager) {
    this.kbase = kbaseManager;
  }

  @Override
  public Optional<InferenceApiInternal._infer> apply(KnowledgeAsset knowledgeAsset) {
    ResourceIdentifier vid = knowledgeAsset.getAssetId();
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

  protected Optional<KnowledgeRepresentationLanguage> detectLanguage(KnowledgeAsset asset) {
    return Optional.ofNullable(canonicalRepresentationOf(asset))
        .map(SyntacticRepresentation::getLanguage);
  }

}
