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
package org.codelibs.opensearch.fess.analysis;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class EmptyTokenizerTest {

    @Test
    public void testIncrementToken() throws IOException {
        final EmptyTokenizer tokenizer = new EmptyTokenizer();
        tokenizer.setReader(new StringReader("test text"));

        // EmptyTokenizer should never return any tokens
        assertFalse(tokenizer.incrementToken());
        assertFalse(tokenizer.incrementToken());

        tokenizer.close();
    }

    @Test
    public void testIncrementTokenWithEmptyString() throws IOException {
        final EmptyTokenizer tokenizer = new EmptyTokenizer();
        tokenizer.setReader(new StringReader(""));

        // EmptyTokenizer should never return any tokens even with empty input
        assertFalse(tokenizer.incrementToken());

        tokenizer.close();
    }

    @Test
    public void testIncrementTokenMultipleCalls() throws IOException {
        final EmptyTokenizer tokenizer = new EmptyTokenizer();
        tokenizer.setReader(new StringReader("some long text with multiple words"));

        // EmptyTokenizer should consistently return false
        for (int i = 0; i < 10; i++) {
            assertFalse(tokenizer.incrementToken());
        }

        tokenizer.close();
    }
}
