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
      		    	sh 'mvn --batch-mode -Dspring.profiles.active=test -s $MAVEN_SETTINGS_XML clean org.jacoco:jacoco-maven-plugin:prepare-agent install'
      		    	step([$class: 'JacocoPublisher', 
      					execPattern: 'target/*.exec',
      					classPattern: 'target/classes',
      					sourcePattern: 'src/main/java',
      					exclusionPattern: 'src/test*'
					])
      		    }
      		}
    	}
    	stage('Code Analysis') {
      		steps {
      		    // SonarQube
      		    echo "SonarQube"
      		    withSonarQubeEnv(installationName: 'localSonar') {
      		        sh 'mvn sonar:sonar -Dsonar.login=c48bde92f1575bdbbfc6755c45af599ecf3a6e9e'
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
