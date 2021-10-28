/*
 * Copyright 2009-2016 the CodeLibs Project and the Others.
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

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.codelibs.opensearch.fess.service.FessAnalysisService;
import org.opensearch.OpenSearchException;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;
import org.opensearch.index.analysis.AbstractCharFilterFactory;
import org.opensearch.index.analysis.CharFilterFactory;

public class TraditionalChineseConvertCharFilterFactory extends AbstractCharFilterFactory {

    private static final String FACTORY = "org.opensearch.index.analysis.STConvertCharFilterFactory";

    private CharFilterFactory charFilterFactory = null;

    public TraditionalChineseConvertCharFilterFactory(final IndexSettings indexSettings, final Environment env, final String name,
            final Settings settings, final FessAnalysisService fessAnalysisService) {
        super(indexSettings, name);

        final Class<?> charFilterFactoryClass = fessAnalysisService.loadClass(FACTORY);
        if (charFilterFactoryClass != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} is found.", FACTORY);
            }
            charFilterFactory = AccessController.doPrivileged((PrivilegedAction<CharFilterFactory>) () -> {
                try {
                    final Constructor<?> constructor =
                            charFilterFactoryClass.getConstructor(IndexSettings.class, Environment.class, String.class, Settings.class);
                    return (CharFilterFactory) constructor.newInstance(indexSettings, env, name, settings);
                } catch (final Exception e) {
                    throw new OpenSearchException("Failed to load " + FACTORY, e);
                }
            });
        } else if (logger.isDebugEnabled()) {
            logger.debug("{} is not found.", FACTORY);
        }
    }

    @Override
    public Reader create(final Reader reader) {
        if (charFilterFactory != null) {
            return charFilterFactory.create(reader);
        }
        return reader;
    }
}