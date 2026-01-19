package com.ecom.service.impl;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product) {
        product = productRepository.save(product);
        return product;
    }

    public List<Product> getAllProducts()
    {
        List<Product> products = productRepository.findAll();
        return  products;
    }


    @Override
    public Boolean deleteProduct(int product_id) {
        Product product = productRepository.findById(product_id).orElseThrow();

        if(!ObjectUtils.isEmpty(product))
        {
            productRepository.delete(product);
            return true;
        }
        return false;


    }

    @Override
    public Product getProductById(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(null);
        return product;
    }


    public Product updateProduct(Product product, MultipartFile image) throws IOException {
        // If user did not give username then we keep old file name
        // I need a product to check it is present in DB or not
        Product dbProduct = getProductById(product.getId());

        String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setCategory(product.getCategory());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setStock(product.getStock());
        dbProduct.setDiscount(product.getDiscount());
        dbProduct.setIsActive(product.getIsActive());


        // Here we can the write the logic to calculate discount
        // 5% = price * 5/100
        Double price = product.getPrice();
        Integer discountPercent = product.getDiscount();

        Double discountAmount = price * discountPercent / 100;
        Double discountPrice = price - discountAmount;

        dbProduct.setDiscountPrice(discountPrice);


        dbProduct.setDiscountPrice(discountPrice);

        Product updateProduct = productRepository.save(dbProduct);

        if(!ObjectUtils.isEmpty(updateProduct)) {
            if (!image.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
                System.out.println(path);

                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            return product;
        }
        return  dbProduct;
    }



    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products = null;
        if(ObjectUtils.isEmpty(category))
        {
            products = productRepository.findByIsActiveTrue();
        }
        else{
             products = productRepository.findByCategory(category);
        }
        return products;
    }
}
