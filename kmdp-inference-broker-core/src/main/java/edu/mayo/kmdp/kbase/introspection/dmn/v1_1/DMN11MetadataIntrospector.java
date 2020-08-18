package edu.mayo.kmdp.kbase.introspection.dmn.v1_1;

import static org.omg.spec.api4kp._20200801.AbstractCarrier.rep;
import static org.omg.spec.api4kp._20200801.surrogate.SurrogateBuilder.newSurrogate;
import static org.omg.spec.api4kp.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_1;
import static org.omg.spec.api4kp.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate_2_0;
import static org.omg.spec.api4kp.taxonomy.parsinglevel.ParsingLevelSeries.Abstract_Knowledge_Expression;

import edu.mayo.kmdp.language.parsers.dmn.v1_1.DMN11Parser;
import java.util.UUID;
import javax.inject.Named;
import org.omg.spec.api4kp._20200801.AbstractCarrier;
import org.omg.spec.api4kp._20200801.Answer;
import org.omg.spec.api4kp._20200801.api.inference.v4.server.IntrospectionApiInternal;
import org.omg.spec.api4kp._20200801.api.transrepresentation.v4.server.DeserializeApiInternal._applyLift;
import org.omg.spec.api4kp._20200801.services.KPComponent;
import org.omg.spec.api4kp._20200801.services.KPOperation;
import org.omg.spec.api4kp._20200801.services.KPSupport;
import org.omg.spec.api4kp._20200801.services.KnowledgeCarrier;
import org.omg.spec.api4kp._20200801.services.SyntacticRepresentation;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeArtifact;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeAsset;
import org.omg.spec.api4kp.taxonomy.knowledgeoperation.KnowledgeProcessingOperationSeries;
import org.omg.spec.dmn._20151101.dmn.TDefinitions;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@KPOperation(KnowledgeProcessingOperationSeries.Language_Information_Detection_Task)
@KPSupport(DMN_1_1)
@KPComponent
public class DMN11MetadataIntrospector implements IntrospectionApiInternal._introspect {

  public static final UUID DMN1_1_EXTRACTOR
      = UUID.fromString("0c6f2884-60ce-4112-8dfd-231c96f7eca6");

  @Autowired
  @KPSupport(DMN_1_1)
  _applyLift parser = new DMN11Parser();

  @Override
  public Answer<KnowledgeCarrier> introspect(
      UUID lambdaId,
      KnowledgeCarrier sourceArtifact,
      String xAccept) {
    return parser
        // Parse as needed
        .applyLift(sourceArtifact, Abstract_Knowledge_Expression, null, null)
        .flatMap(this::extractSurrogate);
  }


  private Answer<KnowledgeCarrier> extractSurrogate(KnowledgeCarrier carrier) {
    TDefinitions dmnModel = carrier.as(TDefinitions.class)
        .orElseThrow(IllegalStateException::new);

    KnowledgeAsset surrogate = newSurrogate(carrier.getAssetId()).get()
        .withName(dmnModel.getName())
        .withCarriers(new KnowledgeArtifact()
            .withArtifactId(carrier.getArtifactId())
            .withRepresentation(rep(DMN_1_1)));

    return Answer.of(
        AbstractCarrier.ofAst(surrogate)
            .withAssetId(carrier.getAssetId())
            .withLevel(Abstract_Knowledge_Expression)
            // TODO improve...
            .withArtifactId(surrogate.getSurrogate().get(0).getArtifactId())
            .withRepresentation(new SyntacticRepresentation()
                .withLanguage(Knowledge_Asset_Surrogate_2_0))
    );
  }


}