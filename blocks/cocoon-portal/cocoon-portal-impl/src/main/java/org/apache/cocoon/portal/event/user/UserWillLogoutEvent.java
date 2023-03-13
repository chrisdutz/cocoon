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
package org.apache.cocoon.portal.event.user;

import org.apache.cocoon.portal.om.PortalUser;

/**
 * This event is send when a user logs out from the portal.
 * Note, that not in all circumstances a logout is received from a user, e.g. when
 * the session expires and the user does not explicitly logout.
 *
 * @version $Id: UserWillLogoutEvent.java 587755 2007-10-24 02:50:56Z vgritsenko $
 * @since 2.2
 */
public class UserWillLogoutEvent extends UserEvent {

    public UserWillLogoutEvent(PortalUser pu) {
        super(pu);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "UserWillLogoutEvent: user=" + this.portalUser;
    }
}
