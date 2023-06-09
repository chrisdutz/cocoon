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
package org.apache.cocoon.components.serializers.encoding;

/**
 * 
 * 
 * @version $Id: DefaultEncoder.java 587752 2007-10-24 02:47:02Z vgritsenko $
 */
public class DefaultEncoder extends UnknownCharset implements Encoder {

    /**
     * Create a new instance of this <code>DefaultEncoder</code>.
     */
    public DefaultEncoder() {
        super();
    }

    /**
     * Encode the specified character as a sequence of characters.
     * <p>
     * This method will return the same character nested into an array.
     * </p>
     */
    public char[] encode(char character) {
        char c[] = { character };
        return(c);
    }
}
