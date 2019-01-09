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

set TMP_CP=.;%basedir%\jcalendartool-${project.version}.jar;
SET JC_CP=%TMP_CP%%basedir%\lib
echo "JC_CP=%JC_CP%"
 

SET PRO_PARMS=-Xms256m -Xmx512m  
 
 
@rem start "jcalendartool" /B  "%LOCAL_JAVA%" -jar "%basedir%\jcalendartool-${project.version}.jar" %PRO_PARMS%

@rem %LOCAL_JAVA% -jar %basedir%\jcalendartool-${project.version}.jar %PRO_PARMS%


start "jcalendartool" /B  "%LOCAL_JAVA%" -cp %JC_CP%  org.ycalendar.JCalendar %PRO_PARMS%

