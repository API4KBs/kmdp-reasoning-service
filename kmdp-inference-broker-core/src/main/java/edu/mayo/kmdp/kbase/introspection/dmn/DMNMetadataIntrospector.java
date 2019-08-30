package edu.mayo.kmdp.kbase.introspection.dmn;

import static edu.mayo.kmdp.kbase.introspection.dmn.DMN11MetadataIntrospector.DMN1_1_EXTRACTOR;

import edu.mayo.kmdp.knowledgebase.v3.server.IntrospectionApiInternal;
import edu.mayo.ontology.taxonomies.api4kp.knowledgeoperations.KnowledgeProcessingOperationSeries;
import edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.services.KPComponent;
import org.omg.spec.api4kp._1_0.services.KPOperation;
import org.omg.spec.api4kp._1_0.services.KPSupport;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;

@KPComponent
@KPOperation(KnowledgeProcessingOperationSeries.Extract_Description_Task)
@KPSupport(KnowledgeRepresentationLanguageSeries.DMN_1_1)
@Named
public class DMNMetadataIntrospector implements IntrospectionApiInternal {

  @Inject
  @KPComponent
  @KPSupport(KnowledgeRepresentationLanguageSeries.DMN_1_1)
  IntrospectionApiInternal dmn11Introspector = new DMN11MetadataIntrospector();

  @Override
  public Answer<KnowledgeCarrier> extractSurrogate(UUID lambdaId,
      KnowledgeCarrier sourceArtifact) {

    // TODO use OperatorID...
    if (DMN1_1_EXTRACTOR.equals(lambdaId)) {
      return dmn11Introspector
          .extractSurrogate(
              DMN1_1_EXTRACTOR,
              sourceArtifact);
    }
    return Answer.unsupported();
  }




}
