package com.trendaura.config;

import com.trendaura.entity.Product;
import com.trendaura.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.List;

/**
 * Seeds sample state-wise fashion products for development.
 * Disabled in production via @Profile("dev").
 */
@Configuration
public class DataSeeder {

    @Bean
    @Profile("dev")
    public CommandLineRunner seedProducts(ProductRepository productRepository) {
        return args -> {
            if (productRepository.count() > 0) return;
            List<Product> products = List.of(
                    product("Green Designer Lehenga", "Dark Olive Green Umbrella Style Net Designer Lehenga", new BigDecimal("5499"), "uttar_pradesh", "lehenga", "https://media.samyakk.com/pub/media/catalog/product/d/a/dark-olive-green-umbrella-style-net-designer-lehenga-with-pentagon-neck-blouse-gg1038.jpg"),
                    product("Red Banarasi Silk Saree", "Sweet Cherry Red Zari Woven Festive Wear Banarasi Silk Saree", new BigDecimal("8999"), "uttar_pradesh", "saree", "https://i.pinimg.com/736x/31/fe/06/31fe06e0c31651e6432d2324f659725c.jpg"),
                    product("Mysore Silk Saree", "Baby Pink Zari Woven Mysore Silk Saree", new BigDecimal("7299"), "karnataka", "saree", "https://rmkv.com/cdn/shop/files/RMKV_oct6th0796.webp?v=1728632108"),
                    product("Kanjeevaram Sarees", "Silk fabric and elaborate designs, vibrant colors and gold zari", new BigDecimal("12999"), "tamil_nadu", "saree", "https://assets0.mirraw.com/images/12118123/image_zoom.jpeg"),
                    product("Punjabi Kurti Set", "Pale Pink Pearl Embroidered Crepe Designer Salwar", new BigDecimal("3499"), "punjab", "salwar_kameez", "https://media.samyakk.com/pub/media/tagalys/product_images/l/i/light-pink-beige-sequins-embroidered-crepe-designer-salwar-ng3360.jpg"),
                    product("Traditional Dhoti", "Dhoti and Kurta - Assam traditional", new BigDecimal("2499"), "assam", "dhoti", "https://i.pinimg.com/736x/56/ef/54/56ef5427aa4ccabb8b889521c23de63f.jpg")
            );
            productRepository.saveAll(products);
        };
    }

    private static Product product(String name, String desc, BigDecimal price, String state, String type, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(desc);
        p.setPrice(price);
        p.setStateCode(state);
        p.setFashionType(type);
        p.setImageUrl(imageUrl);
        p.setActive(true);
        return p;
    }
}
