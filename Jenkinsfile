pipeline {
    environment { 
        secretFile = credentials('gretlProperties') 
    }

    agent {
        docker { 
            image 'sogis/gretl:latest' 
            //args "-e foo=bar"
            args "--network jenkins-backend"
        }
    }
    stages {
        stage('Run schema job') {
            steps {
                git url: "${env.GIT_REPO_URL}", branch: "main", changelog: false
                sh 'pwd'
                sh 'ls -la'
                
                sh 'cp $secretFile ./gradle.properties'
                sh 'cat ./gradle.properties'

                sh "gretl -Ptheme=${env.THEME} -Pschema=${env.SCHEMA} createSchema grantPrivileges"
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}