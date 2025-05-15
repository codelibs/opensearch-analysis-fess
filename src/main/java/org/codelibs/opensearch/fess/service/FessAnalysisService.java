/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.opensearch.fess.service;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.opensearch.fess.FessAnalysisPlugin;
import org.opensearch.OpenSearchException;
import org.opensearch.common.collect.Tuple;
import org.opensearch.common.inject.Inject;
import org.opensearch.common.lifecycle.AbstractLifecycleComponent;
import org.opensearch.common.settings.Settings;
import org.opensearch.plugins.Plugin;
import org.opensearch.plugins.PluginInfo;
import org.opensearch.plugins.PluginsService;

public class FessAnalysisService extends AbstractLifecycleComponent {
    private static final Logger logger = LogManager.getLogger(FessAnalysisService.class);

    private final PluginsService pluginsService;

    private List<Tuple<PluginInfo, Plugin>> plugins;

    @Inject
    public FessAnalysisService(final Settings settings, final PluginsService pluginsService,
            final FessAnalysisPlugin.PluginComponent pluginComponent) {
        this.pluginsService = pluginsService;
        pluginComponent.setFessAnalysisService(this);
    }

    @Override
    protected void doStart() {
        logger.debug("Starting FessAnalysisService");

        plugins = loadPlugins();
    }

    @SuppressWarnings("unchecked")
    private List<Tuple<PluginInfo, Plugin>> loadPlugins() {
        return AccessController.doPrivileged((PrivilegedAction<List<Tuple<PluginInfo, Plugin>>>) () -> {
            try {
                final Field pluginsField = pluginsService.getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                return (List<Tuple<PluginInfo, Plugin>>) pluginsField.get(pluginsService);
            } catch (final Exception e) {
                throw new OpenSearchException("Failed to access plugins in PluginsService.", e);
            }
        });
    }

    @Override
    protected void doStop() {
        logger.debug("Stopping FessAnalysisService");
    }

    @Override
    protected void doClose() {
        logger.debug("Closing FessAnalysisService");
    }

    public Class<?> loadClass(final String className) {
        return AccessController.doPrivileged((PrivilegedAction<Class<?>>) () -> {
            for (final Tuple<PluginInfo, Plugin> p : plugins) {
                final Plugin plugin = p.v2();
                try {
                    return plugin.getClass().getClassLoader().loadClass(className);
                } catch (final ClassNotFoundException e) {
                    // ignore
                }
            }
            return null;
        });
    }

}
