/*participer*/

DELIMITER |
	DROP PROCEDURE IF EXISTS ADD_PARTICIPATION;
	create procedure ADD_PARTICIPATION(NumCan int, NumCom int(25)) 
	BEGIN
		DECLARE etreUneEquipe boolean;
		DECLARE enEquipe boolean;
		SELECT Equipe INTO etreUneEquipe FROM CANDIDAT WHERE NumCandidat = NumCan; 
		SELECT EnEquipe INTO enEquipe FROM COMPETITION WHERE NumComp = NumCom; 
		
		IF(enEquipe && etreUneEquipe) THEN
			INSERT INTO PARTICIPER(NumCandidat, NumComp) VALUES (NumCan, NumCom);
		END IF;
		IF(!enEquipe && !etreUneEquipe) THEN
			INSERT INTO PARTICIPER(NumCandidat, NumComp) VALUES (NumCan, NumCom);
		END IF;
	END;
|
/* pas fait*/
DELIMITER |
	DROP PROCEDURE IF EXISTS DEL_PARTICIPATION;
	create procedure DEL_PARTICIPATION(NumCan int, Num int(25)) 
	BEGIN
		DELETE FROM PARTICIPER  wHERE NumCandidat=NumCan and NumComp=Num;
	END;

|
DELIMITER |
	DROP PROCEDURE IF EXISTS GET_PARTICIPATION;
	create procedure GET_PARTICIPATION () 
	BEGIN

		SELECT NomCandidat, NomComp
		FROM PARTICIPER, CANDIDAT, COMPETITION
		WHERE CANDIDAT.NumCandidat=PARTICIPER.NumCandidat
		AND COMPETITION.NumComp=PARTICIPER.NumComp;
	END;	
|

DELIMITER |
	
	CREATE TRIGGER before_del_comp BEFORE DELETE
	ON COMPETITION FOR EACH ROW
	BEGIN
		
 		DELETE FROM PARTICIPER
 		WHERE NumComp = Old.NumComp;
	END;	
|
