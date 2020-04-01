package edu.mayo.kmdp.kbase.inference.mockRepo;

import edu.mayo.kmdp.metadata.v2.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetCatalogApiInternal;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetRepositoryApiInternal;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetRetrievalApiInternal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.datatypes.Bindings;
import org.omg.spec.api4kp._1_0.id.Pointer;
import org.omg.spec.api4kp._1_0.id.ResourceIdentifier;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.omg.spec.api4kp._1_0.services.repository.KnowledgeAssetCatalog;

public class MockAssetRepository implements KnowledgeAssetRepositoryApiInternal,
    KnowledgeAssetCatalogApiInternal, KnowledgeAssetRetrievalApiInternal {

  private Map<String,KnowledgeCarrier> carriers = new HashMap<>();
  private Map<String, KnowledgeAsset> surrogates = new HashMap<>();

  public MockAssetRepository(List<KnowledgeAsset> surrogates, List<KnowledgeCarrier> carriers) {
    surrogates.forEach(s -> {
      ResourceIdentifier rid = s.getAssetId();
      String key = getKey(rid.getUuid(),rid.getVersionTag());
      this.surrogates.put(key, s);
    });

    carriers.forEach(c -> {
      String key = getKey(c.getAssetId().getUuid(),c.getAssetId().getVersionTag());
      this.carriers.put(key, c);
    });
  }

  @Override
  public Answer<KnowledgeCarrier> getCanonicalKnowledgeAssetCarrier(UUID assetId, String versionTag,
      String xAccept) {
    return Answer.of(carriers.get(getKey(assetId,versionTag)));
  }

  @Override
  public Answer<KnowledgeAsset> getVersionedKnowledgeAsset(UUID assetId, String versionTag) {
    return Answer.of(surrogates.get(getKey(assetId,versionTag)));
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetCarrierVersion(UUID assetId, String versionTag,
      UUID artifactId, String artifactVersionTag) {
    return Answer.of(carriers.get(getKey(assetId,versionTag)));
  }

  @Override
  public Answer<List<Pointer>> listKnowledgeAssets(String assetTypeTag, String assetAnnotationTag,
      String assetAnnotationConcept, Integer offset, Integer limit) {
    return Answer.of(surrogates.values().stream()
        .map(ax -> ax.getAssetId().toPointer())
        .collect(Collectors.toList()));
  }

  private String getKey(UUID uuid, String versionTag) {
    return uuid + ":" + versionTag;
  }


  /********************************************************************************************/

  @Override
  public Answer<Void> addKnowledgeAssetCarrier(UUID assetId, String versionTag, byte[] exemplar) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<KnowledgeCarrier> getCanonicalKnowledgeAssetSurrogate(UUID assetId,
      String versionTag, String xAccept) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<List<Pointer>> getKnowledgeAssetCarriers(UUID assetId, String versionTag) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetSurrogateVersion(UUID assetId, String versionTag,
      String surrogateVersionTag, String xAccept) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<List<Pointer>> getKnowledgeAssetSurrogateVersions(UUID assetId, String versionTag) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<Void> setKnowledgeAssetCarrierVersion(UUID assetId, String versionTag,
      UUID artifactId, String artifactVersionTag, byte[] exemplar) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<KnowledgeAssetCatalog> getAssetCatalog() {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<KnowledgeAsset> getKnowledgeAsset(UUID assetId, String xAccept) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<List<Pointer>> getKnowledgeAssetVersions(UUID assetId, Integer offset,
      Integer limit, String beforeTag, String afterTag, String sort) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<UUID> initKnowledgeAsset() {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<Void> setVersionedKnowledgeAsset(UUID assetId, String versionTag,
      KnowledgeAsset assetSurrogate) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<List<KnowledgeCarrier>> getCompositeKnowledgeAsset(UUID assetId, String versionTag,
      Boolean flat, String xAccept) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<KnowledgeCarrier> getCompositeKnowledgeAssetStructure(UUID assetId,
      String versionTag) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<List<KnowledgeCarrier>> getKnowledgeArtifactBundle(UUID assetId, String versionTag,
      String assetRelationshipTag, Integer depth, String xAccept) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<List<KnowledgeAsset>> getKnowledgeAssetBundle(UUID assetId, String versionTag,
      String assetRelationshipTag, Integer depth) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }

  @Override
  public Answer<List<Bindings>> queryKnowledgeAssets(KnowledgeCarrier graphQuery) {
    throw new UnsupportedOperationException("Test should not have invoked this");
  }
}
