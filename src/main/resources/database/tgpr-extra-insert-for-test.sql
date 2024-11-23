
--avant d'insérer les données faut faire un reset de la base de données


INSERT INTO `account` (iban, title, floor, type, saldo)
VALUES ('BE39 3456 7891 0112', 'JUL', -20, 'checking', 7),
       ('BE88 4006 0069 4306', 'SAV', 0, 'savings', 60),
       ('BE44 7463 3333 2222', 'LOU',0, 'checking', 13);





INSERT INTO `access` (user, account, type)
VALUES (7, 6, 'holder'),
       (7, 7, 'proxy'),
       (6, 8, 'holder');




INSERT INTO `transfer` (amount, description, source_account, target_account, source_saldo,
                        target_saldo, created_at, created_by, effective_at, state)
VALUES (80, 'TEST1', 5, 6, null, 80, '2022-01-05 19:14:44', null, null, 'executed'),
       (60, 'TEST2', 6, 7, 20, 60, '2022-01-09 12:30:48', 7, null, 'executed'),
       (13, 'TEST3', 6, 8, 7, 13, '2022-01-11 23:09:28', 7, '2022-01-14', 'executed'),
       (10, 'TEST4', 8, 5, null, null, '2022-01-12 15:20:41', 6, '2022-01-18', 'future'),
       (50, 'TEST5', 6, 5, null, null, '2022-01-12 22:19:42', 7, '2022-01-15', 'rejected');






--pour supprimer les donnees
      
delete from account where iban IN ('BE39 3456 7891 0112', 'BE88 4006 0069 4306', 'BE44 7463 3333 2222');
delete from access where user IN (7, 6);
delete from transfer where description IN ('TEST1','TEST2','TEST3','TEST4','TEST5');



