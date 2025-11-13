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
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.opensearch.fess.service.FessAnalysisService;
import org.junit.Before;
import org.junit.Test;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;

public class JapaneseReadingFormFilterFactoryTest {

    private IndexSettings indexSettings;
    private Environment environment;
    private FessAnalysisService fessAnalysisService;

    @Before
    public void setUp() {
        indexSettings = mock(IndexSettings.class);
        environment = mock(Environment.class);
        fessAnalysisService = mock(FessAnalysisService.class);

        when(fessAnalysisService.loadClass(anyString())).thenReturn(null);
    }

    @Test
    public void testCreateReturnsOriginalTokenStream() {
        final Settings settings = Settings.EMPTY;
        final JapaneseReadingFormFilterFactory factory =
                new JapaneseReadingFormFilterFactory(indexSettings, environment, "test", settings, fessAnalysisService);

        final TokenStream inputStream = mock(TokenStream.class);
        final TokenStream outputStream = factory.create(inputStream);

        assertNotNull(outputStream);
        assertSame(inputStream, outputStream);
    }

    @Test
    public void testCreateWithUseRomaji() {
        final Settings settings = Settings.builder()
                .put("use_romaji", true)
                .build();

        final JapaneseReadingFormFilterFactory factory =
                new JapaneseReadingFormFilterFactory(indexSettings, environment, "reading_test", settings, fessAnalysisService);

        final TokenStream inputStream = mock(TokenStream.class);
        final TokenStream outputStream = factory.create(inputStream);

        assertNotNull(outputStream);
        assertSame(inputStream, outputStream);
    }
}
