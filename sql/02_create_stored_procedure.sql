-- =====================================================
-- Sakila Film Rental Management System
-- Stored Procedure: sp_manage_film
-- Purpose: Handle both INSERT and UPDATE operations for films
-- Parameters:
--   p_film_id: 0 for INSERT, existing ID for UPDATE
-- =====================================================

USE sakila;

DROP PROCEDURE IF EXISTS sp_manage_film;

DELIMITER $$

CREATE PROCEDURE sp_manage_film(
    IN p_film_id INT,
    IN p_title VARCHAR(255),
    IN p_description TEXT,
    IN p_release_year YEAR,
    IN p_language_id TINYINT,
    IN p_rental_duration TINYINT,
    IN p_rental_rate DECIMAL(4,2),
    IN p_length SMALLINT,
    IN p_replacement_cost DECIMAL(5,2),
    IN p_rating ENUM('G','PG','PG-13','R','NC-17'),
    IN p_special_features SET('Trailers','Commentaries','Deleted Scenes','Behind the Scenes'),
    IN p_category_id TINYINT,
    OUT p_result_film_id INT,
    OUT p_result_message VARCHAR(255)
)
BEGIN
    -- Declare variables for error handling
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        -- Rollback transaction on error
        ROLLBACK;
        SET p_result_message = 'ERROR: Transaction failed. Changes have been rolled back.';
        SET p_result_film_id = -1;
    END;

    -- Start transaction
    START TRANSACTION;

    -- Validate required fields
    IF p_title IS NULL OR p_title = '' THEN
        SET p_result_message = 'ERROR: Title is required';
        SET p_result_film_id = -1;
        ROLLBACK;
    ELSEIF p_language_id IS NULL THEN
        SET p_result_message = 'ERROR: Language is required';
        SET p_result_film_id = -1;
        ROLLBACK;
    ELSEIF p_rental_rate IS NULL OR p_rental_rate < 0 THEN
        SET p_result_message = 'ERROR: Valid rental rate is required';
        SET p_result_film_id = -1;
        ROLLBACK;
    ELSE
        -- Check if this is an INSERT or UPDATE operation
        IF p_film_id = 0 THEN
            -- INSERT new film
            INSERT INTO film (
                title,
                description,
                release_year,
                language_id,
                rental_duration,
                rental_rate,
                length,
                replacement_cost,
                rating,
                special_features,
                last_update
            ) VALUES (
                p_title,
                p_description,
                p_release_year,
                p_language_id,
                COALESCE(p_rental_duration, 3),
                p_rental_rate,
                p_length,
                COALESCE(p_replacement_cost, 19.99),
                COALESCE(p_rating, 'G'),
                p_special_features,
                NOW()
            );

            -- Get the newly inserted film_id
            SET p_result_film_id = LAST_INSERT_ID();

            -- Insert film category mapping if category provided
            IF p_category_id IS NOT NULL THEN
                INSERT INTO film_category (film_id, category_id, last_update)
                VALUES (p_result_film_id, p_category_id, NOW());
            END IF;

            SET p_result_message = CONCAT('SUCCESS: Film created with ID ', p_result_film_id);

        ELSE
            -- UPDATE existing film
            -- First verify the film exists
            IF NOT EXISTS (SELECT 1 FROM film WHERE film_id = p_film_id) THEN
                SET p_result_message = CONCAT('ERROR: Film with ID ', p_film_id, ' does not exist');
                SET p_result_film_id = -1;
                ROLLBACK;
            ELSE
                -- Update film record
                UPDATE film
                SET
                    title = p_title,
                    description = p_description,
                    release_year = p_release_year,
                    language_id = p_language_id,
                    rental_duration = COALESCE(p_rental_duration, rental_duration),
                    rental_rate = p_rental_rate,
                    length = p_length,
                    replacement_cost = COALESCE(p_replacement_cost, replacement_cost),
                    rating = COALESCE(p_rating, rating),
                    special_features = p_special_features,
                    last_update = NOW()
                WHERE film_id = p_film_id;

                -- Update film category mapping
                IF p_category_id IS NOT NULL THEN
                    -- Delete existing category mapping
                    DELETE FROM film_category WHERE film_id = p_film_id;

                    -- Insert new category mapping
                    INSERT INTO film_category (film_id, category_id, last_update)
                    VALUES (p_film_id, p_category_id, NOW());
                END IF;

                SET p_result_film_id = p_film_id;
                SET p_result_message = CONCAT('SUCCESS: Film ID ', p_film_id, ' updated successfully');
            END IF;
        END IF;

        -- Commit transaction
        COMMIT;
    END IF;
END$$

DELIMITER ;

-- Test the stored procedure
-- Test INSERT
CALL sp_manage_film(
    0, -- p_film_id (0 for new)
    'Test Film', -- p_title
    'A test film for demonstration', -- p_description
    2024, -- p_release_year
    1, -- p_language_id (English)
    3, -- p_rental_duration
    4.99, -- p_rental_rate
    120, -- p_length
    19.99, -- p_replacement_cost
    'PG', -- p_rating
    'Trailers,Commentaries', -- p_special_features
    1, -- p_category_id
    @result_id,
    @result_msg
);

SELECT @result_id AS film_id, @result_msg AS message;

-- Test UPDATE (use the ID returned above)
-- CALL sp_manage_film(@result_id, 'Updated Test Film', ...);
