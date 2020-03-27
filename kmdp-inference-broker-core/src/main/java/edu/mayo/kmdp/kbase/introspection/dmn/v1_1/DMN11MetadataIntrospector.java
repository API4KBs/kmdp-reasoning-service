package edu.mayo.kmdp.kbase.introspection.dmn.v1_1;

import static edu.mayo.kmdp.SurrogateBuilder.newSurrogate;
import static edu.mayo.ontology.taxonomies.api4kp.parsinglevel.ParsingLevelSeries.Abstract_Knowledge_Expression;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_1;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate;

import edu.mayo.kmdp.id.helper.DatatypeHelper;
import edu.mayo.kmdp.inference.v4.server.IntrospectionApiInternal;
import edu.mayo.kmdp.language.parsers.dmn.v1_1.DMN11Parser;
import edu.mayo.kmdp.metadata.surrogate.ComputableKnowledgeArtifact;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.metadata.surrogate.Representation;
import edu.mayo.kmdp.tranx.v4.server.DeserializeApiInternal;
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
import org.omg.spec.dmn._20151101.dmn.TDefinitions;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@KPOperation(KnowledgeProcessingOperationSeries.Extract_Description_Task)
@KPSupport(DMN_1_1)
@KPComponent
public class DMN11MetadataIntrospector implements IntrospectionApiInternal._introspect {

  public static final UUID DMN1_1_EXTRACTOR
      = UUID.fromString("0c6f2884-60ce-4112-8dfd-231c96f7eca6");

  @Autowired
  @KPSupport(DMN_1_1)
  DeserializeApiInternal parser = new DMN11Parser();

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

    KnowledgeAsset surrogate = newSurrogate(DatatypeHelper.toURIIdentifier(carrier.getAssetId())).get()
        .withName(dmnModel.getName())
        .withCarriers(new ComputableKnowledgeArtifact()
            .withArtifactId(DatatypeHelper.toURIIdentifier(carrier.getArtifactId()))
            .withRepresentation(new Representation()
                .withLanguage(DMN_1_1)));

    return Answer.of(
        AbstractCarrier.ofAst(surrogate)
            .withAssetId(carrier.getAssetId())
            .withLevel(Abstract_Knowledge_Expression)
            // TODO improve...
            .withArtifactId(DatatypeHelper.toSemanticIdentifier(surrogate.getSurrogate().get(0).getArtifactId()))
            .withRepresentation(new SyntacticRepresentation()
                .withLanguage(Knowledge_Asset_Surrogate))
    );
  }


}