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
package org.apache.cocoon.servletservice.spring;

import org.apache.cocoon.servletservice.CallStackHelper;
import org.apache.cocoon.servletservice.ServletServiceContext;
import org.springframework.beans.factory.FactoryBean;

/**
 * {@link FactoryBean} that exposes the {@link ServletServiceContext} in the current base call frame.
 * It will typically be used together with the call scope for servlet services.
 *
 * @version $Id: BaseServletContextFactoryBean.java 608375 2008-01-03 08:33:00Z reinhard $
 * @since 1.0.0
 */
public final class BaseServletContextFactoryBean implements FactoryBean {

    public Object getObject() throws Exception {
        return CallStackHelper.getBaseServletContext();
    }

    public Class getObjectType() {
        // A ServletServiceContext rather than a ServletContext is needed to make the
        // Absolutizable interface available through the ScopedProxyFactoryBean
        return ServletServiceContext.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
