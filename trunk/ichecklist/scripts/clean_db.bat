@ECHO OFF
REM Script to empty the checklist database.

echo Copying "db\clean_db.sql" to device
adb push db\clean_db.sql /data/data/net.technobuff.ichecklist/databases
echo Cleaning database
adb -s emulator-5554 shell "cd /data/data/net.technobuff.ichecklist/databases; sqlite3 data < clean_db.sql"
echo Done