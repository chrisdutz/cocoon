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

import org.w3c.dom.Element;

/**
 * Interface to be implemented by components that can build a Convertor
 * based on a XML configuration (supplied as DOM Element).
 *
 * <p>The element will (should)
 * always have the local name "convertor" and the cforms definition namespace,
 * but attributes and content of the element can vary depending on the
 * ConvertorBuilder implementation.
 *
 * @version $Id: ConvertorBuilder.java 587759 2007-10-24 03:00:37Z vgritsenko $
 */
public interface ConvertorBuilder {
    /**
     * @param configElement is allowed to be null!
     */
    Convertor build(Element configElement) throws Exception;
}
