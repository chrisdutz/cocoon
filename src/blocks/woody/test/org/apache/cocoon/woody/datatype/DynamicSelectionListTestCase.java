/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cocoon.woody.datatype;

import java.io.Writer;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.avalon.excalibur.testcase.ExcaliburTestCase;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.WrapperServiceManager;
import org.apache.cocoon.woody.Constants;
import org.apache.cocoon.xml.dom.DOMBuilder;
import org.apache.excalibur.source.impl.ResourceSource;
import org.custommonkey.xmlunit.Diff;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test case for Woody's DynamicSelectionList datatype.
 * @version CVS $Id: DynamicSelectionListTestCase.java,v 1.6 2004/03/09 13:54:21 reinhard Exp $
 */
public class DynamicSelectionListTestCase extends ExcaliburTestCase {

    protected ServiceManager serviceManager;
    protected DatatypeManager datatypeManager;
    protected DocumentBuilder parser;

    /**
     * Construct a new test case.
     * @param name The test case's name.
     */
    public DynamicSelectionListTestCase(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        serviceManager = new WrapperServiceManager(manager); 
        datatypeManager = (DatatypeManager) serviceManager.lookup(DatatypeManager.ROLE);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        parser = factory.newDocumentBuilder();
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        if (datatypeManager != null) {
            serviceManager.release(datatypeManager);
        }
        super.tearDown();
    }
    
    /**
     * Test the generateSaxFragment method.
     * @throws MalformedURLException
     * @throws ParserConfigurationException
     */
    public void testGenerateSaxFragment() throws Exception {
        DOMBuilder dest = new DOMBuilder();
        ResourceSource source = 
            new ResourceSource("resource://org/apache/cocoon/woody/datatype/DynamicSelectionListTestCase.source.xml");
        Document sourceDoc = parser.parse(source.getInputStream());
        Element datatypeElement = (Element) sourceDoc.getElementsByTagNameNS(Constants.WD_NS, "convertor").item(0);
        Datatype datatype = datatypeManager.createDatatype(datatypeElement, false);
        DynamicSelectionList list = 
            new DynamicSelectionList(datatype, null, serviceManager);
        list.generateSaxFragment(dest, Locale.ENGLISH, source);
        ResourceSource expectedSource =
            new ResourceSource("resource://org/apache/cocoon/woody/datatype/DynamicSelectionListTestCase.dest.xml");
        Document expected = parser.parse(expectedSource.getInputStream());
        assertEqual("Test if output is what is expected",
                expected, dest.getDocument());
    }

    /**
     * Check is the source document is equal to the one produced by the method under test.
     * @param message A message to print in case of failure.
     * @param expected The expected (source) document.
     * @param actual The actual (output) document.
     */
    private void assertEqual(String message, Document expected, Document actual) {
        expected.getDocumentElement().normalize();
        actual.getDocumentElement().normalize();
        // DIRTY HACK WARNING: we add the "xmlns:wi" attribute reported
        // by DOM, as expected, but not generated by the method under test,
        // otherwise the comparison would fail. 
        actual.getDocumentElement().setAttribute(Constants.WI_PREFIX,
                Constants.WI_NS);
        Diff diff =  new Diff(expected, actual);
        assertTrue(message + ", " + diff.toString(), diff.similar());
    }

    /**
     * Print a document to a writer for debugging purposes.
     * @param document The document to print.
     * @param out The writer to write to.
     */
    public final void print(Document document, Writer out) {
        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            javax.xml.transform.Transformer serializer =
                factory.newTransformer();
            serializer.transform(
                new DOMSource(document),
                new StreamResult(out));
            out.write('\n');
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
