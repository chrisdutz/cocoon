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
package org.apache.cocoon.thread.impl;

import org.apache.cocoon.thread.ThreadFactory;

/**
 * This class is responsible to create new Thread instances to run a command.
 *
 * @version $Id: DefaultThreadFactory.java 587751 2007-10-24 02:41:36Z vgritsenko $
 */
public class DefaultThreadFactory
    implements ThreadFactory, EDU.oswego.cs.dl.util.concurrent.ThreadFactory {
    //~ Instance fields --------------------------------------------------------

    /** The daemon mode */
    private boolean m_isDaemon = false;

    /** The priority of newly created Threads */
    private int m_priority = Thread.NORM_PRIORITY;

    //~ Methods ----------------------------------------------------------------

    /**
     * Set the isDaemon property
     *
     * @param isDaemon Whether or not new <code>Thread</code> should run as
     *        daemons.
     */
    public void setDaemon( boolean isDaemon ) {
        m_isDaemon = isDaemon;
    }

    /**
     * Get the isDaemon property
     *
     * @return Whether or not new <code>Thread</code> will run as daemons.
     */
    public boolean isDaemon() {
        return m_isDaemon;
    }

    /**
     * Set the priority newly created <code>Thread</code>s should have
     *
     * @param priority One of {@link Thread#MIN_PRIORITY}, {@link
     *        Thread#NORM_PRIORITY}, {@link Thread#MAX_PRIORITY}
     */
    public void setPriority( final int priority ) {
        if( ( Thread.MAX_PRIORITY == priority ) ||
            ( Thread.MIN_PRIORITY == priority ) ||
            ( Thread.NORM_PRIORITY == priority ) ) {
            m_priority = priority;
        }
    }

    /**
     * Get the priority newly created <code>Thread</code>s will have
     *
     * @return One of {@link Thread#MIN_PRIORITY}, {@link
     *         Thread#NORM_PRIORITY}, {@link Thread#MAX_PRIORITY}
     */
    public int getPriority() {
        return m_priority;
    }

    /**
     * Create a new Thread for Runnable
     *
     * @param command The {@link Runnable}
     *
     * @return A new Thread instance
     */
    public Thread newThread( final Runnable command ) {
        final Thread thread = new Thread( command );
        thread.setPriority( m_priority );
        thread.setDaemon( m_isDaemon );

        return thread;
    }
}
