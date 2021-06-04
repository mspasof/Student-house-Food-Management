package nl.tudelft.sem.foodmanagement.repositories;

import nl.tudelft.sem.foodmanagement.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
