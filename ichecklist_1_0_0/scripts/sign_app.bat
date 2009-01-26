@ECHO OFF
REM Script to sign the application.

set APP_VERSION=1.0.0
set APP_PKG=../dist/iChecklist-%APP_VERSION%.apk
set KEYSTORE=..\..\..\..\site\keystore\technobuff.keystore

REM Sign
jarsigner -verbose -keystore %KEYSTORE% %APP_PKG% technobuff

REM Verify
jarsigner -verify -verbose -certs %APP_PKG%