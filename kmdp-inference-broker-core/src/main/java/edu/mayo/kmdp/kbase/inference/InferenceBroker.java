/**
 * Copyright © 2018 Mayo Clinic (RSTKNOWLEDGEMGMT@mayo.edu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package edu.mayo.kmdp.kbase.inference;

import edu.mayo.kmdp.inference.v4.server.InferenceApiInternal;
import edu.mayo.kmdp.inference.v4.server.ModelApiInternal;
import edu.mayo.kmdp.metadata.v2.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetCatalogApiInternal;
import edu.mayo.kmdp.util.StreamUtil;
import edu.mayo.ontology.taxonomies.api4kp.knowledgeoperations.KnowledgeProcessingOperationSeries;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.inject.Named;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.id.KeyIdentifier;
import org.omg.spec.api4kp._1_0.id.Pointer;
import org.omg.spec.api4kp._1_0.id.SemanticIdentifier;
import org.omg.spec.api4kp._1_0.services.KPOperation;
import org.omg.spec.api4kp._1_0.services.KPServer;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.springframework.beans.factory.annotation.Autowired;

@KPServer
@Named
@KPOperation(KnowledgeProcessingOperationSeries.Inference_Task)
public class InferenceBroker implements InferenceApiInternal, ModelApiInternal {

  private java.util.Map<KeyIdentifier, KnowledgeAsset> knownModels = new HashMap<>();
  private KnowledgeAssetCatalogApiInternal assetCatalog;

  private Set<Function<KnowledgeAsset, Optional<_infer>>> evaluatorProviders;

  @Autowired
  public InferenceBroker(
      @KPServer KnowledgeAssetCatalogApiInternal assetCatalog,
      Set<Function<KnowledgeAsset, Optional<_infer>>> evaluatorProviders) {
    this.assetCatalog = assetCatalog;
    this.evaluatorProviders = evaluatorProviders;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Answer<Map> infer(UUID modelId, String versionTag, java.util.Map inputFeatures) {
    // Broker pattern
    return getEvaluator(modelId, versionTag)
        .flatMap(evaluator -> evaluator.infer(modelId, versionTag, inputFeatures));
  }


  private Answer<_infer> getEvaluator(UUID modelId, String versionTag) {
    KeyIdentifier vid = SemanticIdentifier.newId(modelId, versionTag).asKey();
    KnowledgeAsset asset;

    if (! knownModels.containsKey(vid)) {
      Answer<KnowledgeAsset> surr = assetCatalog
          .getKnowledgeAssetVersion(modelId, versionTag);
      if (! surr.isSuccess()) {
        return Answer.notFound();
      }
      asset = surr.get();
      knownModels.put(vid,asset);
    } else {
      asset = knownModels.get(vid);
    }

    return Answer.of(
        evaluatorProviders.stream()
            .map(provider -> provider.apply(asset))
            .flatMap(StreamUtil::trimStream)
            .findFirst());
  }





  @Override
  public Answer<KnowledgeCarrier> getModel(UUID modelId, String versionTag) {
    return Answer.unsupported();
  }


  @Override
  public Answer<List<Pointer>> listModels() {
    return assetCatalog.listKnowledgeAssets();
  }
}
