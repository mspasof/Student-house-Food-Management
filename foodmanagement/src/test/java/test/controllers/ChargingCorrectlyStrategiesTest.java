package test.controllers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import nl.tudelft.sem.foodmanagement.controllers.strategies.ChargeStrategy;
import nl.tudelft.sem.foodmanagement.controllers.strategies.SpoiledStrategy;
import nl.tudelft.sem.foodmanagement.entities.Food;
import org.junit.jupiter.api.Test;

class ChargingCorrectlyStrategiesTest {

    private static ChargeStrategy chargeStrategy = new ChargeStrategy();
    private static SpoiledStrategy spoiledStrategy = new SpoiledStrategy();
    private static Food food = new Food("Kiwi", 5, 1);

    @Test
    void testChargeCorrectChargeStrategy() {
        double result = chargeStrategy.calculateCharge(food);
        assertThat(result).isEqualTo(-5);
    }

    @Test
    void testChargeCorrectSpoiledStrategy() {
        double result = spoiledStrategy.calculateCharge(food, 2);
        assertThat(result).isEqualTo(-2.5);
    }
}
