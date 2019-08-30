package edu.mayo.mea3.inference.mockTerms;


import static edu.mayo.kmdp.id.helper.DatatypeHelper.resolveTerm;

import edu.mayo.kmdp.id.Identifier;
import edu.mayo.kmdp.id.Term;
import edu.mayo.kmdp.util.Util;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PCO implements Term {

  PriorTEE(
      "b7cdb1b3-8e96-419a-ac08-01d9b38de3e8",
      "PriorTEE",
      "Prior Transesophageal Echocardiogram"
  ),

  History_Of_Arterial_Thromboembolism(
      "f352e499-7198-442c-856d-1ead0c0af11d",
      "HistoryOfArterialThromboembolism",
      "History of Arterial Thromboembolism"
  ),

  Current_Caffeine_User(
      "CurrentCaffeineUser",
      "Current Caffeine User"
  ),

  Current_Chronological_Age(
      "01aaaf22-ca7f-42d0-a1a2-f027fbf81fa6",
      "CurrentChronologicalAge",
      "Current Chronological Age"
  ),

  Current_Smoker_Type(
      "a8663b10-d01d-4f79-8c8e-42a864f409e1",
      "CurrentSmokerType",
      "Current Smoker Type"
  ),
  Current_Smoking_Status(
      "cb84baf3-f45b-4ac2-bd4d-64eab2a4fbf0",
      "CurrentSmokingStatus",
      "Current Smoking Status");



  public static final Map<UUID,PCO> INDEX = Arrays.stream(PCO.values())
      .collect(Collectors.toConcurrentMap(Term::getConceptUUID, Function.identity()));

  private UUID uuid;
  private String tag;
  private String label;

  PCO(final String code,
      final String displayName) {
    this(Util.uuid(code).toString(),code,displayName);
  }

  PCO(final String conceptUUID,
      final String code,
      final String displayName) {
    this.uuid = UUID.fromString(conceptUUID);
    this.tag = code;
    this.label = displayName;
  }


  public static Optional<PCO> resolve(final String tag) {
    return resolveTag(tag);
  }

  public static Optional<PCO> resolveTag(final String tag) {
    return resolveTerm(tag, PCO.values(), Term::getTag);
  }

  public static Optional<PCO> resolveUUID(final UUID conceptId) {
    return Optional.ofNullable(INDEX.get(conceptId));
  }



  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String getTag() {
    return tag;
  }

  @Override
  public URI getRef() {
    return null;
  }

  @Override
  public URI getConceptId() {
    return null;
  }

  @Override
  public Identifier getNamespace() {
    return null;
  }

}
