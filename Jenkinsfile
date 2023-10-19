pipeline {
    agent {
        docker { image 'sogis/gretl:latest' }
    }
    environment { 
        YOUR_CRED = credentials('dbUriEdit') 
    }
    stages {
        stage('GRETL') {
            steps {
                sh 'gradle --version'
                echo env.SCHEMA
                echo "To call username use ${YOUR_CRED}"
            }
        }
    }
}
