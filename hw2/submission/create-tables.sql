-- Create tables Flights, Carriers, 
-- Months, Weekdays.
-- The imported csv files must be
-- in "starter-code" folder

.mode csv

PRAGMA foreign_keys=ON;

CREATE TABLE Carriers (
	cid varchar(7) PRIMARY KEY, 
	name varchar(83)
);

.import ../starter-code/carriers.csv Carriers


CREATE TABLE Months (
	mid int PRIMARY KEY,
	month varchar(9)
);

.import ../starter-code/months.csv Months


CREATE TABLE Weekdays (
	did int PRIMARY KEY,
	day_of_week varchar(9)
);

.import ../starter-code/weekdays.csv Weekdays


CREATE TABLE Flights (
	fid int PRIMARY KEY, 
	month_id int REFERENCES Months,        -- 1-12
	day_of_month int,    -- 1-31 
	day_of_week_id int REFERENCES Weekdays,  -- 1-7, 1 = Monday, 2 = Tuesday, etc
	carrier_id varchar(7) REFERENCES Carriers, 
	flight_num int,
	origin_city varchar(34), 
	origin_state varchar(47), 
	dest_city varchar(34), 
	dest_state varchar(46), 
	departure_delay int, -- in mins
	taxi_out int,        -- in mins
	arrival_delay int,   -- in mins
	canceled int,        -- 1 means canceled
	actual_time int,     -- in mins
	distance int,        -- in miles
	capacity int, 
	price int           -- in $   
);

.import ../starter-code/flights-small.csv Flights


.mode column
.headers on