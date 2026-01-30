package com.ecom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;
import org.springframework.util.ObjectUtils;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Category saveCategory(Category category) {
		return categoryRepository.save(category);
	}



	@Override
	public Boolean existCategory(String name) {
		return categoryRepository.existsByName(name);
	}

	@Override
	public List<Category> getAllCategory() {
		return categoryRepository.findAll();
	}

    // Delete category
    @Override
    public Boolean deleteCategoryById(int id){
        // first find the category if it present
        Category category = categoryRepository.findById(id).orElseThrow(null);

        if(!ObjectUtils.isEmpty(category))
        {
            categoryRepository.deleteById(id);
            return true;

        }
        return false;
    }

    @Override
    public Category getCategoryById(int id) {
        com.ecom.model.Category category = categoryRepository.findById(id).orElseThrow(null);
        return  category;
    }

    @Override
    public List<Category> getAllActiveCategory() {
      List<Category> categories= categoryRepository.findByIsActiveTrue();
      return categories;
    }


}
