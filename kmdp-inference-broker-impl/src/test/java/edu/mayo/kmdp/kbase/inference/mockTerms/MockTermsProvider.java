package edu.mayo.kmdp.kbase.inference.mockTerms;

import edu.mayo.kmdp.terms.impl.model.ConceptDescriptor;
import edu.mayo.kmdp.terms.v4.server.TermsApiInternal;
import java.util.List;
import java.util.UUID;
import javax.inject.Named;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.id.Pointer;
import org.omg.spec.api4kp._1_0.services.KPServer;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;

@KPServer
@Named
public class MockTermsProvider implements TermsApiInternal {

  @Override
  public Answer<ConceptDescriptor> getTerm(UUID uuid, String s, String s1) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<ConceptDescriptor>> getTerms(UUID uuid, String s, String s1) {
    return Answer.unsupported();
  }

  @Override
  public Answer<KnowledgeCarrier> getVocabulary(UUID uuid, String s, String s1) {
    return Answer.unsupported();
  }

  @Override
  public Answer<Boolean> isAncestor(UUID vocabularyId, String versionTag, String conceptId, String testConceptId) {
    return Answer.unsupported();
  }

  @Override
  public Answer<Void> isMember(UUID uuid, String s, String s1) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<ConceptDescriptor>> listAncestors(UUID vocabularyId, String versionTag, String conceptId) {
    return Answer.unsupported();
  }

  @Override
  public Answer<List<Pointer>> listTerminologies() {
    return Answer.unsupported();
  }

}
