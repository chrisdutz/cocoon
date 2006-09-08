/*
 * Copyright 2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.core.container.spring;

import org.apache.cocoon.configuration.Settings;
import org.springframework.web.context.scope.RequestAttributes;

/**
 * 
 * @version $Id$
 */
public interface Container {

    /**
     * Notify about entering this context.
     * @param attributes The request attributes.
     * @return A handle which should be passed to {@link #leavingContext(RequestAttributes, Object)}.
     */
    Object enteringContext(RequestAttributes attributes);

    /**
     * Notify about leaving this context.
     * @param attributes The request attributes.
     * @param handle     The returned handle from {@link #enteringContext(RequestAttributes)}.
     */
    void leavingContext(RequestAttributes attributes, Object handle);

    ClassLoader getClassLoader();

    Settings getSettings();
}
