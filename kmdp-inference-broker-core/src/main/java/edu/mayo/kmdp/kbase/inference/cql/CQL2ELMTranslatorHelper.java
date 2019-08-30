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
package edu.mayo.kmdp.kbase.inference.cql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import javax.xml.bind.JAXBException;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlTranslatorException;
import org.cqframework.cql.cql2elm.FhirLibrarySourceProvider;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.Library;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumService;
import org.omg.spec.api4kp._1_0.services.BinaryCarrier;
import org.omg.spec.api4kp._1_0.services.KnowledgeCarrier;
import org.opencds.cqf.cql.execution.CqlLibraryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CQL2ELMTranslatorHelper {

  private static final Logger logger = LoggerFactory.getLogger(CQL2ELMTranslatorHelper.class);

  private LibraryManager libraryManager;
  private ModelManager modelManager;
  private UcumService ucumService;

  public CQL2ELMTranslatorHelper() {
    try {
      modelManager = new ModelManager();
      libraryManager = new LibraryManager(modelManager);
      libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
      ucumService = new UcumEssenceService(
          UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  // MASSIVE TODO: Plug the Translator service in here
  public Optional<KnowledgeCarrier> doTranslate(BinaryCarrier input) {
    try {
      CqlTranslator translator = CqlTranslator
          .fromStream(new ByteArrayInputStream(input.getEncodedExpression()),
              modelManager,
              libraryManager,
              ucumService);
      translator.getErrors().stream()
          .map(CqlTranslatorException::getMessage).forEach(logger::error);
      translator.getExceptions().stream()
          .map(CqlTranslatorException::getMessage).forEach(logger::error);

      translator.getMessages().stream()
          .map(CqlTranslatorException::getMessage).forEach(logger::error);

      String xml = translator.toXml();
      return Optional.of(new BinaryCarrier().withEncodedExpression(xml.getBytes()));
    } catch (IOException e) {
      System.err.println(e.getMessage());
      logger.error(e.getMessage(), e);
    }
    return Optional.empty();
  }

  public Optional<Library> cqlToExecutableLibrary(BinaryCarrier binaryCarrier) {
    return doTranslate(binaryCarrier)
        .map(BinaryCarrier.class::cast)
        .flatMap(this::parse);
  }

  private Optional<org.cqframework.cql.elm.execution.Library> parse(BinaryCarrier elm) {
    try {
      return Optional
          .of(CqlLibraryReader.read(new ByteArrayInputStream(elm.getEncodedExpression())));
    } catch (IOException | JAXBException e) {
      logger.error(e.getMessage(), e);
    }
    return Optional.empty();
  }
}
