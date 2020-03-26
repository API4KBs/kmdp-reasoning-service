package edu.mayo.kmdp.kbase.introspection.dmn.v1_2;

import static edu.mayo.kmdp.SurrogateBuilder.newSurrogate;
import static edu.mayo.ontology.taxonomies.api4kp.parsinglevel.ParsingLevelSeries.Abstract_Knowledge_Expression;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_2;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate;

import edu.mayo.kmdp.inference.v3.server.IntrospectionApiInternal;
import edu.mayo.kmdp.language.parsers.dmn.v1_2.DMN12Parser;
import edu.mayo.kmdp.metadata.surrogate.ComputableKnowledgeArtifact;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.metadata.surrogate.Representation;
import edu.mayo.kmdp.tranx.v3.server.DeserializeApiInternal;
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
import org.omg.spec.dmn._20180521.model.TDefinitions;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@KPOperation(KnowledgeProcessingOperationSeries.Extract_Description_Task)
@KPSupport(DMN_1_2)
@KPComponent
public class DMN12MetadataIntrospector implements IntrospectionApiInternal._introspect {

  public static final UUID DMN1_2_EXTRACTOR
      = UUID.fromString("78d054a8-ba2d-4b6f-93a7-667cfe0820ee");

  @Autowired
  @KPSupport(DMN_1_2)
  DeserializeApiInternal parser = new DMN12Parser();

  @Override
  public Answer<KnowledgeCarrier> introspect(
      UUID lambdaId,
      KnowledgeCarrier sourceArtifact,
      String xAccept) {
    return parser
        // Parse as needed
        .lift(sourceArtifact, Abstract_Knowledge_Expression)
        .flatMap(this::extractSurrogate);
  }


  private Answer<KnowledgeCarrier> extractSurrogate(KnowledgeCarrier carrier) {
    TDefinitions dmnModel = carrier.as(TDefinitions.class)
        .orElseThrow(IllegalStateException::new);

    KnowledgeAsset surrogate = newSurrogate(carrier.getAssetId()).get()
        .withName(dmnModel.getName())
        .withCarriers(new ComputableKnowledgeArtifact()
            .withArtifactId(carrier.getArtifactId())
            .withRepresentation(new Representation()
                .withLanguage(DMN_1_2)));

    return Answer.of(
        AbstractCarrier.ofAst(surrogate)
            .withAssetId(carrier.getAssetId())
            .withLevel(Abstract_Knowledge_Expression)
            // TODO improve...
            .withArtifactId(surrogate.getSurrogate().get(0).getArtifactId())
            .withRepresentation(new SyntacticRepresentation()
                .withLanguage(Knowledge_Asset_Surrogate))
    );
  }


}