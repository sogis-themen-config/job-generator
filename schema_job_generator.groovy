def organization = 'sogis-themen-config'
def jobGeneratorRepoName = 'job-generator'

repoApi = new URL("https://api.github.com/orgs/${organization}/repos")
repos = new groovy.json.JsonSlurper().parse(repoApi.newReader())
repos.each {
    def repoName = it.name

    if (repoName.equalsIgnoreCase(jobGeneratorRepoName)) return

    pipelineJob(repoName) {
        
        println(repoName+"XXXX")
        
        environmentVariables {
          // make the Git repository URL available on the Jenkins agent
          env('GIT_REPO_URL', repoName)
        }
    }
}