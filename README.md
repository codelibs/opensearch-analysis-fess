# OpenSearch Analysis Fess Plugin

[![Java CI with Maven](https://github.com/codelibs/opensearch-analysis-fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/opensearch-analysis-fess/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.codelibs.opensearch/opensearch-analysis-fess)](https://central.sonatype.com/artifact/org.codelibs.opensearch/opensearch-analysis-fess)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](LICENSE)

OpenSearch Analysis Fess Plugin provides the analysis components that
[Fess](https://github.com/codelibs/fess) refers to in its index mappings, and
registers the Fess system indices with OpenSearch.

Rather than implementing tokenization itself, the plugin registers stable
`fess_*` names that delegate at runtime to whichever language analysis plugin is
installed. This lets Fess ship a single set of mappings that works on clusters
with different combinations of analysis plugins: if the underlying plugin for a
language is present the component behaves exactly like it, and if it is not, the
component degrades quietly instead of failing the index.

## Compatibility

| Plugin Version | OpenSearch Version | Lucene Version | Java Version |
|----------------|--------------------|----------------|--------------|
| 3.9.x          | 3.9.x              | 10.5.x         | 21+          |
| 3.8.x          | 3.8.x              | 10.5.x         | 21+          |
| 3.7.x          | 3.7.x              | 10.4.x         | 21+          |
| 3.2.x          | 3.2.x              | 10.2.x         | 21+          |
| 3.1.x          | 3.1.x              | 10.x           | 21+          |

Released versions are listed in the
[CodeLibs repository](https://maven.codelibs.org/release/org/codelibs/opensearch/opensearch-analysis-fess/).
Version 3.8.0 and earlier were published to
[Maven Central](https://central.sonatype.com/artifact/org.codelibs.opensearch/opensearch-analysis-fess/versions).

## Installation

```bash
$OPENSEARCH_HOME/bin/opensearch-plugin install https://maven.codelibs.org/release/org/codelibs/opensearch/opensearch-analysis-fess/3.9.0/opensearch-analysis-fess-3.9.0.zip
```

Restart the node, then confirm that the plugin is loaded:

```bash
$OPENSEARCH_HOME/bin/opensearch-plugin list
# analysis-fess
```

To install a locally built package instead:

```bash
mvn clean package
$OPENSEARCH_HOME/bin/opensearch-plugin install file:target/releases/opensearch-analysis-fess-3.9.0-SNAPSHOT.zip
```

Use `opensearch-plugin remove analysis-fess` to uninstall.

## Analysis Components

Each component looks for its delegates in the order listed and uses the first one
available. Install the plugins for the languages you actually need.

### Tokenizers

| Name | Delegates to | Provided by |
|------|--------------|-------------|
| `fess_japanese_tokenizer` | `KuromojiTokenizerFactory` | [analysis-extension](https://github.com/codelibs/opensearch-analysis-extension), or the bundled `analysis-kuromoji` |
| `fess_japanese_reloadable_tokenizer` | `KuromojiTokenizerFactory` | same as above |
| `fess_korean_tokenizer` | `NoriTokenizerFactory` | `analysis-nori` |
| `fess_simplified_chinese_tokenizer` | `SmartChineseTokenizerTokenizerFactory` | `analysis-smartcn` |
| `fess_vietnamese_tokenizer` | `org.codelibs.opensearch.vi.analysis.VietnameseTokenizerFactory` | a plugin providing that class |

### Token Filters

| Name | Delegates to | Provided by |
|------|--------------|-------------|
| `fess_japanese_baseform` | `KuromojiBaseFormFilterFactory` | analysis-extension, or `analysis-kuromoji` |
| `fess_japanese_part_of_speech` | `KuromojiPartOfSpeechFilterFactory` | analysis-extension, or `analysis-kuromoji` |
| `fess_japanese_readingform` | `KuromojiReadingFormFilterFactory` | analysis-extension, or `analysis-kuromoji` |
| `fess_japanese_stemmer` | `KuromojiKatakanaStemmerFactory` | analysis-extension, or `analysis-kuromoji` |

### Character Filters

| Name | Delegates to | Provided by |
|------|--------------|-------------|
| `fess_japanese_iteration_mark` | `KuromojiIterationMarkCharFilterFactory` | analysis-extension, or `analysis-kuromoji` |
| `fess_traditional_chinese_convert` | `STConvertCharFilterFactory` | `analysis-stconvert` |

Settings are passed through to the delegate unchanged, so the options documented
for the underlying component apply here as well.

### Behaviour When a Delegate Is Missing

If no delegate can be found, index creation still succeeds:

- a tokenizer produces no tokens
- a token filter and a character filter pass their input through unchanged

Enable debug logging to see which delegate was selected for each component:

```yaml
logger.org.codelibs.opensearch.fess: DEBUG
```

## Usage

```bash
curl -XPUT 'localhost:9200/documents' -H 'Content-Type: application/json' -d '{
  "settings": {
    "analysis": {
      "analyzer": {
        "fess_japanese_analyzer": {
          "type": "custom",
          "char_filter": ["fess_japanese_iteration_mark"],
          "tokenizer": "fess_japanese_tokenizer",
          "filter": ["fess_japanese_baseform", "fess_japanese_part_of_speech"]
        },
        "fess_chinese_analyzer": {
          "type": "custom",
          "char_filter": ["fess_traditional_chinese_convert"],
          "tokenizer": "fess_simplified_chinese_tokenizer"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": { "type": "text", "analyzer": "fess_japanese_analyzer" },
      "title":   { "type": "text", "analyzer": "fess_chinese_analyzer" }
    }
  }
}'
```

Analyzers are defined per index, so the analyze API has to be called against the
index that defines them:

```bash
curl -XGET 'localhost:9200/documents/_analyze' -H 'Content-Type: application/json' -d '{
  "analyzer": "fess_japanese_analyzer",
  "text": "これはテストです"
}'
```

For a complete, production-tested configuration see the
[Fess index mappings](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess.json).

## System Indices

The plugin registers the indices Fess uses as OpenSearch system indices, which
keeps them out of ordinary wildcard operations:

`.crawler.*`, `.suggest`, `.suggest_analyzer`, `.suggest_array.*`,
`.suggest_badword.*`, `.suggest_elevate.*`, `.fess_config.*`, `.fess_user.*`

## Building from Source

Java 21 and Maven 3.6 or later are required.

```bash
git clone https://github.com/codelibs/opensearch-analysis-fess.git
cd opensearch-analysis-fess
mvn clean package
```

The plugin package is written to `target/releases/`.

```bash
mvn test              # run the test suite
mvn license:check     # verify license headers
mvn license:format    # apply license headers
```

## Contributing

Issues and pull requests are welcome at
[github.com/codelibs/opensearch-analysis-fess](https://github.com/codelibs/opensearch-analysis-fess).
Please add tests for behaviour changes, keep the Apache License 2.0 headers in
place, and make sure `mvn test` and `mvn license:check` pass before opening a pull
request.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.
