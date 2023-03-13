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

package org.apache.cocoon.serialization;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.SitemapComponentTestCase;

/**
 * Test case for the MIDISerializer
 * @version $Id: XMidiSerializerTestCase.java 587761 2007-10-24 03:08:05Z vgritsenko $
 */
public class XMidiSerializerTestCase extends SitemapComponentTestCase {

    public void testMIDISerializer() throws Exception {
        String type = "midi";
        String input = "resource://org/apache/cocoon/generation/prelude.xmi";
        Parameters parameters = new Parameters();
        String control = "resource://org/apache/cocoon/generation/prelude.mid";

       assertIdentical(loadByteArray(control), serialize(type, parameters, load(input)));
    }
}
