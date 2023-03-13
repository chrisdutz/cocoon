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
package org.apache.cocoon.portal.event;

import org.apache.cocoon.portal.om.CopletDefinition;


/**
 * This interface marks an event as an event for a coplet definition (or
 * for all coplet instances).
 *
 * @version $Id: CopletDefinitionEvent.java 682461 2008-08-04 18:49:15Z cziegeler $
 */
public interface CopletDefinitionEvent extends Event {

    /**
     * Return the coplet definition this event is for.
     * @return The coplet definition.
     */
    CopletDefinition getTarget();
}
