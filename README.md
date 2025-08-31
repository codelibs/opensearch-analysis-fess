# OpenSearch Analysis Fess Plugin

[![Java CI with Maven](https://github.com/codelibs/opensearch-analysis-fess/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/opensearch-analysis-fess/actions/workflows/maven.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.codelibs.opensearch/opensearch-analysis-fess/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.codelibs.opensearch/opensearch-analysis-fess)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A comprehensive analysis plugin for OpenSearch that provides custom analyzers, tokenizers, and filters specifically designed for Fess. This plugin enhances full-text search capabilities with advanced linguistic processing for multiple languages including Japanese, Korean, Chinese (Simplified/Traditional), and Vietnamese.

## Key Features

- **Multi-language Tokenization**: Advanced tokenizers for Japanese, Korean, Vietnamese, and Chinese text processing
- **Japanese Language Processing**: Comprehensive Japanese analysis with iteration mark handling, base form filtering, part-of-speech filtering, reading form conversion, and katakana stemming
- **Chinese Language Support**: Traditional Chinese character conversion and simplified Chinese tokenization
- **System Index Management**: Automatic management of Fess-specific system indices
- **Reloadable Configuration**: Support for dynamic Japanese tokenizer configuration reloading
- **OpenSearch Integration**: Native integration with OpenSearch 3.2.0 and Lucene 10.2.2

## Tech Stack

- **Java**: 21
- **OpenSearch**: 3.2.0
- **Apache Lucene**: 10.2.2
- **Build Tool**: Apache Maven 3.x
- **Testing**: JUnit 4.13.2

## Quick Start

### Prerequisites

- Java 21 or higher
- OpenSearch 3.2.0 or compatible version
- Apache Maven 3.6+ (for building from source)

### Installation

#### Option 1: Install from Maven Repository

```bash
$OPENSEARCH_HOME/bin/opensearch-plugin install org.codelibs:opensearch-analysis-fess:3.2.0
```

#### Option 2: Build and Install from Source

```bash
# Clone the repository
git clone https://github.com/codelibs/opensearch-analysis-fess.git
cd opensearch-analysis-fess

# Build the plugin
mvn clean package

# Install the plugin
$OPENSEARCH_HOME/bin/opensearch-plugin install file:///path/to/target/releases/opensearch-analysis-fess-3.2.0.jar
```

### Restart OpenSearch

After installation, restart your OpenSearch cluster to activate the plugin.

## Usage

### Available Analysis Components

#### Tokenizers

- `fess_japanese_tokenizer` - Japanese text tokenization with morphological analysis
- `fess_japanese_reloadable_tokenizer` - Reloadable Japanese tokenizer with dynamic configuration
- `fess_korean_tokenizer` - Korean text tokenization
- `fess_vietnamese_tokenizer` - Vietnamese text tokenization  
- `fess_simplified_chinese_tokenizer` - Simplified Chinese text tokenization

#### Token Filters

- `fess_japanese_baseform` - Converts Japanese tokens to their base forms
- `fess_japanese_part_of_speech` - Filters Japanese tokens by part-of-speech tags
- `fess_japanese_readingform` - Converts Japanese tokens to reading forms (hiragana/katakana)
- `fess_japanese_stemmer` - Japanese katakana stemming

#### Character Filters

- `fess_japanese_iteration_mark` - Handles Japanese iteration marks (々, ゝ, ゞ, etc.)
- `fess_traditional_chinese_convert` - Converts between Traditional and Simplified Chinese

### Configuration Example

```json
{
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
      "content": {
        "type": "text",
        "analyzer": "fess_japanese_analyzer"
      },
      "title": {
        "type": "text",
        "analyzer": "fess_chinese_analyzer"
      }
    }
  }
}
```

### Testing the Plugin

```bash
# Test Japanese analysis
GET /_analyze
{
  "analyzer": "fess_japanese_analyzer", 
  "text": "これはテストです"
}

# Test Chinese analysis
GET /_analyze
{
  "analyzer": "fess_chinese_analyzer",
  "text": "这是一个测试"
}
```

## Development

### Project Structure

```
opensearch-analysis-fess/
├── src/main/java/org/codelibs/opensearch/fess/
│   ├── FessAnalysisPlugin.java              # Main plugin entry point
│   ├── service/FessAnalysisService.java     # Core analysis service
│   ├── index/analysis/                      # Analysis component factories
│   │   ├── JapaneseTokenizerFactory.java
│   │   ├── KoreanTokenizerFactory.java
│   │   ├── ChineseTokenizerFactory.java
│   │   ├── VietnameseTokenizerFactory.java
│   │   └── ...                              # Additional filter factories
│   └── analysis/EmptyTokenizer.java         # Utility components
├── src/main/plugin-metadata/                # Plugin configuration
├── src/test/java/                          # Unit tests
├── pom.xml                                 # Maven build configuration
└── README.md
```

### Development Setup

1. **Clone and Build**:
   ```bash
   git clone https://github.com/codelibs/opensearch-analysis-fess.git
   cd opensearch-analysis-fess
   mvn compile
   ```

2. **Run Tests**:
   ```bash
   mvn test
   ```

3. **Check Code Quality**:
   ```bash
   mvn license:check
   mvn javadoc:javadoc
   ```

4. **Build Plugin Package**:
   ```bash
   mvn package
   # Plugin ZIP will be created in target/releases/
   ```

### Build Commands

| Command | Description |
|---------|-------------|
| `mvn compile` | Compile source code |
| `mvn test` | Run unit tests |
| `mvn package` | Build plugin JAR (output: target/releases/) |
| `mvn -B package --file pom.xml` | Full CI build |
| `mvn license:check` | Verify license headers |
| `mvn license:format` | Add/fix license headers |
| `mvn javadoc:javadoc` | Generate documentation |

## Troubleshooting

### Common Issues

1. **Plugin Installation Fails**
   ```bash
   # Check OpenSearch logs
   tail -f $OPENSEARCH_HOME/logs/opensearch.log
   
   # Verify plugin compatibility
   $OPENSEARCH_HOME/bin/opensearch-plugin list
   ```

2. **Analysis Components Not Available**
   - Ensure OpenSearch was restarted after plugin installation
   - Verify plugin is properly loaded: `GET /_cat/plugins`

3. **Build Issues**
   ```bash
   # Clean build
   mvn clean package
   
   # Check Java version
   java -version  # Should be 21+
   ```

### Getting Help

- **Issues**: [Report issues on GitHub](https://github.com/codelibs/opensearch-analysis-fess/issues)
- **Fess Documentation**: [Fess Official Documentation](https://fess.codelibs.org/)
- **OpenSearch Plugin Development**: [OpenSearch Plugin Development Guide](https://opensearch.org/docs/latest/dev-tools/plugins/)

## Contributing

We welcome contributions! Please follow these guidelines:

1. **Code Style**: Follow existing Java conventions and code formatting
2. **License Headers**: All Java files must include Apache License 2.0 headers
3. **Testing**: Add unit tests for new features and ensure existing tests pass
4. **Documentation**: Update documentation for new features

### Contribution Workflow

```bash
# Fork the repository and create a feature branch
git checkout -b feature/your-feature-name

# Make your changes and run tests
mvn test
mvn license:check

# Commit and push your changes
git commit -m "Add your feature description"
git push origin feature/your-feature-name

# Create a pull request
```

## Version Compatibility

| Plugin Version | OpenSearch Version | Lucene Version | Java Version |
|---------------|-------------------|----------------|--------------|
| 3.2.x         | 3.2.x            | 10.2.x         | 21+          |
| 3.1.x         | 3.1.x            | 10.x           | 21+          |

For older versions, check the [Maven Repository](https://repo1.maven.org/maven2/org/codelibs/opensearch/opensearch-analysis-fess/).

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

- **GitHub Issues**: [Report bugs or request features](https://github.com/codelibs/opensearch-analysis-fess/issues)
- **Developer**: Shinsuke Sugaya ([@codelibs](https://github.com/codelibs))
- **Organization**: [CodeLibs Project](https://www.codelibs.org/)

---

**Note**: This plugin is designed to work specifically with the [Fess](https://github.com/codelibs/fess) enterprise search server. For complete Fess integration examples, refer to the [Fess mapping configuration](https://github.com/codelibs/fess/blob/master/src/main/resources/fess_indices/fess.json).
