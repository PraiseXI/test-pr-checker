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
                    for (var in envVars) {
                        echo "${var}: ${env.getProperty(var)}"
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
                    // Fetch and trim the PR title
                    def prTitle = env.CHANGE_TITLE?.trim() ?: error('PR title is null or empty')
                    echo 'Trimmed PR TITLE: ' + prTitle

                    // Define the title pattern
                    def titlePattern = ~/(?i)^(feat: SMARTJRNYS-|fix: SMARTJRNYS-).*/

                    // Validate the PR title against the pattern
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
                    // Fetch commit messages
                    def commitMessages = sh(script: 'git log --format=%B -n 1', returnStdout: true).trim().split('\n')
                    // Define the Jira link pattern
                    def jiraLinkPattern = ~/(?i)^(feat: SMARTJRNYS-|fix: SMARTJRNYS-).*/

                    // Flag to check if a Jira link is found
                    def jiraLinkFound = false

                    // Check if any commit message contains a Jira link
                    commitMessages.each { commitMessage ->
                        echo "Checking commit message: ${commitMessage}"
                        if (jiraLinkPattern.matcher(commitMessage).matches()) {
                            echo "Format correct in commit message: ${commitMessage}"
                            jiraLinkFound = true
                        }
                    }

                    // Error if no Jira link is found
                    if (!jiraLinkFound) {
                       echo "Format NOT correct in commit message: ${commitMessage}"
                    }
                }
            }
        }
    }
}
