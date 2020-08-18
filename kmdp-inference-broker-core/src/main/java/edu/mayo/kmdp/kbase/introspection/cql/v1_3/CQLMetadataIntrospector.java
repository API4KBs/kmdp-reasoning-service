package edu.mayo.kmdp.kbase.introspection.cql.v1_3;

import static org.omg.spec.api4kp._20200801.AbstractCarrier.rep;
import static org.omg.spec.api4kp._20200801.surrogate.SurrogateBuilder.newSurrogate;
import static org.omg.spec.api4kp.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.FHIR_STU3;
import static org.omg.spec.api4kp.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.HL7_CQL;
import static org.omg.spec.api4kp.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate_2_0;
import static org.omg.spec.api4kp.taxonomy.languagerole.KnowledgeRepresentationLanguageRoleSeries.Schema_Language;
import static org.omg.spec.api4kp.taxonomy.parsinglevel.ParsingLevelSeries.Abstract_Knowledge_Expression;

import java.util.UUID;
import javax.inject.Named;
import org.omg.spec.api4kp._20200801.AbstractCarrier;
import org.omg.spec.api4kp._20200801.Answer;
import org.omg.spec.api4kp._20200801.api.inference.v4.server.IntrospectionApiInternal;
import org.omg.spec.api4kp._20200801.services.KPComponent;
import org.omg.spec.api4kp._20200801.services.KPOperation;
import org.omg.spec.api4kp._20200801.services.KPSupport;
import org.omg.spec.api4kp._20200801.services.KnowledgeCarrier;
import org.omg.spec.api4kp._20200801.services.SyntacticRepresentation;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeArtifact;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeAsset;
import org.omg.spec.api4kp.taxonomy.knowledgeoperation.KnowledgeProcessingOperationSeries;

@Named
// TODO Should be 'Description_Task', once added to the ontology
@KPOperation(KnowledgeProcessingOperationSeries.Language_Information_Detection_Task)
@KPSupport(HL7_CQL)
@KPComponent
public class CQLMetadataIntrospector implements IntrospectionApiInternal._introspect {

  public static final UUID CQL_1_3_EXTRACTOR
      = UUID.fromString("8969059a-0f3e-41c6-83be-fe0d14bdbfc6");

  @Override
  public Answer<KnowledgeCarrier> introspect(UUID lambdaId, KnowledgeCarrier sourceArtifact, String xAccept) {

    KnowledgeAsset surrogate = newSurrogate(sourceArtifact.getAssetId()).get()
        .withName("TODO")
        .withCarriers(new KnowledgeArtifact()
            .withArtifactId(sourceArtifact.getArtifactId())
            .withRepresentation(rep(HL7_CQL)
                .withSubLanguage(rep(FHIR_STU3).withRole(Schema_Language))));

    return Answer.of(
        AbstractCarrier.ofAst(surrogate)
            .withAssetId(sourceArtifact.getAssetId())
            .withLevel(Abstract_Knowledge_Expression)
            // TODO Improve..
            .withArtifactId(surrogate.getSurrogate().get(0).getArtifactId())
            .withRepresentation(new SyntacticRepresentation()
                .withLanguage(Knowledge_Asset_Surrogate_2_0))
    );
  }


}