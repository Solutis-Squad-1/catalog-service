-- Constraints for table `products`
alter table if exists products
    add constraint FK_Products_Image foreign key (image_id) references images;

-- Constraints for table `products_categories`
alter table if exists products_categories
    add constraint FK_ProductsCategories_Categories foreign key (category_id) references categories;

-- Constraints for table `products_categories`
alter table if exists products_categories
    add constraint FK_ProductsCategories_Products foreign key (product_id) references products;