package com.ecom.service.impl;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

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


    public Product updateProduct(Product product, MultipartFile image)
    {
        // If user did not give username then we keep old file name
        // I need a product to check it is present in DB or not
        Product dbProduct = getProductById(product.getId());

        String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

        dbProduct.setTitle(product.getTitle());
        dbProduct.setDescription(product.getDescription());

        return dbProduct;


    }


}
