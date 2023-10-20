pipeline {
    environment { 
        YOUR_CRED = credentials('dbUriEdit') 
        secretFile = credentials('gretlProperties') 
    }

    agent {
        docker { 
            image 'sogis/gretl:latest' 
            //args "-e foo=bar"
        }
    }
    stages {
        stage('Run schema job') {
            steps {
                sh 'cp $secretFile ./gradle.properties'
                sh 'cat ./gradle.properties'

                git url: "${env.GIT_REPO_URL}", branch: "main", changelog: false
                sh 'ls -la'
                
                sh "gretl -Ptheme=${env.THEME} -Pschema=${env.SCHEMA} fubar"
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}