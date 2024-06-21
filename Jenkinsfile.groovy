pipeline {
	agent any

	stages {
		stage('Build') {
			steps {
				echo 'Building....'
			}
		}
		stage('Test') {
				echo 'Testing...'
			}
		}
		stage('Deploy') {
				echo 'Deploying...'
			}
		}
}
