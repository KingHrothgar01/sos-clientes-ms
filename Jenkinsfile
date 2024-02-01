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
      		    sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install'
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
      		    configFileProvider([configFile(fileId: '44874500-0411-492f-a487-6df02337c3d6', variable: 'MAVEN_SETTINGS_XML')]){
      		    	sh 'mvn -s $MAVEN_SETTINGS_XML clean package -DskipTests dockerfile:push'
      		    }
      		}
    	}
    	
  	}
}
