/*  Copyright 2007 Niclas Hedhman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.logging.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * One instance of this class will be instantiated to listen in on events generated by
 * the OSGi framework and log those.
 *
 * <p>By default, all events log by this class are logged at the DEBUG level.  This can be
 * changed to a different level by setting the system property
 * "org.ops4j.pax.logging.service.frameworkEventsLogLevel" to DEBUG, INFO, WARNING, or ERROR.</p>
 */
public class FrameworkHandler
    implements BundleListener, FrameworkListener, ServiceListener
{

    public static final String FRAMEWORK_EVENTS_LOG_LEVEL_PROP_NAME =
        "org.ops4j.pax.logging.service.frameworkEventsLogLevel";

    private final PaxLoggingServiceImpl m_service;
    private final int loggingLevel;

    public FrameworkHandler( final PaxLoggingServiceImpl service )
    {
        m_service = service;

        final String levelName = System.getProperty( FRAMEWORK_EVENTS_LOG_LEVEL_PROP_NAME, "DEBUG" ).trim();
        loggingLevel = convertLevel( levelName );
    }

    public void bundleChanged( final BundleEvent bundleEvent )
    {
        final Bundle bundle = bundleEvent.getBundle();
        String message;
        final int type = bundleEvent.getType();
        switch( type )
        {
            case BundleEvent.INSTALLED:
                message = "BundleEvent INSTALLED";
                break;
            case BundleEvent.STARTED:
                message = "BundleEvent STARTED";
                break;
            case BundleEvent.STOPPED:
                message = "BundleEvent STOPPED";
                break;
            case BundleEvent.UPDATED:
                message = "BundleEvent UPDATED";
                break;
            case BundleEvent.UNINSTALLED:
                message = "BundleEvent UNINSTALLED";
                break;
            case BundleEvent.RESOLVED:
                message = "BundleEvent RESOLVED";
                break;
            case BundleEvent.UNRESOLVED:
                message = "BundleEvent UNRESOLVED";
                break;
            case BundleEvent.STARTING:
                message = "BundleEvent STARTING";
                break;
            case BundleEvent.STOPPING:
                message = "BundleEvent STOPPING";
                break;
            default:
                message = "BundleEvent [unknown:" + type + "]";
                break;
        }
        m_service.log( bundle, loggingLevel, message, null );
    }

    public void frameworkEvent( final FrameworkEvent frameworkEvent )
    {
        final int type = frameworkEvent.getType();
        String message;
        switch( type )
        {
            case FrameworkEvent.ERROR:
                message = "FrameworkEvent ERROR";
                break;
            case FrameworkEvent.INFO:
                message = "FrameworkEvent INFO";
                break;
            case FrameworkEvent.PACKAGES_REFRESHED:
                message = "FrameworkEvent PACKAGES REFRESHED";
                break;
            case FrameworkEvent.STARTED:
                message = "FrameworkEvent STARTED";
                break;
            case FrameworkEvent.STARTLEVEL_CHANGED:
                message = "FrameworkEvent STARTLEVEL CHANGED";
                break;
            case FrameworkEvent.WARNING:
                message = "FrameworkEvent WARNING";
                break;
            default:
                message = "FrameworkEvent [unknown:" + type + "]";
                break;
        }
        final Bundle bundle = frameworkEvent.getBundle();
        final Throwable exception = frameworkEvent.getThrowable();
        m_service.log( bundle, loggingLevel, message, exception );
    }

    public void serviceChanged( final ServiceEvent serviceEvent )
    {
        final ServiceReference serviceRef = serviceEvent.getServiceReference();
        String message;
        final int type = serviceEvent.getType();
        switch( type )
        {
            case ServiceEvent.MODIFIED:
                message = "ServiceEvent MODIFIED";
                break;
            case ServiceEvent.REGISTERED:
                message = "ServiceEvent REGISTERED";
                break;
            case ServiceEvent.UNREGISTERING:
                message = "ServiceEvent UNREGISTERING";
                break;
            default:
                message = "ServiceEvent [unknown:" + type + "]";
                break;
        }
        m_service.log( serviceRef, loggingLevel, message );
    }

    private static int convertLevel( final String levelName )
    {
        if( "DEBUG".equals( levelName ) )
        {
            return LogService.LOG_DEBUG;
        }
        else if( "INFO".equals( levelName ) )
        {
            return LogService.LOG_INFO;
        }
        else if( "WARN".equals( levelName ) || "WARNING".equals( levelName ) )
        {
            return LogService.LOG_WARNING;
        }
        else if( "ERROR".equals( levelName ) )
        {
            return LogService.LOG_ERROR;
        }
        else
        {
            return LogService.LOG_DEBUG;
        }
    }
}
