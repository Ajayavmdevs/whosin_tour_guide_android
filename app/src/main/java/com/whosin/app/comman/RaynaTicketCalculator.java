package com.whosin.app.comman;

import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RaynaTicketCalculator {


    public static String calculateDiscount(Object amountObj, RaynaTicketDetailModel model) {
        BigDecimal amount = paraseToBigDecimal(amountObj);
        BigDecimal roundedValue = customRound(amount);
        return roundedValue.toString();
    }

    public static BigDecimal paraseToBigDecimal(Object obj) {
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

    public static BigDecimal customRound(BigDecimal value) {
        BigDecimal floorValue = value.setScale(0, RoundingMode.DOWN);
        BigDecimal decimalPart = value.subtract(floorValue);

        BigDecimal[] steps = {
                new BigDecimal("0.00"), new BigDecimal("0.25"),
                new BigDecimal("0.50"), new BigDecimal("0.75"), BigDecimal.ONE
        };

        BigDecimal closest = steps[0];
        BigDecimal minDiff = decimalPart.subtract(steps[0]).abs();

        for (BigDecimal step : steps) {
            BigDecimal diff = decimalPart.subtract(step).abs();
            if (diff.compareTo(minDiff) < 0) {
                minDiff = diff;
                closest = step;
            }
        }

        return floorValue.add(closest);
    }

    public static float getDiscountPrice(int totalMember, float price) {
         return Utils.roundFloatToFloat(price * totalMember);
    }

}
