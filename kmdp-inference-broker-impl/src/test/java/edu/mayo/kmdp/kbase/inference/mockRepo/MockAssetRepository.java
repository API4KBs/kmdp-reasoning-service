package edu.mayo.kmdp.kbase.inference.mockRepo;

import edu.mayo.kmdp.metadata.v2.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetCatalogApiInternal;
import edu.mayo.kmdp.repository.asset.v4.server.KnowledgeAssetRepositoryApiInternal;
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
    KnowledgeAssetCatalogApiInternal {

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
    return Answer.of(carriers.get(getKey(assetId,versionTag)));
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
  public Answer<KnowledgeAsset> getKnowledgeAssetVersion(UUID assetId, String versionTag) {
    return Answer.of(surrogates.get(getKey(assetId,versionTag)));
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeGraph(String xAccept) {
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
  public Answer<KnowledgeCarrier> getKnowledgeAssetCarrierVersion(UUID assetId, String versionTag,
      UUID artifactId, String artifactVersionTag) {
    return Answer.of(carriers.get(getKey(assetId,versionTag)));
  }

  @Override
  public Answer<KnowledgeCarrier> getKnowledgeAssetSurrogateVersion(UUID assetId, String versionTag,
      UUID surrogateId, String surrogateVersionTag, String xAccept) {
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
  public Answer<List<Pointer>> listKnowledgeAssets(String assetTypeTag, String assetAnnotationTag,
      String assetAnnotationConcept, Integer offset, Integer limit) {
    return Answer.of(surrogates.values().stream()
        .map(ax -> ax.getAssetId().toPointer())
        .collect(Collectors.toList()));
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

  private String getKey(UUID uuid, String versionTag) {
    return uuid + ":" + versionTag;
  }


  /********************************************************************************************/

 
}
