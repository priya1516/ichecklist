@ECHO OFF
REM Script to sign the application.

set APP_PKG=../dist/iChecklist.apk
set KEYSTORE=..\..\..\..\site\keystore\technobuff.keystore

REM Sign
jarsigner -verbose -keystore %KEYSTORE% %APP_PKG% technobuff

REM Verify
jarsigner -verify -verbose -certs %APP_PKG%