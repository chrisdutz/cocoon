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
package org.apache.cocoon.components.flow.apples;

/**
 * AppleController declares the main processing interfaces for the stateful
 * controller objects.
 * <p>
 * Implementations are advised to implement Avalon lifecycle interfaces.
 * 
 * @version $Id: AppleController.java 586258 2007-10-19 04:19:27Z vgritsenko $
 */
public interface AppleController {

    /**
     * Allows the AppleController implementation to make some business
     * decissions in a given web application flow.
     * <p>
     * Typically those decissions will be based upon what it can find inside the
     * passed {@link AppleRequest} and result into setting specific aspects of
     * the {@link AppleResponse}
     */
    void process(AppleRequest req, AppleResponse res) throws Exception;
}
