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
package org.apache.cocoon.components.treeprocessor;

import java.io.IOException;
import java.util.List;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.Processor;
import org.apache.cocoon.components.source.impl.SitemapSourceInfo;
import org.apache.cocoon.environment.Environment;
import org.apache.cocoon.environment.ForwardRedirector;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.internal.EnvironmentHelper;
import org.apache.cocoon.environment.internal.ForwardEnvironmentWrapper;
import org.apache.cocoon.environment.wrapper.MutableEnvironmentFacade;
import org.apache.cocoon.sitemap.SitemapExecutor;

/**
 * The concrete implementation of {@link Processor}, containing the evaluation tree and associated
 * data such as component manager.
 *
 * @version CVS $Id$
 */
public class ConcreteTreeProcessor extends AbstractLogEnabled implements Processor {

    /** Our ServiceManager */
    private ServiceManager manager;

    /** The processor that wraps us */
    private TreeProcessor wrappingProcessor;

    /** Processing nodes that need to be disposed with this processor */
    private List disposableNodes;

    /** Root node of the processing tree */
    private ProcessingNode rootNode;

    private Configuration componentConfigurations;

    /** Number of simultaneous uses of this processor (either by concurrent request or by internal requests) */
    private int requestCount;

    /** The sitemap executor */
    private SitemapExecutor sitemapExecutor;

    /**
     * Builds a concrete processig, given the wrapping processor
     */
    public ConcreteTreeProcessor(TreeProcessor wrappingProcessor,
                                 SitemapExecutor sitemapExecutor) {
        // Store our wrapping processor
        this.wrappingProcessor = wrappingProcessor;

        // Get the sitemap executor - we use the same executor for each sitemap
        this.sitemapExecutor = sitemapExecutor;
    }
    
    /** Set the processor data, result of the treebuilder job */
    public void setProcessorData(ServiceManager manager, ProcessingNode rootNode, List disposableNodes) {
        if (this.rootNode != null) {
            throw new IllegalStateException("setProcessorData() can only be called once");
        }

        this.manager = manager;
        this.rootNode = rootNode;
        this.disposableNodes = disposableNodes;
    }

    /** Set the sitemap component configurations (called as part of the tree building process) */
    public void setComponentConfigurations(Configuration componentConfigurations) {
        this.componentConfigurations = componentConfigurations;
    }

    public Configuration[] getComponentConfigurations() {
        if (this.componentConfigurations == null) {
            if (this.wrappingProcessor.parent != null) {
                return this.wrappingProcessor.parent.getComponentConfigurations();
            }
            return null;
        }
        if (this.wrappingProcessor.parent == null) {
            return new Configuration[]{this.componentConfigurations};
        }
        final Configuration[] parentArray = this.wrappingProcessor.parent.getComponentConfigurations();
        if ( parentArray != null ) {
            final Configuration[] newArray = new Configuration[parentArray.length + 1];
            System.arraycopy(parentArray, 0, newArray, 1, parentArray.length);
            newArray[0] = this.componentConfigurations;
            return newArray;
        } 
        return new Configuration[] {this.componentConfigurations};
    }

    /**
     * Mark this processor as needing to be disposed. Actual call to {@link #dispose()} will occur when
     * all request processings on this processor will be terminated.
     */
    public void markForDisposal() {
        // Decrement the request count (negative number means dispose)
        synchronized(this) {
            this.requestCount--;
        }

        if (this.requestCount < 0) {
            // No more users : dispose right now
            dispose();
        }
    }

    public TreeProcessor getWrappingProcessor() {
        return this.wrappingProcessor;
    }

    public Processor getRootProcessor() {
        return this.wrappingProcessor.getRootProcessor();
    }

    /**
     * Process the given <code>Environment</code> producing the output.
     * @return If the processing is successfull <code>true</code> is returned.
     *         If not match is found in the sitemap <code>false</code>
     *         is returned.
     * @throws org.apache.cocoon.ResourceNotFoundException If a sitemap component tries
     *                                   to access a resource which can not
     *                                   be found, e.g. the generator
     *         ConnectionResetException  If the connection was reset
     */
    public boolean process(Environment environment) throws Exception {
        InvokeContext context = new InvokeContext();
        context.enableLogging(getLogger());
        try {
            return process(environment, context);
        } finally {
            context.dispose();
        }
    }

    /**
     * Process the given <code>Environment</code> to assemble
     * a <code>ProcessingPipeline</code>.
     * @since 2.1
     */
    public InternalPipelineDescription buildPipeline(Environment environment)
    throws Exception {
        InvokeContext context = new InvokeContext(true);
        context.enableLogging(getLogger());
        try {
            if (process(environment, context)) {
                return context.getInternalPipelineDescription(environment);
            } else {
                return null;
            }
        } finally {
            context.dispose();
        }
    }

