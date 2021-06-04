package test.entities;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import nl.tudelft.sem.foodmanagement.entities.Food;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import test.Config;

@DataJpaTest
@ContextConfiguration(
    classes = {Config.class}
)
public class FoodTest {
    private transient Food food = new Food("Apple", 5,  1.0);
    private transient Food food2 = new Food(2L, "Kiwi", 1, 2);
    private transient Food food3 = new Food(2L, "Kiwi", 1, 2);
    private transient Food food4 = null;

    @Test
    void getIdTest() {
        assertEquals(0, food.getId());
    }

    @Test
    void getIdMutationTest() {
        assertThat(food2.getId()).isEqualTo(2);
    }

    @Test
    void getNameTest() {
        assertEquals("Apple", food.getName());
    }

    @Test
    void setNameTest() {
        food.setName("Orange");
        assertEquals("Orange", food.getName());
    }

    @Test
    void getPriceTest() {
        assertEquals(1.0, food.getPrice());
    }

    @Test
    void setPriceTest() {
        food.setPrice(1.5);
        assertEquals(1.5, food.getPrice());
    }

    @Test
    void getPortionsTest() {
        assertEquals(5, food.getPortions());
    }

    @Test
    void setPortionsTest() {
        food.setPortions(4);
        assertEquals(4, food.getPortions());
    }

    @Test
    void equalsTest() {
        assertTrue(food.equals(food));
    }

    @Test
    void equalsNullTest() {
        assertFalse(food.equals(food4));
    }

    @Test
    void equalsOtherObjectTest() {
        assertFalse(food.equals(new Object()));
    }

    @Test
    void equalsOtherObjectsSameIdTest() {
        assertTrue(food2.equals(food3));
    }

    @Test
    void equalsDifferentFoodsTest() {
        assertFalse(food.equals(food2));
    }

    @Test
    void hashTest() {
        assertThat(food.hashCode()).isNotEqualTo(food2.hashCode());
        assertThat(food.hashCode()).isEqualTo(Objects.hash(food.getId()));
    }
}