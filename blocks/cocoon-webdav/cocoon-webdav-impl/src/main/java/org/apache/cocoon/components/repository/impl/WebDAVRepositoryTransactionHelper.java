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

import org.apache.commons.httpclient.HttpException;

import org.apache.cocoon.components.repository.helpers.CredentialsToken;
import org.apache.cocoon.components.repository.helpers.RepositoryTransactionHelper;
import org.apache.cocoon.components.webdav.WebDAVUtil;
import org.apache.cocoon.util.AbstractLogEnabled;

/**
 * A transaction an locking helper class
 * intended to be used by flowscripts or corresponding wrapper components.
 *
 * @version $Id: WebDAVRepositoryTransactionHelper.java 587761 2007-10-24 03:08:05Z vgritsenko $
 */
public class WebDAVRepositoryTransactionHelper extends AbstractLogEnabled
                                               implements RepositoryTransactionHelper {
    
    /* The repository component */
    private WebDAVRepository repo;

    /* The credentials to be used against the WebDAV repository */
    private CredentialsToken credentials;

    /**
     * create a WebDAVRepositoryTransactionHelper
     * 
     * @param credentials  the user credentials to be used against the WebDAV repository.
     * @param repo  a reference to the WebDAVRepository object.
     */
    public WebDAVRepositoryTransactionHelper(CredentialsToken credentials, WebDAVRepository repo) {
        this.credentials = credentials;
        this.repo = repo;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryTransactionHelper#beginTran()
     */
    public boolean beginTran() {
        //may be implemented via DeltaV activities?
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryTransactionHelper#commitTran()
     */
    public boolean commitTran() {
        //may be implemented via DeltaV activities?
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryTransactionHelper#rollbackTran()
     */
    public boolean rollbackTran() {
        //may be implemented via DeltaV activities?
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryTransactionHelper#lock(java.lang.String)
     */
    public boolean lock(String uri) {

        try {
            return WebDAVUtil.getWebdavResource(this.repo.getAbsoluteURI(uri)).lockMethod();
            
        } catch (HttpException he) {
            this.getLogger().error("HTTP Error locking " + uri, he);
        } catch (IOException ioe) {
            this.getLogger().error("IO Error locking " + uri, ioe);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryTransactionHelper#lock(java.lang.String, int)
     */
    public boolean lock(String uri, int timeout) {

        try {
            return WebDAVUtil.getWebdavResource(this.repo.getAbsoluteURI(uri))
                      .lockMethod(this.credentials.getPrincipal().getName(), timeout);
                      
        } catch (HttpException he) {
            this.getLogger().error("HTTP Error locking " + uri, he);
        } catch (IOException ioe) {
            this.getLogger().error("IO Error locking " + uri, ioe);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryTransactionHelper#unlock(java.lang.String)
     */
    public boolean unlock(String uri) {

        try {
            return WebDAVUtil.getWebdavResource(this.repo.getAbsoluteURI(uri)).unlockMethod();

        } catch (HttpException he) {
            this.getLogger().error("HTTP Error unlocking " + uri, he);
        } catch (IOException ioe) {
            this.getLogger().error("IO Error unlocking " + uri, ioe);
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryTransactionHelper#supportsTransactions()
     */
    public boolean supportsTransactions() {
        //may be implemeted via DeltaV activities? --> make configurable
        return false;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.components.repository.helpers.RepositoryTransactionHelper#supportsLocking()
     */
    public boolean supportsLocking() {
        return true;
    }
}
