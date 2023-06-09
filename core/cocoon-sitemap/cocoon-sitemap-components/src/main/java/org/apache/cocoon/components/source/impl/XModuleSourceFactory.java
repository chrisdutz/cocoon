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

package org.apache.cocoon.components.source.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceFactory;

import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.util.AbstractLogEnabled;

/**
 * A factory for 'xmodule:' sources (see {@link XModuleSource}). 
 *
 * @version $Id: XModuleSourceFactory.java 587751 2007-10-24 02:41:36Z vgritsenko $
 */
public class XModuleSourceFactory extends AbstractLogEnabled
                                  implements SourceFactory, Serviceable, Contextualizable,
                                             ThreadSafe {
    
    private ServiceManager manager;
    private Context context;

    /**
     * Servicable Interface
     */
    public void service( ServiceManager manager ) throws ServiceException {
        this.manager = manager;
    }

    /**
     * Contextualizable, get the object model
     */
    public void contextualize( Context context ) throws ContextException {
        this.context = context;
    }
    
    /**
     * Get a {@link XModuleSource} object.
     * 
     * @param location   The URI to resolve - this URI includes the scheme.
     * @param parameters this is optional and not used here
     */
    public Source getSource(String location, Map parameters) throws IOException {

        Map objectModel = ContextHelper.getObjectModel(this.context);
        return new XModuleSource(objectModel, location, this.manager);
    }

    /**
     * Release a {@link Source} object.
     */
    public void release(Source source) {
        // Do nothing here
    }
}
