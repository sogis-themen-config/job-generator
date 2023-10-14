def organization = 'sogis-themen-config'
def jobGeneratorRepoName = 'job-generator'

repoApi = new URL("https://api.github.com/orgs/${organization}/repos")
repos = new groovy.json.JsonSlurper().parse(repoApi.newReader())
repos.each {
    def repoName = it.name

    if (repoName.equalsIgnoreCase(jobGeneratorRepoName)) return

    job {
        name "${organization}-${repoName}".replaceAll('/','-')
        scm {
            git("git://github.com/${organization}/${repoName}.git", "master")
        }
    }
}
