/* 
 * Copyright 2002-2006 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at 
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.List;


/**
 * This class loader reverses the search order for classes.  It checks this classloader
 * before it checks its parent. In addition it can be configured with includes and excludes.
 *
 * @version $Id$
 * @since 2.2
 */
public class DefaultClassLoader extends URLClassLoader {

    protected final int[][] includes;
    protected final int[][] excludes;

    /**
     * Alternate constructor to define a parent and initial <code>URL</code>
     * s.
     */
    public DefaultClassLoader(URL[] urls, List includePatterns, List excludePatterns, final ClassLoader parent) {
        this(urls, includePatterns, excludePatterns, parent, null);
    }

    /**
     * Alternate constructor to define a parent, initial <code>URL</code>s,
     * and a default <code>URLStreamHandlerFactory</code>.
     */
    public DefaultClassLoader(final URL[] urls, List includePatterns, List excludePatterns, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
        this.includes = compilePatterns(includePatterns);
        this.excludes = compilePatterns(excludePatterns);
    }

    private int[][] compilePatterns(List patternConfigs) {
        if (patternConfigs.size() == 0) {
            return null;
        }
        final int[][] patterns = new int[patternConfigs.size()][];

        for (int i = 0; i < patternConfigs.size(); i++) {
            patterns[i] = WildcardHelper.compilePattern((String)patternConfigs.get(i));
        }

        return patterns;
    }

    protected boolean tryClassHere(String name) {
        // don't include classes in the java or javax.servlet package
        if ( name != null && (name.startsWith("java.") || name.startsWith("javax.servlet") ) ) {
            return false;
        }
        // Scan includes, then excludes
        boolean tryHere;
        
        // If no explicit includes, try here
        if (this.includes == null) {
            tryHere = true;
        } else {
            // See if it matches include patterns
            tryHere = false;
            for (int i = 0; i < this.includes.length; i++) {
                if (WildcardHelper.match(null, name, includes[i])) {
                    tryHere = true;
                    break;
                }
            }
        }
        
        // Go through the exclusion list
        if (tryHere && excludes != null) {
            for (int i = 0; i < this.excludes.length; i++) {
                if (WildcardHelper.match(null, name, excludes[i])) {
                    tryHere = false;
                    break;
                }
            }
        }
        
        return tryHere;
    }

    protected Class getClass(String name)
    throws ClassNotFoundException {
        return findClass(name);
    }

    /**
     * Loads the class from this <code>ClassLoader</class>.  If the
     * class does not exist in this one, we check the parent.  Please
     * note that this is the exact opposite of the
     * <code>ClassLoader</code> spec.  We use it to work around
     * inconsistent class loaders from third party vendors.
     *
     * @param     name the name of the class
     * @param     resolve if <code>true</code> then resolve the class
     * @return    the resulting <code>Class</code> object
     * @exception ClassNotFoundException if the class could not be found
     */
    public final Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // First check if it's already loaded
        Class clazz = findLoadedClass(name);

        if (clazz == null) {
            
            final ClassLoader parent = getParent();

            if (tryClassHere(name)) {
                try {
                    clazz = this.getClass(name);
                } catch (ClassNotFoundException cnfe) {
                    if (parent == null) {
                        // Propagate exception
                        throw cnfe;                        
                    }
                }
            }
            
            if (clazz == null) {
                if (parent == null) {
                    throw new ClassNotFoundException(name);
                } else {
                    // Will throw a CFNE if not found in parent
                    clazz = parent.loadClass(name);
                }
            }
        }

        if (resolve) {
            resolveClass(clazz);
        }

        return clazz;
    }

    /**
     * Gets a resource from this <code>ClassLoader</class>.  If the
     * resource does not exist in this one, we check the parent.
     * Please note that this is the exact opposite of the
     * <code>ClassLoader</code> spec.  We use it to work around
     * inconsistent class loaders from third party vendors.
     *
     * @param name of resource
     */
    public final URL getResource(final String name) {
        URL resource = findResource(name);
        ClassLoader parent = this.getParent();
        if (resource == null && parent != null) {
            resource = parent.getResource(name);
        }

        return resource;
    }

    /**
     * Adds a new directory of class files.
     * 
     * @param file
     *            for jar or directory
     * @throws IOException
     */
    public final void addDirectory(File file) throws IOException {
        this.addURL(file.getCanonicalFile().toURL());
    }

    /**
     * Adds a new URL
     */

    public void addURL(URL url) {
        super.addURL(url);
    }
}

