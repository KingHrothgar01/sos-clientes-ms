pipeline {
	agent {label "master"}
  
  	options {
    	buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: '', numToKeepStr: '5')
    	disableConcurrentBuilds()
  	}
  
	stages {
	    stage('Cleanup Workspace') {
      		steps {
      		    // Checkout the code from the repository
      		    echo "Cleanup Workspace"
      		}
    	}
    	stage('Code Checkout') {
      		steps {
      		    // Checkout the code from the repository
        		echo "checkout scm"
      		}
    	}
    	stage('Unit Testing') {
      		steps {
      		    // Build the Java Maven Project
        		sh 'mvn test'
      		}
    	}
    	stage('Coverage') {
      		steps {
      		    // JaCoCo
      		    echo "clean org.jacoco:jacoco-maven-plugin:prepare-agent install"
      		}
    	}
    	stage('Code Analysis') {
      		steps {
      		    // SonarQube
      		    echo "SonarQube"
      		    //withSonarQubeEnv(installationName: 'localSonar') {
      		    //    sh './mvnw clean org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594:sonar'                                   
      		    //}

      		}
    	}
    	stage('Build Deploy Code') {
      		steps {
      		    // Build the Java Maven Project
        		sh 'mvn clean package dockerfile:push'
      		}
    	}
    	
  	}
}