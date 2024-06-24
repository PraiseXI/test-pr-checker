pipeline {
    agent any

    stages {
        stage('Test Stage') {
            steps {
                script {
                    echo 'Testing PR Variables...'
                    def prTitle = env.CHANGE_TITLE
                    def prDesc = env.CHANGE_DESCRIPTION
                    echo 'PR TITLE: ' + prTitle
                    echo 'prDescription ' + prDesc
                }
            }
        }

        stage('Check PR Title') {
            steps {
                script {
                    def prTitle = env.CHANGE_TITLE

                    // The title pattern ensures that the title contains either the word "fix" or "feat"
                    def titlePattern = ~/(?i).*\b(fix|feat)\b.*/

                    // Validate title and description
                    if (!titlePattern.matcher(prTitle).find()) {
                        error "PR title does not match the required format (must contain 'fix' or 'feat')"
                    }
                    else {
                        echo 'Title all good'
                    }
                }
            }
        }
    }
}
