package edu.mayo.kmdp.kbase.introspection.struct;

import static org.omg.spec.api4kp._20200801.AbstractCarrier.rep;
import static org.omg.spec.api4kp._20200801.surrogate.SurrogateBuilder.newSurrogate;
import static org.omg.spec.api4kp.taxonomy.knowledgeassetrole._20190801.KnowledgeAssetRole.Composite_Knowledge_Asset;
import static org.omg.spec.api4kp.taxonomy.knowledgeoperation.KnowledgeProcessingOperationSeries.Description_Task;
import static org.omg.spec.api4kp.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate_2_0;
import static org.omg.spec.api4kp.taxonomy.krlanguage.KnowledgeRepresentationLanguageSeries.OWL_2;
import static org.omg.spec.api4kp.taxonomy.parsinglevel.ParsingLevelSeries.Concrete_Knowledge_Expression;
import static org.omg.spec.api4kp.taxonomy.structuralreltype.StructuralPartTypeSeries.Has_Structural_Component;

import edu.mayo.kmdp.language.parsers.owl2.JenaOwlParser;
import java.net.URI;
import java.util.UUID;
import javax.inject.Named;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.omg.spec.api4kp._20200801.AbstractCarrier;
import org.omg.spec.api4kp._20200801.Answer;
import org.omg.spec.api4kp._20200801.api.inference.v4.server.IntrospectionApiInternal;
import org.omg.spec.api4kp._20200801.api.transrepresentation.v4.server.DeserializeApiInternal._applyLift;
import org.omg.spec.api4kp._20200801.id.SemanticIdentifier;
import org.omg.spec.api4kp._20200801.services.CompositeKnowledgeCarrier;
import org.omg.spec.api4kp._20200801.services.KPComponent;
import org.omg.spec.api4kp._20200801.services.KPOperation;
import org.omg.spec.api4kp._20200801.services.KPSupport;
import org.omg.spec.api4kp._20200801.services.KnowledgeCarrier;
import org.omg.spec.api4kp._20200801.surrogate.Component;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeArtifact;
import org.omg.spec.api4kp._20200801.surrogate.KnowledgeAsset;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@KPOperation(Description_Task)
@KPSupport(OWL_2)
@KPComponent
public class CompositeAssetMetadataIntrospector
    implements IntrospectionApiInternal._introspect {

  public static final UUID operatorId
      = UUID.fromString("d31acad8-a385-4acf-b30b-1dbb3342b8b4");

  @Autowired
  @KPSupport(OWL_2)
  _applyLift parser = new JenaOwlParser();

  @Override
  public Answer<KnowledgeCarrier> introspect(
      UUID lambdaId,
      KnowledgeCarrier sourceArtifact,
      String xAccept) {
    if (! (sourceArtifact instanceof CompositeKnowledgeCarrier)) {
      return Answer.unsupported();
    }
    CompositeKnowledgeCarrier ckc = (CompositeKnowledgeCarrier) sourceArtifact;
    return parser.applyLift(ckc.getStruct(), Concrete_Knowledge_Expression,null,null)
        .flatOpt(kc -> kc.as(Model.class))
        .flatMap(model -> extractSurrogate(ckc, model));
  }


  private Answer<KnowledgeCarrier> extractSurrogate(
      CompositeKnowledgeCarrier carrier, Model graph) {

    KnowledgeCarrier struct = carrier.getStruct();

    KnowledgeAsset surrogate = newSurrogate(carrier.getAssetId()).get()
        .withName(carrier.getLabel())
        .withRole(Composite_Knowledge_Asset)
        .withCarriers(new KnowledgeArtifact()
            .withArtifactId(struct.getArtifactId())
            .withRepresentation(struct.getRepresentation())
        );

    graph.listStatements(
        ResourceFactory.createResource(carrier.getAssetId().getVersionId().toString()),
        null,
        (RDFNode) null)
        .forEachRemaining(st -> surrogate.withLinks(
            new Component()
                .withRel(Has_Structural_Component)
                .withHref(SemanticIdentifier.newVersionId(
                    URI.create(st.getObject().asResource().getURI())))));

    return Answer.of(AbstractCarrier.ofAst(surrogate)
        .withAssetId(carrier.getAssetId())
        .withRepresentation(rep(Knowledge_Asset_Surrogate_2_0))
    );
  }
}


