package edu.mayo.mea3.inference;

import static edu.mayo.kmdp.kbase.introspection.cql.CQLMetadataIntrospector.CQL_1_3_EXTRACTOR;
import static edu.mayo.kmdp.kbase.introspection.dmn.DMN11MetadataIntrospector.DMN1_1_EXTRACTOR;
import static edu.mayo.ontology.taxonomies.kao.languagerole.KnowledgeRepresentationLanguageRoleSeries.Schema_Language;
import static edu.mayo.ontology.taxonomies.krformat.SerializationFormatSeries.TXT;
import static edu.mayo.ontology.taxonomies.krformat.SerializationFormatSeries.XML_1_1;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_1;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.FHIR_STU3;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.HL7_CQL;
import static org.omg.spec.api4kp._1_0.AbstractCarrier.rep;
import static org.springframework.test.util.AssertionErrors.fail;

import edu.mayo.kmdp.SurrogateBuilder;
import edu.mayo.kmdp.id.helper.DatatypeHelper;
import edu.mayo.kmdp.kbase.inference.dmn.KieDMNHelper;
import edu.mayo.kmdp.kbase.introspection.cql.CQLMetadataIntrospector;
import edu.mayo.kmdp.kbase.introspection.dmn.DMNMetadataIntrospector;
import edu.mayo.kmdp.metadata.surrogate.ComputableKnowledgeArtifact;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.metadata.surrogate.Representation;
import edu.mayo.kmdp.metadata.surrogate.SubLanguage;
import edu.mayo.kmdp.registry.Registry;
import edu.mayo.kmdp.repository.asset.KnowledgeAssetRepositoryService;
import java.io.InputStream;
import java.util.UUID;
import org.kie.dmn.api.core.DMNRuntime;
import org.omg.spec.api4kp._1_0.AbstractCarrier;
import org.omg.spec.api4kp._1_0.services.KPComponent;
import org.omg.spec.api4kp._1_0.services.KnowledgeBase;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;

public class InferenceBaseTest {

  static final String VTAG = "LATEST";

  @KPComponent
  DMNMetadataIntrospector dmnMetadataExtractor = new DMNMetadataIntrospector();

  @KPComponent
  CQLMetadataIntrospector cqlMetadataExtractor = new CQLMetadataIntrospector();


  public KnowledgeAssetRepositoryService initMockRepo(UUID modelId, String version, String path) {
    KnowledgeAssetRepositoryService semRepo =
        KnowledgeAssetRepositoryService.selfContainedRepository();

    KnowledgeAsset surrogate = getSurrogate(modelId, version, path);

    KnowledgeCarrier carrier = AbstractCarrier.of(InferenceBaseTest.class.getResourceAsStream(path))
        .withAssetId(surrogate.getAssetId())
        .withArtifactId(DatatypeHelper.uri(UUID.randomUUID().toString(), version))
        .withRepresentation(rep(
            ((ComputableKnowledgeArtifact) surrogate.getCarriers().get(0)).getRepresentation()));

    semRepo.publish(surrogate, carrier);

    return semRepo;
  }

  private KnowledgeAsset getSurrogate(UUID modelId, String version, String path) {
    if (path.endsWith("dmn")) {

      return dmnMetadataExtractor
          .extractSurrogate(
              DMN1_1_EXTRACTOR,
              AbstractCarrier.of(getBytes(path), rep(DMN_1_1, XML_1_1))
                  .withAssetId(SurrogateBuilder.assetId(modelId, version)))
          .flatOpt(kc -> kc.as(KnowledgeAsset.class))
          .orElseThrow(UnsupportedOperationException::new);

    } else if (path.endsWith("cql")) {

      return cqlMetadataExtractor
          .extractSurrogate(
              CQL_1_3_EXTRACTOR,
              AbstractCarrier.of(getBytes(path), rep(HL7_CQL, TXT))
                  .withAssetId(SurrogateBuilder.assetId(modelId, version)))
          .flatOpt(kc -> kc.as(KnowledgeAsset.class))
          .orElseThrow(UnsupportedOperationException::new);

    } else {
      fail("Not supported");
      throw new UnsupportedOperationException();
    }
  }

  private InputStream getBytes(String path) {
    return InferenceBaseTest.class.getResourceAsStream(path);
  }


  protected DMNRuntime initRuntime(String modelPath) {
    UUID id = UUID.randomUUID();

    KnowledgeAssetRepositoryService semRepo =
        initMockRepo(id, VTAG, modelPath);

    return KieDMNHelper
        .initRuntime(new KnowledgeBase().withManifestation(
            semRepo.getCanonicalKnowledgeAssetCarrier(id, VTAG).get()));
  }

}
