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
package org.apache.cocoon.components.hsqldb;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.cocoon.components.thread.RunnableManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Database;
import org.hsqldb.DatabaseManager;

/**
 * This class runs an instance of the HSQLDB HSQL protocol network database server.
 *
 * @version $Id$
 */
public class ServerImpl
       implements Runnable {

    private static final boolean DEFAULT_TRACE = false;
    private static final boolean DEFAULT_SILENT = true;
    private static final int DEFAULT_PORT = 9002;
    private static final String CONTEXT_PROTOCOL = "context:/";
    private static final String DEFAULT_DB_NAME = "cocoondb";
    private static final String DEFAULT_DB_PATH = "context://WEB-INF/db";

    /** The HSQLDB HSQL protocol network database server instance **/
    private org.hsqldb.Server hsqlServer = new org.hsqldb.Server();

    /** The threadpool name to be used for daemon thread */
    private String daemonThreadPoolName = "daemon";

    /** The path to the db. */
    private String path = DEFAULT_DB_PATH;

    /** By default we use the logger for this class. */
    private Log logger = LogFactory.getLog(getClass());

    /** The servlet context. */
    private ServletContext servletContext;

    /** The db name. */
    private String name = DEFAULT_DB_NAME;

    /** The runnable manager. */
    private RunnableManager runnableManager;

    private boolean trace = DEFAULT_TRACE;
    private boolean silent = DEFAULT_SILENT;
    private int port = DEFAULT_PORT;

    public ServerImpl() {
        hsqlServer.setLogWriter(null); /* Remove console log */
        hsqlServer.setErrWriter(null); /* Remove console log */
        hsqlServer.setNoSystemExit(true);        
    }

    public Log getLogger() {
        return this.logger;
    }

    public void setLogger(Log l) {
        this.logger = l;
    }

    public org.hsqldb.Server getServer() {
        return this.hsqlServer;
    }

    public void setThreadPoolName(String name) {
        this.daemonThreadPoolName = name;
    }

    public void setPath(String newPath) {
        this.path = newPath;
    }

    public void setPort(int p) {
        this.port = p;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setServletContext(ServletContext c) {
        this.servletContext = c;
    }

    public void setRunnableManager(RunnableManager runnableManager) {
        this.runnableManager = runnableManager;
    }

    /**
     * Initialize the ServerImpl.
     */
    public void init() {
        this.hsqlServer.setSilent(this.silent);
        this.hsqlServer.setTrace(this.trace);
        this.hsqlServer.setPort(this.port);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Configure HSQLDB with port: " + hsqlServer.getPort() +
                              ", silent: " + hsqlServer.isSilent() +
                              ", trace: " + hsqlServer.isTrace());
        }

        final String dbCfgPath = this.path;
        String dbPath = dbCfgPath;
        // Test if we are running inside a WAR file
        if(dbPath.startsWith(ServerImpl.CONTEXT_PROTOCOL)) {
            dbPath = this.servletContext.getRealPath(dbPath.substring(ServerImpl.CONTEXT_PROTOCOL.length()));
        }
        if (dbPath == null) {
            throw new IllegalArgumentException("The hsqldb cannot be used inside an unexpanded WAR file. " +
                                         "Real path for <" + dbCfgPath + "> is null.");
        }

        try {
            hsqlServer.setDatabasePath(0, new File(dbPath).getCanonicalPath() + File.separator + name);
        } catch (IOException e) {
            throw new RuntimeException("Could not get database directory <" + dbPath + ">", e);
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Database path is <" + hsqlServer.getDatabasePath(0, true) + ">");
        }
        this.start();
    }

    /**
     * Destroy the server and release everything.
     */
    public void destroy() {
        this.stop();
    }

    /** 
     * Start the server.
     */
    protected void start() {
        this.runnableManager.execute(this.daemonThreadPoolName, this);
    }

    /**
     * Stop the server.
     */
    protected void stop() {
        if ( this.getLogger().isDebugEnabled() ) {
            getLogger().debug("Shutting down HSQLDB");
        }
        // AG: Temporally workaround for http://issues.apache.org/jira/browse/COCOON-1862
        // A newer version of hsqldb or SAP NetWeaver may not need the next line
        DatabaseManager.closeDatabases(Database.CLOSEMODE_COMPACT);
        this.hsqlServer.stop();
        if ( this.getLogger().isDebugEnabled() ) {
            getLogger().debug("Shutting down HSQLDB: Done");
        }
    }

    /** Run the server */
    public void run() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Starting " + hsqlServer.getProductName() + " " + hsqlServer.getProductVersion() + " with parameters:");
        }
        this.hsqlServer.start();
    }
}
