def branch = 'main' // ??

def GRETL_JOB_REPO_BASE_URL = 'https://github.com/sogis-themen-config/'
def jobPropertiesFileName = 'job.properties'

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
        // properties {
        //     disableConcurrentBuilds()
        // }   

        // set defaults for job properties
        def jobProperties = new Properties([
            'authorization.permissions':'nobody',
            'logRotator.numToKeep':'10',
            'parameters.fileParam':'none',
            'parameters.stringParams':'none',
            'triggers.upstream':'none',
            'triggers.cron':''
        ])

        def folderName = "gretl/${jobName}"
        def jobPropertiesFilePath = "${folderName}/${jobPropertiesFileName}"
        def jobPropertiesFile = new File(WORKSPACE, jobPropertiesFilePath)
        println(jobPropertiesFile) 

        if (jobPropertiesFile.exists()) {
            println '    job properties file found: ' + jobPropertiesFilePath
            jobProperties.load(new FileReader(jobPropertiesFile))
        }

        if (jobProperties.getProperty('logRotator.numToKeep') != 'unlimited') {
            logRotator {
                numToKeep(jobProperties.getProperty('logRotator.numToKeep') as Integer)
            }
        }


        logRotator {
            numToKeep(10)
        }

        environmentVariables {
            env('GRETL_GIT_REPO_URL', gretlJobRepoUrl)
            env('GRETL_THEME', theme)
            env('GRETL_JOB_NAME', jobName)
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

listView('GRETL-Jobs') {
    jobs {
        regex(/^(?!(schema_|oereb_|oerebv2_|gretl-job-generator-schema|gretl-job-generator-oereb|gretl-job-generator-oerebv2)).*/)
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

