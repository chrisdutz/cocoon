/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.cocoon.woody.datatype;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.Source;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.xml.AttributesImpl;
import org.apache.cocoon.xml.AbstractXMLPipe;
import org.apache.cocoon.woody.Constants;

import java.util.Locale;

/**
 * SelectionList implementation that always reads its content from the source
 * each time it is requested.
 */
public class DynamicSelectionList implements SelectionList {
    private String src;
    private Datatype datatype;
    private ComponentManager componentManager;

    public DynamicSelectionList(Datatype datatype, String src, ComponentManager componentManager) {
        this.datatype = datatype;
        this.src = src;
        this.componentManager = componentManager;
    }

    public Datatype getDatatype() {
        return datatype;
    }

    public void generateSaxFragment(ContentHandler contentHandler, Locale locale) throws SAXException {
        SourceResolver sourceResolver = null;
        Source source = null;
        try {
            sourceResolver = (SourceResolver)componentManager.lookup(SourceResolver.ROLE);
            source = sourceResolver.resolveURI(src);
            SelectionListHandler handler = new SelectionListHandler(locale);
            handler.setContentHandler(contentHandler);
            SourceUtil.toSAX(source, handler);
        } catch (Exception e) {
            throw new SAXException("Error while generating selection list: " + e.getMessage(), e);
        } finally {
            if (sourceResolver != null) {
                if (source != null)
                    try { sourceResolver.release(source); } catch (Exception e) {}
                componentManager.release(sourceResolver);
            }
        }
    }

    /**
     * XMLConsumer used to handle selection lists generated on the fly.
     */
    public class SelectionListHandler extends AbstractXMLPipe {
        private Object currentValue;
        private boolean hasLabel;
        private Locale locale;

        public SelectionListHandler(Locale locale) {
            this.locale = locale;
        }

        public void startDocument()
                throws SAXException {
        }

        public void endDocument()
                throws SAXException {
        }

        public void endDTD()
                throws SAXException {
        }

        public void startDTD(String name, String publicId, String systemId)
                throws SAXException {
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (namespaceURI.equals(Constants.WD_NS)) {
                if (localName.equals("item")) {
                    hasLabel = false;
                    String unparsedValue = attributes.getValue("value");
                    if (unparsedValue == null)
                        throw new SAXException("Missing value attribute on " + qName + " element.");
                    currentValue = datatype.convertFromString(unparsedValue);
                    if (currentValue == null)
                        throw new SAXException("Could not interpret the following value: \"" + unparsedValue + "\".");
                    AttributesImpl attrs = new AttributesImpl();
                    // currently the duo convertFromString and convertToString here seems meaningless,
                    // but in the future the formats of input and output could change
                    attrs.addCDATAAttribute("value", datatype.convertToString(currentValue));
                    super.startElement(Constants.WI_NS, localName, Constants.WI_PREFIX_COLON + localName, attrs);
                } else if (localName.equals("label")) {
                    hasLabel = true;
                    super.startElement(Constants.WI_NS, localName, Constants.WI_PREFIX_COLON + localName, attributes);
                } else if (localName.equals("selection-list")) {
                    super.startElement(Constants.WI_NS, localName, Constants.WI_PREFIX_COLON + localName, attributes);
                } else {
                    super.startElement(namespaceURI, localName, qName, attributes);
                }
            } else {
                super.startElement(namespaceURI, localName, qName, attributes);
            }
        }

        private static final String LABEL_EL = "label";

        public void endElement(String namespaceURI, String localName, String qName)
                throws SAXException {
            if (namespaceURI.equals(Constants.WD_NS)) {
                if (localName.equals("item")) {
                    if (!hasLabel) {
                        // make the label now
                        super.startElement(Constants.WI_NS, LABEL_EL, Constants.WI_PREFIX_COLON + LABEL_EL, new AttributesImpl());
                        String label = datatype.convertToStringLocalized(currentValue, locale);
                        super.characters(label.toCharArray(), 0, label.length());
                        super.endElement(Constants.WI_NS, LABEL_EL, Constants.WI_PREFIX_COLON + LABEL_EL);
                    }
                    super.endElement(Constants.WI_NS, localName, Constants.WI_PREFIX_COLON + localName);
                } else if (localName.equals("label")) {
                    super.endElement(Constants.WI_NS, localName, Constants.WI_PREFIX_COLON + localName);
                } else if (localName.equals("selection-list")) {
                    super.endElement(Constants.WI_NS, localName, Constants.WI_PREFIX_COLON + localName);
                } else {
                    super.endElement(namespaceURI, localName, qName);
                }
            } else {
                super.endElement(namespaceURI, localName, qName);
            }
        }
    }

}
