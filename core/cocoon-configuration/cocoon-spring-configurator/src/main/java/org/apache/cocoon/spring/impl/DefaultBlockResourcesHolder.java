/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.spring.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.cocoon.configuration.Settings;
import org.apache.cocoon.spring.BlockResourcesHolder;

/**
 * Default implementation of a {@link BlockResourceHolder}.
 *
 * @version $Id$
 * @since 1.0
 */
public class DefaultBlockResourcesHolder
    implements BlockResourcesHolder {

    protected Settings settings;

    protected Map blockContexts = new HashMap();

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void init()
    throws Exception {
        this.blockContexts = DeploymentUtil.deployBlockArtifacts(this.settings.getWorkDirectory());
    }

    /**
     * Return a map with deployed block names as keys and the url of the deployed
     * resources as value.
     */
    public Map getBlockContexts() {
        return this.blockContexts;
    }
}
