/*
 * Copyright 2006 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.core.container.spring;

import org.apache.cocoon.configuration.Settings;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

/**
 * This is a base class for all bean definition parser used in Cocoon.
 * It provides some utility methods.
 *
 * @version $Id$
 * @since 2.2
 */
public abstract class AbstractElementParser implements BeanDefinitionParser {

    /** Logger (we use the same logging mechanism as Spring!) */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Register a bean definition.
     * @param beanDef  The bean definition.
     * @param beanName The name of the bean.
     * @param registry The registry.
     */
    protected void register(BeanDefinition beanDef,
                            String         beanName,
                            BeanDefinitionRegistry registry) {
        this.register(beanDef, beanName, null, registry);
    }

    /**
     * Register a bean definition.
     * @param beanDef  The bean definition.
     * @param beanName The name of the bean.
     * @param alias    Optional alias.
     * @param registry The registry.
     */
    protected void register(BeanDefinition beanDef,
                            String         beanName,
                            String         alias,
                            BeanDefinitionRegistry registry) {
        if ( this.logger.isDebugEnabled() ) {
            this.logger.debug("Registering bean with name " + beanName +
                              (alias != null ? " (alias=" + alias + ") " : " ") +
                              beanDef);
        }
        final BeanDefinitionHolder holder;
        if ( alias != null ) {
            holder = new BeanDefinitionHolder(beanDef, beanName, new String[] {alias});
        } else {
            holder = new BeanDefinitionHolder(beanDef, beanName);
        }
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    /**
     * Helper method to create a new bean definition.
     * @param componentClass    The class of the implementation.
     * @param initMethod        Optional initialization method.
     * @param requiresSettings  If set to true, this bean has a property "settings" for the settings object.
     * @return A new root bean definition.
     */
    protected RootBeanDefinition createBeanDefinition(Class   componentClass,
                                                      String  initMethod,
                                                      boolean requiresSettings) {
        final RootBeanDefinition beanDef = new RootBeanDefinition();
        beanDef.setBeanClass(componentClass);      
        beanDef.setSingleton(true);
        beanDef.setLazyInit(false);
        if ( initMethod != null ) {
            beanDef.setInitMethodName(initMethod);
        }
        if ( requiresSettings ) {
            beanDef.getPropertyValues().addPropertyValue("settings", new RuntimeBeanReference(Settings.ROLE));
        }
        return beanDef;
    }

    /**
     * Helper method to create a new bean definition.
     * @param componentClass    The class of the implementation.
     * @param initMethod        Optional initialization method.
     * @param requiresSettings  If set to true, this bean has a property "settings" for the settings object.
     * @return A new root bean definition.
     */
    protected RootBeanDefinition createBeanDefinition(String   componentClass,
                                                      String  initMethod,
                                                      boolean requiresSettings) {
        final RootBeanDefinition beanDef = new RootBeanDefinition();
        beanDef.setBeanClassName(componentClass);      
        beanDef.setSingleton(true);
        beanDef.setLazyInit(false);
        if ( initMethod != null ) {
            beanDef.setInitMethodName(initMethod);
        }
        if ( requiresSettings ) {
            beanDef.getPropertyValues().addPropertyValue("settings", new RuntimeBeanReference(Settings.ROLE));
        }
        return beanDef;
    }

    /**
     * Add a new bean definition to the registry.
     * @param componentClass    The class of the implementation.
     * @param beanName          The name of the bean.
     * @param initMethod        Optional initialization method.
     * @param requiresSettings  If set to true, this bean has a property "settings" for the settings object.
     * @param registry          The bean registry.
     */
    protected void addComponent(Class                  componentClass,
                                String                 beanName,
                                String                 initMethod,
                                boolean                requiresSettings,
                                BeanDefinitionRegistry registry) {
        final RootBeanDefinition beanDef = this.createBeanDefinition(componentClass, initMethod, requiresSettings);

        this.register(beanDef, beanName, registry);
    }

    /**
     * Add a new bean definition to the registry.
     * @param componentClass    The class of the implementation.
     * @param beanName          The name of the bean.
     * @param initMethod        Optional initialization method.
     * @param requiresSettings  If set to true, this bean has a property "settings" for the settings object.
     * @param registry          The bean registry.
     */
    protected void addComponent(String                 componentClass,
                                String                 beanName,
                                String                 initMethod,
                                boolean                requiresSettings,
                                BeanDefinitionRegistry registry) {
        final RootBeanDefinition beanDef = this.createBeanDefinition(componentClass, initMethod, requiresSettings);

        this.register(beanDef, beanName, registry);
    }
}
