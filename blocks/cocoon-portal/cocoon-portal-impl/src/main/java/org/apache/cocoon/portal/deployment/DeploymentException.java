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
package org.apache.cocoon.portal.deployment;

import org.apache.cocoon.portal.PortalException;

/**
 * Base class for all deployment exceptions.
 *
 * @version $Id: DeploymentException.java 587755 2007-10-24 02:50:56Z vgritsenko $
 */
public class DeploymentException extends PortalException {

    public DeploymentException() {
        super();
    }

    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeploymentException(String message) {
        super(message);
    }

    public DeploymentException(Throwable cause) {
        super(cause);
    }
}
