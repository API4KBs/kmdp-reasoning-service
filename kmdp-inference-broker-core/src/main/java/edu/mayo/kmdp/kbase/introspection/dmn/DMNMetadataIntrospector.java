package edu.mayo.kmdp.kbase.introspection.dmn;


import static edu.mayo.kmdp.kbase.introspection.dmn.v1_1.DMN11MetadataIntrospector.DMN1_1_EXTRACTOR;
import static edu.mayo.kmdp.kbase.introspection.dmn.v1_2.DMN12MetadataIntrospector.DMN1_2_EXTRACTOR;
import static org.omg.spec.api4kp.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_1;
import static org.omg.spec.api4kp.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_2;

import edu.mayo.kmdp.kbase.introspection.dmn.v1_1.DMN11MetadataIntrospector;
import edu.mayo.kmdp.kbase.introspection.dmn.v1_2.DMN12MetadataIntrospector;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Named;
import org.omg.spec.api4kp._20200801.Answer;
import org.omg.spec.api4kp._20200801.api.inference.v4.server.IntrospectionApiInternal;
import org.omg.spec.api4kp._20200801.id.Pointer;
import org.omg.spec.api4kp._20200801.services.KPComponent;
import org.omg.spec.api4kp._20200801.services.KPOperation;
import org.omg.spec.api4kp._20200801.services.KPServer;
import org.omg.spec.api4kp._20200801.services.KPSupport;
import org.omg.spec.api4kp._20200801.services.KnowledgeCarrier;
import org.omg.spec.api4kp.taxonomy.knowledgeoperation.KnowledgeProcessingOperationSeries;

@KPServer
@KPOperation(KnowledgeProcessingOperationSeries.Language_Information_Detection_Task)
@KPSupport({DMN_1_1,DMN_1_2})
@Named
public class DMNMetadataIntrospector implements IntrospectionApiInternal {

  @Inject
  @KPComponent
  @KPSupport(DMN_1_1)
  IntrospectionApiInternal._introspect dmn11Introspector = new DMN11MetadataIntrospector();

  @Inject
  @KPComponent
  @KPSupport(DMN_1_2)
  IntrospectionApiInternal._introspect dmn12Introspector = new DMN12MetadataIntrospector();

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
    } else if (DMN1_2_EXTRACTOR.equals(lambdaId)) {
      return dmn12Introspector
          .introspect(
              DMN1_2_EXTRACTOR,
              sourceArtifact,
              xAccept);
    }
    return Answer.unsupported();
  }


  @Override
  public Answer<Void> getIntrospector(UUID lambdaId) {
    return Answer.unsupported();
  }

  @Override
  public Answer<Void> introspectorsLambdaIdDelete(UUID lambdaId) {
    return Answer.unsupported();
  }

  @Override
  public Answer<Pointer> listIntrospectors() {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> initIntrospector(KnowledgeCarrier sourceArtifact) {
    return Answer.unsupported();
  }
}
