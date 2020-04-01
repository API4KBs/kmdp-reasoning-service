package edu.mayo.kmdp.kbase.introspection.cql.v1_3;

import static edu.mayo.kmdp.metadata.v2.surrogate.SurrogateBuilder.newSurrogate;
import static edu.mayo.ontology.taxonomies.api4kp.parsinglevel.ParsingLevelSeries.Parsed_Knowedge_Expression;
import static edu.mayo.ontology.taxonomies.kao.languagerole.KnowledgeRepresentationLanguageRoleSeries.Schema_Language;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.FHIR_STU3;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.HL7_CQL;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate;
import static org.omg.spec.api4kp._1_0.AbstractCarrier.rep;

import edu.mayo.kmdp.inference.v4.server.IntrospectionApiInternal;
import edu.mayo.kmdp.metadata.v2.surrogate.ComputableKnowledgeArtifact;
import edu.mayo.kmdp.metadata.v2.surrogate.KnowledgeAsset;
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
public class CQLMetadataIntrospector implements IntrospectionApiInternal._introspect {

  public static final UUID CQL_1_3_EXTRACTOR
      = UUID.fromString("8969059a-0f3e-41c6-83be-fe0d14bdbfc6");

  @Override
  public Answer<KnowledgeCarrier> introspect(UUID lambdaId, KnowledgeCarrier sourceArtifact, String xAccept) {

    KnowledgeAsset surrogate = newSurrogate(sourceArtifact.getAssetId()).get()
        .withName("TODO")
        .withCarriers(new ComputableKnowledgeArtifact()
            .withArtifactId(sourceArtifact.getArtifactId())
            .withRepresentation(rep(HL7_CQL)
                .withSubLanguage(rep(FHIR_STU3).withRole(Schema_Language))));

    return Answer.of(
        AbstractCarrier.ofAst(surrogate)
            .withAssetId(sourceArtifact.getAssetId())
            .withLevel(Parsed_Knowedge_Expression)
            // TODO Improve..
            .withArtifactId(surrogate.getSurrogate().get(0).getArtifactId())
            .withRepresentation(new SyntacticRepresentation()
                .withLanguage(Knowledge_Asset_Surrogate))
    );
  }


}