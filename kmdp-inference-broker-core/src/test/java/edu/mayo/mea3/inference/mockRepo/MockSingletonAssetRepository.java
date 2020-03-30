package edu.mayo.mea3.inference.mockRepo;

import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetCatalogApiInternal;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetRepositoryApiInternal;
import java.util.List;
import java.util.UUID;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.id.Pointer;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.omg.spec.api4kp._1_0.services.repository.KnowledgeAssetCatalog;

public class MockSingletonAssetRepository implements KnowledgeAssetRepositoryApiInternal,
    KnowledgeAssetCatalogApiInternal {

  private UUID modelId;
  private String version;
  private KnowledgeCarrier carrier;

  public MockSingletonAssetRepository(UUID modelId, String version, KnowledgeCarrier carrier) {
    this.carrier = carrier;
    this.modelId = modelId;
    this.version = version;
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
  public Answer<Void> addKnowledgeAssetCarrier(UUID assetId, String versionTag, byte[] exemplar) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getCanonicalKnowledgeAssetSurrogate(UUID assetId,
      String versionTag, String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetCarrierVersion(UUID assetId, String versionTag,
      UUID artifactId, String artifactVersionTag) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> getKnowledgeAssetCarriers(UUID assetId, String versionTag) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetSurrogateVersion(UUID assetId, String versionTag,
      String surrogateVersionTag, String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> getKnowledgeAssetSurrogateVersions(UUID assetId, String versionTag) {
    return Answer.unsupported();
  }

  @Override
  public Answer<Void> setKnowledgeAssetCarrierVersion(UUID assetId, String versionTag,
      UUID artifactId, String artifactVersionTag, byte[] exemplar) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeAssetCatalog> getAssetCatalog() {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeAsset> getKnowledgeAsset(UUID assetId, String xAccept) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> getKnowledgeAssetVersions(UUID assetId, Integer offset,
      Integer limit, String beforeTag, String afterTag, String sort) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeAsset> getVersionedKnowledgeAsset(UUID assetId, String versionTag) {
    return Answer.unsupported();
  }

  @Override
  public Answer<UUID> initKnowledgeAsset() {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> listKnowledgeAssets(String assetTypeTag, String assetAnnotationTag,
      String assetAnnotationConcept, Integer offset, Integer limit) {
    return Answer.unsupported();
  }

  @Override
  public Answer<Void> setVersionedKnowledgeAsset(UUID assetId, String versionTag,
      KnowledgeAsset assetSurrogate) {
    return Answer.unsupported();
  }
}
