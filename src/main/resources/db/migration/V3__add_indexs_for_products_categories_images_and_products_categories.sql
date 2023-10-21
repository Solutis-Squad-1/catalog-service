-- Index for table products
CREATE INDEX idx_products_seller_id ON products (seller_id);
CREATE INDEX idx_products_deleted ON products (deleted);

-- Index for table categories
CREATE INDEX idx_categories_name ON categories (name);
CREATE INDEX idx_categories_deleted ON categories (deleted);

-- Index for table images
CREATE INDEX idx_images_deleted ON images (deleted);

-- Index for table products_categories
CREATE INDEX idx_products_categories_product_id ON products_categories (product_id);
CREATE INDEX idx_products_categories_category_id ON products_categories (category_id);