--liquibase formatted sql
--changeset anhdt:1
REPLACE INTO sticker_packs (id, name, publisher, tray_image_file, animated_sticker_pack)
VALUES (1, 'Little Koala Stickers', 'addeep', '01.png', 1),
       (2, 'Clickable Stickers', 'addeep', '01.png', 1);

REPLACE INTO stickers (id, sticker_pack, image_file, emoji) 
VALUES  (1, 1, '01_Hi.webp', '["👋", "🙂", "😁"]'),
        (2, 1, '02_CheerAndDance.webp', '["👏", "💪", "🎉"]'),
        (3, 1, '03_GiveLove.webp', '["❤️", "🥰", "💕"]'),
        (4, 1, '04_Like.webp', '["👍", "☺️", "😉"]'),
        (5, 1, '05_Broken.webp', '["😢", "😭", "💔"]'),
        (6, 1, '06_HealBrokenHeart.webp', '["❤️", "🥰", "😘"]'),
        (7, 1, '07_Question.webp', '["🙄", "🤔", "❓"]'),
        (8, 1, '08_Celebration.webp', '["🥳", "🤩", "🎉"]'),
        (9, 1, '09_OK.webp', '["👌", "🆗", "🙆"]'),
        (10, 1, '10_No.webp', '["👎", "😑", "😕"]'),
        (11, 1, '11_WaitingForMessages.webp', '["⏳", "😤", "😠"]'),
        (12, 1, '12_Cry.webp', '["😭", "😢", "😿"]'),
        (13, 1, '13_OnMyWay.webp', '["👣", "🏃‍♀️", "🏃"]'),
        (14, 1, '14_Goodnight.webp', '["🥱", "😴", "💤"]'),
        (15, 1, '15_NightOwl.webp', '["👩‍💻", "💻", "👀"]'),
        (16, 1, '16_ByeBye.webp', '["👋", "🥱", "🏃‍♀️"]'),
        (17, 2, '01_FeelTheBeat.webp', '["🥰", "😘", "❤️"]'),
        (18, 2, '02_CoffeeSick.webp', '["✊", "💪", "🙏"]'),
        (19, 2, '03_Laughing.webp', '["❤️", "🥰", "💕"]'),
        (20, 2, '04_SendingLove.webp', '["🖐", "🙌", "✋"]'),
        (21, 2, '05_JumpToLove.webp', '["📱", "👯", "👋"]'),
        (22, 2, '06_Haha.webp', '["😂", "🤣", "😆"]'),
        (23, 2, '07_Like.webp', '["👍", "☺️", "😉"]'),
        (24, 2, '08_Question.webp', '["❓", "❔", "😕"]'),
        (25, 2, '09_OK.webp', '["👌", "🆗", "🙆"]'),
        (26, 2, '10_No.webp', '["👎", "😑", "😕"]'),
        (27, 2, '11_Cry.webp', '["😭", "😢", "😿"]'),
        (28, 2, '12_NightOwl.webp', '["👩‍💻", "💻", "👀"]');
