package eu.epptec.controller;

import eu.epptec.calculator.Calculator;
import org.springframework.web.bind.annotation.*;

@RestController
public class CalculatorController {
    @RequestMapping(value = "/{expr}", method = RequestMethod.GET)
    public Double calculate(@PathVariable("expr") String expr) {
        try {
            return Calculator.calculate(expr);
        } catch (IllegalArgumentException e) {
            return .0;
        }
    }
}
