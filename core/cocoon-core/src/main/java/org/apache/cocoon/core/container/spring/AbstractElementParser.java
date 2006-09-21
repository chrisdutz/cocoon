/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.IOException;
import java.util.Arrays;

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
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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

    /**
     * Handle include for spring bean configurations.
     * @throws ConfigurationException
     */
    protected void handleBeanInclude(ParserContext parserContext,
                                     String         src,
                                     String         dir,
                                     String         pattern,
                                     boolean optional)
    throws Exception {
        final ResourceLoader resourceLoader = parserContext.getReaderContext().getReader().getResourceLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);
        final String includeURI = src;
        String directoryURI = null;
        if (includeURI == null) {
            // check for directories
            directoryURI = dir;
        }
        if (includeURI == null && directoryURI == null) {
            throw new Exception("Include statement must either have a 'src' or 'dir' attribute.");
        }

        if (includeURI != null) {
            Resource rsrc = null;
            try {
                rsrc = resourceLoader.getResource(includeURI);

                this.handleImport(parserContext, rsrc.getURL().toExternalForm());
            } catch (Exception e) {
                throw new Exception("Cannot load '" + includeURI + "'.", e);
            }

        } else {
            // test if directory exists
            Resource dirResource = resourceLoader.getResource(directoryURI);
            if ( dirResource.exists() ) {
                try {
                    Resource[] resources = resolver.getResources(directoryURI + '/' + pattern);
                    if ( resources != null ) {
                        Arrays.sort(resources, AbstractSettingsBeanFactoryPostProcessor.getResourceComparator());
                        for(int i=0; i < resources.length; i++) {
                            this.handleImport(parserContext, resources[i].getURL().toExternalForm());
                        }
                    }
                } catch (IOException ioe) {
                    throw new Exception("Unable to read configurations from " + directoryURI);
                }
            } else {
                if ( !optional ) {
                    throw new Exception("Directory '" + directoryURI + "' does not exist.");
                }
            }
        }
    }

    protected void handleImport(ParserContext parserContext, String uri) {
        final ResourceLoader resourceLoader = parserContext.getReaderContext().getReader().getResourceLoader();
        parserContext.getDelegate().getReaderContext().getReader().loadBeanDefinitions(resourceLoader.getResource(uri));        
    }
}
