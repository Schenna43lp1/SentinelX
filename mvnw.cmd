@REM Maven Wrapper (Windows)
@SET MAVEN_PROJECTBASEDIR=%~dp0

@SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@SET DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
@SET WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar

@IF EXIST "%WRAPPER_JAR%" GOTO validateJarFile
@powershell -Command "$webclient = new-object System.Net.WebClient; $webclient.DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%')"
:validateJarFile

@SET JAVA_HOME_TEMP=%JAVA_HOME%
@IF NOT "%JAVA_HOME%"=="" GOTO javaHomeSet
@FOR /F "tokens=*" %%i IN ('where java') DO SET JAVA_EXE=%%i
@GOTO javaFound
:javaHomeSet
@SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
:javaFound

"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -cp "%WRAPPER_JAR%" %WRAPPER_LAUNCHER% %MAVEN_PROJECTBASEDIR% %*
