package edu.mayo.kmdp.kbase.inference.fhir3;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.mayo.kmdp.kbase.inference.mockTerms.PCO;
import edu.mayo.kmdp.util.Util;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.omg.spec.api4kp._20200801.Answer;
import org.omg.spec.api4kp._20200801.api.inference.v4.InferenceApi;
import org.omg.spec.api4kp._20200801.api.inference.v4.ModelApi;
import org.omg.spec.api4kp._20200801.api.inference.v4.server.InferenceApiInternal;
import org.omg.spec.api4kp._20200801.api.inference.v4.server.Swagger2SpringBoot;
import org.omg.spec.api4kp._20200801.id.Pointer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = Swagger2SpringBoot.class)
@ContextConfiguration(classes = BaseInferenceIntegrationTest.InferenceTestConfig.class)
public class CQLInferenceTest extends BaseInferenceIntegrationTest {

	private static final UUID 		modelId 			= Util.uuid("mockCQL");

	String serverUrl;

	@BeforeEach
	void init() {
		serverUrl = "http://localhost:" + port;
	}

	@Test
	@Disabled("Current CQL engine does not support HAPI FHIR 4.x")
	public void testCQLaaS() {
		InferenceApiInternal infService = InferenceApi.newInstance(serverUrl);

		Map<String, Base> inputs = new HashMap<>();
		inputs.put( PCO.Current_Smoker_Type.getTag(), getSmokerType());

		Map out = infService.infer(modelId, VTAG, inputs )
				.orElse(emptyMap());

		validateOutputs(out);
	}

	@Test
	public void testInitialization() {
		ModelApi client = ModelApi.newInstance(serverUrl);

		Answer<List<Pointer>> availableModels = client.listModels();

		assertTrue(availableModels.isSuccess());
		List<Pointer> modelRefs = availableModels.orElse(Collections.emptyList());
		modelRefs
				.forEach(ptr -> {
					System.out.println(ptr.getName());
					System.out.println(ptr.getHref());
				});
	}


	private Base getSmokerType() {
		return new Observation().setCode(new CodeableConcept()
				.setCoding(Collections.singletonList(new Coding().setCode("loinc-something"))))
				.setValue(new CodeableConcept()
						.setCoding(Collections.singletonList(new Coding().setCode("smoker"))));
	}


	private void validateOutputs(Map out) {
		assertNotNull( out );

		assertTrue( out.containsKey( PCO.Current_Smoking_Status.getTag() ) );
		Object o1 = out.get( PCO.Current_Smoking_Status.getTag() );
		assertTrue( o1 instanceof Observation );

		assertTrue( out.containsKey( PCO.Current_Smoker_Type.getTag() ) );
		Object o2 = out.get( PCO.Current_Smoker_Type.getTag() );
		assertTrue( o2 instanceof Coding );
	}



}
