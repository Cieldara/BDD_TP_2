/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  GONTARD Benjamin
 * Created: 21 oct. 2017
 */

/* Test d'ajout et de suppression de maladies, voir compte rendu partie TEST */

SELECT * FROM LesAnimaux where nomA = 'Chloe';

INSERT INTO LesMaladies values ('Chloe','MALADIE');

COMMIT;

SELECT * FROM LesAnimaux where nomA = 'Chloe';

DELETE FROM LesMaladies;

SELECT * FROM LesAnimaux;

COMMIT;
