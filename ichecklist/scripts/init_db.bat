@ECHO OFF
REM Script to initialize the checklist database.

echo Copying "db\init_db.sql" to device
adb push db\init_db.sql /data/data/net.technobuff.ichecklist/databases
echo Initializing database
adb -s emulator-5554 shell "cd /data/data/net.technobuff.ichecklist/databases; sqlite3 data < init_db.sql"
echo Done