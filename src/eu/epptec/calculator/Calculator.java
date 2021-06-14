package eu.epptec.calculator;
import java.util.*;

import static java.lang.Math.pow;

public class Calculator {
    public static Map<String, OperatorAttr> operators;
    static {
        Map<String, OperatorAttr> tmpOperators = new HashMap<String, OperatorAttr>();
        tmpOperators.put("+", new OperatorAttr(2, OperatorAttr.Assoc.LEFT));
        tmpOperators.put("-", new OperatorAttr(2, OperatorAttr.Assoc.LEFT));
        tmpOperators.put("*", new OperatorAttr(3, OperatorAttr.Assoc.LEFT));
        tmpOperators.put("/", new OperatorAttr(3, OperatorAttr.Assoc.LEFT));
        tmpOperators.put("^", new OperatorAttr(4, OperatorAttr.Assoc.RIGHT));
        operators = tmpOperators;
    }

    // Tests if the given string is a number by trying to convert it to double
    private static boolean isNumber (String token) {
        try {
            Double.parseDouble(token);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Checks validity and solves given binary operation
    private static double solveExpr(List<String> expr) {
        // If the given expression is not a valid binary operation, throw an exception
        if (expr.size() != 3 || !isNumber(expr.get(0)) || !isNumber(expr.get(2))) {
            throw new IllegalArgumentException("Error: Invalid input");
        }

        double x = Double.parseDouble(expr.get(0)), y = Double.parseDouble(expr.get(2));
        switch (expr.get(1)) {
            case "+":
                return x + y;
            case "-":
                return x - y;
            case "*":
                return x * y;
            case "/":
                if (y == 0)
                    throw new IllegalArgumentException("Error: Division by zero");
                return x / y;
            case "^":
                return pow(x, y);
            default:
                throw new IllegalArgumentException("Error: Invalid input");
        }
    }

    // Expression needs to be converted to suffix, so that it's more easily solved
    // Shunting-Yard algorithm is used to convert it
    private static List<String> infixToSuffix (List<String> infixExpr) {
        Stack<String> stack = new Stack<String>();
        List<String> suffixExpr = new ArrayList<String>();

        for (String i : infixExpr) {
            System.out.println(i + " : " + operators.containsKey(i));
            if (isNumber(i)) {
                suffixExpr.add(i);
            } else if (operators.containsKey(i)) {
                while (!stack.empty() && !stack.peek().equals("(") &&
                        ((operators.get(i).getAssoc() == OperatorAttr.Assoc.LEFT && operators.get(i).getPreced() <= operators.get(stack.peek()).getPreced()) ||
                         (operators.get(i).getAssoc() == OperatorAttr.Assoc.RIGHT && operators.get(i).getPreced() < operators.get(stack.peek()).getPreced()))) {
                    suffixExpr.add(stack.pop());
                }
                stack.push(i);
            } else if (i.equals("(")) {
                stack.push(i);
            } else if (i.equals(")")) {
                while (!stack.empty() && !stack.peek().equals("("))
                    suffixExpr.add(stack.pop());
                if (stack.empty())
                    throw new IllegalArgumentException("Error: Mismatched parentheses");
                stack.pop();
            } else {
                throw new IllegalArgumentException("Error: Invalid input");
            }
        }
        while (!stack.empty()) {
            System.out.println("TOP");
            if (stack.peek().equals("(")) {
                throw new IllegalArgumentException("Error: Mismatched parentheses");
            }
            suffixExpr.add(stack.pop());
        }
        return suffixExpr;
    }

    // Solves unary "-" operation by converting it to binary -1 * x operation
    private static List<String> solveUnaryMinus (List<String> expr) {
        for (int i = 0; i < expr.size(); i++) {
            // "-" is unary if it's either first in the expression
            // or if the token preceding is not a number or ending parenthesis (which will always equal a number)
            if (expr.get(i).equals("-") && (i == 0 || !(isNumber(expr.get(i - 1)) || expr.get(i - 1).equals(")")))) {
                expr.set(i, "-1");
                expr.add(i + 1, "*");
            }
        }
        return expr;
    }

    // Converts the input string to a string of separate expression tokens
    // For example: (4 + 4) * 68.5 => [(, 4, +, 4, ), *, 68.5]
    private static List<String> parseExpr(String expr) {

        List<String> parsedExpr = new ArrayList<String>();

        // Splits:
        //  - if the previous char was a number and current is not a number or a dot
        //  - if the previous char was not a number or a dot and current is a number
        //  - if the previous char was not a number and current is not a number
        // Which results in an array of strings of one character long operators and numbers that may contain a dot
        expr = expr.replaceAll("\\s", "");

        Collections.addAll(parsedExpr, expr.split("(?<=\\d)(?=\\D)(?!\\.)|(?<=\\D)(?<!\\.)(?=\\d)|(?<=\\D)(?=\\D)"));

        parsedExpr = solveUnaryMinus(parsedExpr);

        return parsedExpr;
    }

    public static void main (String[] args) {
        Scanner myScanner = new Scanner(System.in);

        System.out.println("Enter the equations (enter 0 to stop the program):");

        for (String expr = myScanner.nextLine(); !expr.equals("0"); expr = myScanner.nextLine()){
            /*for (String i : parseExpr(expr)) {

                if (isNumber(i))
                    System.out.println(Double.parseDouble(i));
            }*/

            List<String> parsedExpr = parseExpr(expr);
            try {
                System.out.println(parsedExpr.toString());
                System.out.println(infixToSuffix(parsedExpr).toString());
                //System.out.println("= " + solveExpr(parsedExpr));
            } catch (IllegalArgumentException e) {
                //System.out.println(e.getMessage());
            }
        }
    }
}
