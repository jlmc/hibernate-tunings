INSERT INTO PUBLIC.SHOPPING_LIST (ID, CREATE_AT, DESCRIPTION, RED_LINE) VALUES (1001, '2020-04-11 13:05:02.000000', 'covid-19 2nd week', '2020-04-16 13:05:25.000000');
INSERT INTO PUBLIC.SHOPPING_LIST (ID, CREATE_AT, DESCRIPTION, RED_LINE) VALUES (1002, '2020-03-01 13:05:02.000000', 'covid-19 1nd week', '2020-04-01 13:05:25.000000');

INSERT INTO PUBLIC.TOPIC (ID, NOTES, NUMBER_OF_ITEMS, TITLE, SHOPPING_LIST_ID) VALUES (1001, 'package of 1 litro', 9, 'Milk', 1001);
INSERT INTO PUBLIC.TOPIC (ID, NOTES, NUMBER_OF_ITEMS, TITLE, SHOPPING_LIST_ID) VALUES (1002, '', 1, 'Bear', 1001);