    /**
     * Do the actual processing, be it producing the response or just building the pipeline
     * @param environment
     * @param context
     * @return true if the pipeline was successfully built, false otherwise.
     * @throws Exception
     */
    protected boolean process(Environment environment, InvokeContext context)
    throws Exception {

        // Increment the concurrent requests count
        synchronized (this) {
            requestCount++;
        }

        try {
            // and now process
            EnvironmentHelper.enterProcessor(this, this.manager, environment);
            final Redirector oldRedirector = context.getRedirector();

            // Build a redirector
            TreeProcessorRedirector redirector = new TreeProcessorRedirector(environment, context);
            setupLogger(redirector);
            context.setRedirector(redirector);
            context.service(this.manager);
            context.setLastProcessor(this);

            try {
                final boolean success = this.rootNode.invoke(environment, context);
                return success;
            } finally {
                EnvironmentHelper.leaveProcessor();
                // Restore old redirector
                context.setRedirector(oldRedirector);
            }

        } finally {
            // Decrement the concurrent request count
            synchronized (this) {
                requestCount--;
            }

            if (requestCount < 0) {
                // Marked for disposal and no more concurrent requests.
                dispose();
            }
        }
    }

    protected boolean handleCocoonRedirect(String uri, Environment environment, InvokeContext context)
    throws Exception {
        // Build an environment wrapper
        // If the current env is a facade, change the delegate and continue processing the facade, since
        // we may have other redirects that will in turn also change the facade delegate

        MutableEnvironmentFacade facade = environment instanceof MutableEnvironmentFacade ?
            ((MutableEnvironmentFacade)environment) : null;

        if (facade != null) {
            // Consider the facade delegate (the real environment)
            environment = facade.getDelegate();
        }

        // test if this is a call from flow
        boolean isRedirect = (environment.getObjectModel().remove("cocoon:forward") == null);
        final SitemapSourceInfo info = SitemapSourceInfo.parseURI(environment, uri);
        Environment newEnv = new ForwardEnvironmentWrapper(environment, info, getLogger());
        if (isRedirect) {
            ((ForwardEnvironmentWrapper) newEnv).setInternalRedirect(true);
        }

        if (facade != null) {
            // Change the facade delegate
            facade.setDelegate((ForwardEnvironmentWrapper)newEnv);
            newEnv = facade;
        }

        // Get the processor that should process this request
        ConcreteTreeProcessor processor;
        if (newEnv.getURIPrefix().equals("")) {
            processor = ((TreeProcessor)getRootProcessor()).concreteProcessor;
        } else {
            processor = this;
        }

        // Process the redirect
        // No more reset since with TreeProcessorRedirector, we need to pop values from the redirect location
        // context.reset();
        // The following is a fix for bug #26854 and #26571
        final boolean result = processor.process(newEnv, context);
        if (facade != null) {
            newEnv = facade.getDelegate();
        }
        if (((ForwardEnvironmentWrapper) newEnv).getRedirectURL() != null) {
            environment.redirect(((ForwardEnvironmentWrapper) newEnv).getRedirectURL(), false, false);
        }
        return result;
    }

    public void dispose() {
        if (this.disposableNodes != null) {
            // we must dispose the nodes in reverse order
            // otherwise selector nodes are freed before the components node
            for (int i = this.disposableNodes.size() - 1; i > -1; i--) {
                ((Disposable) disposableNodes.get(i)).dispose();
            }
            this.disposableNodes = null;
        }

        // Ensure it won't be used anymore
        this.rootNode = null;
        this.sitemapExecutor = null;
    }

    private class TreeProcessorRedirector extends ForwardRedirector {
        private InvokeContext context;

        public TreeProcessorRedirector(Environment env, InvokeContext context) {
            super(env);
            this.context = context;
        }

        protected void cocoonRedirect(String uri) throws IOException, ProcessingException {
            try {
                ConcreteTreeProcessor.this.handleCocoonRedirect(uri, this.env, this.context);
            } catch (IOException e) {
                throw e;
            } catch (ProcessingException e) {
                throw e;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new ProcessingException(e);
            }
        }
    }

    public SourceResolver getSourceResolver() {
        return wrappingProcessor.getSourceResolver();
    }

    public String getContext() {
        return wrappingProcessor.getContext();
    }

    /**
     * Return the sitemap executor
     */
    public SitemapExecutor getSitemapExecutor() {
        return this.sitemapExecutor;
    }
    
    public ServiceManager getServiceManager() {
        return this.manager;
    }
}
