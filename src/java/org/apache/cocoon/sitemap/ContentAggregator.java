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
package org.apache.cocoon.sitemap;

import org.apache.cocoon.generation.Generator;

/**
 * A content aggregator is a special generator used to implement &ltmap:aggregate&gt;.
 * It combines several parts into one big XML document which is streamed
 * into the pipeline.
 *
 * @version $Id$
 */
public interface ContentAggregator extends Generator {

    /**
     * Set the root element. Please make sure that the parameters are not null!
     */
    public void setRootElement(String element, String namespace, String prefix);

    /**
     * Add a part. Please make sure that the parameters are not null!
     */
    public void addPart(String uri,
                        String element,
                        String namespace,
                        String stripRootElement,
                        String prefix);
}
