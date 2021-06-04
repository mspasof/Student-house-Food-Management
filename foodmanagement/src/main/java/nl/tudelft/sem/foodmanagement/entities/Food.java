package nl.tudelft.sem.foodmanagement.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "pricePerPortion")
    private double price;

    @Column(name = "portions")
    private int portions;

    public Food(){
    }

    /**
     * Create a new Food instance.
     *
     * @param name the name of the food
     * @param price the price of the food
     * @param portions the portions of the food
     */
    public Food(String name, int portions, double price) {
        this.name = name;
        this.price = price;
        this.portions = portions;
    }

    /**
     * Create a new Food instance.
     *
     * @param id the id of the food
     * @param name the name of the food
     * @param price the price of the food
     * @param portions the portions of the food
     */
    public Food(Long id, String name, int portions, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.portions = portions;
    }

    /**
     * Get food id.
     *
     * @return the food's id
     */
    public long getId() {
        return id;
    }

    /**
     * Set food id.
     *
     * @param id the new id
     */
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * Get food name.
     *
     * @return the food's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set food name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get food price.
     *
     * @return the food's price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Set food price.
     *
     * @param price the new price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Get food portions.
     *
     * @return the food's portions
     */
    public int getPortions() {
        return portions;
    }

    /**
     * Set amount of food portions.
     *
     * @param portions the new amount of portions
     */
    public void setPortions(int portions) {
        this.portions = portions;
    }

    /**
     * Check whether the Food instance and another are equal.
     *
     * @param o Object to compare with
     * @return Whether the provided object and the Food instance are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Food food = (Food) o;

        return id == food.id;
    }

    /**
     * Hash food id.
     *
     * @return the food's hashed id
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
