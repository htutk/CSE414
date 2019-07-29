.read hw1-q3.sql

-- return name and distance of restaurants
-- that are within 20 minutes of commute
-- ordered by alphabetical name
SELECT Name, Distance
FROM MyRestaurants
WHERE Distance <= 20
ORDER BY Name;