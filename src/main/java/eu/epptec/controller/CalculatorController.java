package eu.epptec.controller;

import eu.epptec.calculator.Calculator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CalculatorController {
    @RequestMapping(value = "/{expr}", method = RequestMethod.GET)
    public Double calculate(@PathVariable("expr") String expr) {
        return Calculator.calculate(expr);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody String
    badRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}