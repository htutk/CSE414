-- modify the setting for readability
.mode column
.header on

-- create the Edges table that contains int Source and 
-- int Destination
CREATE TABLE Edges 
	(Source integer, 
	Destination integer);

-- insert the given values
INSERT INTO Edges VALUES(10, 5), (6, 25), (1, 3), (4, 4);

-- return all tuples
SELECT * FROM Edges;

-- return Source column for all tuples
SELECT Source FROM Edges;

-- return all tuples where Source > Destination
SELECT * FROM Edges WHERE Source > Destination;

-- test-insert a string-pair value
INSERT INTO Edges VALUES('-1', '2000');

-- why is there no error?
-- Since SQLite supports 'type affinity' on columns, 
-- its rigidly-typed database system will convert
-- the strings into pre-defined affinity integer
-- before inserting the value into the table.