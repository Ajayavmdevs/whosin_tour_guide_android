package com.whosin.business.comman;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DiscountCalculator {

    public static int calculateDiscount(Object discountObj, Object amountObj) {
        BigDecimal discount = paraseToBigDecimal(discountObj);
        BigDecimal amount = paraseToBigDecimal(amountObj);

        // value = (discount * amount) / 100
        BigDecimal value = discount.multiply(amount)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

        // discountPrice = amount - value
        BigDecimal discountPrice = amount.subtract(value);

        // Round to nearest integer
        return discountPrice.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private static BigDecimal paraseToBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        try {
            if (obj instanceof Integer) {
                return new BigDecimal((Integer) obj);
            } else if (obj instanceof Float || obj instanceof Double) {
                return new BigDecimal(String.valueOf(obj));
            } else if (obj instanceof String) {
                return new BigDecimal((String) obj);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    public static boolean isDiscountPriceSame(Object discountObj, Object amountObj) {
        int calculatedDiscountPrice = calculateDiscount(discountObj, amountObj);
        BigDecimal amount = paraseToBigDecimal(amountObj);

        // Compare calculated discount price with amountObj
        return BigDecimal.valueOf(calculatedDiscountPrice).compareTo(amount.setScale(0, RoundingMode.HALF_UP)) == 0;
    }


}

/*
 * Log.d("calculateDiscount", "Test Case 1: " + DiscountCalculator.calculateDiscount(15, 200) + " Expected: 170");
 * Log.d("calculateDiscount", "Test Case 2: " + DiscountCalculator.calculateDiscount(5.5, 300) + " Expected: 284");
 * Log.d("calculateDiscount", "Test Case 3: " + DiscountCalculator.calculateDiscount("10.5", 100) + " Expected: 90");
 * Log.d("calculateDiscount", "Test Case 4: " + DiscountCalculator.calculateDiscount(20, "150.75") + " Expected: 121");
 * Log.d("calculateDiscount", "Test Case 5: " + DiscountCalculator.calculateDiscount("7.25", "400.50") + " Expected: 371");
 * Log.d("calculateDiscount", "Test Case 6: " + DiscountCalculator.calculateDiscount(12.5, 500.25) + " Expected: 438");
 * Log.d("calculateDiscount", "Test Case 7: " + DiscountCalculator.calculateDiscount(0, 999) + " Expected: 999");
 * Log.d("calculateDiscount", "Test Case 8: " + DiscountCalculator.calculateDiscount(25, 0) + " Expected: 0");
 * Log.d("calculateDiscount", "Test Case 9: " + DiscountCalculator.calculateDiscount(null, "500") + " Expected: 500");
 * Log.d("calculateDiscount", "Test Case 10: " + DiscountCalculator.calculateDiscount("15", null) + " Expected: 0");
 * Log.d("calculateDiscount", "Test Case 11: " + DiscountCalculator.calculateDiscount("invalid", 200) + " Expected: 200");
 * Log.d("calculateDiscount", "Test Case 12: " + DiscountCalculator.calculateDiscount(10, "wrong") + " Expected: 0");
 * Log.d("calculateDiscount", "Test Case 13: " + DiscountCalculator.calculateDiscount(0, "455") + " Expected: 455");
 */


