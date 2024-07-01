pipeline {
    agent any

    stages {
        stage('Print All Available Variables') {
            steps {
                script {
                    echo 'Printing all relevant environment variables...'
                    def envVars = [
                        'BRANCH_NAME', 'BRANCH_IS_PRIMARY', 'CHANGE_ID', 'CHANGE_URL', 'CHANGE_TITLE',
                        'CHANGE_AUTHOR', 'CHANGE_AUTHOR_DISPLAY_NAME', 'CHANGE_AUTHOR_EMAIL', 'CHANGE_TARGET',
                        'CHANGE_BRANCH', 'CHANGE_FORK', 'TAG_NAME', 'TAG_TIMESTAMP', 'TAG_UNIXTIME', 'TAG_DATE',
                        'JOB_DISPLAY_URL', 'RUN_DISPLAY_URL', 'RUN_ARTIFACTS_DISPLAY_URL', 'RUN_CHANGES_DISPLAY_URL',
                        'RUN_TESTS_DISPLAY_URL', 'CI', 'BUILD_NUMBER', 'BUILD_ID', 'BUILD_DISPLAY_NAME', 'JOB_NAME',
                        'JOB_BASE_NAME', 'BUILD_TAG', 'EXECUTOR_NUMBER', 'NODE_NAME', 'NODE_LABELS', 'WORKSPACE',
                        'WORKSPACE_TMP', 'JENKINS_HOME', 'JENKINS_URL', 'BUILD_URL', 'JOB_URL', 'GIT_COMMIT',
                        'GIT_PREVIOUS_COMMIT', 'GIT_PREVIOUS_SUCCESSFUL_COMMIT', 'GIT_BRANCH', 'GIT_LOCAL_BRANCH',
                        'GIT_CHECKOUT_DIR', 'GIT_URL', 'GIT_COMMITTER_NAME', 'GIT_AUTHOR_NAME', 'GIT_COMMITTER_EMAIL',
                        'GIT_AUTHOR_EMAIL'
                    ]
                    envVars.each { var ->
                        echo "${var}: ${env[var]}"
                    }
                }
            }
        }

        stage('Print PR Commits') {
            steps {
                script {
                    echo 'Recent Commits:'
                    sh 'git log --format="%h - %s" -n 10'
                }
            }
        }

        stage('Check PR Title') {
            steps {
                script {
                    def prTitle = env.CHANGE_TITLE?.trim() ?: error('PR title is null or empty')
                    echo 'Trimmed PR TITLE: ' + prTitle

                    def titlePattern = ~/(?i)^(feat: SMARTJRNYS-|fix: SMARTJRNYS-).*/
                    if (titlePattern.matcher(prTitle).matches()) {
                        echo 'Title all good'
                    } else {
                        error "PR title does not match the required format (must start with 'feat: SMARTJRNYS-' or 'fix: SMARTJRNYS-')"
                    }
                }
            }
        }

        stage('Check PR Commits') {
            steps {
                script {
                    def commitMessages = sh(script: 'git log --format=%B -n 10', returnStdout: true).trim()
                    def jiraLinkPattern = ~/(?i)https:\/\/jira\.devops\.lloydsbanking\.com\/browse\/SMARTJRNYS\S*/

                    if (jiraLinkPattern.matcher(commitMessages).find()) {
                        echo "Jira link found in commits."
                    } else {
                        error 'No Jira link found in recent commit messages'
                    }
                }
            }
        }
    }
}