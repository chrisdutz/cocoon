/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.components.xpointer;

import org.xml.sax.SAXException;

/**
 * Implements support for the XPointer xmlns() Scheme.
 * See also <a href="http://www.w3.org/TR/xptr-xmlns/">http://www.w3.org/TR/xptr-xmlns/</a>.
 *
 * @version $Id: XmlnsPart.java 587751 2007-10-24 02:41:36Z vgritsenko $
 */
public class XmlnsPart implements PointerPart {
    private String prefix;
    private String namespace;

    /**
     * Creates an XmlnsPart.
     */
    public XmlnsPart(String prefix, String namespace) {
        this.prefix = prefix;
        this.namespace = namespace;
    }

    public boolean process(XPointerContext xpointerContext) throws SAXException {
        xpointerContext.addPrefix(prefix, namespace);
        return false;
    }
}
