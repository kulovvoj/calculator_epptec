package eu.epptec.calculator;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.Math.pow;

public class Calculator {
    private static Map<String, OperatorAttr> operators;
    static {
        Map<String, OperatorAttr> tmpOperators = new HashMap<>();
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
    // If the given expression is not a valid binary operation, throw an exception
    private static Double solveOperator(Double x, Double y, String operator) {
        switch (operator) {
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
                throw new IllegalArgumentException("Error: Invalid operator \'" + operator + "\'");
        }
    }

    // Takes suffix math expression and solves it using a stack
    // Operators used can only be binary, so two operands are always taken from the stack
    private static Double solveExpr(List<String> expr) {
        Stack<Double> numberStack = new Stack<>();
        for (String i : expr) {
            if (isNumber(i)) {
                numberStack.push(Double.parseDouble(i));
            } else {
                try {
                    // Since added to the stack from left to right
                    // we have to swap the numbers to keep the order of non-commutative operations
                    Double val2 = numberStack.pop();
                    Double val1 = numberStack.pop();
                    numberStack.push(solveOperator(val1, val2, i));
                } catch (EmptyStackException e) {
                    throw new IllegalArgumentException("Error: Invalid number of operands for operator \'" + i + "\'");
                }
            }
        }
        if (numberStack.size() > 1)
            throw new IllegalArgumentException("Error: Too many operands");
        if (numberStack.size() < 1)
            throw new IllegalArgumentException("Error: Too few operands");
        return numberStack.pop();
    }

    // Expression needs to be converted to suffix, so that it's more easily solved
    // Shunting-Yard algorithm is used to convert it
    private static List<String> infixToSuffix (List<String> infixExpr) {
        Stack<String> stack = new Stack<>();
        List<String> suffixExpr = new ArrayList<>();

        for (String i : infixExpr) {
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
                throw new IllegalArgumentException("Error: Invalid operator \'" + i + "\'");
            }
        }
        while (!stack.empty()) {
            if (stack.peek().equals("(")) {
                throw new IllegalArgumentException("Error: Mismatched parentheses");
            }
            suffixExpr.add(stack.pop());
        }
        return suffixExpr;
    }

    // Solves unary "-" operation by converting it to binary -1 * x operation
    // "-" is unary if it's either first in the expression
    // or if the token preceding is not a number or an ending parenthesis (which will always equal a number in the end)
    private static List<String> solveUnaryMinus (List<String> expr) {
        for (int i = 0; i < expr.size(); i++) {
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
        List<String> parsedExpr = new ArrayList<>();

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
        try {
            File file = new File(args[0]);
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                String expr = fileReader.nextLine();
                if (expr.isEmpty())
                    continue;
                List<String> parsedExpr = parseExpr(expr);
                try {
                    List<String> suffixExpr = infixToSuffix(parsedExpr);
                    System.out.println(expr + " = " + solveExpr(suffixExpr));
                } catch (IllegalArgumentException e) {
                    System.out.println("\"" + expr + "\" - " + e.getMessage());
                }
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: Path doesn't lead to any file");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Pass file path as an argument");
        }
    }
}
