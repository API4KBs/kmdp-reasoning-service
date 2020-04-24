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
import org.omg.spec.api4kp._1_0.id.Pointer;
import org.omg.spec.api4kp._1_0.id.ResourceIdentifier;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;

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
  public Answer<KnowledgeCarrier> getCanonicalKnowledgeAssetCarrier(UUID assetId, String versionTag,
      String xAccept) {
    return Answer.of(carriers.get(getKey(assetId,versionTag)));
  }


  @Override
  public Answer<KnowledgeAsset> getKnowledgeAssetVersion(UUID assetId, String versionTag) {
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

 
}
