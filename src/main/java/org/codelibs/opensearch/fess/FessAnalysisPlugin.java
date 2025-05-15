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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.codelibs.opensearch.fess.index.analysis.ChineseTokenizerFactory;
import org.codelibs.opensearch.fess.index.analysis.JapaneseBaseFormFilterFactory;
import org.codelibs.opensearch.fess.index.analysis.JapaneseIterationMarkCharFilterFactory;
import org.codelibs.opensearch.fess.index.analysis.JapaneseKatakanaStemmerFactory;
import org.codelibs.opensearch.fess.index.analysis.JapanesePartOfSpeechFilterFactory;
import org.codelibs.opensearch.fess.index.analysis.JapaneseReadingFormFilterFactory;
import org.codelibs.opensearch.fess.index.analysis.JapaneseTokenizerFactory;
import org.codelibs.opensearch.fess.index.analysis.KoreanTokenizerFactory;
import org.codelibs.opensearch.fess.index.analysis.ReloadableJapaneseTokenizerFactory;
import org.codelibs.opensearch.fess.index.analysis.TraditionalChineseConvertCharFilterFactory;
import org.codelibs.opensearch.fess.index.analysis.VietnameseTokenizerFactory;
import org.codelibs.opensearch.fess.service.FessAnalysisService;
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
import org.opensearch.plugins.AnalysisPlugin;
import org.opensearch.plugins.MapperPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.plugins.SystemIndexPlugin;
import org.opensearch.repositories.RepositoriesService;
import org.opensearch.script.ScriptService;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.transport.client.Client;
import org.opensearch.watcher.ResourceWatcherService;

public class FessAnalysisPlugin extends Plugin implements AnalysisPlugin, MapperPlugin, SystemIndexPlugin {

    private final PluginComponent pluginComponent = new PluginComponent();

    @Override
    public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
        final Collection<Class<? extends LifecycleComponent>> services = new ArrayList<>();
        services.add(FessAnalysisService.class);
        return services;
    }

    @Override
    public Collection<Object> createComponents(Client client, ClusterService clusterService, ThreadPool threadPool,
            ResourceWatcherService resourceWatcherService, ScriptService scriptService, NamedXContentRegistry xContentRegistry,
            Environment environment, NodeEnvironment nodeEnvironment, NamedWriteableRegistry namedWriteableRegistry,
            IndexNameExpressionResolver indexNameExpressionResolver, Supplier<RepositoriesService> repositoriesServiceSupplier) {
        final Collection<Object> components = new ArrayList<>();
        components.add(pluginComponent);
        return components;
    }

    @Override
    public Map<String, AnalysisProvider<CharFilterFactory>> getCharFilters() {
        final Map<String, AnalysisProvider<CharFilterFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_iteration_mark",
                (indexSettings, env, name, settings) -> new JapaneseIterationMarkCharFilterFactory(indexSettings, env, name, settings,
                        pluginComponent.getFessAnalysisService()));
        extra.put("fess_traditional_chinese_convert",
                (indexSettings, env, name, settings) -> new TraditionalChineseConvertCharFilterFactory(indexSettings, env, name, settings,
                        pluginComponent.getFessAnalysisService()));
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        final Map<String, AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_baseform", (indexSettings, env, name, settings) -> new JapaneseBaseFormFilterFactory(indexSettings, env,
                name, settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_japanese_part_of_speech",
                (indexSettings, env, name, settings) -> new JapanesePartOfSpeechFilterFactory(indexSettings, env, name, settings,
                        pluginComponent.getFessAnalysisService()));
        extra.put("fess_japanese_readingform", (indexSettings, env, name, settings) -> new JapaneseReadingFormFilterFactory(indexSettings,
                env, name, settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_japanese_stemmer", (indexSettings, env, name, settings) -> new JapaneseKatakanaStemmerFactory(indexSettings, env,
                name, settings, pluginComponent.getFessAnalysisService()));
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        final Map<String, AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();
        extra.put("fess_japanese_tokenizer", (indexSettings, env, name, settings) -> new JapaneseTokenizerFactory(indexSettings, env, name,
                settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_japanese_reloadable_tokenizer",
                (indexSettings, env, name, settings) -> new ReloadableJapaneseTokenizerFactory(indexSettings, env, name, settings,
                        pluginComponent.getFessAnalysisService()));
        extra.put("fess_korean_tokenizer", (indexSettings, env, name, settings) -> new KoreanTokenizerFactory(indexSettings, env, name,
                settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_vietnamese_tokenizer", (indexSettings, env, name, settings) -> new VietnameseTokenizerFactory(indexSettings, env,
                name, settings, pluginComponent.getFessAnalysisService()));
        extra.put("fess_simplified_chinese_tokenizer", (indexSettings, env, name, settings) -> new ChineseTokenizerFactory(indexSettings,
                env, name, settings, pluginComponent.getFessAnalysisService()));
        return extra;
    }

    @Override
    public Collection<SystemIndexDescriptor> getSystemIndexDescriptors(Settings settings) {
        return Collections.unmodifiableList(Arrays.asList(//
                new SystemIndexDescriptor(".crawler.*", "Contains crawler data"), //
                new SystemIndexDescriptor(".suggest", "Contains suggest setting data"), //
                new SystemIndexDescriptor(".suggest_analyzer", "Contains suggest analyzer data"), //
                new SystemIndexDescriptor(".suggest_array.*", "Contains suggest setting data"), //
                new SystemIndexDescriptor(".suggest_badword.*", "Contains suggest badword data"), //
                new SystemIndexDescriptor(".suggest_elevate.*", "Contains suggest elevate data"), //
                new SystemIndexDescriptor(".fess_config.*", "Contains config data for Fess"), //
                new SystemIndexDescriptor(".fess_user.*", "Contains user data for Fess")));
    }

    public static class PluginComponent {
        private FessAnalysisService fessAnalysisService;

        public FessAnalysisService getFessAnalysisService() {
            return fessAnalysisService;
        }

        public void setFessAnalysisService(final FessAnalysisService fessAnalysisService) {
            this.fessAnalysisService = fessAnalysisService;
        }
    }
}
