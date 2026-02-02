mvn -Pnative -DskipTests native:compile -Dnative.buildArgs="--initialize-at-build-time=org.slf4j,ch.qos.logback -H:+ReportExceptionStackTraces -H:+ReportUnsupportedElementsAtRuntime"
pause