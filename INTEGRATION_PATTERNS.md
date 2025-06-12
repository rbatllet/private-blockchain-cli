# 🔗 Integration Patterns

Complete guide for integrating the Private Blockchain CLI with external systems, automation pipelines, and enterprise environments.

## 📋 Table of Contents

- [CI/CD Integration](#cicd-integration)

## 🚀 CI/CD Integration

### GitHub Actions Integration
```yaml
# .github/workflows/blockchain-test.yml
name: Blockchain CI/CD
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Setup Java 21
        uses: actions/setup-java@v2
        with:
          java-version: '21'
          
      - name: Build JAR
        run: mvn package
        
      - name: Test Blockchain CLI
        run: |
          java -jar target/blockchain-cli.jar --version
          java -jar target/blockchain-cli.jar status
          java -jar target/blockchain-cli.jar add-key "CI-Test" --generate
          java -jar target/blockchain-cli.jar add-block "CI test block" --generate-key
          java -jar target/blockchain-cli.jar validate
          
      - name: Docker Test
        run: |
          docker build -t blockchain-cli .
          docker run blockchain-cli status
```

### Jenkins Pipeline Integration
```groovy
pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Test Blockchain') {
            steps {
                sh '''
                    java -jar target/blockchain-cli.jar status
                    java -jar target/blockchain-cli.jar validate
                '''
            }
        }
        
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh 'docker build -t blockchain-cli:latest .'
                sh 'docker push registry/blockchain-cli:latest'
            }
        }
    }
}
```
