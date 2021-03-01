package com.javaops.voting.util;

import com.javaops.voting.model.Dish;
import com.javaops.voting.model.Restaurant;
import com.javaops.voting.model.Vote;
import com.javaops.voting.to.RestaurantTo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RestaurantUtil {

    public static List<RestaurantTo> getTos(List<Restaurant> restaurants, List<Dish> dishes) {
        Map<Integer, List<Dish>> dishesGroupedByRestaurantId = dishes.stream()
                .collect(Collectors.groupingBy(dish -> dish.getRestaurant().getId()));

        return restaurants.stream()
                .map(restaurant -> createTo(restaurant, dishesGroupedByRestaurantId.get(restaurant.getId())))
                .collect(Collectors.toList());
    }

    private static RestaurantTo createTo(Restaurant restaurant, List<Dish> menu) {
        return new RestaurantTo(restaurant.getId(), restaurant.getName(), menu);
    }
}
