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
 * The <code>Charset</code> implementation provided by this factory
 * for the unknown charset.
 * 
 * @version $Id: UnknownCharset.java 587752 2007-10-24 02:47:02Z vgritsenko $
 */
public class UnknownCharset implements Charset {

    /**
     * Create a new instance of this <code>UnknownCharset</code>.
     */
    public UnknownCharset() {
        super();
    }

    /**
     * Return the primary name of this <code>Charset</code>
     */
    public String getName() {
        return(null);
    }

    /**
     * Return all alias names for this <code>Charset</code>
     */
    public String[] getAliases() {
        return(new String[0]);
    }

    /**
     * Check if the specified character by representable in this specifiec
     * <code>Charset</code> instance.
     * <p>
     * This implementation always returns <b>true</b>.
     * </p>
     */
    public boolean allows(char c) {
        return(true);
    }

    /**
     * Compare two <code>Charset</code> instances for equality.
     */
    public boolean equals(Object object) {
        return(object instanceof UnknownCharset);
    }

    /**
     * Compare an object to this <code>Charset</code> instances for equality.
     */
    public boolean equals(Charset charset) {
        return(charset instanceof UnknownCharset);
    }
}
