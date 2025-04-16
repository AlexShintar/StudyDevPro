package homework;

import java.util.ArrayDeque;
import java.util.Deque;

public class CustomerReverseOrder {
    private final Deque<Customer> stack = new ArrayDeque<>();

    public void add(Customer customer) {
        stack.push(new Customer(customer));
    }

    public Customer take() {
        return stack.isEmpty() ? null : new Customer(stack.pop());
    }
}
