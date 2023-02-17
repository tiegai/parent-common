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
        "cerberusEnv": "china-v2",
        "sdbPath": "shared/notification/credentials",
        // TODO apply for new pipeline creds
        // https://nikedigital.slack.com/archives/C043C89M2FP/p1673256474819139?thread_ts=1673253792.087529&cid=C043C89M2FP
        "userNameKey": "gc-ncp-cds-pipelineuser",
        "passwordKey": "gc-ncp-cds-pipelinepasswordpat"
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