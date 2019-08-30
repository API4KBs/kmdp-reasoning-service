/**
 * Copyright © 2018 Mayo Clinic (RSTKNOWLEDGEMGMT@mayo.edu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.mea3.inference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.mayo.kmdp.kbase.inference.InferenceBroker;
import edu.mayo.kmdp.kbase.inference.cql.CQLEngineProvider;
import edu.mayo.kmdp.kbase.inference.dmn.DMNEngineProvider;
import edu.mayo.kmdp.kbase.inference.knowledgebase.KnowledgeBaseProvider;
import edu.mayo.kmdp.knowledgebase.v3.server.KnowledgeBaseApiInternal;
import edu.mayo.kmdp.repository.asset.KnowledgeAssetRepositoryService;
import edu.mayo.kmdp.util.DateTimeUtil;
import edu.mayo.mea3.inference.mockTerms.PCO;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.BaseResource;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Quantity;
import org.junit.jupiter.api.Test;

public class CQLTest extends InferenceBaseTest {

	static String PATIENT = "Patient";

	InferenceBroker initServer(KnowledgeAssetRepositoryService semRepo) {
		KnowledgeBaseApiInternal kbp = new KnowledgeBaseProvider(semRepo);
		return new InferenceBroker(
				semRepo,
				new HashSet<>(Arrays.asList(new DMNEngineProvider(kbp), new CQLEngineProvider(kbp))));
	}

	@Test
	public void testEngine() {
		UUID id = UUID.randomUUID();

		KnowledgeAssetRepositoryService semRepo =
				initMockRepo(id, VTAG, "/helloworld.cql");

		InferenceBroker server = initServer(semRepo);

		java.util.Map<?,?> out = server.infer(id, VTAG, new HashMap<>())
				.orElse(Collections.emptyMap());


		assertEquals(1, out.size());
		assertEquals(3, out.get("Number3"));
	}



	@Test
	public void testEngineWithFHIR() {
		UUID id = UUID.randomUUID();

		KnowledgeAssetRepositoryService semRepo =
				initMockRepo(id, VTAG, "/testFHIR.cql");

		InferenceBroker server = initServer(semRepo);

		Map<String, BaseResource> inputs = new HashMap<>();
		inputs.put(PATIENT, new Patient().setGender(AdministrativeGender.OTHER)
				.setName(Collections.singletonList(new HumanName().addGiven("Doe"))));

		inputs.put("Observation", new Observation().setCode(
				new CodeableConcept().setCoding(Collections.singletonList(new Coding().setCode("foo"))))
				.setValue(new Quantity().setValue(42)
						.setUnit("brapples")));

		java.util.Map<?,?> out =
				server.infer(id, VTAG, inputs).orElse(Collections.emptyMap());

		assertEquals(3, out.size());
		assertSame(inputs.get(PATIENT), out.get(PATIENT));
		assertSame(inputs.get("Observation"), out.get("theObs"));
		Object outQ = out.get("theVal");
		assertTrue(outQ instanceof Quantity);
		assertEquals(42, ((Quantity) outQ).getValue().intValue());
	}


	@Test
	public void testEngineWithPCO() {
		UUID id = UUID.randomUUID();

		KnowledgeAssetRepositoryService semRepo =
				initMockRepo(id, VTAG, "/testFHIRPCO.cql");

		InferenceBroker server = initServer(semRepo);

		Map<String, Base> inputs = new HashMap<>();

		inputs.put(PATIENT, new Patient().setGender(AdministrativeGender.OTHER)
				.setName(Collections.singletonList(new HumanName().addGiven("Doe"))));

		inputs.put(PCO.Current_Smoking_Status.getTag(),
				new Observation().setCode(new CodeableConcept()
						.setCoding(Collections.singletonList(new Coding().setCode("loinc-something"))))
						.setValue(new CodeableConcept().setCoding(Collections
								.singletonList(new Coding().setCode("smoker").setSystem("http://foo.bar#")))));

		java.util.Map<?, ?> out = server.infer(id, VTAG, inputs)
				.orElse(Collections.emptyMap());

		assertEquals(5, out.size());
		assertEquals(true, out.get("Answer"));
		assertSame(inputs.get(PATIENT), out.get(PATIENT));

		Object x = out.get(PCO.Current_Smoker_Type.getTag());
		assertTrue(x instanceof CodeableConcept);
		assertEquals("smoker", ((CodeableConcept) x).getCodingFirstRep().getCode());

	}

	@Test
	public void testEngineWithAgeBuiltIn() {
		UUID id = UUID.randomUUID();

		KnowledgeAssetRepositoryService semRepo =
				initMockRepo(id, VTAG, "/testAge.cql");

		InferenceBroker server = initServer(semRepo);

		Map<String, Base> inputs = new HashMap<>();
		inputs.put(PATIENT, new Patient()
				.setGender(AdministrativeGender.OTHER)
				.setBirthDate( DateTimeUtil.parseDate("1981-01-12")));

		java.util.Map<?, ?> out =
				server.infer(id, VTAG, inputs).orElse(Collections.emptyMap());

		Object x = out.get(PCO.Current_Chronological_Age.getTag());
		assertTrue(x instanceof Quantity);

		int estimatedAge = Year.now().getValue() - 1981;
		int delta = Math.abs( ((Quantity) x).getValue().intValue() - estimatedAge );
		assertTrue( delta <= 1  );
	}



	@Test
	public void testTEEInference() {
		UUID id = UUID.randomUUID();

		KnowledgeAssetRepositoryService semRepo =
				initMockRepo(id, VTAG, "/testTEE.cql");

		InferenceBroker server = initServer(semRepo);

		Map<String, Base> inputs = new HashMap<>();

		inputs.put(PATIENT, new Patient());
		inputs.put(PCO.PriorTEE.getTag(),
				new DiagnosticReport().addCodedDiagnosis(
						new CodeableConcept()
								.addCoding(new Coding()
										.setSystem("http://snomed.info/sct")
										.setCode("266262004"))));

		java.util.Map<?, ?> out = server.infer(id, VTAG, inputs)
				.orElse(Collections.emptyMap());

		Object x = out.get(PCO.History_Of_Arterial_Thromboembolism.getTag());
		assertTrue(x instanceof BooleanType);
		assertTrue(((BooleanType) x).getValue() );

	}
}

