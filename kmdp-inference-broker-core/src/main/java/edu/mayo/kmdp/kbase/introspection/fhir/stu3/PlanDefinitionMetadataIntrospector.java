package edu.mayo.kmdp.kbase.introspection.fhir.stu3;

import static edu.mayo.kmdp.SurrogateBuilder.newSurrogate;
import static edu.mayo.ontology.taxonomies.api4kp.parsinglevel.ParsingLevelSeries.Parsed_Knowedge_Expression;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.FHIR_STU3;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate;

import edu.mayo.kmdp.id.helper.DatatypeHelper;
import edu.mayo.kmdp.inference.v4.server.IntrospectionApiInternal;
import edu.mayo.kmdp.metadata.surrogate.ComputableKnowledgeArtifact;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.metadata.surrogate.Representation;
import edu.mayo.kmdp.registry.Registry;
import edu.mayo.ontology.taxonomies.api4kp.knowledgeoperations.KnowledgeProcessingOperationSeries;
import edu.mayo.ontology.taxonomies.kao.knowledgeassettype.KnowledgeAssetTypeSeries;
import java.util.UUID;
import javax.inject.Named;
import org.omg.spec.api4kp._1_0.AbstractCarrier;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.identifiers.URIIdentifier;
import org.omg.spec.api4kp._1_0.services.KPComponent;
import org.omg.spec.api4kp._1_0.services.KPOperation;
import org.omg.spec.api4kp._1_0.services.KPSupport;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.omg.spec.api4kp._1_0.services.SyntacticRepresentation;

/**
 * Introspection class for PlanDefinitions. Generates the surrogate (KnowledgeAsset) for the
 * PlanDefinition given the KnowledgeCarrier.
 */
@Named
@KPOperation(KnowledgeProcessingOperationSeries.Extract_Description_Task)
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
    URIIdentifier uri = DatatypeHelper.uri(Registry.MAYO_ASSETS_BASE_URI, uuid.toString(), "LATEST");
    KnowledgeAsset surrogate = newSurrogate(uri).get()
        .withName(knowledgeCarrier.getLabel())
        .withFormalType(KnowledgeAssetTypeSeries.Cognitive_Care_Process_Model)
        .withCarriers(new ComputableKnowledgeArtifact()
            .withArtifactId(DatatypeHelper.toURIIdentifier(knowledgeCarrier.getArtifactId()))
            .withRepresentation(new Representation()
                .withLanguage(FHIR_STU3)));

    return Answer.of(
        AbstractCarrier.ofAst(surrogate)
            .withAssetId(knowledgeCarrier.getAssetId())
            .withLevel(Parsed_Knowedge_Expression)
            // TODO improve...
            .withArtifactId(DatatypeHelper.toSemanticIdentifier(surrogate.getSurrogate().get(0).getArtifactId()))
            .withRepresentation(new SyntacticRepresentation()
                .withLanguage(Knowledge_Asset_Surrogate))
    );

  }
}
