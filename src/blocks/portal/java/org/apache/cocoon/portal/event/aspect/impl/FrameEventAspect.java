/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.cocoon.portal.event.aspect.impl;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.portal.PortalService;
import org.apache.cocoon.portal.event.Event;
import org.apache.cocoon.portal.event.Publisher;
import org.apache.cocoon.portal.event.aspect.EventAspect;
import org.apache.cocoon.portal.event.aspect.EventAspectContext;
import org.apache.cocoon.portal.event.impl.ChangeAspectDataEvent;
import org.apache.cocoon.portal.layout.Layout;
import org.apache.cocoon.portal.profile.ProfileManager;

/**
 *
 * @author <a href="mailto:cziegeler@s-und-n.de">Carsten Ziegeler</a>
 * @author <a href="mailto:volker.schmitt@basf-it-services.com">Volker Schmitt</a>
 * 
 * @version CVS $Id: FrameEventAspect.java,v 1.4 2003/05/23 14:20:09 cziegeler Exp $
 */
public class FrameEventAspect
    extends AbstractLogEnabled
    implements EventAspect, ThreadSafe, Composable {

    protected ComponentManager manager;

    /* (non-Javadoc)
     * @see org.apache.cocoon.portal.event.aspect.EventAspect#process(org.apache.cocoon.portal.event.aspect.EventAspectContext, org.apache.cocoon.portal.PortalService)
     */
    public void process(EventAspectContext context, PortalService service) {
        // TODO - make this configurable
        final String requestParameterName = "frame";
        final Request request = ObjectModelHelper.getRequest(context.getObjectModel());
        String[] values = request.getParameterValues(requestParameterName);
        if (values != null) {
            final Publisher publisher = context.getEventPublisher();
            for (int i = 0; i < values.length; i++) {
                final String value = values[i];
                Event e = null;
                try {
                    e = context.getEventConverter().decode(value);
                    if (null != e) {
                        publisher.publish(e);
                        service.getLinkService().addEventToLink(e);
                    }
                } catch (Exception ignore) {
                }
                if (e == null) {
                    // Use '|' character as delimiter between ID and URI
                    int index = value.indexOf('|');
                    if (index != -1) {
                        String id = value.substring(0, index);
                        String uri = null;
                        if (index != (value.length() - 1)) {
                            uri = value.substring(index + 1);
                        }
                        
                        ProfileManager profileManager = null;
                        try {
                            profileManager = (ProfileManager)this.manager.lookup(ProfileManager.ROLE);
                            Layout layout = profileManager.getPortalLayout( id );
                            if ( layout != null ) {
                                e = new ChangeAspectDataEvent(layout, "frame", uri);
                                publisher.publish(e);
                            }
                        } catch (ComponentException ignore) {
                        } finally {
                            this.manager.release( profileManager );
                        }
                        
                        if (uri != null) {
                            service.getLinkService().addEventToLink(e);
                        }
                    }
                }
            }
        }
        // and invoke next one
        context.invokeNext(service);
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.component.Composable#compose(org.apache.avalon.framework.component.ComponentManager)
     */
    public void compose(ComponentManager manager) throws ComponentException {
        this.manager = manager;
    }

}
