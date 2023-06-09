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
package org.apache.cocoon.portal.event.layout;

import org.apache.cocoon.portal.PortalService;
import org.apache.cocoon.portal.om.Item;
import org.apache.cocoon.portal.om.LayoutFeatures;
import org.apache.cocoon.portal.om.LayoutInstance;
import org.apache.cocoon.portal.om.NamedItem;

/**
 *
 * @version $Id: ChangeTabEvent.java 587755 2007-10-24 02:50:56Z vgritsenko $
 * @since 2.2
 */
public class ChangeTabEvent
    extends LayoutInstanceChangeAttributeEvent {

    protected Item item;

    protected boolean useName;

    public ChangeTabEvent(PortalService service, String eventData) {
        super(service, eventData);
    }

    public ChangeTabEvent(LayoutInstance instance, Item item, boolean useName) {
        super(instance, LayoutFeatures.ATTRIBUTE_TAB, (useName ? ((NamedItem)item).getName(): String.valueOf(item.getParent().getItems().indexOf(item))), true);
        this.item = item;
        this.useName = useName;
    }

    public Item getItem() {
        return item;
    }

    public boolean isUseName() {
        return useName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ChangeTabEvent (" + this.hashCode() + ") : " + this.asString();
    }
}
