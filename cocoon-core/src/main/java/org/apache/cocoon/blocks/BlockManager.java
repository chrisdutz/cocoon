/*
 * Copyright 1999-2005 The Apache Software Foundation.
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
package org.apache.cocoon.blocks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.Constants;
import org.apache.cocoon.Processor;
import org.apache.cocoon.blocks.util.CoreUtil;
import org.apache.cocoon.blocks.util.ServletConfigurationWrapper;
import org.apache.cocoon.components.ContextHelper;
import org.apache.cocoon.components.LifecycleHelper;
import org.apache.cocoon.components.container.CocoonServiceManager;
import org.apache.cocoon.components.container.ComponentContext;
import org.apache.cocoon.core.container.CoreServiceManager;
import org.apache.cocoon.environment.http.HttpContext;
import org.apache.cocoon.environment.http.HttpEnvironment;

/**
 * @version $Id$
 */
public class BlockManager
    extends AbstractLogEnabled
    implements Servlet, Block, Configurable, Contextualizable, Disposable, Initializable { 

    public static String ROLE = BlockManager.class.getName();

    private ServletConfig servletConfig;
    private ServletContext servletContext;
    private String containerEncoding;

    private Context context;
    private Configuration config;
    private ServiceManager serviceManager;

    private Processor blockProcessor;
    private BlockWiring blockWiring;
    private BlockContext blockContext;
    private Blocks blocks;
    private String contextURL;

    public void contextualize(Context context) throws ContextException {
        this.context = context;
    }

    public void configure(Configuration config)
        throws ConfigurationException {
        this.config = config;
    }

    public void initialize() throws Exception {
        this.blockWiring = new BlockWiring();
        this.blockWiring.setServletContext(this.servletContext);
        LifecycleHelper.setupComponent(this.blockWiring,
                                       this.getLogger(),
                                       null,
                                       null,
                                       this.config);    

        getLogger().debug("Initializing new Block Manager: " + this.blockWiring.getId());

        this.blockContext = new BlockContext(this.servletContext, this.blockWiring, this.blocks);
        this.contextURL = CoreUtil.getContextURL(this.blockContext, "COB-INF/block.xml");
        Context newContext = this.getAvalonContext();
        String confLocation = this.contextURL + "::";

        if (this.blockWiring.isCore()) {
            this.getLogger().debug("Block with core=true");
            ServletConfig blockServletConfig =
            	new ServletConfigurationWrapper(this.servletConfig, this.blockContext);
            CoreUtil coreUtil = new CoreUtil(blockServletConfig);
            this.serviceManager = coreUtil.getServiceManager();
       } else {
            // Create a service manager for getting components from other blocks
            ServiceManager topServiceManager = new InterBlockServiceManager(this.blockWiring, this.blocks);
            ((InterBlockServiceManager)topServiceManager).enableLogging(this.getLogger());

            this.serviceManager =
                this.createLocalSourceResolverSM(newContext, topServiceManager, confLocation);
        }
        // FIXME this.settings = (Settings) this.serviceManager.lookup(Core.ROLE);
        
        // Create a service manager with the exposed components of the block
        if (this.blockWiring.getComponentConfiguration() != null) {
            DefaultConfiguration componentConf =
                new DefaultConfiguration("components", confLocation);
            componentConf.addAll(this.blockWiring.getComponentConfiguration());
            this.serviceManager = new CocoonServiceManager(this.serviceManager);
            LifecycleHelper.setupComponent(this.serviceManager,
                    this.getLogger(),
                    newContext,
                    null,
                    componentConf);
        }

        // Create a processor for the block
        if (this.blockWiring.getProcessorConfiguration() != null) {
            this.blockProcessor = new BlockProcessor();
            LifecycleHelper.setupComponent(this.blockProcessor,
                    this.getLogger(),
                    newContext,
                    this.serviceManager,
                    this.blockWiring.getProcessorConfiguration());    
            
        }
    }

    public void dispose() {
    }

    /**
     * @throws Exception
     */
    protected Context getAvalonContext() throws Exception {
        ComponentContext newContext = new ComponentContext(this.context);
        // A block is supposed to be an isolated unit so it should not have
        // any direct access to the global root context
        newContext.put(ContextHelper.CONTEXT_ROOT_URL, new URL(this.contextURL));
        newContext.put(Constants.CONTEXT_ENVIRONMENT_CONTEXT, new HttpContext(this.blockContext));
        newContext.makeReadOnly();
        
        return newContext;
    }

    /**
     * @param newContext
     * @param confLocation
     * @throws Exception
     */
    protected ServiceManager createLocalSourceResolverSM(Context newContext, ServiceManager parentServiceManager, String confLocation) throws Exception {
        // The source resolver must be defined in this service
        // manager, otherwise the root path will be the one from the
        // parent manager, we add a resolver to get it right. If the
        // components section contain includes the CoreComponentManager
        // use the location of the configuration an the parent SourceResolver
        // for resolving the include.
        DefaultConfiguration sourceManagerConf =
            new DefaultConfiguration("components", confLocation);
        // FIXME: Need a local role manager as it is not inherited through the InterBlockServiceManager 
        DefaultConfiguration roleInclude =
            new DefaultConfiguration("include");
        roleInclude.setAttribute("src", "resource://org/apache/cocoon/cocoon.roles");
        sourceManagerConf.addChild(roleInclude);
        DefaultConfiguration resolverConf =
            new DefaultConfiguration("source-resolver");
        sourceManagerConf.addChild(resolverConf);
        ServiceManager sourceResolverSM =
            new CoreServiceManager(parentServiceManager);
        LifecycleHelper.setupComponent(
                sourceResolverSM,
                this.getLogger(),
                newContext,
                null,
                sourceManagerConf);
        return sourceResolverSM;
    }

    // Block methods

    // The blocks manager should not be available within a block so I
    // didn't want to make it part of the parent manager. But this is
    // a little bit clumsy. Question is what components, if any, the
    // blocks should have in common.
    public void setBlocks(Blocks blocks) {
    	this.blocks = blocks;
    }

    /**
     * Get the mount path of the block
     */
    public String getMountPath() {
        return this.blockWiring.getMountPath();
    }

    /**
     * Get a block property
     */
    public String getProperty(String name) {
    	return this.blockContext.getInitParameter(name);
    }

    // TODO: We should have a reflection friendly Map getProperties() also

    /**
     * The exported components of the block. Return null if the block doesn't export components.
     * 
     * @return a ServiceManager containing the blocks exported components
     */
    public ServiceManager getServiceManager() {
        // Check that the block have a local service manager
        if (this.blockWiring.getComponentConfiguration() != null) {
            return this.serviceManager;
        } else {
            return null;
        }
    }
    
    /**
     * Takes the scheme specific part of a block URI (the scheme is
     * the responsibilty of the BlockSource) and resolve it with
     * respect to the blocks mount point.
     */
    public URI absolutizeURI(URI uriToResolve, URI base) throws URISyntaxException {
        URI uri = resolveURI(uriToResolve, base);
        String blockName = uri.getScheme();
        Block block = null;
        if (blockName == null) {
            // this block
            block = this;
        } else {
            // another block
        	String blockId = this.blockWiring.getBlockId(blockName);
            block = this.blocks.getBlock(blockId);
        }
        if (block == null)
            throw new URISyntaxException(uriToResolve.toString(), "Unknown block name");

        String mountPath = block.getMountPath();
        if (mountPath == null)
            throw new URISyntaxException(uri.toString(), "No mount point for this URI");
        if (mountPath.endsWith("/"))
            mountPath = mountPath.substring(0, mountPath.length() - 1);
        String absoluteURI = mountPath + uri.getSchemeSpecificPart();
        getLogger().debug("Resolving " + uri.toString() + " to " + absoluteURI);
        return new URI(absoluteURI);
    }

    /**
     * Parses and resolves the scheme specific part of a block URI
     * with respect to the base URI of the current sitemap. The scheme
     * specific part of the block URI has the form
     * <code>foo:/bar</code> when refering to another block, in this
     * case only an absolute path is allowed. For reference to the own
     * block, both absolute <code>/bar</code> and relative
     * <code>./foo</code> paths are allowed.
     */
    public URI resolveURI(URI uri, URI base) throws URISyntaxException {
        getLogger().debug("BlockManager: resolving " + uri.toString() + " with scheme " +
                          uri.getScheme() + " and ssp " + uri.getSchemeSpecificPart());
        if (uri.getPath() != null && uri.getPath().length() >= 2 &&
            uri.getPath().startsWith("./")) {
            // self reference relative to the current sitemap, e.g. ./foo
            if (uri.isAbsolute())
                throw new URISyntaxException(uri.toString(), "When the protocol refers to other blocks the path must be absolute");
            URI resolvedURI = base.resolve(uri);
            getLogger().debug("BlockManager: resolving " + uri.toString() +
                              " to " + resolvedURI.toString() + " with base URI " + base.toString());
            uri = resolvedURI;
        }
        return uri;
    }
    
    // Servlet methods

	public void init(ServletConfig servletConfig) throws ServletException {
    	this.servletConfig = servletConfig;
    	this.servletContext = servletConfig.getServletContext();
        this.containerEncoding = this.servletConfig.getInitParameter("container-encoding");
        if (this.containerEncoding == null) {
        	this.containerEncoding = "ISO-8859-1";
        }
    }

    public ServletConfig getServletConfig() {
		return this.servletConfig;
	}
    
	public void service(ServletRequest request0, ServletResponse response0) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest)request0;
		HttpServletResponse response =(HttpServletResponse)response0;

        String blockName = (String) request.getAttribute(Block.NAME);

        if (blockName != null) {
            // Request to other block.
            String blockId = this.blockWiring.getBlockId(blockName);
            boolean superCall = false;
            // Call to named block
            if (blockId != null && !Block.SUPER.equals(blockName)) {
                // The block name should not be used in the recieving block.
                request.removeAttribute(Block.NAME);
            } else {
                if (Block.SUPER.equals(blockName)) {
                    // Explicit call to super block
                    // The block name should not be used in the recieving block.
                    request.removeAttribute(Block.NAME);
                } else if (blockId == null) {
                    // If there is a super block, the connection might
                    // be defined there instead.
                    blockId = this.blockWiring.getBlockId(Block.SUPER);
                }
                superCall = true;
            }
            Block block = this.blocks.getBlock(blockId);
            if (block == null)
                throw new ServletException("No block with name=" + blockName +
                        " id=" + blockId);
            this.getLogger().debug("Enter processing in block " + blockName);
            try {
                // A super block should be called in the context of
                // the called block to get polymorphic calls resolved
                // in the right way. Therefore no new current block is
                // set.
                if (!superCall) {
                    // It is important to set the current block each time
                    // a new block is entered, this is used for the block
                    // protocol
                    BlockEnvironmentHelper.enterBlock(block);
                }
                block.service(request, response);
            } finally {
                if (!superCall) {
                    BlockEnvironmentHelper.leaveBlock();
                }
                this.getLogger().debug("Leaving processing in block " + blockName);
            }               

        } else {
            // Request to the own block
            String uri = request.getPathInfo();

            if (uri.charAt(0) == '/') {
                uri = uri.substring(1);
            }

            String formEncoding = request.getParameter("cocoon-form-encoding");
            if (formEncoding == null) {
                formEncoding = "ISO-8859-1";
                // FIXME formEncoding = this.settings.getFormEncoding();
            }
            HttpEnvironment env =
                new HttpEnvironment(uri,
                        this.contextURL,
                        request,
                        response,
                        this.servletContext,
                        new HttpContext(this.servletContext),
                        this.containerEncoding,
                        formEncoding);
            env.enableLogging(getLogger());
            
            try {
                this.blockProcessor.process(env);
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e);
            }
            env.commitResponse();       
        }
	}

	public String getServletInfo() {
		return "BlockManager";
	}

	public void destroy() {
	}
}
