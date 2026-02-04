@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION

REM create bin directory if it doesn't exist
if not exist ..\bin mkdir ..\bin

REM delete output from previous run
if exist ACTUAL.TXT del ACTUAL.TXT

REM delete old tasks.txt from previous run
if exist data\tasks.txt del data\tasks.TXT

set SRC=..\src\main\java

REM build a list of all Java files under src
set FILES=
for /R "%SRC%" %%f in (*.java) do set FILES=!FILES! "%%f"

javac -Xlint:none -d ..\bin -cp "%SRC%" %FILES%
IF ERRORLEVEL 1 (
    echo ********** BUILD FAILURE **********
    exit /b 1
)

REM no error here, errorlevel == 0

REM run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
java -classpath ..\bin revel.core.Revel < input.txt > ACTUAL.TXT

REM compare the output to the expected output
FC ACTUAL.TXT EXPECTED.TXT

pause
