// Reset the identity counters of all tables
// after each test to ensure new id counters
// for every following test

// Reset users.id
ALTER TABLE IF EXISTS `users`
    ALTER COLUMN `id`
        RESTART WITH 1;

// Reset repos.id
ALTER TABLE IF EXISTS `repos`
    ALTER COLUMN `id`
        RESTART WITH 1;

// Reset docs.id
ALTER TABLE IF EXISTS `docs`
    ALTER COLUMN `id`
        RESTART WITH 1;

// Reset collaborators.id
ALTER TABLE IF EXISTS `collaborators`
    ALTER COLUMN `id`
        RESTART WITH 1;

// Reset history.id
ALTER TABLE IF EXISTS `history`
    ALTER COLUMN `id`
        RESTART WITH 1;
