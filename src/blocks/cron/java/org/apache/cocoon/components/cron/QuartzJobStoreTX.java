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
package org.apache.cocoon.components.cron;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceManager;

import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.jdbcjobstore.DriverDelegate;
import org.quartz.impl.jdbcjobstore.NoSuchDelegateException;

/**
 * Overrides JobStoreTX's getDelegate method.
 *
 * @version CVS $Id$
 * @since 2.1.6
 */
public class QuartzJobStoreTX extends JobStoreTX {
    private Logger logger;
    private ServiceManager manager;
    private Context context;
    private DriverDelegate delegate;

    public QuartzJobStoreTX(Logger logger, ServiceManager manager, Context context) {
        this.logger = logger;
        this.manager = manager;
        this.context = context;
    }

    protected DriverDelegate getDelegate() throws NoSuchDelegateException {
        if (delegate == null) {
            delegate = new QuartzDriverDelegate(this.logger, this.manager, this.context, super.getDelegate());
        }

        return delegate;
    }
}
