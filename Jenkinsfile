// TEA
node {
 
  properties([pipelineTriggers([[$class: 'GitHubPushTrigger']])])

  jdk = tool name: 'adopt-jdk11'
  env.JAVA_HOME = "${jdk}"

  def mvnHome = tool 'maven3'

  configFileProvider(
        [configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
        //sh 'mvn -s $MAVEN_SETTINGS clean package'
    }

    mavenOptions="-s $MAVEN_SETTINGS -B -U -up"

  stage('Clean') {
    checkout scm
    sh "${mvnHome}/bin/mvn -version"
    sh "${mvnHome}/bin/mvn ${mavenOptions} clean"
  }

  stage('Build') {
    sh "${mvnHome}/bin/mvn ${mavenOptions}  compile test-compile"
  }

  stage('Test') {
    sh "${mvnHome}/bin/mvn ${mavenOptions}  -Dmaven.test.failure.ignore test"
    junit '**/target/surefire-reports/TEST-*.xml'
  }

  if (env.BRANCH_NAME == 'dev' || env.BRANCH_NAME == 'master') {
	  
	  stage('Deploy') {
		  sh "${mvnHome}/bin/mvn ${mavenOptions} -Dmaven.test.failure.ignore deploy"
		  step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
	  }

    // Trigger sub builds on dev
    if (env.BRANCH_NAME == 'dev') {
      stage("Downstream") { 
        build job: '../ooxoo-core/dev', wait: false, propagate: false
      } 
      
    }

  } else {
	  
    stage('Package') {
        sh "${mvnHome}/bin/mvn ${mavenOptions} -Dmaven.test.failure.ignore package"
        step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
    }
	
  }

  


}
