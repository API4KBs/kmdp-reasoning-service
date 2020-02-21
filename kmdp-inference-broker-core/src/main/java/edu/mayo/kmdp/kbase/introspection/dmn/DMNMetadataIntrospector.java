package edu.mayo.kmdp.kbase.introspection.dmn;

import static edu.mayo.kmdp.kbase.introspection.dmn.v1_1.DMN11MetadataIntrospector.DMN1_1_EXTRACTOR;

import edu.mayo.kmdp.inference.v3.server.IntrospectionApiInternal;
import edu.mayo.kmdp.kbase.introspection.dmn.v1_1.DMN11MetadataIntrospector;
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
public class DMNMetadataIntrospector implements IntrospectionApiInternal._introspect {

  @Inject
  @KPComponent
  @KPSupport(KnowledgeRepresentationLanguageSeries.DMN_1_1)
  IntrospectionApiInternal._introspect dmn11Introspector = new DMN11MetadataIntrospector();

  @Override
  public Answer<KnowledgeCarrier> introspect(UUID lambdaId,
      KnowledgeCarrier sourceArtifact,
      String xAccept) {

    // TODO use OperatorID...
    if (DMN1_1_EXTRACTOR.equals(lambdaId)) {
      return dmn11Introspector
          .introspect(
              DMN1_1_EXTRACTOR,
              sourceArtifact,
              xAccept);
    }
    return Answer.unsupported();
  }


}
