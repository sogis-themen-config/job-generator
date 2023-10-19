def organization = 'sogis-themen-config'
def jobGeneratorRepoName = 'job-generator'

def branch = 'main' // muss als Job-Parameter definiert werden

// Weil es mehrere Jobs geben kann innerhalb eines Repo, reicht das Wissen, dass es ein Repo gibt, nicht aus.
// Eher eine Datei (testweise Array im Code) mit allen Repos und Schema-Jobs.
// Die Datei könnte später ein Pipeline nachführen, wenn einen neuen Schema-Job erstellt (im Themen-Repo).

def jobsFile = readFileFromWorkspace('schema_jobs.txt')
jobsFile.eachLine { line ->
    def binding = ["branch": branch]
    def path = new groovy.text.SimpleTemplateEngine()
    .createTemplate(line)
    .make(binding)    
    .toString()

    def pathEl = path.split("/")

    println path
    println(pathEl[0])
}


repoApi = new URL("https://api.github.com/orgs/${organization}/repos")
repos = new groovy.json.JsonSlurper().parse(repoApi.newReader())
repos.each {
    def repoName = it.name

    if (repoName.equalsIgnoreCase(jobGeneratorRepoName)) return

    pipelineJob(repoName) {
        
        println(repoName+"XXXX")
        print("Hallo Welt.")
        
        environmentVariables {
          // make the Git repository URL available on the Jenkins agent
          env('GIT_REPO_URL', repoName)
        }
    }
}
