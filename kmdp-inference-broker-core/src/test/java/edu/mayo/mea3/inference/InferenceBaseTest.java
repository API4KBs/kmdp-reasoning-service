package edu.mayo.mea3.inference;

import static edu.mayo.kmdp.kbase.introspection.cql.v1_3.CQLMetadataIntrospector.CQL_1_3_EXTRACTOR;
import static edu.mayo.kmdp.kbase.introspection.dmn.v1_1.DMN11MetadataIntrospector.DMN1_1_EXTRACTOR;
import static edu.mayo.ontology.taxonomies.krformat.SerializationFormatSeries.TXT;
import static edu.mayo.ontology.taxonomies.krformat.SerializationFormatSeries.XML_1_1;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.DMN_1_1;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.HL7_CQL;
import static org.omg.spec.api4kp._1_0.AbstractCarrier.rep;
import static org.springframework.test.util.AssertionErrors.fail;

import edu.mayo.kmdp.id.helper.DatatypeHelper;
import edu.mayo.kmdp.kbase.inference.dmn.KieDMNHelper;
import edu.mayo.kmdp.kbase.introspection.cql.v1_3.CQLMetadataIntrospector;
import edu.mayo.kmdp.kbase.introspection.dmn.DMNMetadataIntrospector;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.registry.Registry;
import edu.mayo.kmdp.repository.asset.KnowledgeAssetRepositoryService;
import edu.mayo.ontology.taxonomies.api4kp.parsinglevel.ParsingLevelSeries;
import java.io.InputStream;
import java.util.UUID;
import org.kie.dmn.api.core.DMNRuntime;
import org.omg.spec.api4kp._1_0.AbstractCarrier;
import org.omg.spec.api4kp._1_0.services.KPComponent;
import org.omg.spec.api4kp._1_0.services.KnowledgeBase;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.omg.spec.api4kp._1_0.services.SyntacticRepresentation;

public abstract class InferenceBaseTest {

  static final String VTAG = "LATEST";

  @KPComponent
  DMNMetadataIntrospector dmnMetadataExtractor = new DMNMetadataIntrospector();

  @KPComponent
  CQLMetadataIntrospector cqlMetadataExtractor = new CQLMetadataIntrospector();


  public KnowledgeAssetRepositoryService initMockRepo(UUID modelId, String version,
      String path) {
    return initMockRepo(modelId,version,path,getRepresentation(path));
  }

  protected SyntacticRepresentation getRepresentation(String modelPath) {
    SyntacticRepresentation rep;
    if (modelPath.endsWith(".dmn")) {
      rep = rep(DMN_1_1,XML_1_1);
    } else if (modelPath.endsWith("cql")) {
      rep = rep(HL7_CQL, TXT);
    } else {
      throw new IllegalArgumentException("Unable to detect model type from path " + modelPath);
    }
    return rep;
  }

  public KnowledgeAssetRepositoryService initMockRepo(UUID modelId, String version,
      String path,
      SyntacticRepresentation rep) {
    KnowledgeAssetRepositoryService semRepo =
        KnowledgeAssetRepositoryService.selfContainedRepository();

    KnowledgeCarrier carrier = AbstractCarrier
        .of(getBytes(path))
        .withAssetId(DatatypeHelper.uri(Registry.MAYO_ASSETS_BASE_URI,modelId.toString(),version))
        .withArtifactId(DatatypeHelper.uri(Registry.MAYO_ARTIFACTS_BASE_URI,UUID.randomUUID().toString(),VTAG))
        .withLevel(ParsingLevelSeries.Encoded_Knowledge_Expression)
        .withRepresentation(rep);

    KnowledgeAsset surrogate = getSurrogate(carrier);

    semRepo.publish(surrogate, carrier);

    return semRepo;
  }

  private KnowledgeAsset getSurrogate(KnowledgeCarrier carrier) {
    switch (carrier.getRepresentation().getLanguage().asEnum()) {
      case DMN_1_1:
        return dmnMetadataExtractor
            .introspect(DMN1_1_EXTRACTOR, carrier, null)
            .flatOpt(kc -> kc.as(KnowledgeAsset.class))
            .orElseThrow(UnsupportedOperationException::new);

      case HL7_CQL:
        return cqlMetadataExtractor
            .introspect(CQL_1_3_EXTRACTOR, carrier, null)
            .flatOpt(kc -> kc.as(KnowledgeAsset.class))
            .orElseThrow(UnsupportedOperationException::new);
      default:
        fail("Not supported");
        throw new UnsupportedOperationException();
    }
  }

  private InputStream getBytes(String path) {
    return InferenceBaseTest.class.getResourceAsStream(path);
  }


  protected DMNRuntime initRuntime(String modelPath) {
    UUID id = UUID.randomUUID();

    SyntacticRepresentation rep = getRepresentation(modelPath);
    KnowledgeAssetRepositoryService semRepo =
        initMockRepo(id, VTAG, modelPath, rep);

    return KieDMNHelper
        .initRuntime(new KnowledgeBase().withManifestation(
            semRepo.getCanonicalKnowledgeAssetCarrier(id, VTAG).get()));
  }

}
