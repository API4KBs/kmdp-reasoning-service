package edu.mayo.kmdp.kbase.inference.fhir3;

import static edu.mayo.kmdp.kbase.introspection.cql.v1_3.CQLMetadataIntrospector.CQL_1_3_EXTRACTOR;
import static edu.mayo.kmdp.kbase.introspection.dmn.v1_1.DMN11MetadataIntrospector.DMN1_1_EXTRACTOR;
import static edu.mayo.kmdp.kbase.introspection.fhir.stu3.PlanDefinitionMetadataIntrospector.FHIR_STU3_EXTRACTOR;
import static edu.mayo.kmdp.metadata.v2.surrogate.SurrogateBuilder.artifactId;
import static edu.mayo.kmdp.metadata.v2.surrogate.SurrogateBuilder.assetId;
import static edu.mayo.ontology.taxonomies.krformat.SerializationFormatSeries.TXT;
import static edu.mayo.ontology.taxonomies.krformat.SerializationFormatSeries.XML_1_1;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_1;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.HL7_CQL;
import static org.junit.jupiter.api.Assertions.fail;

import edu.mayo.kmdp.kbase.inference.InferenceBroker;
import edu.mayo.kmdp.kbase.introspection.cql.v1_3.CQLMetadataIntrospector;
import edu.mayo.kmdp.kbase.introspection.dmn.DMNMetadataIntrospector;
import edu.mayo.kmdp.kbase.introspection.fhir.stu3.PlanDefinitionMetadataIntrospector;
import edu.mayo.kmdp.knowledgebase.KnowledgeBaseProvider;
import edu.mayo.kmdp.language.LanguageDeSerializer;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.repository.asset.KnowledgeAssetRepositoryService;
import edu.mayo.kmdp.util.Util;
import edu.mayo.ontology.taxonomies.api4kp.parsinglevel.ParsingLevelSeries;
import edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguage;
import java.io.InputStream;
import java.util.UUID;
import org.omg.spec.api4kp._1_0.AbstractCarrier;
import org.omg.spec.api4kp._1_0.id.ResourceIdentifier;
import org.omg.spec.api4kp._1_0.services.KPServer;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.omg.spec.api4kp._1_0.services.SyntacticRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

public abstract class BaseInferenceIntegrationTest {

  static final String VTAG = "LATEST";

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
    DMNMetadataIntrospector dmnMetadataExtractor;

    @Autowired
    CQLMetadataIntrospector cqlMetadataExtractor;

    @Autowired
    PlanDefinitionMetadataIntrospector pdMetadataExtractor;

    @Bean
    @KPServer
    public KnowledgeAssetRepositoryService backingRepo() {
      KnowledgeAssetRepositoryService semRepo =
          KnowledgeAssetRepositoryService.selfContainedRepository();

      publish(semRepo, "mockCQL", "/mockCQL.cql",
          AbstractCarrier.rep(HL7_CQL, TXT));
      publish(semRepo, "mockPredictor", "/MockPredictor.dmn",
          AbstractCarrier.rep(DMN_1_1, XML_1_1));

      return semRepo;
    }


    private void publish(KnowledgeAssetRepositoryService semRepo, String modelName,
        String path, SyntacticRepresentation rep) {
      ResourceIdentifier assetId = assetId(Util.uuid(modelName), VTAG);

      KnowledgeCarrier carrier = AbstractCarrier
          .of(getBytes(path))
          .withAssetId(assetId)
          .withArtifactId(artifactId(UUID.randomUUID().toString(),VTAG))
          .withLevel(ParsingLevelSeries.Encoded_Knowledge_Expression)
          .withRepresentation(rep);

      KnowledgeAsset surrogate = getSurrogate(carrier);

      semRepo.publish(surrogate, carrier);
    }


    private KnowledgeAsset getSurrogate(KnowledgeCarrier artifactCarrier) {
      KnowledgeRepresentationLanguage lang = artifactCarrier.getRepresentation().getLanguage();
      // TODO Implement an introspector broker
      switch (lang.asEnum()) {
        case DMN_1_1:
          return dmnMetadataExtractor
              .introspect(
                  DMN1_1_EXTRACTOR, artifactCarrier, null)
              .flatOpt(kc -> kc.as(KnowledgeAsset.class))
              .orElseThrow(UnsupportedOperationException::new);
        case HL7_CQL:
          return cqlMetadataExtractor
              .introspect(
                  CQL_1_3_EXTRACTOR, artifactCarrier, null)
              .flatOpt(kc -> kc.as(KnowledgeAsset.class))
              .orElseThrow(UnsupportedOperationException::new);
        case FHIR_STU3:
          return pdMetadataExtractor
              .introspect(
                  FHIR_STU3_EXTRACTOR, artifactCarrier, null)
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
