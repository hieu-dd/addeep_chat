--liquibase formatted sql
--changeset anhdt:1
REPLACE INTO sticker_packs (id, name, publisher, tray_image_file, animated_sticker_pack)
VALUES (1, 'Little Koala Stickers', 'addeep', '01.png', 1),
       (2, 'Clickable Stickers', 'addeep', '01.png', 1);

REPLACE INTO stickers (id, sticker_pack, image_file, emoji) 
VALUES  (1, 1, '01_Hi.webp', '["π", "π", "π"]'),
        (2, 1, '02_CheerAndDance.webp', '["π", "πͺ", "π"]'),
        (3, 1, '03_GiveLove.webp', '["β€οΈ", "π₯°", "π"]'),
        (4, 1, '04_Like.webp', '["π", "βΊοΈ", "π"]'),
        (5, 1, '05_Broken.webp', '["π’", "π­", "π"]'),
        (6, 1, '06_HealBrokenHeart.webp', '["β€οΈ", "π₯°", "π"]'),
        (7, 1, '07_Question.webp', '["π", "π€", "β"]'),
        (8, 1, '08_Celebration.webp', '["π₯³", "π€©", "π"]'),
        (9, 1, '09_OK.webp', '["π", "π", "π"]'),
        (10, 1, '10_No.webp', '["π", "π", "π"]'),
        (11, 1, '11_WaitingForMessages.webp', '["β³", "π€", "π "]'),
        (12, 1, '12_Cry.webp', '["π­", "π’", "πΏ"]'),
        (13, 1, '13_OnMyWay.webp', '["π£", "πββοΈ", "π"]'),
        (14, 1, '14_Goodnight.webp', '["π₯±", "π΄", "π€"]'),
        (15, 1, '15_NightOwl.webp', '["π©βπ»", "π»", "π"]'),
        (16, 1, '16_ByeBye.webp', '["π", "π₯±", "πββοΈ"]'),
        (17, 2, '01_FeelTheBeat.webp', '["π₯°", "π", "β€οΈ"]'),
        (18, 2, '02_CoffeeSick.webp', '["β", "πͺ", "π"]'),
        (19, 2, '03_Laughing.webp', '["β€οΈ", "π₯°", "π"]'),
        (20, 2, '04_SendingLove.webp', '["π", "π", "β"]'),
        (21, 2, '05_JumpToLove.webp', '["π±", "π―", "π"]'),
        (22, 2, '06_Haha.webp', '["π", "π€£", "π"]'),
        (23, 2, '07_Like.webp', '["π", "βΊοΈ", "π"]'),
        (24, 2, '08_Question.webp', '["β", "β", "π"]'),
        (25, 2, '09_OK.webp', '["π", "π", "π"]'),
        (26, 2, '10_No.webp', '["π", "π", "π"]'),
        (27, 2, '11_Cry.webp', '["π­", "π’", "πΏ"]'),
        (28, 2, '12_NightOwl.webp', '["π©βπ»", "π»", "π"]');
