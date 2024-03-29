package edu.mayo.kmdp.kbase.inference.fhir3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.mayo.kmdp.kbase.inference.mockTerms.PCO;
import edu.mayo.kmdp.util.Util;
import java.util.UUID;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Quantity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.spec.api4kp._20200801.Answer;
import org.omg.spec.api4kp._20200801.api.inference.v4.ReasoningApi;
import org.omg.spec.api4kp._20200801.api.inference.v4.server.Swagger2SpringBoot;
import org.omg.spec.api4kp._20200801.datatypes.Bindings;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = Swagger2SpringBoot.class)
@ContextConfiguration(classes = BaseInferenceIntegrationTest.InferenceTestConfig.class)
public class DMNInferenceTest extends BaseInferenceIntegrationTest {

  private static final UUID modelId = Util.uuid("mockPredictor");

  String serverUrl;

  @BeforeEach
  void init() {
    serverUrl = "http://localhost:" + port;
  }

  @Test
  void testDMNaaS() {
    ReasoningApi infService = ReasoningApi.newInstance(serverUrl);

    Bindings<String, Base> map = new Bindings<>();
    map.put(PCO.Current_Caffeine_User.getTag(),
        new BooleanType().setValue(true));
    map.put(PCO.Current_Chronological_Age.getTag(),
        new IntegerType().setValue(37));

    Answer<Bindings> out = infService.evaluate(modelId, VTAG, map, null);

    validate(out.orElseGet(Assertions::fail));
  }


  private void validate(Bindings<?, ?> out) {
    assertTrue(out.get(PCO.Current_Caffeine_User.getTag()) instanceof BooleanType);

    Object ans = out.get(PCO.Hodgkin_Lymphoma_5_Year_Survival_Rate.getTag());
    assertNotNull(ans);
    assertTrue(ans instanceof Quantity);
    assertEquals(42, ((Quantity) ans).getValue().intValue());
  }

}
