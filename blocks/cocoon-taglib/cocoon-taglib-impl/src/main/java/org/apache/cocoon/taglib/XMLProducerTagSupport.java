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
package org.apache.cocoon.taglib;

import org.apache.cocoon.xml.XMLConsumer;

/**
 * @version $Id: XMLProducerTagSupport.java 587761 2007-10-24 03:08:05Z vgritsenko $
 */
public abstract class XMLProducerTagSupport extends TagSupport implements XMLProducerTag {

    protected XMLConsumer xmlConsumer;

    /*
     * @see XMLProducer#setConsumer(XMLConsumer)
     */
    public void setConsumer(XMLConsumer xmlConsumer) {
        this.xmlConsumer = xmlConsumer;
    }

    /*
     * @see Recyclable#recycle()
     */
    public void recycle() {
        this.xmlConsumer = null;
        super.recycle();
    }
}
