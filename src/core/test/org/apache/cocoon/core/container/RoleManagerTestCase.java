/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
package org.apache.cocoon.components.container;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.cocoon.core.container.RoleManager;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * Test cases for {@link RoleManager}.
 * 
 * @version CVS $Id: RoleManagerTestCase.java 55163 2004-10-20 16:41:11Z ugo $
 */
public class RoleManagerTestCase extends MockObjectTestCase {
    
    /**
     * Constructor for RoleManagerTestCase.
     * @param name The test name
     */
    public RoleManagerTestCase(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
    }
    
    public void testConfigureWithoutHints() throws ConfigurationException {
        Mock conf = new Mock(Configuration.class);
        Mock child0 = new Mock(Configuration.class);
        Configuration roles[] = new Configuration[1];
        roles[0] = (Configuration) child0.proxy();
        Configuration hints[] = new Configuration[0];
        conf.expects(once()).method("getChildren").with(eq("role"))
                .will(returnValue(roles));
        child0.expects(once()).method("getAttribute").with(eq("name"))
                .will(returnValue("testName"));
        child0.expects(once()).method("getAttribute").with(eq("shorthand"))
                .will(returnValue("testShorthand"));
        child0.expects(once()).method("getAttribute").with(eq("default-class"), eq(null))
                .will(returnValue("testClass"));
        child0.expects(once()).method("getChildren").with(eq("hint"))
                .will(returnValue(hints));
        Mock logger = new Mock(Logger.class);
        logger.expects(this.atLeastOnce()).method("isDebugEnabled").will(returnValue(false));
        RoleManager rm = new RoleManager(null);
        rm.enableLogging((Logger) logger.proxy());
        rm.configure((Configuration) conf.proxy());
        conf.verify();
        child0.verify();
        assertEquals("Role name for shorthand incorrect", "testName", rm.getRoleForName("testShorthand"));
        assertEquals("Default class for role incorrect", "testClass", rm.getDefaultClassNameForRole("testName"));
        assertEquals("Default class for hint must be empty", "", rm.getDefaultClassNameForHint("testName", "testShorthand"));
    }

}
