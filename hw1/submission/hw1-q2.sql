.mode column
.header on

-- variable     varchar_length
-- Name         100
-- Type		   100
-- Last_visit   10  
-- Last_visit has date format 'YYYY-MM-DD' 
-- Note: pre-defining varchar length does not actually matter
CREATE TABLE MyRestaurants 
	(Name varchar(100), 
	Type varchar(100), 
	Distance integer, 
	Last_visit varchar(10), 
	Favorite integer);