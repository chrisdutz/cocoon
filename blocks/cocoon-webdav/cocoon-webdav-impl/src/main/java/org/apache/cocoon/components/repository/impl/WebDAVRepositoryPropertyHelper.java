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
package org.apache.cocoon.components.repository.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.transform.OutputKeys;

import org.apache.commons.httpclient.HttpException;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.repository.helpers.RepositoryPropertyHelper;
import org.apache.cocoon.components.source.helpers.SourceProperty;
import org.apache.cocoon.components.webdav.WebDAVUtil;
import org.apache.cocoon.util.AbstractLogEnabled;
import org.apache.cocoon.xml.XMLUtils;

import org.w3c.dom.Node;

/**
 * A property helper class for the WebDAV repository
 * intended to be used by flowscripts or corresponding wrapper components.
 *
 * @version $Id: WebDAVRepositoryPropertyHelper.java 587761 2007-10-24 03:08:05Z vgritsenko $
 */
public class WebDAVRepositoryPropertyHelper extends AbstractLogEnabled
                                            implements RepositoryPropertyHelper {
    
    /* The repository component */
    private WebDAVRepository repo;

    /**
     * create a WebDAVRepositoryPropertyHelper
     * 
     * @param repo  a reference to the WebDAVRepository object.
     */
    public WebDAVRepositoryPropertyHelper(WebDAVRepository repo) {
        this.repo = repo;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryPropertyHelper#getProperty(java.lang.String, java.lang.String, java.lang.String)
     */
    public SourceProperty getProperty(String uri, String name, String namespace) {

        try {
            return WebDAVUtil.getProperty(this.repo.getAbsoluteURI(uri), name, namespace);

        } catch (HttpException he) {
            this.getLogger().error("HTTP Error getting property " + namespace + ":" + name + " for " + uri, he);

        } catch (IOException ioe) {
            this.getLogger().error("IO Error getting property " + namespace + ":" + name + " for " + uri, ioe);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryPropertyHelper#getProperties(java.lang.String, java.util.Set)
     */
    public Map getProperties(String uri, Set propNames) {

        try {
            return WebDAVUtil.getProperties(this.repo.getAbsoluteURI(uri), propNames);

        } catch (HttpException he) {
            this.getLogger().error("HTTP Error getting properties for " + uri, he);

        } catch (IOException ioe) {
            this.getLogger().error("IO Error getting properties for " + uri, ioe);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryPropertyHelper#getAllProperties(java.lang.String)
     */
    public List getAllProperties(String uri) {

        try {
            return WebDAVUtil.getAllProperties(this.repo.getAbsoluteURI(uri));

        } catch (HttpException he) {
            this.getLogger().error("HTTP Error getting properties for " + uri, he);

        } catch (IOException ioe) {
            this.getLogger().error("IO Error getting properties for " + uri, ioe);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryPropertyHelper#setProperty(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean setProperty(String uri, String name, String namespace, String value) {

        try {
            WebDAVUtil.setProperty(this.repo.getAbsoluteURI(uri), name, namespace, value);
            return true;

        } catch (HttpException he) {
            this.getLogger().error("HTTP Error setting property " + namespace + ":" + name + " for " + uri, he);
        } catch (IOException ioe) {
            this.getLogger().error("IO Error setting property " + namespace + ":" + name + " for " + uri, ioe);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryPropertyHelper#setProperty(java.lang.String, java.lang.String, java.lang.String, org.w3c.dom.Node)
     */
    public boolean setProperty(String uri, String name, String namespace, Node value) {

        try {
            Properties format = new Properties();
            format.put(OutputKeys.METHOD, "xml");
            format.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
            return this.setProperty(uri, name, namespace, XMLUtils.serializeNode(value, format));

        } catch (ProcessingException pe) {
            this.getLogger().error("Error serializing node " + value, pe);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryPropertyHelper#setProperties(java.lang.String, java.util.Map)
     */
    public boolean setProperties(final String uri, final Map properties) {

        try {
            WebDAVUtil.setProperties(this.repo.getAbsoluteURI(uri), properties);
            return true;

        } catch (HttpException he) {
            this.getLogger().error("HTTP Error setting properties for " + uri, he);
        } catch (IOException ioe) {
            this.getLogger().error("IO Error setting properties for " + uri, ioe);
        }

        return false;
    }
}
