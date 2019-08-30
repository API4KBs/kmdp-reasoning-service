package edu.mayo.kmdp.kbase.inference.fhir3;

import static edu.mayo.kmdp.kbase.introspection.cql.CQLMetadataIntrospector.CQL_1_3_EXTRACTOR;
import static edu.mayo.kmdp.kbase.introspection.dmn.DMN11MetadataIntrospector.DMN1_1_EXTRACTOR;
import static edu.mayo.ontology.taxonomies.kao.languagerole.KnowledgeRepresentationLanguageRoleSeries.Schema_Language;
import static edu.mayo.ontology.taxonomies.krformat.SerializationFormatSeries.TXT;
import static edu.mayo.ontology.taxonomies.krformat.SerializationFormatSeries.XML_1_1;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_1;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.FHIR_STU3;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.HL7_CQL;
import static org.junit.jupiter.api.Assertions.fail;
import static org.omg.spec.api4kp._1_0.AbstractCarrier.rep;

import edu.mayo.kmdp.SurrogateBuilder;
import edu.mayo.kmdp.id.helper.DatatypeHelper;
import edu.mayo.kmdp.kbase.inference.InferenceBroker;
import edu.mayo.kmdp.kbase.inference.knowledgebase.KnowledgeBaseProvider;
import edu.mayo.kmdp.kbase.introspection.cql.CQLMetadataIntrospector;
import edu.mayo.kmdp.kbase.introspection.dmn.DMNMetadataIntrospector;
import edu.mayo.kmdp.language.LanguageDeSerializer;
import edu.mayo.kmdp.metadata.surrogate.ComputableKnowledgeArtifact;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.metadata.surrogate.Representation;
import edu.mayo.kmdp.metadata.surrogate.SubLanguage;
import edu.mayo.kmdp.repository.asset.KnowledgeAssetRepositoryService;
import edu.mayo.kmdp.util.Util;
import edu.mayo.ontology.taxonomies.api4kp.parsinglevel.ParsingLevelSeries;
import edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguage;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.omg.spec.api4kp._1_0.AbstractCarrier;
import org.omg.spec.api4kp._1_0.identifiers.URIIdentifier;
import org.omg.spec.api4kp._1_0.services.KPComponent;
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
          InferenceBroker.class,
          LanguageDeSerializer.class,
          KnowledgeBaseProvider.class})
  @TestPropertySource(value = {"classpath:application.test.properties"})
  public static class InferenceTestConfig {

    @Autowired
    DMNMetadataIntrospector dmnMetadataExtractor;

    @Autowired
    CQLMetadataIntrospector cqlMetadataExtractor;


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
      URIIdentifier assetId = SurrogateBuilder.assetId(Util.uuid(modelName), VTAG);

      KnowledgeCarrier carrier = AbstractCarrier
          .of(getBytes(path))
          .withAssetId(assetId)
          .withLevel(ParsingLevelSeries.Encoded_Knowledge_Expression)
          .withArtifactId(DatatypeHelper.uri(UUID.randomUUID().toString(), VTAG))
          .withRepresentation(rep);

      KnowledgeAsset surrogate = getSurrogate(modelName, carrier);

      semRepo.publish(surrogate, carrier);
    }


    private KnowledgeAsset getSurrogate(
        String modelName, KnowledgeCarrier artifactCarrier) {
      KnowledgeRepresentationLanguage lang = artifactCarrier.getRepresentation().getLanguage();
      // TODO Implement an introspector broker
      switch (lang.asEnum()) {
        case DMN_1_1:
          return dmnMetadataExtractor
              .extractSurrogate(
                  DMN1_1_EXTRACTOR, artifactCarrier)
              .flatOpt(kc -> kc.as(KnowledgeAsset.class))
              .orElseThrow(UnsupportedOperationException::new);
        case HL7_CQL:
          return cqlMetadataExtractor
              .extractSurrogate(
                  CQL_1_3_EXTRACTOR, artifactCarrier)
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
