# PureRules: JVM Rules Engine

PureRules is a JVM-based rules engine library designed as a flexible, developer-friendly alternative to traditional engines like Drools. It provides deterministic and probabilistic rule evaluation with comprehensive lifecycle control.

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the information provided here.

## Working Effectively

### Prerequisites and Setup
- Install Java 21 (required for compilation and runtime):
  ```bash
  # Ubuntu/Debian
  sudo apt-get update && sudo apt-get install -y openjdk-21-jdk
  export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
  export PATH=$JAVA_HOME/bin:$PATH
  
  # Verify installation
  java -version  # Should show OpenJDK 21.x.x
  ```

### Build and Test Commands
- Clean, build, and test the complete project:
  ```bash
  ./gradlew clean build
  ```
  **Timing: 10-15 seconds total. NEVER CANCEL - set timeout to 60+ seconds for safety.**

- Build only (without tests):
  ```bash
  ./gradlew build -x test
  ```
  **Timing: 8-10 seconds. NEVER CANCEL - set timeout to 60+ seconds for safety.**

- Run tests only:
  ```bash
  ./gradlew test
  ```
  **Timing: <1 second (58 tests). Set timeout to 30+ seconds for safety.**

- Check code formatting and run all verification tasks:
  ```bash
  ./gradlew check
  ```
  **Timing: 10-15 seconds. NEVER CANCEL - set timeout to 60+ seconds for safety.**

### Measured Build Performance
Based on validation testing, actual observed timings are:
- Clean build: ~4 seconds
- Tests only: <1 second  
- Incremental builds: <1 second
- Full validation sequence: 3-5 seconds total
**Always use conservative timeout values (60+ seconds) to account for system variability.**

### Code Quality and Documentation
- Check code formatting (Google Java Format via Spotless):
  ```bash
  ./gradlew spotlessCheck
  ```
  **Timing: <1 second. Set timeout to 30+ seconds for safety.**

- Auto-fix code formatting issues:
  ```bash
  ./gradlew spotlessApply
  ```

- Generate Javadoc documentation:
  ```bash
  ./gradlew javadoc
  ```
  **Timing: 4-5 seconds. Documentation will be in `build/docs/javadoc/`. Set timeout to 60+ seconds for safety.**

## Validation

- **ALWAYS** run the following validation sequence after making changes:
  1. `./gradlew spotlessApply` - Fix any formatting issues  
  2. `./gradlew check` - Run all checks including tests and linting
  3. `./gradlew build` - Ensure complete build succeeds
  **Expected timing: Total validation sequence takes 3-5 seconds when everything is already built.**
  
- **NEVER** skip the formatting step - the CI pipeline (.github/workflows/build-and-check.yml) will fail if code formatting is incorrect.

- **Manual Testing Scenarios**: Since this is a library, test your changes by:
  1. Running the complete test suite (`./gradlew test`) - should show "58 tests completed" 
  2. Running specific test classes: `./gradlew test --tests "*EngineTest"` or `./gradlew test --tests "*EvaluationServiceTest"`
  3. Examining test outputs in `build/reports/tests/test/index.html`
  4. For new functionality, add tests to the existing test structure rather than standalone programs
  5. Checking that Javadoc generation still works if you modified documentation
  6. Verify examples in README.md still compile by checking relevant test cases

## Project Structure and Key Locations

### Repository Root Structure
```
.
├── README.md              # Project overview and usage examples
├── build.gradle          # Build configuration and dependencies
├── settings.gradle       # Gradle settings
├── .sdkmanrc            # Java version specification (21.0.7-amzn)
├── .github/
│   └── workflows/
│       └── build-and-check.yml  # CI pipeline
├── src/
│   ├── main/java/       # Main source code
│   └── test/java/       # Test code
├── gradlew              # Gradle wrapper script (executable)
├── gradlew.bat         # Gradle wrapper for Windows  
└── thinking_duke.svg   # Project logo
```

### Key Source Packages
Navigate to these locations for different types of work:

- **`src/main/java/com/github/sneakytowelsuit/purerules/`**
  - `engine/` - Core PureRulesEngine implementation and EngineMode configuration
  - `conditions/` - Rule, RuleGroup, Field, and Operator interfaces and implementations  
  - `evaluation/` - DeterministicEvaluationService and ProbabilisticEvaluationService
  - `operators/` - Built-in comparison operators (equals, contains, greater than, etc.)
  - `serialization/` - JSON serialization support for rules and rule groups
  - `context/` - Context management for field caching and evaluation debugging
  - `exceptions/` - Custom exception types for error handling

- **`src/test/java/com/github/sneakytowelsuit/purerules/`**
  - `engine/` - Tests for core engine functionality
  - `evaluation/` - Tests for deterministic and probabilistic evaluation  
  - `operators/` - Tests for all built-in operators
  - `serialization/` - Tests for JSON serialization/deserialization
  - `testutils/` - Helper classes and utilities for testing

### Key Files to Reference
- `README.md` - Contains usage examples and API documentation
- `build.gradle` - Dependencies, build configuration, and custom tasks
- `.github/workflows/build-and-check.yml` - CI pipeline configuration
- `src/main/java/.../engine/PureRulesEngine.java` - Main engine class
- `src/main/java/.../engine/package-info.java` - Engine documentation
- `src/test/java/.../engine/PureRulesEngineTest.java` - Core engine tests

## Common Development Tasks

### When working with rule evaluation logic:
- Always check both `DeterministicEvaluationService` and `ProbabilisticEvaluationService` if your changes affect evaluation
- Update corresponding tests in `src/test/java/.../evaluation/`
- Run specific test classes: `./gradlew test --tests "*EvaluationServiceTest"`

### When adding new operators:
- Implement the `Operator<V>` interface in `src/main/java/.../operators/`
- Add comprehensive tests in `src/test/java/.../operators/`
- Check existing operators for patterns and consistency

### When modifying serialization:
- Update both `RuleSerializer` and `RuleGroupSerializer` if needed
- Always test JSON round-trip serialization/deserialization
- Verify backward compatibility with existing JSON formats

### When working on conditions/rules:
- Remember that rules are immutable after engine instantiation (by design)
- Test both individual rules and rule groups
- Verify combinators (AND/OR) work correctly with your changes

## Build Dependencies and Configuration

### Key Dependencies (from build.gradle)
- **Java 21** (sourceCompatibility and targetCompatibility)
- **Lombok 1.18.38** (compile-time annotation processing)
- **Jackson 2.19.0** (JSON serialization)
- **JUnit 5** (testing framework)  
- **Spotless 7.0.4** (code formatting with Google Java Format)

### Special Build Tasks
- `delombok` - Generates delomboked sources for Javadoc (runs automatically)
- Custom Javadoc configuration points to delomboked sources for proper documentation

## Troubleshooting Common Issues

### Build Failures
- **Java version mismatch**: Ensure you're using Java 21 (check with `java -version`)
- **Lombok compilation issues**: Run `./gradlew clean` and rebuild
- **Formatting failures**: Run `./gradlew spotlessApply` first

### Test Failures  
- All 58 tests should pass in normal builds
- Run individual test classes: `./gradlew test --tests "ClassName"` (e.g., `./gradlew test --tests "*PureRulesEngineTest"`)
- Check test output in `build/reports/tests/test/index.html`  
- Use `--info` or `--debug` flags for more verbose output: `./gradlew test --info`

### IDE Setup
- Import as Gradle project
- Enable annotation processing for Lombok
- Configure code style to use Google Java Format
- Set project SDK to Java 21