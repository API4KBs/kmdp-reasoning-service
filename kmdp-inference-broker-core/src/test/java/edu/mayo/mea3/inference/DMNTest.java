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

import static edu.mayo.kmdp.util.NameUtils.camelCase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import edu.mayo.kmdp.kbase.inference.dmn.KieDMNHelper;
import edu.mayo.kmdp.repository.asset.KnowledgeAssetRepositoryService;
import edu.mayo.mea3.inference.mockTerms.PCO;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.Type;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.omg.spec.api4kp._1_0.datatypes.Map;
import org.omg.spec.api4kp._1_0.services.BinaryCarrier;
import org.omg.spec.api4kp._1_0.services.KnowledgeBase;

public class DMNTest extends InferenceBaseTest {

	static IParser JSON = FhirContext.forDstu3().newJsonParser();

	@Test
	public void testEngine() {
		DMNRuntime runtime = initRuntime("/Scorecipe.dmn");
		DMNModel model = runtime.getModels().get(0);

		DMNContext ctx = runtime.newContext();
		ctx.set( "spaghetti", new Quantity().setCode( "Kg" ).setValue( 1 ).setCode( "kg" ) );
		ctx.set( "tagliatelle", new Foo() );
		ctx.set( "linguine", "aaa" );

		ctx = runtime.evaluateAll( model, ctx ).getContext();
		ctx.getAll().forEach( ( k, v ) ->
				System.out.println( k + " \t :: \t " + toString( v ) ) );

		assertEquals( "400", toString( ctx.get( "calories" ) ) );
		assertEquals( "0.42", toString( ctx.get( "sauce" ) ) );
	}


	@Test
	@SuppressWarnings("unchecked")
	public void testEngineComplexOutput() {
		UUID id = UUID.randomUUID();

		KnowledgeAssetRepositoryService semRepo =
				initMockRepo(id, VTAG, "/MockPredictor.dmn");

		DMNRuntime runtime = KieDMNHelper
				.initRuntime(new KnowledgeBase().withManifestation(
						semRepo.getCanonicalKnowledgeAssetCarrier(id, VTAG).get()));
		DMNModel model = runtime.getModels().get(0);

		DMNContext ctx = runtime.newContext();
		ctx.set( camelCase( PCO.Current_Caffeine_User.getLabel() ), new BooleanType().setValue( true ) );
		ctx.set( camelCase( PCO.Current_Chronological_Age.getLabel() ), new IntegerType().setValue( 37 ) );
		ctx = runtime.evaluateAll( model, ctx ).getContext();

		assertTrue( ctx.get( "riskOfHeartFailure" ) instanceof java.util.Map );

		java.util.Map<String,Object> obj =
				( java.util.Map<String, Object> ) ctx.get( "riskOfHeartFailure" );
		assertEquals( "mock", obj.get( "newElement" ) );
		assertEquals( BigDecimal.valueOf( 42 ), obj.get( "newElement2" ) );
		assertEquals( "%", obj.get( "newElement3" ) );
	}



	public static String toString( Object v ) {
		if ( v instanceof Type ) {
			return JSON.encodeResourceToString( new Observation().setValue( ( Type ) v ) );
		}
		if ( v instanceof Map ) {
			return ( ( Map ) v ).keySet().stream()
					.collect( Collectors.toMap( k -> k, DMNTest::toString) ).toString();
		}
		return v != null ? v.toString() : null;
	}

	public static class Foo {

		public Quantity amount = new Quantity()
				.setUnit("Kg")
				.setValue(44)
				.setCode("kg")
				.setSystem("ucum");

		public Quantity getAmount() {
			return amount;
		}

		public void setAmount(Quantity amount) {
			this.amount = amount;
		}

		@Override
		public String toString() {
			return "Foo{" +
					"amount=" + DMNTest.toString( amount )+
					'}';
		}
	}

}

