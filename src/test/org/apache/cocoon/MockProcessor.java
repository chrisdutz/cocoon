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
package org.apache.cocoon;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.environment.SourceResolver;

/**
 * Mock processor
 * 
 * @version CVS $Id$
 */
public class MockProcessor implements Processor {
    
    /* (non-Javadoc)
     * @see org.apache.cocoon.Processor#buildPipeline(org.apache.cocoon.environment.Environment)
     */
    public InternalPipelineDescription buildPipeline(Environment environment)
    throws Exception {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.cocoon.Processor#getComponentConfigurations()
     */
    public Configuration[] getComponentConfigurations() {
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.cocoon.Processor#getContext()
     */
    public String getContext() {
        return null;
    }
    /* (non-Javadoc)
     * @see org.apache.cocoon.Processor#getRootProcessor()
     */
    public Processor getRootProcessor() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.apache.cocoon.Processor#getSourceResolver()
     */
    public SourceResolver getSourceResolver() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.Processor#process(org.apache.cocoon.environment.Environment)
     */
    public boolean process(Environment environment) throws Exception {
        return false;
    }
}
