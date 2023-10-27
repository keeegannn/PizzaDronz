package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * a sample order validator which does nothing
 */
public class OrderValidator implements OrderValidation {
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        orderToValidate.setOrderValidationCode(validOrderCheck(orderToValidate, definedRestaurants));
        return orderToValidate;
    }

    public OrderValidationCode validOrderCheck(Order orderToValidate, Restaurant[] definedRestaurants) {
        //checks number of pizzas in valid
        int NumofPizzas = orderToValidate.getPizzasInOrder().length;
        if (NumofPizzas > SystemConstants.MAX_PIZZAS_PER_ORDER) {
            //returns validation code for this case
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        }
        //uses the CreditCardNumVal function to validate card number
        if (CreditCardNumVal(orderToValidate.getCreditCardInformation())) {
            //returns validation code for this case
            return OrderValidationCode.CARD_NUMBER_INVALID;
        }
        //uses the CreditCardCVVVal function to validate CVV number
        if (CreditCardCVVVal(orderToValidate.getCreditCardInformation())) {
            //returns validation code for this case
            return OrderValidationCode.CVV_INVALID;
        }
        //uses the CreditCardDateVal function to validate card expiry date
        if (CreditCardDateVal(orderToValidate.getCreditCardInformation(), orderToValidate.getOrderDate())) {
            //returns validation code for this case
            return OrderValidationCode.EXPIRY_DATE_INVALID;
        }
        //uses the priceInPenceCheck function to validate that the order price is correct
        if (priceInPenceCheck(orderToValidate.getPizzasInOrder(), orderToValidate.getPriceTotalInPence())) {
            //returns validation code for this case
            return OrderValidationCode.TOTAL_INCORRECT;
        }
        //uses the mulRestaurantsVal function to check if the pizzas are from multiple restaurants
        if (mulRestaurantsVal(orderToValidate.getPizzasInOrder(), definedRestaurants) == 0) {
            //returns validation code for this case
            return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
        }
        //uses the mulRestaurantsVal function to check if the pizzas are defined on a restaurants menu
        if (mulRestaurantsVal(orderToValidate.getPizzasInOrder(), definedRestaurants) == 2) {
            //returns validation code for this case
            return OrderValidationCode.PIZZA_NOT_DEFINED;
        }
        //case of the function mulRestaurantsVal that catches undefined behaviour
        if (mulRestaurantsVal(orderToValidate.getPizzasInOrder(), definedRestaurants) == 3) {
            //returns validation code for this case
            return OrderValidationCode.UNDEFINED;
        }
        //uses the isClosed function to check if the restaurant the order is from is open
        if (isClosed(getPizzaRestaurant(orderToValidate.getPizzasInOrder()[0], definedRestaurants), orderToValidate.getOrderDate())) {
            //returns validation code for this case
            return OrderValidationCode.RESTAURANT_CLOSED;
        }
        //returns no error if all tests are passed
        return OrderValidationCode.NO_ERROR;
    }

    public boolean CreditCardNumVal(CreditCardInformation CardInfo) {
        //check if the length of the string is 16 digits
        boolean Number = CardInfo.getCreditCardNumber().length() == 16;
        //check if every digit in the string is numeric
        for (int i = 0; i < CardInfo.getCreditCardNumber().length(); i++) {
            char z = CardInfo.getCreditCardNumber().charAt(i);
            if (!Character.isDigit(z)) {
                Number = false;
            }
        }
        //returns inverse as it used in an if statement to check for invalidity
        return !Number;
    }

    public boolean CreditCardCVVVal(CreditCardInformation CardInfo) {
        //check if length of the CVV string is 3 digits
        boolean CVV = CardInfo.getCvv().length() == 3;
        //check if every digit in CVV is numeric
        for (int i = 0; i < CardInfo.getCvv().length(); i++) {
            char z = CardInfo.getCvv().charAt(i);
            if (!Character.isDigit(z)) {
                CVV = false;
            }
        }
        //returns inverse as it used in an if statement to check for invalidity
        return !CVV;
    }


    public boolean CreditCardDateVal(CreditCardInformation CardInfo, LocalDate orderDate) {
        //check length of CVV string
        if (!(CardInfo.getCreditCardExpiry().length() == 5)) {
            return true;
        }
        if(!(CardInfo.getCreditCardExpiry().charAt(2) == '/')){
            return true;
        }
        //get the month and year of the expiry date into substrings
        String month = CardInfo.getCreditCardExpiry().substring(0, 2);
        String year = CardInfo.getCreditCardExpiry().substring(3, 5);
        //check if the month and year substrings are composed of numeric digits
        for (int i = 0; i < 2; i++) {
            if (!Character.isDigit(month.charAt(i)) || !Character.isDigit(year.charAt(i))) {
                return true;
            }
        }
        //parse the substrings into integers to allow comparisons
        int cardMonth = Integer.parseInt(month);
        int cardYear = Integer.parseInt(year);

        //get the order date month and year and format the year to compare to the expiry
        int orderYear = orderDate.getYear() % 100;
        int orderMonth = orderDate.getMonthValue();
        if (cardMonth > 12) {
            return true;
        }
        //compares the expiry date to the order date returns true when card is out of date
        return cardYear <= orderYear && (cardYear != orderYear || cardMonth < orderMonth);
    }

    public boolean priceInPenceCheck(Pizza[] pizzas, int OrderPrice) {
        int total = 0;
        //loop through every pizza and sum their prices
        for (Pizza pizza : pizzas) {
            total += pizza.priceInPence();
        }
        //adds the £1 delivery fee
        total += 100;
        //compare the total of the Pizza prices against the order price
        return total != OrderPrice; //returns inverse as it used in an if statement to check for invalidity
    }


    public int mulRestaurantsVal(Pizza[] orderedPizzas, Restaurant[] restaurants) {
        Set<Restaurant> countedRestaurants = new HashSet<>();
        //loops through every pizza in the order
        for (Pizza orderedPizza : orderedPizzas) {
            //loops through every restaurant given to interface
            Restaurant foundRestaurant = getPizzaRestaurant(orderedPizza, restaurants);
            //adds pizza restaurant to hashset if it doesn't contain it
            if (foundRestaurant == null) {
                return 2; //returns 2 if pizza isn't on any menus
            } else countedRestaurants.add(foundRestaurant);
            if (countedRestaurants.size() > 1) {
                return 0; //returns 0 if there is more than one restaurant in the order
            }
        }
        if (countedRestaurants.size() == 1) {
            return 1; //return 1 for valid number of restaurants
        } else {
            return 3;// return 3 for undefined behavior
        }
    }

    public Restaurant getPizzaRestaurant(Pizza orderedPizza, Restaurant[] restaurants) {
        //loops through all restaurants
        for (Restaurant restaurant : restaurants) {
            //checks if pizza is on the restaurants menu
            if (checkMenu(orderedPizza, restaurant)) {
                return restaurant; // returns the restaurant that the pizza is on the menu
            }
        }
        return null; //returns null if the pizza couldn't be found on any menu
    }

    public boolean checkMenu(Pizza pizza, Restaurant restaurant) {
        //loops through restaurants menu
        for (int i = 0; i < restaurant.menu().length; i++) {
            if (Objects.equals(pizza.name(), restaurant.menu()[i].name())) {
                return true; //returns true when pizza argument is on the restaurant menu
            }
        }
        return false; //otherwise returns false

    }

    public boolean isClosed(Restaurant restaurant, LocalDate orderDate) {
        //checks if the restaurants open days contains the day of the week of the order
        for (int i = 0; i < restaurant.openingDays().length; i++) {
            if (restaurant.openingDays()[i].equals(orderDate.getDayOfWeek())) {
                return false;
            }
        }
        return true;
    }
}