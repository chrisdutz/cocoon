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
package org.apache.cocoon.spring.configurator.impl;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * The parser for the wildcard-bean-map element.
 *
 * @version $Id: WildcardBeanMapElementParser.java 754972 2009-03-16 18:20:54Z reinhard $
 * @since 1.0.1
 */
public class WildcardBeanMapElementParser extends BeanMapElementParser {

    /**
     * {@inheritDoc}
     *
     * @see org.apache.cocoon.spring.configurator.impl.BeanMapElementParser#parse(org.w3c.dom.Element,
     *      org.springframework.beans.factory.xml.ParserContext)
     */
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        // create a new bean definition for the aspect chain
        RootBeanDefinition beanDef = this.createBeanDefinition(WildcardBeanMap.class, null, false);
        beanDef.getPropertyValues().addPropertyValue("wildcard", this.getAttributeValue(element, "wildcard", null));
        beanDef.getPropertyValues().addPropertyValue("checkParent",
                this.getAttributeValue(element, "check-parent", "true"));
        beanDef.getPropertyValues().addPropertyValue("hasProperties",
                this.getAttributeValue(element, "has-properties", ""));
        beanDef.getPropertyValues()
                .addPropertyValue("keyProperty", this.getAttributeValue(element, "key-property", ""));

        // register bean if it's a global definition
        if (!parserContext.isNested()) {
            this.register(beanDef, element, parserContext.getRegistry());
        }
        return beanDef;
    }
}
