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
package org.apache.cocoon.portal.services;

import org.apache.cocoon.portal.PortalException;
import org.apache.cocoon.portal.om.CopletDefinition;
import org.apache.cocoon.portal.om.CopletInstance;
import org.apache.cocoon.portal.om.CopletType;

/**
 * This factory is for creating and managing coplet objects.
 *
 * @version $Id: CopletFactory.java 682461 2008-08-04 18:49:15Z cziegeler $
 */
public interface CopletFactory  {

    /** 
     * Create a new coplet instance.
     * This is also registered at the profile manager.
     */
    CopletInstance newInstance(CopletDefinition copletDef)
    throws PortalException;

    /** 
     * Create a new coplet instance.
     * This is also registered at the profile manager.
     */
    CopletInstance newInstance(CopletDefinition copletDef,
                               String           id)
    throws PortalException;

    /**
     * Remove the coplet instance data.
     * This is also unregistered at the profile manager.
     */
    void remove(CopletInstance copletInstanceData);

    /**
     * Remove the coplet definition.
     * This is also unregistered at the profile manager.
     */
    void remove(CopletDefinition copletDefinition);

    /**
     * Create a new coplet data instance.
     */
    CopletDefinition newInstance(CopletType copletType, String id)
    throws PortalException;
}
