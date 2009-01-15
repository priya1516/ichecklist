# Script to initialize iChecklist database.

BEGIN TRANSACTION;
INSERT INTO "checklist" VALUES(1,'First');
INSERT INTO "checklist_item" VALUES(1,1,0,'Item1');
DELETE FROM sqlite_sequence;
INSERT INTO "sqlite_sequence" VALUES('checklist',5);
INSERT INTO "sqlite_sequence" VALUES('checklist_item',5);
COMMIT;