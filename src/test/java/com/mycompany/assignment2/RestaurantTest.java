package com.mycompany.assignment2;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
/**
 *
 * @author suvd
 */

public class RestaurantTest {
    private RecipeBook recipeBook;
    private Stock stock; 
    private ShoppingList shoppingList;
    private Restaurant restaurant;
    
    private Ingredient tomato;
    private Ingredient rice;
    private Ingredient onion;
    
    private Recipe r1;
    private Recipe r2;

    @BeforeEach
    public void setUp() {
        recipeBook = new RecipeBook();

        tomato = new Ingredient("Tomato", "Vegetable", "pieces");
        rice = new Ingredient("Rice", "Grain", "grams");
        onion = new Ingredient("Onion", "Vegetable", "pieces");

        r1 = new Recipe("Tomato Soup", "Italian", 20);
        r1.addIngredient(tomato);
        r1.addIngredient(onion);

        r2 = new Recipe("Fried Rice", "Chinese", 30);
        r2.addIngredient(rice);
        r2.addIngredient(tomato);

        recipeBook.addRecipe(r1);
        recipeBook.addRecipe(r2);
        
        stock = new Stock();
        shoppingList = new ShoppingList();

        restaurant = new Restaurant(recipeBook, stock, shoppingList);
    }

    @Test
    public void testAddRecipe() {
        assertEquals(2, recipeBook.getAllRecipes().size());
    }

    @Test
    public void testRemoveRecipe() {
        recipeBook.removeRecipe(r1);
        assertEquals(1, recipeBook.getAllRecipes().size());
    }

    @Test
    public void testSearchByName() {
        List<Recipe> result = recipeBook.searchByName("Tomato Soup");
        assertEquals(1, result.size());
        assertEquals("Tomato Soup", result.get(0).getRecipeName());
    }

    @Test
    public void testSearchByCuisine() {
        List<Recipe> result = recipeBook.searchByCuisine("Chinese");
        assertEquals(1, result.size());
        assertEquals("Fried Rice", result.get(0).getRecipeName());
    }

    @Test
    public void testSearchByIngredient() {
        List<Recipe> result = recipeBook.searchByIngredient(tomato);
        assertEquals(2, result.size());
        assertTrue(result.contains(r1));
        assertTrue(result.contains(r2));
    }

    @Test
    public void testMostPopularIngredient() {
        Ingredient mostPopular = recipeBook.mostPopularIngredient();
        assertNotNull(mostPopular);
        assertEquals("Tomato", mostPopular.getIngName());
        assertEquals(2, mostPopular.getCountRecipe());
    }

    @Test
    public void testMostPopularCategory() {
        String mostPopularCategory = recipeBook.mostPopularCategory();
        assertEquals("Vegetable", mostPopularCategory); 
    }
    
    @Test
    public void testGetRecipeDetail() {
        String expectedStart = "Name: Tomato Soup, Cuisine: Italian, Cooking Time: 20 mins";
        String detail = r1.getRecipeDetail();
        assertTrue(detail.startsWith(expectedStart));
        assertTrue(detail.contains("Ingredients:>.<"));
        assertTrue(detail.contains("Tomato"));
        assertTrue(detail.contains("Onion"));
    }
    
    @Test 
    public void testAddStock(){
        stock.addStock(tomato, 10);
        stock.addStock(rice, 5);
        
        assertEquals(10, stock.getAmountOfIng(tomato));
        assertEquals(5, stock.getAmountOfIng(rice));
        
        stock.addStock(tomato, 3);
        stock.addStock(rice, 2);
        
        assertEquals(13, stock.getAmountOfIng(tomato));
        assertEquals(7, stock.getAmountOfIng(rice));      
    }
    
    @Test 
    public void testReduceStock(){
        stock.addStock(tomato, 10);
        stock.addStock(rice, 5);
        
        stock.reduceStock(tomato);
        assertEquals(9, stock.getAmountOfIng(tomato));
    }
    
    @Test 
    public void testIsavailable(){
        stock.addStock(tomato, 10);
        stock.addStock(rice, 0);
        
        assertTrue(stock.isAvailable(tomato));
        assertFalse(stock.isAvailable(rice));
    }
    
    @Test 
    public void testBuyAll(){
       stock.addStock(tomato, 0); 
       shoppingList.addToList(tomato, 5, stock);
       
       shoppingList.buyAll(stock);
       assertTrue(shoppingList.getIngredients().isEmpty());
       assertEquals(5, stock.getAmountOfIng(tomato));
    }
    
    @Test 
    public void testBuySome(){
        stock.addStock(tomato, 0);  
        shoppingList.addToList(tomato, 5, stock); 
        shoppingList.buySome(tomato, 3, stock);
        
        assertEquals(2, shoppingList.getAmounts().get(shoppingList.getIngredients().indexOf(tomato)));
        assertEquals(3, stock.getAmountOfIng(tomato));
    }
     
    @Test
    public void testPrepareRecipe() {
        Ingredient chicken = new Ingredient("Chicken", "Meat", "Kilogram");
        Ingredient garlic = new Ingredient("Garlic", "Vegetable", "Gram");
        
        stock.addStock(chicken, 1);
        stock.addStock(garlic, 2);
        
        
        Recipe chickenSoup = new Recipe("Chicken Soup", "Hungarian", 20);
        chickenSoup.addIngredient(chicken);
        chickenSoup.addIngredient(garlic);
        
        ShoppingList shoppingList = new ShoppingList();
        Restaurant restaurant = new Restaurant(recipeBook, stock, shoppingList);
        
        assertTrue(stock.isAvailable(chicken));
        assertTrue(stock.isAvailable(garlic));
        
        restaurant.prepareRecipe(chickenSoup);
        
        assertEquals(0, stock.getAmountOfIng(chicken));  
        assertEquals(1, stock.getAmountOfIng(garlic));   
        
        assertTrue(shoppingList.getIngredients().isEmpty());
    }
    
    @Test
    public void testPrepareRecipeNotEnoughtIngredient() {
        Ingredient chicken = new Ingredient("Chicken", "Meat", "Kilogram");
        Ingredient garlic = new Ingredient("Garlic", "Vegetable", "Gram");

        stock.addStock(chicken, 1); 
        stock.addStock(garlic, 0); 
        
        Recipe chickenSoup = new Recipe("Chicken Soup", "Hungarian", 20);
        chickenSoup.addIngredient(chicken);
        chickenSoup.addIngredient(garlic);
        
        ShoppingList shoppingList = new ShoppingList();
        Restaurant restaurant = new Restaurant(recipeBook, stock, shoppingList);
        
        assertEquals(1, stock.getAmountOfIng(chicken));  
        assertEquals(0, stock.getAmountOfIng(garlic)); 

        restaurant.prepareRecipe(chickenSoup);
        
        assertEquals(0, stock.getAmountOfIng(chicken));
        assertEquals(0, stock.getAmountOfIng(garlic));
        
        assertTrue(shoppingList.getIngredients().contains(garlic));
    }
    
}
