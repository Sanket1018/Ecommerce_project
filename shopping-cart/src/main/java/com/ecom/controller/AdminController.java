package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.ecom.model.Product;
import com.ecom.service.ProductService;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

    @Autowired
    private ProductService productService;

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

    @GetMapping("/category")
    public String category(Model m) {
        m.addAttribute("categorys",categoryService.getAllCategory());
        return "admin/category";
    }

	// @ModelAttribute
	// HttpSession session used to store the data at the network time when we refresh it then it will gone
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category,HttpSession session,@RequestParam("file") MultipartFile file) throws IOException {
        String imageName = file!=null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean existCategory= categoryService.existCategory(category.getName());

		// checks if category is exists
		if(existCategory)
		{
			session.setAttribute("errorMsg","Category Name already exists");
		}
		else {
            Category saveCategory = categoryService.saveCategory(category);
            // checks the file is not null
            if (ObjectUtils.isEmpty(saveCategory)) {
                session.setAttribute("errorMsg", "Not saved : Internal server error");
            } else {
                // If category saved successfully into database then we have to add it into our folder
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());
                System.out.println(path);

                Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);


                session.setAttribute("succMsg", "Saved successfully");
            }


        }
		return "redirect:/admin/category";
	}

    // Delete category by id
    @GetMapping("/deleteCategory/{id}")
    public String deleteCategoryById(@PathVariable int id,HttpSession session)
    {
        Boolean deleteCategory = categoryService.deleteCategoryById(id);

        if(deleteCategory)
        {
            session.setAttribute("succMsg","Category deleted successfully");
        }
        else{
            session.setAttribute("errorMsg","Something wrong on server");
        }

        return "redirect:/admin/category";
    }

    // Edit category

    ///  first we have to load the add category page
    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id,Model m)
    {
        m.addAttribute("category",categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    ///  Now we have to update
    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException {

        Category oldCategory = categoryService.getCategoryById(category.getId());

        // If user not uploaded image to update then we have to check this
        String fileName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();


        if(!ObjectUtils.isEmpty(oldCategory))
        {
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(fileName);
        }
        // Now we have to save
        Category updatedcategory = categoryService.saveCategory(oldCategory);

        if(!ObjectUtils.isEmpty(updatedcategory))
        {
            if(!file.isEmpty())
            {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());
                System.out.println(path);

                Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg","Category Updated successfully");
        }
        else{
            session.setAttribute("errorMsg","something wrong on server");
        }

        return "redirect:/admin/loadEditCategory/"+category.getId();
    }




//    ************************************************************************************************************************
    // Product ------------------------------------------------------------
    // If we want to show on url then we are going to use Model


    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m)
    {
        // We want to show the all categories on the add product page
        List<Category> categories= categoryService.getAllCategory();
        m.addAttribute("categories",categories);
        // We display this categories on the UI using for each of thymleaf
        return "admin/add_product";
    }

//    =======================================================================================================================

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute  Product product,HttpSession session,@RequestParam("file") MultipartFile image) throws IOException {
        // check user is giving the file or not if not then we give default name to it
        String imageName = image.isEmpty()?"default.jpg":image.getOriginalFilename();
        product.setImage(imageName);
        // At the time of adding product set the price discount to 0
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        Product saveProduct = productService.saveProduct(product);

        // checks the object of the product is present or not
        if(!ObjectUtils.isEmpty(saveProduct))
        {
            // apana same logic to save the image to folder
            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"product_img"+File.separator+image.getOriginalFilename());
            System.out.println(path);

            Files.copy(image.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);


            session.setAttribute("succMsg","Product saved successfully");
        }
        else{
            session.setAttribute("errorMsg","Something wrong on server");
        }
        // Now also get a image in add product so we have to get it

        // below line says when save the product redirect to the loadAddProduct
        return "redirect:/admin/loadAddProduct";
    }

//===============================================================================================================================================================
    // Product


    // Product - view product
    @GetMapping("/products")
    public String loadViewProduct(Model m )
    {
       m.addAttribute("products",productService.getAllProducts());
        return "admin/products";
    }

    // Delete product
    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id,HttpSession session)
    {
        Boolean deleteProduct = productService.deleteProduct(id);
        if(deleteProduct)
        {
            session.setAttribute("succMsg","Product deleted successfully");
        }
        else{
            session.setAttribute("errorMsg","Something went wrong");
        }
        return "redirect:/admin/products";
    }

    // Edit product
    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id,Model m)
    {
        m.addAttribute("product",productService.getProductById(id));
        m.addAttribute("categories",categoryService.getAllCategory());
        return "admin/edit_product";
    }

    //updateProduct
    @PostMapping("/updateProduct")
    public String editProduct(@ModelAttribute Product product, Model m,HttpSession session,@RequestParam("file") MultipartFile image) throws IOException {


        // check for the discount percentage is between 0 to 100
        if(product.getDiscount() < 0 || product.getDiscount() >100)
        {
            session.setAttribute("errorMsg","Invalid discount");
        }
        else{
            Product updateProduct = productService.updateProduct(product,image);
            // checking object is empty or not
            if(!ObjectUtils.isEmpty(updateProduct))
            {
                session.setAttribute("succMsg","Product updated successfully");
            }
            else {
                session.setAttribute("errorMsg", "something went wrong on server");
            }
        }
        return "redirect:/admin/editProduct/"+product.getId();
    }
}
