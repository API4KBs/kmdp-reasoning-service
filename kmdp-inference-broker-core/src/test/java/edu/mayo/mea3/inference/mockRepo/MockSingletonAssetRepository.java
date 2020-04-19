package edu.mayo.mea3.inference.mockRepo;

import edu.mayo.kmdp.metadata.v2.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetCatalogApiInternal;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetRepositoryApiInternal;
import java.util.List;
import java.util.UUID;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.datatypes.Bindings;
import org.omg.spec.api4kp._1_0.id.Pointer;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.omg.spec.api4kp._1_0.services.repository.KnowledgeAssetCatalog;

public class MockSingletonAssetRepository implements KnowledgeAssetRepositoryApiInternal,
    KnowledgeAssetCatalogApiInternal {

  private UUID modelId;
  private String version;
  private KnowledgeCarrier carrier;
  private KnowledgeAsset surrogate;

  public MockSingletonAssetRepository(UUID modelId, String version,
      KnowledgeCarrier carrier, KnowledgeAsset surrogate) {
    this.carrier = carrier;
    this.modelId = modelId;
    this.version = version;
    this.surrogate = surrogate;
  }

  @Override
  public Answer<Void> addKnowledgeAssetCarrier(UUID assetId, String versionTag,
      KnowledgeCarrier assetCarrier) {
    return Answer.unsupported();
  }

  @Override
  public Answer<Void> addKnowledgeAssetSurrogate(UUID assetId, String versionTag,
      KnowledgeCarrier surrogateCarrier) {
    return Answer.unsupported();
  }

  @Override
  public Answer<Void> addKnowledgeAssetVersion(UUID assetId, String versionTag,
      KnowledgeCarrier surrogateCarrier) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getCanonicalKnowledgeAssetCarrier(UUID assetId, String versionTag,
      String xAccept) {
    if (assetId.equals(this.modelId) && versionTag.equals(this.version)) {
      return Answer.of(carrier);
    } else {
      return Answer.notFound();
    }
  }

  @Override
  public Answer<KnowledgeAsset> getKnowledgeAssetVersion(UUID assetId, String versionTag) {
    if (assetId.equals(this.modelId) && versionTag.equals(this.version)) {
      return Answer.of(surrogate);
    } else {
      return Answer.notFound();
    }
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeGraph(String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetCarrierVersion(UUID assetId, String versionTag,
      UUID artifactId, String artifactVersionTag) {
    if (assetId.equals(this.modelId) && versionTag.equals(this.version)) {
      return Answer.of(carrier);
    } else {
      return Answer.notFound();
    }
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetSurrogateVersion(UUID assetId, String versionTag,
      UUID surrogateId, String surrogateVersionTag, String xAccept) {
    return Answer.unsupported();
  }


  @Override
  public Answer<KnowledgeCarrier> getCanonicalKnowledgeAssetSurrogate(UUID assetId,
      String versionTag, String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getCompositeKnowledgeAssetCarrier(UUID assetId, String versionTag,
      Boolean flat, String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getCompositeKnowledgeAssetStructure(UUID assetId,
      String versionTag, String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getCompositeKnowledgeAssetSurrogate(UUID assetId,
      String versionTag, Boolean flat, String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetCarrier(UUID assetId, String versionTag,
      UUID artifactId, String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetCarrierVersion(UUID assetId, String versionTag,
      UUID artifactId, String artifactVersionTag, String xAccept) {
    return Answer.unsupported();
  }


  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetSurrogateVersion(UUID assetId, String versionTag,
      UUID artifactId, String artifactVersionTag) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<KnowledgeCarrier>> initCompositeKnowledgeAsset(UUID assetId, String versionTag,
      String assetRelationshipTag, Integer depth) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> listKnowledgeAssetCarrierVersions(UUID assetId, String versionTag,
      UUID artifactId) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> listKnowledgeAssetCarriers(UUID assetId, String versionTag,
      Integer offset, Integer limit, String beforeTag, String afterTag, String sort) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> listKnowledgeAssetSurrogateVersions(UUID assetId, String versionTag,
      UUID surrogateId, Integer offset, Integer limit, String beforeTag, String afterTag,
      String sort) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> listKnowledgeAssetSurrogates(UUID assetId, String versionTag,
      Integer offset, Integer limit, String beforeTag, String afterTag, String sort) {
    return Answer.unsupported();
  }


  @Override
  public Answer<Void> setKnowledgeAssetCarrierVersion(UUID assetId, String versionTag,
      UUID artifactId, String artifactVersionTag, byte[] exemplar) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeAsset> getKnowledgeAsset(UUID assetId, String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeAssetCatalog> getKnowledgeAssetCatalog() {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeAsset> getKnowledgeAssetVersion(UUID assetId, String versionTag,
      String xAccept) {
    return Answer.unsupported();
  }


  @Override
  public Answer<UUID> initKnowledgeAsset() {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> listKnowledgeAssetVersions(UUID assetId, Integer offset,
      Integer limit, String beforeTag, String afterTag, String sort) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> listKnowledgeAssets(String assetTypeTag, String assetAnnotationTag,
      String assetAnnotationConcept, Integer offset, Integer limit) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Bindings>> queryKnowledgeAssetGraph(KnowledgeCarrier graphQuery) {
    return Answer.unsupported();
  }

  @Override
  public Answer<Void> setKnowledgeAssetVersion(UUID assetId, String versionTag,
      KnowledgeAsset assetSurrogate) {
    return Answer.unsupported();
  }


}
