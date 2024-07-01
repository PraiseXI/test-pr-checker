pipeline {
    agent any

    stages {
        stage('Print All Available Variables') {
            steps {
                script {
                    echo 'Printing all relevant environment variables...'
                    echo "BRANCH_NAME: ${env.BRANCH_NAME}"
                    echo "BRANCH_IS_PRIMARY: ${env.BRANCH_IS_PRIMARY}"
                    echo "CHANGE_ID: ${env.CHANGE_ID}"
                    echo "CHANGE_URL: ${env.CHANGE_URL}"
                    echo "CHANGE_TITLE: ${env.CHANGE_TITLE}"
                    echo "CHANGE_AUTHOR: ${env.CHANGE_AUTHOR}"
                    echo "CHANGE_AUTHOR_DISPLAY_NAME: ${env.CHANGE_AUTHOR_DISPLAY_NAME}"
                    echo "CHANGE_AUTHOR_EMAIL: ${env.CHANGE_AUTHOR_EMAIL}"
                    echo "CHANGE_TARGET: ${env.CHANGE_TARGET}"
                    echo "CHANGE_BRANCH: ${env.CHANGE_BRANCH}"
                    echo "CHANGE_FORK: ${env.CHANGE_FORK}"
                    echo "TAG_NAME: ${env.TAG_NAME}"
                    echo "TAG_TIMESTAMP: ${env.TAG_TIMESTAMP}"
                    echo "TAG_UNIXTIME: ${env.TAG_UNIXTIME}"
                    echo "TAG_DATE: ${env.TAG_DATE}"
                    echo "JOB_DISPLAY_URL: ${env.JOB_DISPLAY_URL}"
                    echo "RUN_DISPLAY_URL: ${env.RUN_DISPLAY_URL}"
                    echo "RUN_ARTIFACTS_DISPLAY_URL: ${env.RUN_ARTIFACTS_DISPLAY_URL}"
                    echo "RUN_CHANGES_DISPLAY_URL: ${env.RUN_CHANGES_DISPLAY_URL}"
                    echo "RUN_TESTS_DISPLAY_URL: ${env.RUN_TESTS_DISPLAY_URL}"
                    echo "CI: ${env.CI}"
                    echo "BUILD_NUMBER: ${env.BUILD_NUMBER}"
                    echo "BUILD_ID: ${env.BUILD_ID}"
                    echo "BUILD_DISPLAY_NAME: ${env.BUILD_DISPLAY_NAME}"
                    echo "JOB_NAME: ${env.JOB_NAME}"
                    echo "JOB_BASE_NAME: ${env.JOB_BASE_NAME}"
                    echo "BUILD_TAG: ${env.BUILD_TAG}"
                    echo "EXECUTOR_NUMBER: ${env.EXECUTOR_NUMBER}"
                    echo "NODE_NAME: ${env.NODE_NAME}"
                    echo "NODE_LABELS: ${env.NODE_LABELS}"
                    echo "WORKSPACE: ${env.WORKSPACE}"
                    echo "WORKSPACE_TMP: ${env.WORKSPACE_TMP}"
                    echo "JENKINS_HOME: ${env.JENKINS_HOME}"
                    echo "JENKINS_URL: ${env.JENKINS_URL}"
                    echo "BUILD_URL: ${env.BUILD_URL}"
                    echo "JOB_URL: ${env.JOB_URL}"
                    echo "GIT_COMMIT: ${env.GIT_COMMIT}"
                    echo "GIT_PREVIOUS_COMMIT: ${env.GIT_PREVIOUS_COMMIT}"
                    echo "GIT_PREVIOUS_SUCCESSFUL_COMMIT: ${env.GIT_PREVIOUS_SUCCESSFUL_COMMIT}"
                    echo "GIT_BRANCH: ${env.GIT_BRANCH}"
                    echo "GIT_LOCAL_BRANCH: ${env.GIT_LOCAL_BRANCH}"
                    echo "GIT_CHECKOUT_DIR: ${env.GIT_CHECKOUT_DIR}"
                    echo "GIT_URL: ${env.GIT_URL}"
                    echo "GIT_COMMITTER_NAME: ${env.GIT_COMMITTER_NAME}"
                    echo "GIT_AUTHOR_NAME: ${env.GIT_AUTHOR_NAME}"
                    echo "GIT_COMMITTER_EMAIL: ${env.GIT_COMMITTER_EMAIL}"
                    echo "GIT_AUTHOR_EMAIL: ${env.GIT_AUTHOR_EMAIL}"
                }
            }
        }

        stage('Debug: Print PR Commits') {
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

        stage('Check PR Commits for Jira Link') {
            steps {
                script {
                    // Fetch commit messages for the current branch
                    def commitMessages = sh(script: 'git log --format=%B -n 10', returnStdout: true).trim()

                    // Search for Jira link in commit messages
                    def jiraLinkFound = false
                    def matcher = commitMessages =~ /(?i)https:\/\/jira\.devops\.lloydsbanking\.com\/browse\/SMARTJRNYS\S*/
                    if (matcher.find()) {
                        jiraLinkFound = true
                        echo "Jira link found in commits: ${matcher.group()}"
                    } else {
                        echo 'No Jira link found in recent commit messages'
                    }

                    // Fail the build if no Jira link is found
                    if (!jiraLinkFound) {
                        error 'No Jira link found in recent commit messages'
                    }
                }
            }
        }
    }
}
