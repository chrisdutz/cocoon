/*
 * Copyright 1999-2002,2004-2005 The Apache Software Foundation.
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
package org.apache.cocoon.portal.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.portal.LayoutException;
import org.apache.cocoon.portal.PortalRuntimeException;
import org.apache.cocoon.portal.PortalService;
import org.apache.cocoon.portal.event.layout.ChangeTabEvent;
import org.apache.cocoon.portal.om.CompositeLayout;
import org.apache.cocoon.portal.om.Item;
import org.apache.cocoon.portal.om.Layout;
import org.apache.cocoon.portal.om.LayoutFeatures;
import org.apache.cocoon.portal.om.NamedItem;

/**
 * Manages the various activities required for page labels.
 *
 * The name of the request parameter used to identify the page labelmay be configured
 * here by declaring
 * <br><parameter-name><i>request-parm-name</i></parameter-name><br>
 * in the configuration for this component. The default request parameter name is
 * 'pageLabel'.
 *
 * @version $Id$
 */
public class PageLabelManager
    extends AbstractComponent
    implements Configurable {

    public static final String ROLE = PageLabelManager.class.getName();

    protected String aspectName;
    private String requestParameterName;
    private boolean nonStickyTabs;

    protected static final String LABEL_ARRAY = PageLabelManager.class.getName() + "A";
    protected static final String LABEL_MAP = PageLabelManager.class.getName() + "M";
    protected static final String EVENT_MAP = PageLabelManager.class.getName() + "E";
    private static final String DEFAULT_REQUEST_PARAMETER_NAME = "pageLabel";

    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) {
        this.requestParameterName =
            config.getChild("parameterName").getValue(DEFAULT_REQUEST_PARAMETER_NAME);
        this.aspectName = config.getChild("aspectName").getValue("tab");
        this.nonStickyTabs =
            Boolean.valueOf(config.getChild("nonStickyTabs").getValue("false")).booleanValue();
    }

    /**
     * Return the current page label.
     * @return The current page label.
     */
    public String getCurrentLabel() {
        String[] labels = getLabels();

        return labels[0];
    }

    /**
     * Return the page label from the previous request.
     * @return The previous page label.
     */
    public String getPreviousLabel() {
        String[] labels = getLabels();

        return labels[1];
    }

    /**
     * Sets the current page label.
     * @return The current page label.
     */
    public String  setCurrentLabel() {
        final Request request =
            ObjectModelHelper.getRequest(this.portalService.getProcessInfoProvider().getObjectModel());
        String value = request.getParameter(this.requestParameterName);
        String[] labels = getLabels();

        if (value != null) {
            labels[1] = labels[0];
            labels[0] = value;
        }
        return labels[0];
    }

    /**
     * Returns the request parameter being used to identify the page label.
     * @return A String containing the request parameter name used for page labels.
     */
    public String getRequestParameterName() {
        return this.requestParameterName;
    }

    /**
     * Return the Map that contains events for all the page labels.
     * @return The Map to use for converting events to and from urls.
     */
    public Map getPageEventMap() {
        PortalService service = null;
        try {
            service = (PortalService) this.manager.lookup(PortalService.ROLE);
            Map map = (Map) service.getUserService().getAttribute(EVENT_MAP);
            if (null == map) {
                map = new HashMap();
                service.getUserService().setAttribute(EVENT_MAP, map);
            }

            return map;
        } catch (ServiceException ce) {
            throw new PortalRuntimeException("Unable to lookup component.", ce);
        } finally {
            this.manager.release(service);
        }
    }

    /**
     * Retrieve the events associated with the specified page label.
     *
     * @param pageLabel The label to retrieve the events for.
     * @return A List containing all the events associated with the page label in the order they
     *         should occur.
     */
    public List getPageLabelEvents(String pageLabel) {
        PortalService service = null;
        try {
            service = (PortalService) this.manager.lookup(PortalService.ROLE);
            Map map = (Map) service.getUserService().getAttribute(LABEL_MAP);
            if (null == map) {
                map = initializeLabels(service);
                service.getUserService().setAttribute(LABEL_MAP, map);
            }

            List list = (List) map.get(pageLabel);

            if (list == null) {
                list = new ArrayList();
                map.put(pageLabel, list);
            }

            return list;
        } catch (ServiceException ce) {
            throw new PortalRuntimeException("Unable to lookup component.", ce);
        } finally {
            this.manager.release(service);
        }
    }

    /**
     * Return the array containing the current and previous labels.
     */
    private String[] getLabels() {
        PortalService service = null;
        try {
            service = (PortalService) this.manager.lookup(PortalService.ROLE);
            String[] labels = (String[]) service.getUserService().getAttribute(LABEL_ARRAY);
            if (null == labels) {
                labels = new String[2];
                service.getUserService().setAttribute(LABEL_ARRAY, labels);
            }
            return labels;
        }
        catch (ServiceException ce) {
            throw new PortalRuntimeException("Unable to lookup component.", ce);
        }
        finally {
            this.manager.release(service);
        }
    }

    /**
     * Create the page label event map and return it.
     *
     * @param service The portal service
     * @return The page label map.
     */
    private Map initializeLabels(PortalService service) {
        Map map = new HashMap();

        Layout rootLayout = service.getProfileManager().getLayout(null);
        Layout portalLayout = LayoutFeatures.getFullScreenInfo(service, rootLayout);
        if (portalLayout == null) {
            portalLayout = rootLayout;
        }

        if (portalLayout instanceof CompositeLayout) {
            try {
                populate(service, (CompositeLayout) portalLayout, map, "", new ArrayList());
            } catch (LayoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return map;
    }

    /**
     * Populate the event map
     *
     * @param layout
     * @param map
     * @param name
     * @param parentEvents
     */
    private List populate(PortalService service, CompositeLayout layout, Map map, String name, List parentEvents)
    throws LayoutException {
        List lhList = null;
        for (int j = 0; j < layout.getSize(); j++) {
            Item tab = layout.getItem(j);
            ChangeTabEvent event =
                new ChangeTabEvent(LayoutFeatures.getLayoutInstance(service, layout, true), tab, false);
            StringBuffer label = new StringBuffer(name);
            if (label.length() > 0) {
                label.append(".");
            }
            label.append((tab instanceof NamedItem) ? ((NamedItem) tab).getName() :
                Integer.toString(j));
            List events = new ArrayList(parentEvents);
            events.add(event);
            Layout child = tab.getLayout();
            List allEvents = null;
            if (child != null && child instanceof CompositeLayout) {
                allEvents = populate(service, (CompositeLayout) child, map, label.toString(), events);
            }
            if (this.nonStickyTabs) {
                // With non-sticky tabs the non-leaf nodes always display
                // the left-most child tabs
                if (lhList == null) {
                    if (allEvents != null) {
                        lhList = allEvents;
                    } else {
                        lhList = events;
                    }
                }
                if (allEvents != null) {
                    map.put(label.toString(), allEvents);
                } else {
                    map.put(label.toString(), events);
                }
            } else {
                map.put(label.toString(), events);
            }
        }
        return lhList;
    }
}