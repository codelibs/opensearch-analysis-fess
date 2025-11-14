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
package org.codelibs.opensearch.fess.index.analysis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.lucene.analysis.Tokenizer;
import org.codelibs.opensearch.fess.analysis.EmptyTokenizer;
import org.codelibs.opensearch.fess.service.FessAnalysisService;
import org.junit.Before;
import org.junit.Test;
import org.opensearch.Version;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.common.settings.Settings;
import org.opensearch.core.index.Index;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;

public class KoreanTokenizerFactoryTest {

    private IndexSettings indexSettings;
    private Environment environment;
    private FessAnalysisService fessAnalysisService;

    @Before
    public void setUp() {
        final Index index = new Index("test_index", "test_uuid");
        final Settings nodeSettings = Settings.builder().build();
        indexSettings = new IndexSettings(
            IndexMetadata.builder("test_index")
                .settings(Settings.builder()
                    .put(IndexMetadata.SETTING_VERSION_CREATED, Version.CURRENT)
                    .put(IndexMetadata.SETTING_NUMBER_OF_SHARDS, 1)
                    .put(IndexMetadata.SETTING_NUMBER_OF_REPLICAS, 0))
                .build(),
            nodeSettings
        );
        environment = mock(Environment.class);
        fessAnalysisService = mock(FessAnalysisService.class);

        when(fessAnalysisService.loadClass(anyString())).thenReturn(null);
    }

    @Test
    public void testCreateReturnsEmptyTokenizer() {
        final Settings settings = Settings.EMPTY;
        final KoreanTokenizerFactory factory =
                new KoreanTokenizerFactory(indexSettings, environment, "test", settings, fessAnalysisService);

        final Tokenizer tokenizer = factory.create();

        assertNotNull(tokenizer);
        assertTrue(tokenizer instanceof EmptyTokenizer);
    }

    @Test
    public void testCreateWithDecompoundMode() {
        final Settings settings = Settings.builder()
                .put("decompound_mode", "mixed")
                .build();

        final KoreanTokenizerFactory factory =
                new KoreanTokenizerFactory(indexSettings, environment, "korean_test", settings, fessAnalysisService);

        final Tokenizer tokenizer = factory.create();

        assertNotNull(tokenizer);
        assertTrue(tokenizer instanceof EmptyTokenizer);
    }
}
