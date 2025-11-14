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
package org.codelibs.opensearch.fess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.Map;

import org.codelibs.opensearch.fess.service.FessAnalysisService;
import org.junit.Before;
import org.junit.Test;
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.lifecycle.LifecycleComponent;
import org.opensearch.common.settings.Settings;
import org.opensearch.core.common.io.stream.NamedWriteableRegistry;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.env.Environment;
import org.opensearch.env.NodeEnvironment;
import org.opensearch.index.analysis.CharFilterFactory;
import org.opensearch.index.analysis.TokenFilterFactory;
import org.opensearch.index.analysis.TokenizerFactory;
import org.opensearch.indices.SystemIndexDescriptor;
import org.opensearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.opensearch.repositories.RepositoriesService;
import org.opensearch.script.ScriptService;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.transport.client.Client;
import org.opensearch.watcher.ResourceWatcherService;

public class FessAnalysisPluginUnitTest {

    private FessAnalysisPlugin plugin;

    @Before
    public void setUp() {
        plugin = new FessAnalysisPlugin();
    }

    @Test
    public void testGetGuiceServiceClasses() {
        final Collection<Class<? extends LifecycleComponent>> services = plugin.getGuiceServiceClasses();

        assertNotNull(services);
        assertEquals(1, services.size());
        assertTrue(services.contains(FessAnalysisService.class));
    }

    @Test
    public void testCreateComponents() {
        final Client client = mock(Client.class);
        final ClusterService clusterService = mock(ClusterService.class);
        final ThreadPool threadPool = mock(ThreadPool.class);
        final ResourceWatcherService resourceWatcherService = mock(ResourceWatcherService.class);
        final ScriptService scriptService = mock(ScriptService.class);
        final NamedXContentRegistry xContentRegistry = mock(NamedXContentRegistry.class);
        final Environment environment = mock(Environment.class);
        final NodeEnvironment nodeEnvironment = mock(NodeEnvironment.class);
        final NamedWriteableRegistry namedWriteableRegistry = mock(NamedWriteableRegistry.class);
        final IndexNameExpressionResolver indexNameExpressionResolver = mock(IndexNameExpressionResolver.class);

        final Collection<Object> components = plugin.createComponents(client, clusterService, threadPool,
                resourceWatcherService, scriptService, xContentRegistry, environment, nodeEnvironment,
                namedWriteableRegistry, indexNameExpressionResolver, () -> null);

        assertNotNull(components);
        assertEquals(1, components.size());
        assertTrue(components.iterator().next() instanceof FessAnalysisPlugin.PluginComponent);
    }

    @Test
    public void testGetCharFilters() {
        final Map<String, AnalysisProvider<CharFilterFactory>> charFilters = plugin.getCharFilters();

        assertNotNull(charFilters);
        assertEquals(2, charFilters.size());
        assertTrue(charFilters.containsKey("fess_japanese_iteration_mark"));
        assertTrue(charFilters.containsKey("fess_traditional_chinese_convert"));
    }

    @Test
    public void testGetTokenFilters() {
        final Map<String, AnalysisProvider<TokenFilterFactory>> tokenFilters = plugin.getTokenFilters();

        assertNotNull(tokenFilters);
        assertEquals(4, tokenFilters.size());
        assertTrue(tokenFilters.containsKey("fess_japanese_baseform"));
        assertTrue(tokenFilters.containsKey("fess_japanese_part_of_speech"));
        assertTrue(tokenFilters.containsKey("fess_japanese_readingform"));
        assertTrue(tokenFilters.containsKey("fess_japanese_stemmer"));
    }

    @Test
    public void testGetTokenizers() {
        final Map<String, AnalysisProvider<TokenizerFactory>> tokenizers = plugin.getTokenizers();

        assertNotNull(tokenizers);
        assertEquals(5, tokenizers.size());
        assertTrue(tokenizers.containsKey("fess_japanese_tokenizer"));
        assertTrue(tokenizers.containsKey("fess_japanese_reloadable_tokenizer"));
        assertTrue(tokenizers.containsKey("fess_korean_tokenizer"));
        assertTrue(tokenizers.containsKey("fess_vietnamese_tokenizer"));
        assertTrue(tokenizers.containsKey("fess_simplified_chinese_tokenizer"));
    }

    @Test
    public void testGetSystemIndexDescriptors() {
        final Settings settings = Settings.EMPTY;
        final Collection<SystemIndexDescriptor> descriptors = plugin.getSystemIndexDescriptors(settings);

        assertNotNull(descriptors);
        assertEquals(8, descriptors.size());

        // Check that all expected system indices are present
        assertTrue(descriptors.stream().anyMatch(d -> d.getIndexPattern().equals(".crawler.*")));
        assertTrue(descriptors.stream().anyMatch(d -> d.getIndexPattern().equals(".suggest")));
        assertTrue(descriptors.stream().anyMatch(d -> d.getIndexPattern().equals(".suggest_analyzer")));
        assertTrue(descriptors.stream().anyMatch(d -> d.getIndexPattern().equals(".suggest_array.*")));
        assertTrue(descriptors.stream().anyMatch(d -> d.getIndexPattern().equals(".suggest_badword.*")));
        assertTrue(descriptors.stream().anyMatch(d -> d.getIndexPattern().equals(".suggest_elevate.*")));
        assertTrue(descriptors.stream().anyMatch(d -> d.getIndexPattern().equals(".fess_config.*")));
        assertTrue(descriptors.stream().anyMatch(d -> d.getIndexPattern().equals(".fess_user.*")));
    }

    @Test
    public void testPluginComponent() {
        final FessAnalysisPlugin.PluginComponent component = new FessAnalysisPlugin.PluginComponent();
        final FessAnalysisService service = mock(FessAnalysisService.class);

        component.setFessAnalysisService(service);

        assertNotNull(component.getFessAnalysisService());
        assertEquals(service, component.getFessAnalysisService());
    }
}
