package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/")
	public String index() {
		return "admin/index";
	}

    @GetMapping("/category")
    public String category() {
        return "admin/category";
    }

	// @ModelAttribute
	// HttpSession session used to store the data at the network time when we refresh it then it will gone
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category,HttpSession session,@RequestParam("file") MultipartFile file)
    {
        String imageName = file!=null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        Boolean existCategory= categoryService.existCategory(category.getName());

		// checks if category is exists
		if(existCategory)
		{
			session.setAttribute("errorMsg","Category Name already exists");
		}
		else {

            // checks the file is not null
            if (ObjectUtils.isEmpty(existCategory)) {
                session.setAttribute("errorMsg", "Not saved : Internal server error");
            } else {
                session.setAttribute("success", "Saved successfully");
            }

            Category saveCategory = categoryService.saveCategory(category);
        }
		return "redirect:/admin/category";




	}



}
