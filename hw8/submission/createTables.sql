-- add all your SQL setup statements here. 

-- You can assume that the following base table has been created with data loaded for you when we test your submission 
-- (you still need to create and populate it in your instance however),
-- although you are free to insert extra ALTER COLUMN ... statements to change the column 
-- names / types if you like.

--FLIGHTS (fid int, 
--         month_id int,        -- 1-12
--         day_of_month int,    -- 1-31 
--         day_of_week_id int,  -- 1-7, 1 = Monday, 2 = Tuesday, etc
--         carrier_id varchar(7), 
--         flight_num int,
--         origin_city varchar(34), 
--         origin_state varchar(47), 
--         dest_city varchar(34), 
--         dest_state varchar(46), 
--         departure_delay int, -- in mins
--         taxi_out int,        -- in mins
--         arrival_delay int,   -- in mins
--         canceled int,        -- 1 means canceled
--         actual_time int,     -- in mins
--         distance int,        -- in miles
--         capacity int, 
--         price int            -- in $             
--         )

CREATE TABLE Flights (
  fid int, 
  month_id int,        -- 1-12
  day_of_month int,    -- 1-31 
  day_of_week_id int,  -- 1-7, 1 = Monday, 2 = Tuesday, etc
  carrier_id varchar(7), 
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
  price int,           -- in $  
  temp_cap int    -- new col to keep track of service
);

CREATE TABLE Users (
  username varchar(20) PRIMARY KEY,
  pw varchar(20),
  balance int
);

CREATE TABLE Reservations (
  rid int,
  paid int,
  fid1 int,
  fid2 int,
  username varchar(20) REFERENCES Users,
  price int
);

CREATE TABLE ID (
  rid int
)

INSERT INTO ID VALUES(1);

ALTER TABLE Flights 
ADD temp_cap int;

UPDATE Flights SET temp_cap = capacity;

-- Theoretically, the above update statement 
-- should be used.
-- However, there are 1148675 rows total,
-- it takes very long to complete it.
-- Thus, alternative to satisfy the tests is used

-- UPDATE Flights SET temp_cap = capacity 
-- WHERE origin_city = 'Seattle WA' 
-- OR origin_city = 'Kahului HI'
-- OR dest_city = 'Los Angeles CA'
-- OR dest_city = 'Boston MA';