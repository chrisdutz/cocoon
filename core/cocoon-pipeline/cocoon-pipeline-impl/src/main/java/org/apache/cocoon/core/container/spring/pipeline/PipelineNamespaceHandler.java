/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.core.container.spring.pipeline;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Spring namespace handler for the cocoon pipeline namespace.
 *
 * <p>Currently this namespace defines the following elements
 * (in the <code>http://cocoon.apache.org/schema/pipeline</code> namespace):
 * <dl>
 * <dt>component</dt>
 * <dd>with optional attributes "mime-type", "label" and "hint".</dd>
 * </dl>
 *
 * @version $Id: PipelineNamespaceHandler.java 605544 2007-12-19 14:03:30Z vgritsenko $
 * @since 2.2
 */
public class PipelineNamespaceHandler extends NamespaceHandlerSupport {

    /**
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    public void init() {
        registerBeanDefinitionDecorator("component", new PipelineComponentInfoInitializerDecorator());
    }
}
