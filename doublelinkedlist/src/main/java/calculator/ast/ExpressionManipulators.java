package calculator.ast;

import calculator.gui.ImageDrawer;
import calculator.interpreter.Environment;
import calculator.errors.EvaluationError;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;


/**
 * All of the public static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Checks to make sure that the given node is an operation AstNode with the expected
     * name and number of children. Throws an EvaluationError otherwise.
     */
    private static void assertNodeMatches(AstNode node, String expectedName, int expectedNumChildren) {
        if (!node.isOperation()
                && !node.getName().equals(expectedName)
                && node.getChildren().size() != expectedNumChildren) {
            throw new EvaluationError("Node is not valid " + expectedName + " node.");
        }
    }

    /**
     * Accepts an 'toDouble(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     * <p>
     * Preconditions:
     * <p>
     * - The 'node' parameter is an operation AstNode with the name 'toDouble'.
     * - The 'node' parameter has exactly one child: the AstNode to convert into a double.
     * <p>
     * Postconditions:
     * <p>
     * - Returns a number AstNode containing the computed double.
     * <p>
     * For example, if this method receives the AstNode corresponding to
     * 'toDouble(3 + 4)', this method should return the AstNode corresponding
     * to '7'.
     * <p>
     * This method is required to handle the following binary operations
     * +, -, *, /, ^
     * (addition, subtraction, multiplication, division, and exponentiation, respectively)
     * and the following unary operations
     * negate, sin, cos
     *
     * @throws EvaluationError if any of the expressions contains an undefined variable.
     * @throws EvaluationError if any of the expressions uses an unknown operation.
     */
    public static AstNode handleToDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        // You should fill in the locations specified by "your code here"
        // in the 'toDoubleHelper' method.
        //
        // If you're not sure why we have a public method calling a private
        // recursive helper method, review your notes from CSE 143 (or the
        // equivalent class you took) about the 'public-private pair' pattern.

        assertNodeMatches(node, "toDouble", 1);
        AstNode exprToConvert = node.getChildren().get(0);
        return new AstNode(toDoubleHelper(env.getVariables(), exprToConvert));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // There are three types of nodes, so we have three cases.
        double toDoubleValue;
        if (node.isNumber()) {
            toDoubleValue = node.getNumericValue();
        } else if (node.isVariable()) {
            // Case 1: It is already defined as another ast node
            // Case 2: It is not defined yet, then throws error
            if (variables.containsKey(node.getName())) {
                toDoubleValue = toDoubleHelper(variables, variables.get(node.getName()));
            } else {
                throw new EvaluationError("Can't Compute Undefined Variable");
            }
        } else {
            // You may assume the expression node has the correct number of children.
            // If you wish to make your code more robust, you can also use the provided
            // "assertNodeMatches" method to verify the input is valid.
            // String name = node.getName();
            // 8 Operation Cases : +, -, *, /, ^, negate, sin, cos, and an exception if none of the above
            if (node.getName().equals("+")) {
                assertNodeMatches(node, "+", 2);
                IList<AstNode> nextGen = node.getChildren();
                toDoubleValue = toDoubleHelper(variables, nextGen.get(0)) + toDoubleHelper(variables, nextGen.get(1));
            } else if (node.getName().equals("-")) {
                assertNodeMatches(node, "-", 2);
                IList<AstNode> nextGen = node.getChildren();
                toDoubleValue = toDoubleHelper(variables, nextGen.get(0)) - toDoubleHelper(variables, nextGen.get(1));
            } else if (node.getName().equals("*")) {
                assertNodeMatches(node, "*", 2);
                IList<AstNode> nextGen = node.getChildren();
                toDoubleValue = toDoubleHelper(variables, nextGen.get(0)) * toDoubleHelper(variables, nextGen.get(1));
            } else if (node.getName().equals("/")) {
                assertNodeMatches(node, "/", 2);
                IList<AstNode> nextGen = node.getChildren();
                toDoubleValue = toDoubleHelper(variables, nextGen.get(0)) / toDoubleHelper(variables, nextGen.get(1));
            } else if (node.getName().equals("^")) {
                assertNodeMatches(node, "^", 2);
                IList<AstNode> nextGen = node.getChildren();
                toDoubleValue = Math.pow(toDoubleHelper(variables, nextGen.get(0)),
                        toDoubleHelper(variables, nextGen.get(1)));
            } else if (node.getName().equals("sin")) { // sin only has one child
                assertNodeMatches(node, "sin", 1);
                IList<AstNode> nextGen = node.getChildren();
                toDoubleValue = Math.sin(toDoubleHelper(variables, nextGen.get(0)));
            } else if (node.getName().equals("cos")) { // cos only has one child
                assertNodeMatches(node, "cos", 1);
                IList<AstNode> nextGen = node.getChildren();
                toDoubleValue = Math.cos(toDoubleHelper(variables, nextGen.get(0)));
            } else if (node.getName().equals("negate")) { // negate only has one child
                assertNodeMatches(node, "negate", 1);
                IList<AstNode> nextGen = node.getChildren();
                toDoubleValue = -1 * toDoubleHelper(variables, nextGen.get(0));
            } else {
                throw new EvaluationError("Cannot Compute Unexpected Operation");
            }
        }
        return toDoubleValue;
    }

    /**
     * Accepts a 'simplify(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     * <p>
     * Preconditions:
     * <p>
     * - The 'node' parameter is an operation AstNode with the name 'simplify'.
     * - The 'node' parameter has exactly one child: the AstNode to simplify
     * <p>
     * Postconditions:
     * <p>
     * - Returns an AstNode containing the simplified inner parameter.
     * <p>
     * For example, if we received the AstNode corresponding to the expression
     * "simplify(3 + 4)", you would return the AstNode corresponding to the
     * number "7".
     * <p>
     * Note: there are many possible simplifications we could implement here,
     * but you are only required to implement a single one: constant folding.
     * <p>
     * That is, whenever you see expressions of the form "NUM + NUM", or
     * "NUM - NUM", or "NUM * NUM", simplify them.
     */
    public static AstNode handleSimplify(Environment env, AstNode node) {
        // Try writing this one on your own!
        // Hint 1: Your code will likely be structured roughly similarly
        //         to your "handleToDouble" method
        // Hint 2: When you're implementing constant folding, you may want
        //         to call your "handleToDouble" method in some way
        // Hint 3: When implementing your private pair, think carefully about
        //         when you should recurse. Do you recurse after simplifying
        //         the current level? Or before?

        assertNodeMatches(node, "simplify", 1);
        AstNode exprToConvert = node.getChildren().get(0);
        return simplifyHelper(env.getVariables(), exprToConvert);
    }

    private static AstNode simplifyHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // There are three types of nodes, so we have three cases.
        if (node.isNumber()) {
            return node;
        } else if (node.isVariable()) {
            // Case 1: It is already defined as another ast node
            // Case 2: It is not defined yet, then we do nothing with it. ?? this is the point of simplify
            if (variables.containsKey(node.getName())) {
                return simplifyHelper(variables, variables.get(node.getName()));
            } else {
                return node;
            }
        } else {
            // You may assume the expression node has the correct number of children.
            // If you wish to make your code more robust, you can also use the provided
            // "assertNodeMatches" method to verify the input is valid.
            // String name = node.getName();
            return simplifier(variables, node);
        }
    }

    private static AstNode simplifier(IDictionary<String, AstNode> variables, AstNode node) {
        // 8 Operation Cases : +, -, *, /, ^, negate, sin, cos, and an exception if none of the above
        if (node.getName().equals("+")) {
            assertNodeMatches(node, "+", 2);
            IList<AstNode> nextGen = node.getChildren();
            AstNode childOne = simplifyHelper(variables, nextGen.get(0));
            AstNode childTwo = simplifyHelper(variables, nextGen.get(1));
            try { // if (nextGen.get(0).isNumber() && nextGen.get(1).isNumber()) {
                return new AstNode(childOne.getNumericValue() + childTwo.getNumericValue());
            } catch (EvaluationError e) {
                nextGen.set(0, childOne);
                nextGen.set(1, childTwo);
                return new AstNode("+", nextGen);
            }
        } else if (node.getName().equals("-")) {
            assertNodeMatches(node, "-", 2);
            IList<AstNode> nextGen = node.getChildren();
            AstNode childOne = simplifyHelper(variables, nextGen.get(0));
            AstNode childTwo = simplifyHelper(variables, nextGen.get(1));
            try { // if (nextGen.get(0).isNumber() && nextGen.get(1).isNumber()) {
                return new AstNode(childOne.getNumericValue() - childTwo.getNumericValue());
            } catch (EvaluationError e) {
                nextGen.set(0, childOne);
                nextGen.set(1, childTwo);
                return new AstNode("-", nextGen);
            }
        } else if (node.getName().equals("*")) {
            assertNodeMatches(node, "*", 2);
            IList<AstNode> nextGen = node.getChildren();
            AstNode childOne = simplifyHelper(variables, nextGen.get(0));
            AstNode childTwo = simplifyHelper(variables, nextGen.get(1));
            try { // if (nextGen.get(0).isNumber() && nextGen.get(1).isNumber()) {
                return new AstNode(childOne.getNumericValue() * childTwo.getNumericValue());
            } catch (EvaluationError e) {
                nextGen.set(0, childOne);
                nextGen.set(1, childTwo);
                return new AstNode("*", nextGen);
            }
        } else if (node.getName().equals("/")) {
            assertNodeMatches(node, "/", 2);
            IList<AstNode> nextGen = node.getChildren();
            AstNode childOne = simplifyHelper(variables, nextGen.get(0));
            AstNode childTwo = simplifyHelper(variables, nextGen.get(1));
            nextGen.set(0, childOne);
            nextGen.set(1, childTwo);
            return new AstNode("/", nextGen);
        } else if (node.getName().equals("^")) {
            assertNodeMatches(node, "^", 2);
            IList<AstNode> nextGen = node.getChildren();
            AstNode childOne = simplifyHelper(variables, nextGen.get(0));
            AstNode childTwo = simplifyHelper(variables, nextGen.get(1));
            try { // if (nextGen.get(0).isNumber() && nextGen.get(1).isNumber()) {
                return new AstNode(Math.pow(childOne.getNumericValue(), childTwo.getNumericValue()));
            } catch (EvaluationError e) {
                nextGen.set(0, childOne);
                nextGen.set(1, childTwo);
                return new AstNode("^", nextGen);
            }
        } else if (node.getName().equals("sin")) { // sin only has one child
            assertNodeMatches(node, "sin", 1);
            IList<AstNode> nextGen = node.getChildren();
            AstNode childOne = simplifyHelper(variables, nextGen.get(0));
            nextGen.set(0, childOne);
            return new AstNode("sin", nextGen);
        } else if (node.getName().equals("cos")) { // cos only has one child
            assertNodeMatches(node, "cos", 1);
            IList<AstNode> nextGen = node.getChildren();
            AstNode childOne = simplifyHelper(variables, nextGen.get(0));
            nextGen.set(0, childOne);
            return new AstNode("cos", nextGen);
        } else if (node.getName().equals("negate")) { // negate only has one child
            assertNodeMatches(node, "negate", 1);
            IList<AstNode> nextGen = node.getChildren();
            AstNode childOne = simplifyHelper(variables, nextGen.get(0));
            try { // if (nextGen.get(0).isNumber()) {
                return new AstNode(-1 * childOne.getNumericValue());
            } catch (EvaluationError e) {
                nextGen.set(0, childOne);
                return new AstNode("negate", nextGen);
            }
        } else {
            throw new EvaluationError("Cannot Compute Unexpected Operation");
        }
    }

    /**
     * Accepts an Environment variable and a 'plot(exprToPlot, var, varMin, varMax, step)'
     * AstNode and generates the corresponding plot on the ImageDrawer attached to the
     * environment. Returns some arbitrary AstNode.
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This method will receive the AstNode corresponding to 'plot(3 * x, x, 2, 5, 0.5)'.
     * Your 'handlePlot' method is then responsible for plotting the equation
     * "3 * x", varying "x" from 2 to 5 in increments of 0.5.
     *
     * In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    //'plot(exprToPlot, var, varMin, varMax, step)'
    public static AstNode plot(Environment env, AstNode node) {
        assertNodeMatches(node, "plot", 5);
        IDictionary<String, AstNode> variables = env.getVariables();
        IList<AstNode> plotArgs = node.getChildren();
        String var = plotArgs.get(1).getName();
        double min;
        double max;
        double step;
        try {
            min = toDoubleHelper(variables, plotArgs.get(2));
            max = toDoubleHelper(variables, plotArgs.get(3));
            step = toDoubleHelper(variables, plotArgs.get(4));
        } catch (EvaluationError e) {
            throw new EvaluationError("Min, Max, or Step invalid");
        }
        if (min > max) {
            throw new EvaluationError("Min is greater than Max");
        }
        if (variables.containsKey(var)) {
            throw new EvaluationError("Variable is already defined");
        }
        if (step <= 0) {
            throw new EvaluationError("Step is less than or equal To zero");
        }


        ImageDrawer plotter = env.getImageDrawer();
        AstNode func = simplifyHelper(variables, plotArgs.get(0));
        String title = "Graph of f(" + var + ") vs. " + var;
        String xAxisLbl = "Linspace of " + var + " from " + min + " to " + max + " in steps of " + step;
        String yAxisLbl = "f(" + var + ")";
        IList<Double> xValues = new DoubleLinkedList<>();
        IList<Double> yValues = new DoubleLinkedList<>();
        for (double curr = min; curr <= max; curr += step) {
            xValues.add(curr);
            variables.put(var, new AstNode(curr));
            try {
                yValues.add(toDoubleHelper(variables, func));
            } catch (EvaluationError e) {
                throw new EvaluationError("Undefined variable in function");
            }
        }
        variables.remove(var);

        plotter.drawScatterPlot(title, xAxisLbl, yAxisLbl, xValues, yValues);


        // Note: every single function we add MUST return an
        // AST node that your "simplify" function is capable of handling.
        // However, your "simplify" function doesn't really know what to do
        // with "plot" functions (and what is the "plot" function supposed to
        // evaluate to anyways?) so we'll settle for just returning an
        // arbitrary number.
        //
        // When working on this method, you should uncomment the following line:
        //
        return new AstNode(1);
    }
}
