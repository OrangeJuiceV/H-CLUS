CREATE DATABASE IF NOT EXISTS MapDB;				
USE MapDB;											

DROP TABLE IF EXISTS exampleTab;				
CREATE TABLE exampleTab(					
	X1 float, 
	X2 float, 
	X3 float
);


INSERT INTO exampleTab VALUES(1,2,0); 
INSERT INTO exampleTab VALUES(0,1,-1); 
INSERT INTO exampleTab VALUES(1,3,5); 
INSERT INTO exampleTab VALUES(1,3,4); 
INSERT INTO exampleTab VALUES(2,2,0); 

