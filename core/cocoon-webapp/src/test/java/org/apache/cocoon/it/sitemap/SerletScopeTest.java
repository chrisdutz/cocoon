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
package org.apache.cocoon.it.sitemap;

import javax.servlet.http.HttpServletResponse;

import org.apache.cocoon.tools.it.HtmlUnitTestCase;
import org.junit.Assert;

public class SerletScopeTest extends HtmlUnitTestCase {

    public void testServletScope() throws Exception {
        this.loadResponse("/cocoon-it/demo1/setCode?code=300");
        Assert.assertEquals(HttpServletResponse.SC_OK, this.response.getStatusCode());
        this.loadResponse("/cocoon-it/demo2/setCode?code=301");
        Assert.assertEquals(HttpServletResponse.SC_OK, this.response.getStatusCode());

        this.loadResponse("/cocoon-it/demo1/getCode");
        Assert.assertEquals(300, this.response.getStatusCode());
        this.loadResponse("/cocoon-it/demo2/getCode");
        Assert.assertEquals(301, this.response.getStatusCode());
    }
}
