def branch = 'main' // ??

def GRETL_JOB_REPO_BASE_URL = 'https://github.com/sogis-themen-config/'
def GRETL_JOB_REPO_BASE_RAW_URL = 'https://raw.githubusercontent.com/sogis-themen-config/'
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

        def jobPropertiesFileLocation = GRETL_JOB_REPO_BASE_RAW_URL + theme + "/main/gretl/" + jobName + "/job.properties"  
        try {
            def jobPropertiesFileContent = new URL(jobPropertiesFileLocation).text
            def is = new ByteArrayInputStream(jobPropertiesFileContent.getBytes());
            jobProperties.load(is)
        } catch (java.io.FileNotFoundException e) {
            println("No job.properties file found: " + theme + " -- " + jobName)
        }

        if (jobProperties.getProperty('logRotator.numToKeep') != 'unlimited') {
            logRotator {
                numToKeep(jobProperties.getProperty('logRotator.numToKeep') as Integer)
            }
        }

        if (jobProperties.getProperty('triggers.cron') != '') { 
            properties {
                pipelineTriggers {
                    triggers {
                        cron {
                        spec(jobProperties.getProperty('triggers.cron'))
                        }
                    }
                }
            }
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

