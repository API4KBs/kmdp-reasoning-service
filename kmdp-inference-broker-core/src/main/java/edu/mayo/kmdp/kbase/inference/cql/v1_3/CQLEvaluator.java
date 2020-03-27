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
package edu.mayo.kmdp.kbase.inference.cql.v1_3;

import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.FHIR_STU3;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.HL7_CQL;

import edu.mayo.kmdp.SurrogateHelper;
import edu.mayo.kmdp.id.helper.DatatypeHelper;
import edu.mayo.kmdp.inference.v4.server.InferenceApiInternal._infer;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.ontology.taxonomies.api4kp.knowledgeoperations.KnowledgeProcessingOperationSeries;
import edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.cqframework.cql.elm.execution.AccessModifier;
import org.cqframework.cql.elm.execution.FunctionDef;
import org.cqframework.cql.elm.execution.Library;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.id.KeyIdentifier;
import org.omg.spec.api4kp._1_0.services.BinaryCarrier;
import org.omg.spec.api4kp._1_0.services.KPOperation;
import org.omg.spec.api4kp._1_0.services.KPSupport;
import org.omg.spec.api4kp._1_0.services.KnowledgeBase;
import org.omg.spec.api4kp._1_0.services.SyntacticRepresentation;
import org.opencds.cqf.cql.execution.Context;


@KPOperation(KnowledgeProcessingOperationSeries.Inference_Task)
@KPSupport(HL7_CQL)
public class CQLEvaluator
    implements _infer {

  private Map<KeyIdentifier, byte[]> artifactCache = new HashMap<>();

  private CQL2ELMTranslatorHelper translator;

  private KnowledgeRepresentationLanguage modelLanguage;

  public CQLEvaluator(KnowledgeBase knowledgeBase) {
    BinaryCarrier carrier = (BinaryCarrier) knowledgeBase.getManifestation();

    artifactCache.put(carrier.getAssetId().asKey(),
        carrier.getEncodedExpression());
    this.translator = new CQL2ELMTranslatorHelper();
    modelLanguage = detectInformationModel(carrier.getRepresentation())
        .orElse(FHIR_STU3);
  }

  @Override
  public Answer<Map> infer(UUID modelId, String versionTag, Map features) {
    Map<String, Object> out = getCarrier(modelId, versionTag)
        .flatMap(translator::cqlToExecutableLibrary)
        .map(lib -> internalEvaluate(lib, initExecutableKB(lib, features).orElse(null)))
        .orElse(new HashMap<>());

    return Answer.of(out);
  }

  private Optional<KnowledgeRepresentationLanguage> detectInformationModel(
      SyntacticRepresentation asset) {
    // TODO Actually inspect..
    return Optional.of(FHIR_STU3);
  }

  private KnowledgeRepresentationLanguage detectRepresentationLanguage(KnowledgeAsset asset) {
    return SurrogateHelper.canonicalRepresentationOf(asset).getLanguage();
  }

  private Map<String, Object> internalEvaluate(Library lib, Context ctx) {
    Map<String, Object> out = new HashMap<>();
    if (ctx != null) {
      lib.getStatements().getDef().stream()
          .filter(def -> !(def instanceof FunctionDef))
          .forEach(
              def -> {
                Object val = def.evaluate(ctx);
                if (val != null && def.getAccessLevel() == AccessModifier.PUBLIC) {
                  out.put(def.getName(), val);
                }
              });
    }
    return out;
  }


  private Optional<Context> initExecutableKB(Library elm, Map features) {
    Context ctx = new Context(elm);
    ctx.setExpressionCaching(true);

    ctx.registerLibraryLoader(new DefaultFHIRHelperLibraryLoader(translator));

    switch (modelLanguage.asEnum()) {
      case FHIR_STU3:
        ctx.registerDataProvider("http://hl7.org/fhir", new InMemoryFhir3DataProvider(features));
        break;
     default:
        throw new UnsupportedOperationException(
            "Unable to use " + modelLanguage + " in combination with CQL");
    }
    return Optional.of(ctx);
  }


  private Optional<BinaryCarrier> getCarrier(UUID modelId, String versionTag) {
    return Optional.ofNullable(new BinaryCarrier()
        .withEncodedExpression(artifactCache.getOrDefault(modelId.toString(), null)));
  }

}
