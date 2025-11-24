-- =====================================================
-- Sakila Film Rental Management System
-- VIEW: vw_film_rental_details
-- Purpose: Comprehensive view combining film, category,
--          inventory, rental, and actor information
-- =====================================================

USE sakila;

DROP VIEW IF EXISTS vw_film_rental_details;

CREATE VIEW vw_film_rental_details AS
SELECT
    f.film_id,
    f.title,
    f.description,
    f.release_year,
    f.rental_rate,
    f.rating,
    f.length,
    f.replacement_cost,
    f.special_features,
    c.name AS category_name,
    cat.category_id,
    l.name AS language_name,
    f.language_id,

    -- Rental statistics
    COUNT(DISTINCT r.rental_id) AS rental_count,

    -- Inventory statistics
    COUNT(DISTINCT i.inventory_id) AS total_copies,
    COUNT(DISTINCT CASE WHEN r.return_date IS NULL THEN i.inventory_id END) AS rented_copies,
    COUNT(DISTINCT CASE WHEN r.return_date IS NOT NULL OR r.rental_id IS NULL THEN i.inventory_id END) AS available_copies,

    -- Lead actor (first actor alphabetically)
    (SELECT CONCAT(a.first_name, ' ', a.last_name)
     FROM actor a
     INNER JOIN film_actor fa ON a.actor_id = fa.actor_id
     WHERE fa.film_id = f.film_id
     ORDER BY a.first_name, a.last_name
     LIMIT 1
    ) AS lead_actor_name,

    -- Total revenue from this film
    COALESCE(SUM(p.amount), 0) AS total_revenue,

    -- Last rental date
    MAX(r.rental_date) AS last_rental_date

FROM film f
LEFT JOIN film_category cat ON f.film_id = cat.film_id
LEFT JOIN category c ON cat.category_id = c.category_id
LEFT JOIN language l ON f.language_id = l.language_id
LEFT JOIN inventory i ON f.film_id = i.film_id
LEFT JOIN rental r ON i.inventory_id = r.inventory_id
LEFT JOIN payment p ON r.rental_id = p.rental_id

GROUP BY
    f.film_id,
    f.title,
    f.description,
    f.release_year,
    f.rental_rate,
    f.rating,
    f.length,
    f.replacement_cost,
    f.special_features,
    c.name,
    cat.category_id,
    l.name,
    f.language_id;

-- Test the view
SELECT * FROM vw_film_rental_details LIMIT 10;
