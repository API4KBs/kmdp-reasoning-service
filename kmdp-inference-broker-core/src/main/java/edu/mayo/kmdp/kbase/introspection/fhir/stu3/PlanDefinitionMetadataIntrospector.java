package edu.mayo.kmdp.kbase.introspection.fhir.stu3;

import static org.omg.spec.api4kp._20200801.AbstractCarrier.rep;
import static org.omg.spec.api4kp._20200801.surrogate.SurrogateBuilder.newSurrogate;
import static org.omg.spec.api4kp._20200801.taxonomy.knowledgeassettype.KnowledgeAssetTypeSeries.Cognitive_Care_Process_Model;
import static org.omg.spec.api4kp._20200801.taxonomy.knowledgeoperation.KnowledgeProcessingOperationSeries.Description_Task;
import static org.omg.spec.api4kp._20200801.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.FHIR_STU3;
import static org.omg.spec.api4kp._20200801.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate_2_0;

import java.util.UUID;
import javax.inject.Named;
import org.omg.spec.api4kp._20200801.AbstractCarrier;
import org.omg.spec.api4kp._20200801.Answer;
import org.omg.spec.api4kp._20200801.api.inference.v4.server.IntrospectionApiInternal;
import org.omg.spec.api4kp._20200801.id.ResourceIdentifier;
import org.omg.spec.api4kp._20200801.services.KPComponent;
import org.omg.spec.api4kp._20200801.services.KPOperation;
import org.omg.spec.api4kp._20200801.services.KPSupport;
import org.omg.spec.api4kp._20200801.services.KnowledgeCarrier;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeArtifact;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeAsset;
import org.omg.spec.api4kp._20200801.surrogate.SurrogateBuilder;
import org.omg.spec.api4kp._20200801.taxonomy.parsinglevel.ParsingLevelSeries;

/**
 * Introspection class for PlanDefinitions. Generates the surrogate (KnowledgeAsset) for the
 * PlanDefinition given the KnowledgeCarrier.
 */
@Named
@KPOperation(Description_Task)
@KPSupport(FHIR_STU3)
@KPComponent
public class PlanDefinitionMetadataIntrospector implements IntrospectionApiInternal._introspect {

  // TBD: Ask Davide -- where does this UUID come from?? For now, make one up
  public static final UUID FHIR_STU3_EXTRACTOR
      = UUID.fromString("ae1302ac-fafa-46e5-8ceb-b59b6959aa9d");

  /**
   * Provide the KnowledgeAsset surrogate for the KnowledgeCarrier.
   *
   * @param uuid UUID of the asset
   * @param knowledgeCarrier The carrier of the artifact
   * @param s ??? ignore for now
   * @return A KnowledgeCarrier that is a KnowledgeAssetSurrogate
   */
  @Override
  public Answer<KnowledgeCarrier> introspect(UUID uuid, KnowledgeCarrier knowledgeCarrier,
      String s) {
    // ignore 's' for now
    ResourceIdentifier uri = SurrogateBuilder.assetId(uuid.toString(), "LATEST");
    KnowledgeAsset surrogate = newSurrogate(uri).get()
        .withName(knowledgeCarrier.getLabel())
        .withFormalType(Cognitive_Care_Process_Model)
        .withCarriers(new KnowledgeArtifact()
            .withArtifactId(knowledgeCarrier.getArtifactId())
            .withRepresentation(rep(FHIR_STU3)));

    return Answer.of(
        AbstractCarrier.ofAst(surrogate)
            .withAssetId(knowledgeCarrier.getAssetId())
            .withLevel(ParsingLevelSeries.Abstract_Knowledge_Expression)
            // TODO improve...
            .withArtifactId(surrogate.getSurrogate().get(0).getArtifactId())
            .withRepresentation(rep(Knowledge_Asset_Surrogate_2_0))
    );

  }
}
