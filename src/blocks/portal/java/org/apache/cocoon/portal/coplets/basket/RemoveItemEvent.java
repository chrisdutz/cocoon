/*
 * Copyright 2004,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.portal.coplets.basket;


/**
 * Remove an item from the basket 
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * 
 * @version CVS $Id: RemoveItemEvent.java,v 1.2 2004/03/05 13:02:11 bdelacretaz Exp $
 */
public class RemoveItemEvent extends BasketEvent {
    
    /** The item to remove */
    protected Object item;
    
    /**
     * Constructor
     * @param item The item to remove
     */
    public RemoveItemEvent(Object item) {
        this.item = item;
    }
    
    /**
     * Return the item to remove
     */
    public Object getItem() {
        return this.item;
    }
}
