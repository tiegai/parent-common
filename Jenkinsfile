#!groovy
@Library(['cicd-pipeline','cop-pipeline-configuration']) _

String appName = 'ncp-common'
String teamName = 'GC-NCP'

def config = [
    buildFlow: [
        PULL_REQUEST : ['Build', /* 'Quality Gate', */ 'Scan' ],
        BUILD_JAR : ['Build', 'Quality Gate', 'Scan' ],
        PUBLISH_JAR : ['Build', 'Publish'],
    ],
    branchMatcher: [
        BUILD_JAR: ['^(?!main$).*$'],
        PUBLISH_JAR: ['main'],
    ],
    qma: [ configFile: 'quality-config.yaml' ],
    pra: [
        sdbPath: 'shared/bmx-github-cloud/service-user',
        userNameKey: 'username',
        passwordKey: 'password',
    ],
    build : [
        image : "artifactory.nike.com:9002/openjdk:11-jdk",
        cmd : "./gradlew clean build --parallel --daemon --build-cache",
        artifacts : ['build/libs/'],
        cerberus : [
            env: "china-v2",
            sdbPath : "shared/notification/credentials",
            sdbKeys : [
                'sharedU'   : 'SA_U',
                'sharedP'   : 'SA_P',
                'sonarToken': 'SONAR_TOKEN'
            ]
        ],
        cache : [
            tool: 'gradle'
        ]
    ],
    publish : [
        image       : "openjdk:11-jdk",
        cmd         : "./gradlew publish",
        cerberus : [
            env: "china-v2",
            sdbPath: "shared/notification/credentials", //Cerberus credentials path
            sdbKeys: [
                'sharedU'   : 'SA_U', //artifactory user
                'sharedP'   : 'SA_P'
            ]
        ],
        cerberusEnv: "china-v2",
        sdbPath    : "shared/notification/credentials",
        userNameKey: "sharedU",
        passwordKey: "sharedP",
        cache: [
            tool: 'gradle'
        ]
    ],
]

jarLibraryPipeline(config)