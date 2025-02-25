ALTER TABLE product_sub_image
	DROP COLUMN created_at,
	DROP COLUMN modified_at,
    MODIFY COLUMN product_portfolio_id BIGINT NOT NULL,
    MODIFY COLUMN product_sub_image_path VARCHAR(255) NOT NULL;
