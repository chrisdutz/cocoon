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
package org.apache.cocoon.forms.datatype.convertor;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.Locale;

/**
 * Convertor for java.lang.Doubles that does not do any (Locale-dependent)
 * formatting. It simply uses String.valueOf() and Long.parseLong().
 *
 * @version $Id: PlainDoubleConvertor.java 587759 2007-10-24 03:00:37Z vgritsenko $
 */
public class PlainDoubleConvertor implements Convertor {
    public ConversionResult convertFromString(String value, Locale locale, Convertor.FormatCache formatCache) {
        try {
            return new ConversionResult(new Double(Double.parseDouble(value)));
        } catch (NumberFormatException e) {
            return ConversionResult.create("double");
        }
    }

    public String convertToString(Object value, Locale locale, Convertor.FormatCache formatCache) {
        return String.valueOf(value);
    }

    public Class getTypeClass() {
        return Double.class;
    }

    public void generateSaxFragment(ContentHandler contentHandler, Locale locale) throws SAXException {
        // intentionally empty
    }
}
