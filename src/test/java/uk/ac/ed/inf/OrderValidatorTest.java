package uk.ac.ed.inf;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.jetbrains.annotations.TestOnly;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;

import java.time.LocalDate;

public class OrderValidatorTest extends TestCase {
    OrderValidator orderval = new OrderValidator();
    CreditCardInformation testCardInfo = new CreditCardInformation("1234567890123456", "07/28", "123");
    CreditCardInformation invalidnum = new CreditCardInformation("123456","07/28","123");
    CreditCardInformation invaliddate = new CreditCardInformation("1234567890123456", "07/22", "123");
    CreditCardInformation invalidCVV = new CreditCardInformation("1234567890123456", "07/28", "abc");
    Pizza[] pizzahutpizzas = {
            new Pizza("Margarita", 800),
            new Pizza("Pepperoni", 900),
            new Pizza("Hawaiian", 950)
    };
    Pizza[] papajhonspizzas = {
            new Pizza("Mushroom", 1000),
            new Pizza("meatfeast", 750),
            new Pizza("BBQChicken", 900)
    };
    Pizza[] dominoespizzas = {
            new Pizza("bacon", 800),
            new Pizza("steak and cheese", 900),
            new Pizza("GarlicBreak", 950)
    };
    Pizza[] mulrestaurants = {
            new Pizza("bacon", 800),
            new Pizza("steak and cheese", 900),
            new Pizza("Pepperoni", 900)
    };
    Pizza[] undefinedpizza = {
            new Pizza("bacon", 800),
            new Pizza("steak and cheese", 900),
            new Pizza("vegetable", 1000)
    };

    Restaurant pizzahut = new Restaurant("Pizza Hut", new LngLat(1.2, 4.5), new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY}, pizzahutpizzas);
    Restaurant papaJhons = new Restaurant("Papa Jhons", new LngLat(1.5, 4.2), new DayOfWeek[]{DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY},papajhonspizzas);
    Restaurant dominoes = new Restaurant("dominoes", new LngLat(1.0, 4.0), new DayOfWeek[]{DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY},dominoespizzas);
    Restaurant[] restaurants = {pizzahut,papaJhons,dominoes};
    Order test = new Order("1",LocalDate.of(2023, 10, 12), OrderStatus.UNDEFINED, OrderValidationCode.UNDEFINED, 2750, dominoespizzas, testCardInfo);
    public void testValidateOrder() {
        Assert.assertEquals(orderval.validateOrder(test,restaurants).getOrderValidationCode(),OrderValidationCode.NO_ERROR);
    }

    public void testValidOrderCheck() {
        test.setPizzasInOrder(mulrestaurants);
        test.setPriceTotalInPence(2700);
        Assert.assertEquals(orderval.validOrderCheck(test,restaurants), OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
    }
    public void testValidOrderCheck2() {
        test.setCreditCardInformation(invalidnum);
        Assert.assertEquals(orderval.validOrderCheck(test,restaurants), OrderValidationCode.CARD_NUMBER_INVALID);
    }
    public void testValidOrderCheck3() {
        test.setCreditCardInformation(invaliddate);
        Assert.assertEquals(orderval.validOrderCheck(test,restaurants), OrderValidationCode.EXPIRY_DATE_INVALID);
    }

    public void testValidOrderCheck4() {
        test.setCreditCardInformation(invalidCVV);
        Assert.assertEquals(orderval.validOrderCheck(test,restaurants), OrderValidationCode.CVV_INVALID);
    }
    public void testValidOrderCheck5() {
        test.setPizzasInOrder(undefinedpizza);
        test.setPriceTotalInPence(2800);
        Assert.assertEquals(orderval.validOrderCheck(test,restaurants), OrderValidationCode.PIZZA_NOT_DEFINED);
    }
    public void testValidOrderCheck6() {
        test.setPriceTotalInPence(2800);
        Assert.assertEquals(orderval.validOrderCheck(test,restaurants), OrderValidationCode.TOTAL_INCORRECT);
    }
    public void testValidOrderCheck7() {
        test.setPizzasInOrder(pizzahutpizzas);
        test.setPriceTotalInPence(2750);
        Assert.assertEquals(orderval.validOrderCheck(test,restaurants), OrderValidationCode.RESTAURANT_CLOSED);
    }
    public void testCreditCardNumVal() {
        Assert.assertFalse(orderval.CreditCardNumVal(testCardInfo));
    }

    public void testCreditCardCVVVal() {
        Assert.assertFalse(orderval.CreditCardCVVVal(testCardInfo));
    }

    public void testCreditCardDateVal() {
        Assert.assertFalse(orderval.CreditCardCVVVal(testCardInfo));
    }

    public void testPriceInPenceCheck() {
        Assert.assertFalse(orderval.priceInPenceCheck(dominoespizzas,2750));
    }

    public void testMulRestaurantsVal() {
        Assert.assertEquals(orderval.mulRestaurantsVal(mulrestaurants, restaurants), 0);
    }

    public void testGetPizzaRestaurant() {
        Assert.assertEquals(orderval.getPizzaRestaurant(new Pizza("bacon", 800), restaurants),dominoes);
    }

    public void testCheckMenu() {
        Assert.assertTrue(orderval.checkMenu(new Pizza("Mushroom", 1000), papaJhons));
    }

    public void testIsClosed() {
        Assert.assertFalse(orderval.isClosed(papaJhons,LocalDate.of(2023, 10, 12)));
    }
}