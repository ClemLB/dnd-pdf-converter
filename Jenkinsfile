void setBuildStatus(String message, String state) {
  step([
      $class: "GitHubCommitStatusSetter",
      reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/ClemLB/dnd-pdf-manager"],
      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}

def customImage;
pipeline {
	agent any
	options {
		buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
		disableConcurrentBuilds()
		preserveStashes()
		timeout(time: 2, unit: 'HOURS')
	}
	parameters {
		booleanParam(name: "RELEASE", description: "Build a release from current commit", defaultValue: false)
	}
	tools {
		maven 'Maven 3.9.9'
        jdk 'Java 17'
    }
    stages {
		stage ('Initialize') {
			steps {
				sh '''
                    echo "PATH = ${M2_HOME}/bin:${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }
		stage ('Build') {
			when {
				expression {
					!(params.RELEASE && env.BRANCH_NAME == 'main')
				}
			}
			steps {
				sh 'mvn -DskipTests=true clean install'
				script {
					pom = readMavenPom file: 'pom.xml'
					currentVersion = pom.getVersion()
					currentBuild.displayName = "#"+env.BUILD_ID + " - " + currentVersion
                }
            }
        }
        stage ('Test') {
			when {
				expression {
					!(params.RELEASE && env.BRANCH_NAME == 'main')
				}
			}
			steps {
				sh 'mvn verify'
            }
        }
        stage ('Release') {
			when {
				expression {
					params.RELEASE && env.BRANCH_NAME == 'main'
				}
			}
			steps {
				sh "mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.incrementalVersion} versions:commit"
				sh "mvn clean install"
				script {
					pom = readMavenPom file: 'pom.xml'
					currentVersion = pom.getVersion()
					currentBuild.displayName = "#"+env.BUILD_ID + " - " + currentVersion
					customImage = docker.build("astrofia-app/dnd-pdf-manager")
                }
			}
		}
		stage ('Deploy') {
			when {
				expression {
					params.RELEASE && env.BRANCH_NAME == 'main'
				}
			}
			steps {
			    script {
                    docker.withRegistry("https://astrofia-app.ddns.net:5001", "DOCKER_REGISTRY_CREDENTIALS") {
                        customImage.push(currentVersion)
                        customImage.push("latest")
                    }
                }
				sh "mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion}-SNAPSHOT versions:commit"
				withCredentials([gitUsernamePassword(credentialsId: 'GITHUB_CREDENTIALS', gitToolName: 'Default')]) {
					sh "git commit -am '[Jenkins] Bumping version'"
					sh "git push origin HEAD:${env.BRANCH_NAME}"
				}
			}
		}
    }
    post {
        success {
            setBuildStatus("Build succeeded", "SUCCESS");
        }
        failure {
            setBuildStatus("Build failed", "FAILURE");
        }
      }
}