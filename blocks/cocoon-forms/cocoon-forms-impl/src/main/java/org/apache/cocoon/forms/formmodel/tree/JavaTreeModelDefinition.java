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
package org.apache.cocoon.forms.formmodel.tree;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * A {@link org.apache.cocoon.forms.formmodel.tree.TreeModelDefinition} based on a Spring bean
 * implementing {@link org.apache.cocoon.forms.formmodel.tree.TreeModel}.
 *
 * @version $Id: JavaTreeModelDefinition.java 587759 2007-10-24 03:00:37Z vgritsenko $
 */
public class JavaTreeModelDefinition implements TreeModelDefinition, BeanFactoryAware {

    private String modelBeanRef;
    private BeanFactory beanFactory;

    public void setBeanFactory( BeanFactory beanFactory)
                                                  throws BeansException
    {
        this.beanFactory = beanFactory;
    }

    public void setModelBeanRef(String beanId) {
        this.modelBeanRef = beanId;
    }

    public TreeModel createInstance() {
        TreeModel model;
        try {
            model = (TreeModel)beanFactory.getBean( modelBeanRef );
        } catch (Exception e) {
            throw new RuntimeException("Cannot get an instance of Spring bean " + modelBeanRef, e);
        }

        return model;
    }
}
