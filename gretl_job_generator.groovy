def branch = 'main' // ??

def GRETL_JOB_REPO_BASE_URL = 'https://github.com/sogis-themen-config/'
//def jobNamePrefix = 'schema_'

def jobsFile = readFileFromWorkspace('gretl_jobs.txt')
jobsFile.eachLine { line ->
    def binding = ["branch": branch]
    def path = new groovy.text.SimpleTemplateEngine()
        .createTemplate(line)
        .make(binding)    
        .toString()

    def pathEl = path.split("/")

    def theme = pathEl[0]
    def jobName = pathEl[3]

    def gretlJobRepoUrl = GRETL_JOB_REPO_BASE_URL + theme + ".git"

    println(gretlJobRepoUrl+"*****")

    //def jobName = "${jobNamePrefix}${schema}"

    pipelineJob(jobName) {
        properties {
            disableConcurrentBuilds()
        }   

        environmentVariables {
            env('GIT_REPO_URL', gretlJobRepoUrl)
            env('THEME', theme)
            env('JOB_NAME', jobName)
            // branch hier als env
        }

        def pipelineScript = readFileFromWorkspace('Jenkinsfile.gretl')

        definition {
            cps {
                script(pipelineScript)
                sandbox()
            }
        }
    }
}

// listView('GRETL-Jobs') {
//     jobs {
//         regex(/^(?!(schema_|oereb_|oerebv2_|gretl-job-generator-schema|gretl-job-generator-oereb|gretl-job-generator-oerebv2)).*/)
//     }
//     columns {
//         status()
//         weather()
//         name()
//         lastSuccess()
//         lastFailure()
//         lastDuration()
//         buildButton()
//     }
// }

