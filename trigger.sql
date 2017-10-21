CREATE OR REPLACE TRIGGER maj_nb_maladie_on_delete
      AFTER  
        DELETE 
      ON LesMaladies
      FOR EACH ROW  
    BEGIN

    UPDATE lesAnimaux set nb_maladies = nb_maladies-1 WHERE nomA = :old.nomA;

    END;

/

CREATE OR REPLACE TRIGGER maj_nb_maladie_on_insert
      AFTER  
        INSERT 
      ON LesMaladies
      FOR EACH ROW  
    BEGIN

    UPDATE lesAnimaux set nb_maladies = nb_maladies+1 WHERE nomA = :new.nomA;

    END;
/


CREATE OR REPLACE TRIGGER cage_bad_fonction
      BEFORE
        INSERT OR UPDATE ON lesAnimaux
      FOR EACH ROW  
      DECLARE 
        lol varchar2(20);
      BEGIN


      SELECT fonction INTO lol from lesCages where noCage = :new.noCage;
      
        /*Si la fonction */
        IF lol!=:new.fonction_cage THEN
          raise_application_error(-20001, 'ERREUR: Vous tentez dinserer dans une cage avec une mauvaise fonction');
        END IF;
      END;
/


CREATE OR REPLACE TRIGGER non_garde
      BEFORE
        INSERT OR UPDATE ON lesAnimaux
      FOR EACH ROW  
      DECLARE 
        nbGardien NUMBER;
      BEGIN
      
      /*Si pas de gardien pour une cage...*/
      SELECT count(*) INTO nbGardien from LesGardiens where noCage = :new.noCage;

        IF nbGardien<1 THEN
          raise_application_error(-20001, 'BUG');
        END IF;
      END;
/

