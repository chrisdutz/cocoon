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
package org.apache.cocoon.acting;

import java.util.Collections;
import java.util.Map;

import org.apache.cocoon.util.AbstractLogEnabled;

/**
 * AbstractAction gives you the infrastructure for easily deploying more
 * Actions.  In order to get at the Logger, use getLogger().
 *
 * @version $Id: AbstractAction.java 587751 2007-10-24 02:41:36Z vgritsenko $
 */
public abstract class AbstractAction extends AbstractLogEnabled
                                     implements Action {

    /**
     * Empty unmodifiable map.
     */
    protected static final Map EMPTY_MAP = Collections.EMPTY_MAP;

}
