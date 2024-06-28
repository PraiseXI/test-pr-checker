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
