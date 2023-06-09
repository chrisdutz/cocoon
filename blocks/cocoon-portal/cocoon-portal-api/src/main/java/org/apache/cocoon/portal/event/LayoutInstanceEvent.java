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

import org.apache.cocoon.portal.om.LayoutInstance;

/**
 * This interface marks an event as targetted at a
 * {@link org.apache.cocoon.portal.om.LayoutInstance} object.
 *
 * @version $Id: LayoutInstanceEvent.java 682461 2008-08-04 18:49:15Z cziegeler $
 */
public interface LayoutInstanceEvent extends Event {

    /**
     * Return the targetted layout instance.
     * @return The layout instance.
     */
    LayoutInstance getTarget();
}
