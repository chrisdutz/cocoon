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
package org.apache.cocoon.template.jxtg.script.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.cocoon.template.jxtg.expression.JXTExpression;
import org.apache.cocoon.template.jxtg.script.Parser;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class StartDefine extends StartInstruction {

    final String name;
    final String namespace;
    private final String qname;
    private final Map parameters;
    private Event body;

    public StartDefine(StartElement raw, Attributes attrs, Stack stack) 
        throws SAXException {

        super(raw);

        // <macro name="myTag" targetNamespace="myNamespace">
        // <parameter name="paramName" required="Boolean"
        // default="value"/>
        // body
        // </macro>
        this.namespace = StringUtils.defaultString(attrs.getValue("targetNamespace"));
        this.name = attrs.getValue("name");
        if (this.name == null) {
            throw new SAXParseException("macro: \"name\" is required", getLocation(), null);
        }
        
        this.qname = "{" + namespace + "}" + name;
        this.parameters = new HashMap();
    }

    public void finish() throws SAXException {
        Event e = next;
        boolean params = true;
        while (e != this.getEndInstruction()) {
            if (e instanceof StartParameter) {
                StartParameter startParam = (StartParameter) e;
                if (!params) {
                    throw new SAXParseException(
                            "<parameter> not allowed here: \""
                                    + startParam.name + "\"", startParam
                                    .getLocation(), null);
                }
                Object prev = getParameters().put(startParam.name, startParam);
                if (prev != null) {
                    throw new SAXParseException("duplicate parameter: \""
                            + startParam.name + "\"", location, null);
                }
                e = startParam.getEndInstruction();
            } else if (e instanceof IgnorableWhitespace) {
                // EMPTY
            } else if (e instanceof Characters) {
                // check for whitespace
                char[] ch = ((TextEvent) e).getRaw();
                int len = ch.length;
                for (int i = 0; i < len; i++) {
                    if (!Character.isWhitespace(ch[i])) {
                        if (params) {
                            params = false;
                            setBody(e);
                        }
                        break;
                    }
                }
            } else {
                if (params) {
                    params = false;
                    setBody(e);
                }
            }
            e = e.getNext();
        }
        if (this.getBody() == null) {
            this.setBody(this.getEndInstruction());
        }
    }

    public Map getParameters() {
        return parameters;
    }

    void setBody(Event body) {
        this.body = body;
    }

    public Event getBody() {
        return body;
    }

    public String getQname() {
        return qname;
    }
}
