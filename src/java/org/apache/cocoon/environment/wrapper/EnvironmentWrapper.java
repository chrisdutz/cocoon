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
package org.apache.cocoon.environment.wrapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.logger.Logger;
import org.apache.cocoon.components.source.impl.SitemapSourceInfo;
import org.apache.cocoon.environment.AbstractEnvironment;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.util.BufferedOutputStream;


/**
 * This is a wrapper class for the <code>Environment</code> object.
 * It has the same properties except that the object model
 * contains a <code>RequestWrapper</code> object.
 *
 * @author <a href="mailto:bluetkemeier@s-und-n.de">Bj&ouml;rn L&uuml;tkemeier</a>
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: EnvironmentWrapper.java,v 1.20 2004/06/23 17:13:00 cziegeler Exp $
 */
public class EnvironmentWrapper 
    extends AbstractEnvironment {

    /** The wrapped environment */
    protected Environment environment;

    /** The object model */
    protected Map objectModel;

    /** The redirect url */
    protected String redirectURL;

    /** The request object */
    protected Request request;

    /** The stream to output to */
    protected OutputStream outputStream;
    
    protected String contentType;

    protected boolean internalRedirect = false;
    
    /**
     * Constructs an EnvironmentWrapper object from a Request
     * and Response objects
     */
    public EnvironmentWrapper(Environment env,
                              String      requestURI,
                              String      queryString,
                              Logger      logger) {
        this(env, requestURI, queryString, logger,  false, null);
    }

    /**
     * Constructs an EnvironmentWrapper object from a Request
     * and Response objects
     */
    public EnvironmentWrapper(Environment      env,
                              String           requestURI,
                              String           queryString,
                              Logger           logger,
                              boolean          rawMode,
                              String           view) {
        super(env.getURI(), view, env.getAction());
        init(env, requestURI, queryString, logger, rawMode, view);
        this.setURI(env.getURIPrefix(), env.getURI());
    }
    
    /**
     * Constructor
     * @param env
     * @param uri
     * @param logger
     * @throws MalformedURLException
     */
    public EnvironmentWrapper(Environment env, String uri,  Logger logger) throws MalformedURLException {
        super(env.getURI(), env.getView(), env.getAction());

        SitemapSourceInfo info = SitemapSourceInfo.parseURI(env, uri);

        this.init(env, info.requestURI, info.queryString, logger, info.rawMode, info.view);
        this.setURI(info.prefix, info.uri);
        
    }
    
    private void init(Environment    env,
                      String         requestURI,
                      String         queryString,
                      Logger         logger,
                      boolean        rawMode,
                      String         view){

        this.enableLogging(logger);
        this.environment = env;
        this.view = view;

        // create new object model and replace the request object
        Map oldObjectModel = env.getObjectModel();
        if (oldObjectModel instanceof HashMap) {
            this.objectModel = (Map)((HashMap)oldObjectModel).clone();
        } else {
            this.objectModel = new HashMap(oldObjectModel.size()*2);
            Iterator entries = oldObjectModel.entrySet().iterator();
            Map.Entry entry;
            while (entries.hasNext()) {
                entry = (Map.Entry)entries.next();
                this.objectModel.put(entry.getKey(), entry.getValue());
            }
        }
        this.request = new RequestWrapper(ObjectModelHelper.getRequest(oldObjectModel),
                                          requestURI,
                                          queryString,
                                          this,
                                          rawMode);
        this.objectModel.put(ObjectModelHelper.REQUEST_OBJECT, this.request);
    }
   
    /* (non-Javadoc)
     * @see org.apache.cocoon.environment.Environment#redirect(java.lang.String, boolean, boolean)
     */
    public void redirect(String newURL, boolean global, boolean permanent)
    throws IOException {
        if ( !global && !this.internalRedirect ) {
            this.redirectURL = newURL;
        } else {
            this.environment.redirect(newURL, global, permanent);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.environment.Environment#getOutputStream(int)
     */
    public OutputStream getOutputStream(int bufferSize)
    throws IOException {
        return this.outputStream == null
                ? this.environment.getOutputStream(bufferSize)
                : this.outputStream;
    }

    /**
     * Set the output stream for this environment. It hides the one of the
     * wrapped environment.
     */
    public void setOutputStream(OutputStream stream) {
        this.outputStream = stream;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.environment.Environment#tryResetResponse()
     */
    public boolean tryResetResponse()
    throws IOException {
        final OutputStream os = this.getOutputStream(-1);
        if (os != null
            && os instanceof BufferedOutputStream) {
            ((BufferedOutputStream)os).clearBuffer();
            return true;
        } else {
            return super.tryResetResponse();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.environment.Environment#commitResponse()
     */
    public void commitResponse() 
    throws IOException {
        final OutputStream os = this.getOutputStream(-1);
        if (os != null
            && os instanceof BufferedOutputStream) {
            ((BufferedOutputStream)os).realFlush();
        } else {
            super.commitResponse();
        }
    }

    /**
     * if a redirect should happen this returns the url,
     * otherwise <code>null</code> is returned
     */
    public String getRedirectURL() {
        return this.redirectURL;
    }
    
    public void reset() {
        this.redirectURL = null;
    }

    /**
     * Set the StatusCode
     */
    public void setStatus(int statusCode) {
        // ignore this
    }

    public void setContentLength(int length) {
        // ignore this
    }

    /**
     * Set the ContentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Get the ContentType
     */
    public String getContentType() {
        return this.contentType;
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.environment.Environment#getObjectModel()
     */
    public Map getObjectModel() {
        return this.objectModel;
    }

    /**
     * Lookup an attribute in this instance, and if not found search it
     * in the wrapped environment.
     *
     * @param name a <code>String</code>, the name of the attribute to
     * look for
     * @return an <code>Object</code>, the value of the attribute or
     * null if no such attribute was found.
     */
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value == null)
            value = this.environment.getAttribute(name);

        return value;
    }

    /**
     * Remove attribute from the current instance, as well as from the
     * wrapped environment.
     *
     * @param name a <code>String</code> value
     */
    public void removeAttribute(String name) {
        super.removeAttribute(name);
        this.environment.removeAttribute(name);
    }

    /**
     * Always return <code>false</code>.
     */
    public boolean isExternal() {
        return false;
    }

    public void setInternalRedirect(boolean flag) {
        this.internalRedirect = flag;
        if ( flag ) {
            ((RequestWrapper)this.request).setRequestURI(this.prefix, this.uri);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.environment.Environment#isInternRedirect()
     */
    public boolean isInternalRedirect() {
        return this.internalRedirect;
    }
}
