package edu.mayo.kmdp.kbase.introspection.cql;

import static edu.mayo.kmdp.SurrogateBuilder.newSurrogate;
import static edu.mayo.ontology.taxonomies.api4kp.parsinglevel.ParsingLevelSeries.Parsed_Knowedge_Expression;
import static edu.mayo.ontology.taxonomies.kao.languagerole.KnowledgeRepresentationLanguageRoleSeries.Schema_Language;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.FHIR_STU3;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.HL7_CQL;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate;

import edu.mayo.kmdp.knowledgebase.v3.server.IntrospectionApiInternal;
import edu.mayo.kmdp.metadata.surrogate.ComputableKnowledgeArtifact;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.metadata.surrogate.Representation;
import edu.mayo.kmdp.metadata.surrogate.SubLanguage;
import edu.mayo.ontology.taxonomies.api4kp.knowledgeoperations.KnowledgeProcessingOperationSeries;
import java.util.UUID;
import javax.inject.Named;
import org.omg.spec.api4kp._1_0.AbstractCarrier;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.services.KPComponent;
import org.omg.spec.api4kp._1_0.services.KPOperation;
import org.omg.spec.api4kp._1_0.services.KPSupport;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.omg.spec.api4kp._1_0.services.SyntacticRepresentation;

@Named
@KPOperation(KnowledgeProcessingOperationSeries.Extract_Description_Task)
@KPSupport(HL7_CQL)
@KPComponent
public class CQLMetadataIntrospector implements IntrospectionApiInternal {

  public static final UUID CQL_1_3_EXTRACTOR
      = UUID.fromString("8969059a-0f3e-41c6-83be-fe0d14bdbfc6");

  @Override
  public Answer<KnowledgeCarrier> extractSurrogate(UUID lambdaId, KnowledgeCarrier sourceArtifact) {

    KnowledgeAsset surrogate = newSurrogate(sourceArtifact.getAssetId()).get()
        .withName("TODO")
        .withCarriers(new ComputableKnowledgeArtifact()
            .withRepresentation(new Representation()
                .withLanguage(HL7_CQL)
                .withWith(new SubLanguage().withRole(Schema_Language)
                    .withSubLanguage(new Representation()
                        // TODO: This depends on the 'import' in the CQL library
                        .withLanguage(FHIR_STU3)))));

    return Answer.of(
        AbstractCarrier.ofAst(surrogate)
            .withAssetId(sourceArtifact.getAssetId())
            .withLevel(Parsed_Knowedge_Expression)
            // TODO improve...
            .withArtifactId(surrogate.getSurrogate().get(0).getArtifactId())
            .withRepresentation(new SyntacticRepresentation()
                .withLanguage(Knowledge_Asset_Surrogate))
    );
  }



}