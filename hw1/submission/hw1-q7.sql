.read hw1-q3.sql

-- return all restaurants that are
-- within 10 mins of commute
SELECT *
FROM MyRestaurants
WHERE Distance <= 10;