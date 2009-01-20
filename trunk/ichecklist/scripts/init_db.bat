@ECHO OFF
REM Script to populate the checklist database

adb push db\init_db.sql /data/data/net.technobuff.ichecklist/databases
adb -s emulator-5554 shell "cd /data/data/net.technobuff.ichecklist/databases; sqlite3 data < init_db.sql"
