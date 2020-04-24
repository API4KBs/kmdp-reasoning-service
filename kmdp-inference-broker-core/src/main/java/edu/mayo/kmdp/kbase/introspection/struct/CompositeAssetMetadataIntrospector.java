package edu.mayo.kmdp.kbase.introspection.struct;

import static edu.mayo.kmdp.metadata.v2.surrogate.SurrogateBuilder.newSurrogate;
import static edu.mayo.ontology.taxonomies.api4kp.parsinglevel.ParsingLevelSeries.Parsed_Knowedge_Expression;
import static edu.mayo.ontology.taxonomies.kao.knowledgeassetrole.KnowledgeAssetRoleSeries.Composite_Knowledge_Asset;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.Knowledge_Asset_Surrogate_2_0;
import static edu.mayo.ontology.taxonomies.krlanguage.KnowledgeRepresentationLanguageSeries.OWL_2;
import static org.omg.spec.api4kp._1_0.AbstractCarrier.rep;

import edu.mayo.kmdp.inference.v4.server.IntrospectionApiInternal;
import edu.mayo.kmdp.language.parsers.owl2.JenaOwlParser;
import edu.mayo.kmdp.metadata.v2.surrogate.Component;
import edu.mayo.kmdp.metadata.v2.surrogate.ComputableKnowledgeArtifact;
import edu.mayo.kmdp.metadata.v2.surrogate.KnowledgeAsset;
import edu.mayo.kmdp.tranx.v4.server.DeserializeApiInternal._applyLift;
import edu.mayo.ontology.taxonomies.api4kp.knowledgeoperations.KnowledgeProcessingOperationSeries;
import edu.mayo.ontology.taxonomies.kao.rel.structuralreltype.StructuralPartTypeSeries;
import java.net.URI;
import java.util.UUID;
import javax.inject.Named;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.omg.spec.api4kp._1_0.AbstractCarrier;
import org.omg.spec.api4kp._1_0.Answer;
import org.omg.spec.api4kp._1_0.id.SemanticIdentifier;
import org.omg.spec.api4kp._1_0.services.CompositeKnowledgeCarrier;
import org.omg.spec.api4kp._1_0.services.KPComponent;
import org.omg.spec.api4kp._1_0.services.KPOperation;
import org.omg.spec.api4kp._1_0.services.KPSupport;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@KPOperation(KnowledgeProcessingOperationSeries.Extract_Description_Task)
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
    return parser.applyLift(ckc.getStruct(),Parsed_Knowedge_Expression,null,null)
        .flatOpt(kc -> kc.as(Model.class))
        .flatMap(model -> extractSurrogate(ckc, model));
  }


  private Answer<KnowledgeCarrier> extractSurrogate(
      CompositeKnowledgeCarrier carrier, Model graph) {

    KnowledgeCarrier struct = carrier.getStruct();

    KnowledgeAsset surrogate = newSurrogate(carrier.getAssetId()).get()
        .withName(carrier.getLabel())
        .withRole(Composite_Knowledge_Asset)
        .withCarriers(new ComputableKnowledgeArtifact()
            .withArtifactId(struct.getArtifactId())
            .withRepresentation(struct.getRepresentation())
        );

    graph.listStatements(
        ResourceFactory.createResource(carrier.getAssetId().getVersionId().toString()),
        null,
        (RDFNode) null)
        .forEachRemaining(st -> surrogate.withLinks(
            new Component()
                .withRel(StructuralPartTypeSeries.Has_Part)
                .withHref(SemanticIdentifier.newVersionId(
                    URI.create(st.getObject().asResource().getURI())))));

    return Answer.of(AbstractCarrier.ofAst(surrogate)
        .withAssetId(carrier.getAssetId())
        .withRepresentation(rep(Knowledge_Asset_Surrogate_2_0))
    );
  }
}


