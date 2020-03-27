package edu.mayo.kmdp.kbase.inference.fhir3;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.mayo.kmdp.inference.v4.InferenceApi;
import edu.mayo.kmdp.inference.v4.server.InferenceApiInternal;
import edu.mayo.kmdp.inference.v4.server.Swagger2SpringBoot;
import edu.mayo.kmdp.kbase.inference.mockTerms.PCO;
import edu.mayo.kmdp.util.Util;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = Swagger2SpringBoot.class)
@ContextConfiguration(classes = BaseInferenceIntegrationTest.InferenceTestConfig.class)
public class DMNInferenceTest extends BaseInferenceIntegrationTest {

	private static final UUID 		modelId 			= Util.uuid("mockPredictor");

	String serverUrl;

	@BeforeEach
	void init() {
		serverUrl = "http://localhost:" + port;
	}

	@Test
	public void testDMNaaS() {
		InferenceApiInternal infService = InferenceApi.newInstance(serverUrl);

		Map<String, Type> map = new HashMap<>();
		map.put( PCO.Current_Caffeine_User.getTag(),
				new BooleanType().setValue( true ) );
		map.put( PCO.Current_Chronological_Age.getTag(),
				new IntegerType().setValue( 37 ) );

		java.util.Map out = infService.infer( modelId, VTAG, map )
				.orElse(emptyMap());

		validate(out);
	}


	private void validate(Map out) {
		assertTrue( out.get( PCO.Current_Caffeine_User.getTag() ) instanceof BooleanType );

		Object ans = out.get( PCO.Hodgkin_Lymphoma_5_Year_Survival_Rate.getTag() );
		assertNotNull( ans );
		assertTrue( ans instanceof Quantity );
		assertEquals( 42, ((Quantity) ans).getValue().intValue() );
	}

}
