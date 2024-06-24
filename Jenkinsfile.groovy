pipeline {
    agent any

    stages {
        stage('Test Stage') {
            steps {
                echo 'Testing... - This should complete'
            }
        }
    
        stage('Echo Env Variables') {
            steps {
		script {
			def prTitle = env.CHANGE_TITLE
			def prDesc = env.CHANGE_DESCRIPTION                
			echo '!PR TITLE: ' prTitle
			echo '!prDescription: ' prDesc
		}
            }
        }
    }
}

