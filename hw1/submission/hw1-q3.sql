.read hw1-q2.sql

-- display NULL as "NULL"
-- pre-define all column widths to be  15
.nullvalue "NULL"
.width 15 15 15 15 15

INSERT INTO MyRestaurants values('Mai''s Place', 'Chinese', 10, '2018-11-01', 1);
INSERT INTO MyRestaurants values('Olive Garden', 'Italian', 20, '2016-05-10', 0);
INSERT INTO MyRestaurants values('Taco Truck', 'Mexican', 3, '2019-03-22', NULL);
INSERT INTO MyRestaurants values('Thai Recipe', 'Thai', 40, '2018-12-12', 1);
INSERT INTO MyRestaurants values('Java', 'Indonesian', 60, '2019-01-30', 0);