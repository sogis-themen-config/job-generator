pipeline {
    agent {
        docker { image 'sogis/gretl:latest' }
    }
    stages {
        stage('Test') {
            steps {
                sh 'gradle --version'
                echo env.SCHEMA
            }
        }
    }
}
