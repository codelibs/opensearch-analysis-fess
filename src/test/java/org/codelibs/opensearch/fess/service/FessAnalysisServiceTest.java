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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.codelibs.opensearch.fess.FessAnalysisPlugin;
import org.junit.Test;
import org.opensearch.common.settings.Settings;
import org.opensearch.plugins.PluginsService;

public class FessAnalysisServiceTest {

    @Test
    public void testServiceCreation() {
        final Settings settings = Settings.EMPTY;
        final PluginsService pluginsService = mock(PluginsService.class);
        final FessAnalysisPlugin.PluginComponent pluginComponent = new FessAnalysisPlugin.PluginComponent();

        final FessAnalysisService service = new FessAnalysisService(settings, pluginsService, pluginComponent);

        assertNotNull(service);
        assertNotNull(pluginComponent.getFessAnalysisService());
        assertEquals(service, pluginComponent.getFessAnalysisService());
    }

    @Test
    public void testLoadClassReturnsNullForNonExistentClass() {
        final Settings settings = Settings.EMPTY;
        final PluginsService pluginsService = mock(PluginsService.class);
        final FessAnalysisPlugin.PluginComponent pluginComponent = new FessAnalysisPlugin.PluginComponent();

        final FessAnalysisService service = new FessAnalysisService(settings, pluginsService, pluginComponent);

        // Start the service to initialize plugins
        service.start();

        // Try to load a non-existent class
        final Class<?> clazz = service.loadClass("com.example.NonExistentClass");

        assertNull(clazz);

        service.stop();
        service.close();
    }

    @Test
    public void testServiceLifecycle() {
        final Settings settings = Settings.EMPTY;
        final PluginsService pluginsService = mock(PluginsService.class);
        final FessAnalysisPlugin.PluginComponent pluginComponent = new FessAnalysisPlugin.PluginComponent();

        final FessAnalysisService service = new FessAnalysisService(settings, pluginsService, pluginComponent);

        // Test start
        service.start();

        // Test stop
        service.stop();

        // Test close
        service.close();

        // No exceptions should be thrown during lifecycle
        assertNotNull(service);
    }
}
