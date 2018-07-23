@ECHO OFF
gradlew cleanCache && gradlew setupDecompWorkspace --refresh-dependencies && gradlew.bat eclipse && pause