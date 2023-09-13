package io.alviss.recipe_api.recipe;

import io.alviss.recipe_api.model.Category;
import io.alviss.recipe_api.model.Ingredient;
import io.alviss.recipe_api.user.User;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Recipe {

    @Id
    @Column(nullable = false, updatable = false)
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String image;

    @Column
    private Integer prepTime;

    @Column
    private Integer servingSize;

    @Column(nullable = false, unique = true)
    private String description;

    @Column
    private Integer rating;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Ingredient> ingredients;

    @Column(nullable = false)
    private Category category;

    @Column(columnDefinition = "text")
    private String nutrients;

    @Column
    private Integer cookTime;

    @Column(nullable = false)
    private String country;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;

}
