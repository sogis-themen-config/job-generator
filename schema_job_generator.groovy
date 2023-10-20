def branch = 'main' // Das kann nicht durch Benutzer gewählt werden? Sonst werden ja alle überschrieben mit dem Branch.

def SCHEMA_JOB_REPO_URL = 'https://github.com/sogis-themen-config/schema-job.git'
def jobNamePrefix = 'schema_'

// schema_jobs.txt kann (?) durch Pipeline nachgeführt werden.
def jobsFile = readFileFromWorkspace('schema_jobs.txt')
jobsFile.eachLine { line ->
    def binding = ["branch": branch]
    def path = new groovy.text.SimpleTemplateEngine()
        .createTemplate(line)
        .make(binding)    
        .toString()

    def pathEl = path.split("/")

    def theme = pathEl[0]
    def schema = pathEl[3]

    def jobName = "${jobNamePrefix}${schema}"

    pipelineJob(jobName) {
        properties {
            disableConcurrentBuilds()
        }   

        environmentVariables {
            env('GIT_REPO_URL', SCHEMA_JOB_REPO_URL)
            env('THEME', theme)
            env('SCHEMA', schema)
        }

        def pipelineScript = readFileFromWorkspace('Jenkinsfile.schema')

        definition {
            cps {
                script(pipelineScript)
                sandbox()
            }
        }
    }
}

listView('Schema-Jobs') {
    jobs {
        regex(/^(schema_.*|gretl-job-generator-schema)/)
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}

