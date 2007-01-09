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
package org.apache.cocoon.components.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;


/**
 * The DefaultThreadPool class implements the {@link ThreadPool} interface.
 * Instances of this class are made by the {@link RunnableManager} passing a
 * configuration into the <code>configure</code> method.
 *
 * @version $Id$
 */
public class DefaultThreadPool
    extends PooledExecutor
    implements ThreadPool {

    //~ Static fields/initializers ---------------------------------------------

    /** Default ThreadPool block policy */
    public static final String POLICY_DEFAULT = POLICY_RUN;

    //~ Instance fields --------------------------------------------------------

    /** Wrapps a channel */
    private ChannelWrapper channelWrapper;

    /** By default we use the logger for this class. */
    private Log logger = LogFactory.getLog(getClass());

    /** The Queue */
    private Queue queue;

    /** The blocking policy */
    private String blockPolicy;

    /** The name of this thread pool */
    private String name;

    /** Should we wait for running jobs to terminate on shutdown ? */
    private boolean shutdownGraceful;

    /** The maximum queue size */
    private int queueSize;

    /** How long to wait for running jobs to terminate on disposition */
    private int shutdownWaitTimeMs;

    //~ Constructors -----------------------------------------------------------

    /**
     * Create a new pool.
     */
    DefaultThreadPool() {
        this( new ChannelWrapper() );
    }

    /**
     * Create a new pool.
     *
     * @param channel DOCUMENT ME!
     */
    private DefaultThreadPool( final ChannelWrapper channel ) {
        super( channel );
        channelWrapper = channel;
    }

    //~ Methods ----------------------------------------------------------------

    public Log getLogger() {
        return this.logger;
    }

    public void setLogger(Log l) {
        this.logger = l;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the blockPolicy.
     */
    public String getBlockPolicy() {
        return blockPolicy;
    }

    /**
     * DOCUMENT ME!
     *
     * @return maximum size of the queue (0 if isQueued() == false)
     *
     * @see org.apache.cocoon.components.thread.ThreadPool#getQueueSize()
     */
    public int getMaxQueueSize() {
        return ( ( queueSize < 0 ) ? Integer.MAX_VALUE : queueSize );
    }

    /**
     * DOCUMENT ME!
     *
     * @return size of queue (0 if isQueued() == false)
     *
     * @see org.apache.cocoon.components.thread.ThreadPool#getQueueSize()
     */
    public int getMaximumQueueSize() {
        return queueSize;
    }

    /**
     * @see org.apache.cocoon.components.thread.ThreadPool#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * Get hte priority used to create Threads
     *
     * @return {@link Thread#MIN_PRIORITY}, {@link Thread#NORM_PRIORITY}, or
     *         {@link Thread#MAX_PRIORITY}
     */
    public int getPriority() {
        return ((ThreadFactory)super.getThreadFactory()).getPriority();
    }

    /**
     * DOCUMENT ME!
     *
     * @return current size of the queue (0 if isQueued() == false)
     *
     * @see org.apache.cocoon.components.thread.ThreadPool#getQueueSize()
     */
    public int getQueueSize() {
        return queue.getQueueSize();
    }

    /**
     * Whether this DefaultThreadPool has a queue
     *
     * @return Returns the m_isQueued.
     *
     * @see org.apache.cocoon.components.thread.ThreadPool#isQueued()
     */
    public boolean isQueued() {
        return queueSize != 0;
    }

    /**
     * Execute a command
     *
     * @param command The {@link Runnable} to execute
     *
     * @throws InterruptedException In case of interruption
     */
    public void execute( Runnable command ) throws InterruptedException {
        if( getLogger().isDebugEnabled() ) {
            getLogger().debug( "Executing Command: " + command.toString() +
                                 ",pool=" + getName() );
        }

        super.execute( command );
    }

    /**
     * @see org.apache.cocoon.components.thread.ThreadPool#shutdown()
     */
    public void shutdown() {
        if( shutdownGraceful ) {
            shutdownAfterProcessingCurrentlyQueuedTasks();
        } else {
            shutdownNow();
        }

        try {
            if( getShutdownWaitTimeMs() > 0 ) {
                if( ! awaitTerminationAfterShutdown( getShutdownWaitTimeMs()) ) {
                    getLogger().warn( "running commands have not terminated within " +
                                      getShutdownWaitTimeMs() +
                                      "ms. Will shut them down by interruption" );
                    interruptAll();
                    shutdownNow();
                }
            }

            awaitTerminationAfterShutdown();
        } catch( final InterruptedException ie ) {
            getLogger().error( "cannot shutdown ThreadPool", ie );
        }
    }

    /**
     * Set the blocking policy
     *
     * @param blockPolicy The blocking policy value
     */
    void setBlockPolicy( final String blockPolicy ) {
        this.blockPolicy = blockPolicy;

        if( POLICY_ABORT.equalsIgnoreCase( blockPolicy ) ) {
            abortWhenBlocked(  );
        } else if( POLICY_DISCARD.equalsIgnoreCase( blockPolicy ) ) {
            discardWhenBlocked(  );
        } else if( POLICY_DISCARD_OLDEST.equalsIgnoreCase( blockPolicy ) ) {
            discardOldestWhenBlocked();
        } else if( POLICY_RUN.equalsIgnoreCase( blockPolicy ) ) {
            runWhenBlocked();
        } else if( POLICY_WAIT.equalsIgnoreCase( blockPolicy ) ) {
            waitWhenBlocked();
        } else {
            final StringBuffer msg = new StringBuffer();
            msg.append( "WARNING: Unknown block-policy configuration \"" )
               .append( blockPolicy );
            msg.append( "\". Should be one of \"" ).append( POLICY_ABORT );
            msg.append( "\",\"" ).append( POLICY_DISCARD );
            msg.append( "\",\"" ).append( POLICY_DISCARD_OLDEST );
            msg.append( "\",\"" ).append( POLICY_RUN );
            msg.append( "\",\"" ).append( POLICY_WAIT );
            msg.append( "\". Will use \"" ).append( POLICY_DEFAULT ).append( "\"" );
            getLogger().warn( msg.toString() );
            setBlockPolicy( POLICY_DEFAULT );
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name The name to set.
     */
    void setName( String name ) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param queueSize DOCUMENT ME!
     */
    void setQueue( final int queueSize ) {
        if( queueSize != 0 ) {
            if( queueSize > 0 ) {
                queue = new BoundedQueue( queueSize );
            } else {
                queue = new LinkedQueue(  );
            }
        } else {
            queue = new SynchronousChannel(  );
        }

        this.queueSize = queueSize;
        channelWrapper.setChannel( queue );
    }

    /**
     * DOCUMENT ME!
     *
     * @param shutdownGraceful The shutdownGraceful to set.
     */
    void setShutdownGraceful( boolean shutdownGraceful ) {
        this.shutdownGraceful = shutdownGraceful;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the shutdownGraceful.
     */
    boolean isShutdownGraceful() {
        return shutdownGraceful;
    }

    /**
     * DOCUMENT ME!
     *
     * @param shutdownWaitTimeMs The shutdownWaitTimeMs to set.
     */
    void setShutdownWaitTimeMs( int shutdownWaitTimeMs ) {
        this.shutdownWaitTimeMs = shutdownWaitTimeMs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the shutdownWaitTimeMs.
     */
    int getShutdownWaitTimeMs() {
        return shutdownWaitTimeMs;
    }
}
