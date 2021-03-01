package com.javaops.voting.test;

import com.javaops.voting.controller.DishController;
import com.javaops.voting.model.Dish;
import com.javaops.voting.model.Restaurant;
import com.javaops.voting.service.RestaurantService;
import com.javaops.voting.util.exception.NotFoundException;
import com.javaops.voting.util.json.JsonUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static com.javaops.voting.TestData.*;
import static com.javaops.voting.TestUtil.*;
import static org.junit.Assert.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminTest extends AbstractTest {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private DishController dishController;

    @Test
    public void getAllRestaurants() throws Exception {
        perform(MockMvcRequestBuilders.get("/admin/restaurants")
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(RESTAURANT1, RESTAURANT2));
    }

    @Test
    public void getRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get("/admin/restaurants/" + RESTAURANT1_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(RESTAURANT1));
    }

    @Test
    public void getRestaurantNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get("/admin/restaurants/" + NOT_FOUND)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void createRestaurant() throws Exception {
        Restaurant newRestaurant = getNewRestaurant();
        ResultActions action = perform(MockMvcRequestBuilders.post("/admin/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(newRestaurant)))
                .andExpect(status().isCreated());

        Restaurant created = readFromJson(action, Restaurant.class);
        int newId = created.id();
        newRestaurant.setId(newId);
        RESTAURANT_MATCHER.assertMatch(created, newRestaurant);
        RESTAURANT_MATCHER.assertMatch(restaurantService.get(newId), newRestaurant);
    }

    @Test
    public void createUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.post("/admin/restaurants"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createForbidden() throws Exception {
        perform(MockMvcRequestBuilders.post("/admin/restaurants")
                .with(userHttpBasic(USER)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createRestaurantInvalid() throws Exception {
        Restaurant expected = new Restaurant(null, "A");
        perform(MockMvcRequestBuilders.post("/admin/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(expected)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    public void deleteRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.delete("/admin/restaurants/" + RESTAURANT1_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> restaurantService.get(RESTAURANT1_ID));
    }

    @Test
    public void deleteRestaurantNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete("/admin/restaurants/" + NOT_FOUND)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    public void updateRestaurant() throws Exception {
        Restaurant updated = getUpdatedRestaurant();
        perform(MockMvcRequestBuilders.put("/admin/restaurants/" + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        RESTAURANT_MATCHER.assertMatch(restaurantService.get(RESTAURANT1_ID), updated);
    }

    @Test
    public void updateRestaurantNotOwn() throws Exception {
        Restaurant updated = getUpdatedRestaurant();
        perform(MockMvcRequestBuilders.put("/admin/restaurants/" + RESTAURANT2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    public void updateRestaurantInvalid() throws Exception {
        Restaurant updated = getUpdatedRestaurant();
        updated.setName("");
        perform(MockMvcRequestBuilders.put("/admin/restaurants/" + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    public void getAllDishes() throws Exception {
        perform(MockMvcRequestBuilders.get("/admin/dishes")
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(DISHES));
    }

    @Test
    public void getDish() throws Exception {
        perform(MockMvcRequestBuilders.get("/admin/dishes/" + DISH1_ID)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(DISH1));
    }

    @Test
    public void createDish() throws Exception {
        Dish newDish = getNewDish();
        ResultActions action = perform(MockMvcRequestBuilders.post("/admin/dishes/" + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(newDish)))
                .andExpect(status().isCreated());

        Dish created = readFromJson(action, Dish.class);
        int newId = created.id();
        newDish.setId(newId);
        DISH_MATCHER.assertMatch(created, newDish);
        DISH_MATCHER.assertMatch(dishController.get(newId), newDish);
    }

    @Test
    public void createDishInvalid() throws Exception {
        Dish expected = new Dish(null, "", 5500d, LocalDate.now());
        perform(MockMvcRequestBuilders.post("/admin/dishes/" + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(expected)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteDish() throws Exception {
        perform(MockMvcRequestBuilders.delete("/admin/dishes/" + DISH1_ID)
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> dishController.get(DISH1_ID));
    }

    @Test
    public void deleteDishNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete("/admin/dishes/" + NOT_FOUND)
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    public void updateDish() throws Exception {
        Dish updated = getUpdatedDish();
        perform(MockMvcRequestBuilders.put("/admin/dishes/" + DISH1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());

        DISH_MATCHER.assertMatch(dishController.get(DISH1_ID), updated);
    }

    @Test
    public void updateDishNotOwn() throws Exception {
        Dish updated = getUpdatedDish();
        perform(MockMvcRequestBuilders.put("/admin/dishes/" + DISH2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    public void updateDishInvalid() throws Exception {
        Dish updated = getUpdatedDish();
        updated.setDate(null);
        perform(MockMvcRequestBuilders.put("/admin/dishes/" + DISH1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(ADMIN))
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print());
    }

    @Test
    public void updateHtmlUnsafe() throws Exception {
        Dish invalid = new Dish(DISH1_ID, "<script>alert(123)</script>", 20.4d, LocalDate.now());
        perform(MockMvcRequestBuilders.put("/admin/dishes/" + DISH1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid))
                .with(userHttpBasic(ADMIN)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void getAllVotes() throws Exception {
        perform(MockMvcRequestBuilders.get("/admin/votes")
                .with(userHttpBasic(ADMIN)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_MATCHER.contentJson(VOTES));
    }
}
