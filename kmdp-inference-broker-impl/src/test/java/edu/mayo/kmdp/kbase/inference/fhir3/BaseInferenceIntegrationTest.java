package edu.mayo.kmdp.kbase.inference.fhir3;

import static org.junit.jupiter.api.Assertions.fail;
import static org.omg.spec.api4kp._20200801.surrogate.SurrogateBuilder.artifactId;
import static org.omg.spec.api4kp._20200801.surrogate.SurrogateBuilder.assetId;
import static org.omg.spec.api4kp._20200801.taxonomy.krformat.SerializationFormatSeries.TXT;
import static org.omg.spec.api4kp._20200801.taxonomy.krformat.SerializationFormatSeries.XML_1_1;
import static org.omg.spec.api4kp._20200801.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_1;
import static org.omg.spec.api4kp._20200801.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.HL7_CQL_1_3;
import static org.omg.spec.api4kp._20200801.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.asEnum;
import static org.omg.spec.api4kp._20200801.taxonomy.parsinglevel.ParsingLevelSeries.Encoded_Knowledge_Expression;

import edu.mayo.kmdp.kbase.inference.InferenceBroker;
import edu.mayo.kmdp.kbase.inference.mockRepo.MockAssetRepository;
import edu.mayo.kmdp.knowledgebase.KnowledgeBaseProvider;
import edu.mayo.kmdp.knowledgebase.introspectors.cql.v1_3.CQLMetadataIntrospector;
import edu.mayo.kmdp.knowledgebase.introspectors.dmn.DMNMetadataIntrospector;
import edu.mayo.kmdp.knowledgebase.introspectors.dmn.v1_1.DMN11MetadataIntrospector;
import edu.mayo.kmdp.knowledgebase.introspectors.fhir.stu3.PlanDefinitionMetadataIntrospector;
import edu.mayo.kmdp.language.LanguageDeSerializer;
import edu.mayo.kmdp.util.Util;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.omg.spec.api4kp._20200801.AbstractCarrier;
import org.omg.spec.api4kp._20200801.api.knowledgebase.v4.server.KnowledgeBaseApiInternal;
import org.omg.spec.api4kp._20200801.id.IdentifierConstants;
import org.omg.spec.api4kp._20200801.id.Pointer;
import org.omg.spec.api4kp._20200801.id.ResourceIdentifier;
import org.omg.spec.api4kp._20200801.services.KPServer;
import org.omg.spec.api4kp._20200801.services.KnowledgeCarrier;
import org.omg.spec.api4kp._20200801.services.SyntacticRepresentation;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeAsset;
import org.omg.spec.api4kp._20200801.taxonomy.krlanguage.KnowledgeRepresentationLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

public abstract class BaseInferenceIntegrationTest {

  static final String VTAG = IdentifierConstants.VERSION_ZERO;

  @LocalServerPort
  protected int port;

  protected BaseInferenceIntegrationTest() {

  }

  @Configuration
  @ComponentScan(
      basePackageClasses = {
          DMNMetadataIntrospector.class,
          CQLMetadataIntrospector.class,
          PlanDefinitionMetadataIntrospector.class,
          InferenceBroker.class,
          LanguageDeSerializer.class,
          KnowledgeBaseProvider.class})
  @TestPropertySource(value = {"classpath:application.test.properties"})
  public static class InferenceTestConfig {

    @Autowired
    KnowledgeBaseApiInternal kbManager;

    @Autowired
    DMNMetadataIntrospector dmnMetadataExtractor;

    @Autowired
    CQLMetadataIntrospector cqlMetadataExtractor;

    @Autowired
    PlanDefinitionMetadataIntrospector pdMetadataExtractor;

    @Bean
    @KPServer
    public MockAssetRepository backingRepo() {
      KnowledgeCarrier c1 = loadCarrier("mockCQL", "/mockCQL.cql",
          AbstractCarrier.rep(HL7_CQL_1_3, TXT));
      KnowledgeCarrier c2 = loadCarrier("mockPredictor", "/MockPredictor.dmn",
          AbstractCarrier.rep(DMN_1_1, XML_1_1));

      return new MockAssetRepository(
          Arrays.asList(getSurrogate(c1), getSurrogate(c2)),
          Arrays.asList(c1, c2)
      );
    }

    private KnowledgeCarrier loadCarrier(String modelName,
        String path, SyntacticRepresentation rep) {
      ResourceIdentifier assetId = assetId(Util.uuid(modelName), VTAG);

      return AbstractCarrier
          .of(getBytes(path))
          .withAssetId(assetId)
          .withArtifactId(artifactId(UUID.randomUUID().toString(),VTAG))
          .withLevel(Encoded_Knowledge_Expression)
          .withRepresentation(rep);
    }


    private KnowledgeAsset getSurrogate(KnowledgeCarrier artifactCarrier) {

      Pointer ptr = kbManager.initKnowledgeBase(artifactCarrier).orElseGet(Assertions::fail);

      KnowledgeRepresentationLanguage lang = artifactCarrier.getRepresentation().getLanguage();
      // TODO Implement an introspector broker
      switch (asEnum(lang)) {
        case DMN_1_1:
          return dmnMetadataExtractor
              .applyNamedIntrospect(
                  DMN11MetadataIntrospector.id, ptr.getUuid(), ptr.getVersionTag(), null)
              .flatOpt(kc -> kc.as(KnowledgeAsset.class))
              .orElseThrow(UnsupportedOperationException::new);
        case HL7_CQL_1_3:
          return cqlMetadataExtractor
              .applyNamedIntrospect(
                  CQLMetadataIntrospector.id, ptr.getUuid(), ptr.getVersionTag(), null)
              .flatOpt(kc -> kc.as(KnowledgeAsset.class))
              .orElseThrow(UnsupportedOperationException::new);
        case FHIR_STU3:
          return pdMetadataExtractor
              .applyNamedIntrospect(
                  PlanDefinitionMetadataIntrospector.id, ptr.getUuid(), ptr.getVersionTag(), null)
              .flatOpt(kc -> kc.as(KnowledgeAsset.class))
              .orElseThrow(UnsupportedOperationException::new);

        default:
          fail("Not supported");
          throw new UnsupportedOperationException();
      }
    }

    private InputStream getBytes(String path) {
      return BaseInferenceIntegrationTest.class.getResourceAsStream(path);
    }

  }


}
