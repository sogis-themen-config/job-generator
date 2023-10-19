# job-generator

```
pipeline {
    environment { 
        YOUR_CRED = credentials('dbUriEdit') 
        secretFile = credentials('gretlProperties') 
    }

    agent {
        docker { 
            image 'sogis/gretl:latest' 
            //args "--env-file $secretfile"
        }
    }
    stages {
        stage('Run schema job') {
            steps {
                sh 'cp $secretFile ./gradle.properties'
                sh 'cat ./gradle.properties'

                git url: "https://github.com/sogis-themen-config/schema-job.git", branch: "main", changelog: false
                sh 'ls -la'
                sh "gretl fubar"
                //echo env.SCHEMA
                //echo "To call username use ${YOUR_CRED}"
            }
        }
    }
    post {
        // Clean after build
        always {
            cleanWs()
        }
    }
}
```

```
pipeline {
    environment { 
        YOUR_CRED = credentials('dbUriEdit') 
        secretFile = credentials('gretlProperties') 
    }

    agent {
        docker { 
            image 'sogis/gretl:latest' 
            //args "--env-file $secretfile"
        }
    }
    stages {
        stage('Run schema job') {
            steps {
                sh 'cp $secretFile ./gradle.properties'
                sh 'cat ./gradle.properties'

                git url: "https://github.com/sogis-themen-config/schema-job.git", branch: "main", changelog: false
                sh 'ls -la'
                
                sh "gretl -Ptheme=${env.THEME} -Pschema=${env.SCHEMA} fubar"
                //echo env.SCHEMA
                //echo "To call username use ${YOUR_CRED}"
            }
        }
    }
    /*
    post {
        // Clean after build
        always {
            cleanWs()
        }
    }
    */
}
```