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
        		echo "Checkout SCM"
      		}
    	}
    	stage('Unit Testing') {
      		steps {
      		    // Build the Java Maven Project
      		    echo "Unit Tests"
        		sh 'mvn test'
      		}
    	}
    	stage('Coverage') {
      		steps {
      		    // JaCoCo
      		    echo "Jacoco"
      		    configFileProvider([configFile(fileId: '44874500-0411-492f-a487-6df02337c3d6', variable: 'MAVEN_SETTINGS_XML')]){
      		    	sh 'mvn --batch-mode -s $MAVEN_SETTINGS_XML clean org.jacoco:jacoco-maven-plugin:prepare-agent'
      		    	step([$class: 'JacocoPublisher', 
      					execPattern: 'target/*.exec',
      					classPattern: 'target/classes',
      					sourcePattern: 'src/main/java',
      					exclusionPattern: ['src/test*', 'com/sosa/SosClientesApplication**']
					])
      		    }
      		}
    	}
    	stage('Code Analysis') {
      		steps {
      		    // SonarQube
      		    echo "SonarQube"
      		    withSonarQubeEnv(installationName: 'localSonar') {
      		        sh 'mvn clean org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594:sonar'                                   
      		    }

      		}
    	}
    	stage('Build Deploy Code') {
      		steps {
      		    // Build the Java Maven Project
      		    echo "Dockerizing Application"
      		    configFileProvider([configFile(fileId: '44874500-0411-492f-a487-6df02337c3d6', variable: 'MAVEN_SETTINGS_XML')]){
      		    	sh 'mvn -s $MAVEN_SETTINGS_XML clean package -DskipTests dockerfile:push'
      		    }
      		}
    	}
  	}
}
