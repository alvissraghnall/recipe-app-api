package io.alviss.recipe_api.model;

import io.alviss.recipe_api.recipe.Recipe;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private String size;

    @ManyToMany
    private Set<Recipe> recipes;

}
