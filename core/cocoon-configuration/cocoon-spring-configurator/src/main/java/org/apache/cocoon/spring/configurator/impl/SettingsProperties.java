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
package org.apache.cocoon.spring.configurator.impl;

import java.util.Properties;

import org.apache.cocoon.configuration.Settings;

/**
 * A properties implementation using the settings object.
 * @since 1.0
 * @version $Id$
 */
public class SettingsProperties extends Properties {

    protected final Settings settings;

    public SettingsProperties(Settings s) {
        this.settings = s;
    }

    /**
     * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
     */
    public String getProperty(String key, String defaultValue) {
        return this.settings.getProperty(key, defaultValue);
    }

    /**
     * @see java.util.Properties#getProperty(java.lang.String)
     */
    public String getProperty(String key) {
        return this.settings.getProperty(key);
    }
    
}