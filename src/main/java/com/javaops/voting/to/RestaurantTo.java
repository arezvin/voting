package com.javaops.voting.to;

import com.javaops.voting.model.Dish;

import java.beans.ConstructorProperties;
import java.util.List;

public class RestaurantTo extends BaseTo {
    private final String name;

    private final List<Dish> menu;

    @ConstructorProperties({"id", "name", "menu"})
    public RestaurantTo(Integer id, String name, List<Dish> menu) {
        super(id);
        this.name = name;
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public List<Dish> getMenu() {
        return menu;
    }

    @Override
    public String toString() {
        return "RestaurantTo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", menu=" + menu +
                '}';
    }
}
