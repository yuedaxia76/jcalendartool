@echo off

 
 

 

if exist %JAVA_HOME%\bin\javaw.exe" (
  set "LOCAL_JAVA=%JAVA_HOME%\bin\javaw.exe"
) else (
  set LOCAL_JAVA=javaw.exe
)



 

set basedir=%~f0
 
:strip
set removed=%basedir:~-1%
set basedir=%basedir:~0,-1%
if NOT "%removed%"=="\" goto strip
 
 echo Using java: %LOCAL_JAVA%
 echo base dir: %basedir%
 
 

SET PRO_PARMS=-Xms256m -Xmx512m 
 
@rem 
start "jcalendartool" /B  "%LOCAL_JAVA%" -jar "%basedir%\jcalendartool-${project.version}.jar" %PRO_PARMS%

@rem %LOCAL_JAVA% -jar %basedir%\jcalendartool-0.2.1.jar %PRO_PARMS%

:ExitForWrongJavaVersion

