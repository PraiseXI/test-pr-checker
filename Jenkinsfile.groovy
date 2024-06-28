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
                    echo 'PR DESCRIPTION: ' + prDesc
                }
            }
        }

        stage('Check PR Title') {
            steps {
                script {
                    def prTitle = env.CHANGE_TITLE
                    if (prTitle == null) {
                        error 'PR title is null'
                    }

                    prTitle = prTitle.trim() // Trim to remove any leading/trailing spaces
                    echo 'Trimmed PR TITLE: ' + prTitle

                    // The title pattern ensures that the title starts with "feat: SMARTJRNYS-" or "fix: SMARTJRNYS-"
                    def titlePattern = ~/(?i)^(feat: SMARTJRNYS-|fix: SMARTJRNYS-).*/

                    // Validate title
                    if (titlePattern.matcher(prTitle).find()) {
                        echo 'Title all good'
                    } else {
                        error "PR title does not match the required format (must start with 'feat: SMARTJRNYS-' or 'fix: SMARTJRNYS-')"
                    }
                }
            }
        }

        stage('Check PR Description') {
            steps {
                script {
                    def prDescription = env.CHANGE_DESCRIPTION
                    if (prDescription == null) {
                        error 'PR description is null'
                    }

                    prDescription = prDescription.trim() // Trim to remove any leading/trailing spaces
                    echo 'Trimmed PR DESCRIPTION: ' + prDescription

                    // The description pattern ensures it contains a link with the base URL
                    def descriptionPattern = ~/(?i)https:\/\/jira\.devops\.lloydsbanking\.com\/browse\/SMARTJRNYS\S*/

                    // Validate description
                    if (descriptionPattern.matcher(prDescription).find()) {
                        echo 'Description all good'
                    } else {
                        error 'PR description does not contain the required Jira link'
                    }
                }
            }
        }
    }
}
