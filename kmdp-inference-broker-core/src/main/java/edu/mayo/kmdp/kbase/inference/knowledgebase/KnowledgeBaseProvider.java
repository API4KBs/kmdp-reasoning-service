package edu.mayo.kmdp.kbase.inference.knowledgebase;

import static edu.mayo.kmdp.util.Util.uuid;

import edu.mayo.kmdp.id.VersionedIdentifier;
import edu.mayo.kmdp.id.helper.DatatypeHelper;
import edu.mayo.kmdp.knowledgebase.v3.server.KnowledgeBaseApiInternal;
import edu.mayo.kmdp.metadata.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.repository.asset.v3.server.KnowledgeAssetRepositoryApiInternal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Named;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.datatypes.Bindings;
import org.omg.spec.api4kp._1_0.identifiers.Pointer;
import org.omg.spec.api4kp._1_0.identifiers.VersionIdentifier;
import org.omg.spec.api4kp._1_0.services.KPServer;
import org.omg.spec.api4kp._1_0.services.KnowledgeBase;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.springframework.beans.factory.annotation.Autowired;

@KPServer
@Named
public class KnowledgeBaseProvider
    implements KnowledgeBaseApiInternal
// TODO extends KBManager {
{

  private KnowledgeAssetRepositoryApiInternal assetRepository;

  private Map<VersionedIdentifier, KnowledgeBase> knowledgeBaseMap = new HashMap<>();

  @Autowired
  public KnowledgeBaseProvider(
      @KPServer KnowledgeAssetRepositoryApiInternal assetRepository) {
    this.assetRepository = assetRepository;
  }

  @Override
  public Answer<Pointer> bind(UUID kbaseId, String versionTag, Bindings bindings) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeBase> getKnowledgeBase(UUID kbaseId, String versionTag) {
    VersionedIdentifier vid = new VersionIdentifier()
        .withTag(kbaseId.toString())
        .withVersion(versionTag);
    return Answer.of(knowledgeBaseMap.get(vid));
  }

  @Override
  public Answer<Pointer> initKnowledgeBase(KnowledgeAsset asset) {
    VersionedIdentifier assetId = DatatypeHelper.toVersionIdentifier(asset.getAssetId());
    KnowledgeBase kb = assetRepository.
        getCanonicalKnowledgeAssetCarrier(UUID.fromString(assetId.getTag()), assetId.getVersion())
        .map(kc -> new KnowledgeBase()
            .withManifestation(kc))
        .orElseThrow(IllegalArgumentException::new);
    knowledgeBaseMap.put(assetId,kb);
    return Answer.of(new Pointer()
        .withEntityRef(asset.getAssetId()));
  }

  @Override
  public Answer<Pointer> populateKnowledgeBase(UUID kbaseId, String versionTag,
      KnowledgeCarrier sourceArtifact) {
    return Answer.unsupported();
  }

}
