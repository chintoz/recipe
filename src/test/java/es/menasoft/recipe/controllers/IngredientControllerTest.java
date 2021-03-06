package es.menasoft.recipe.controllers;

import es.menasoft.recipe.commands.IngredientCommand;
import es.menasoft.recipe.commands.RecipeCommand;
import es.menasoft.recipe.commands.UnitOfMeasureCommand;
import es.menasoft.recipe.service.IngredientService;
import es.menasoft.recipe.service.RecipeService;
import es.menasoft.recipe.service.UnitOfMeasureService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class IngredientControllerTest {

    @Mock
    RecipeService recipeService;

    @Mock
    IngredientService ingredientService;

    @Mock
    UnitOfMeasureService unitOfMeasureService;

    IngredientController ingredientController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientController = new IngredientController(recipeService, ingredientService, unitOfMeasureService);
    }

    @Test
    @SneakyThrows
    void listIngredients() {
        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();

        when(recipeService.findCommandById(eq(1L))).thenReturn(RecipeCommand.builder().id(1L).build());

        mockMvc.perform(get("/recipe/1/ingredients"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/list"))
                .andExpect(model().attributeExists("recipe"));

        verify(recipeService, times(1)).findCommandById(eq(1L));

    }

    @Test
    @SneakyThrows
    void showIngredient() {
        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();

        when(ingredientService.findByRecipeIdAndIngredientId(eq(1L), eq(1L)))
                .thenReturn(IngredientCommand.builder().id(1L).recipeId(1L).build());

        mockMvc.perform(get("/recipe/1/ingredient/1/show"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/show"))
                .andExpect(model().attributeExists("ingredient"));

        verify(ingredientService, times(1)).findByRecipeIdAndIngredientId(eq(1L), eq(1L));
    }

    @Test
    @SneakyThrows
    void updateRecipeIngredient() {

        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();

        when(ingredientService.findByRecipeIdAndIngredientId(eq(1L), eq(1L)))
                .thenReturn(IngredientCommand.builder().id(1L).recipeId(1L).build());

        when(unitOfMeasureService.listAll()).thenReturn(List.of(UnitOfMeasureCommand.builder().build()));

        mockMvc.perform(get("/recipe/1/ingredient/1/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("uomList"));

        verify(ingredientService, times(1)).findByRecipeIdAndIngredientId(eq(1L), eq(1L));
        verify(unitOfMeasureService, times(1)).listAll();
    }

    @Test
    @SneakyThrows
    void newRecipeIngredient() {

        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();

        when(recipeService.findCommandById(eq(1L)))
                .thenReturn(RecipeCommand.builder().id(1L).build());

        when(unitOfMeasureService.listAll()).thenReturn(List.of(UnitOfMeasureCommand.builder().build()));

        mockMvc.perform(get("/recipe/1/ingredient/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipe/ingredient/ingredientform"))
                .andExpect(model().attributeExists("recipe"))
                .andExpect(model().attributeExists("ingredient"))
                .andExpect(model().attributeExists("uomList"));

        verify(recipeService, times(1)).findCommandById(eq(1L));
        verify(unitOfMeasureService, times(1)).listAll();
    }

    @Test
    @SneakyThrows
    void saveOrUpdate() {
        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();

        when(ingredientService.saveIngredientCommand(any())).thenReturn(IngredientCommand.builder().recipeId(1L).id(1L).build());

        mockMvc.perform(post("/recipe/1/ingredient")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", "1")
                .param("recipeId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/1/ingredient/1/show"));

        verify(ingredientService, times(1)).saveIngredientCommand(any());
    }


    @Test
    @SneakyThrows
    void deleteRecipeIngredient() {

        mockMvc = MockMvcBuilders.standaloneSetup(ingredientController).build();

        mockMvc.perform(get("/recipe/1/ingredient/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/recipe/1/ingredients"));

        verify(ingredientService, times(1)).deleteByRecipeIdAndIngredientId(eq(1L), eq(1L));
    }
}