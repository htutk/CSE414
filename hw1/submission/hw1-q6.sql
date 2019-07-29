.read hw1-q3.sql

-- return all restaurants I like and haven't
-- visisted more than 3 months ago.	
SELECT *
FROM MyRestaurants
WHERE Favorite = 1 AND 
	date('now', '-3 month') > date(Last_visit);