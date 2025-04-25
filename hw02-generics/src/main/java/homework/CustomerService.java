package homework;

import java.util.*;

public class CustomerService {

    private final NavigableMap<Customer, String> map = new TreeMap<>();

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> entry = map.firstEntry();
        if (entry == null) return null;
        return new AbstractMap.SimpleEntry<>(new Customer(entry.getKey()), entry.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> entry = map.higherEntry(customer);
        if (entry == null) return null;
        return new AbstractMap.SimpleEntry<>(new Customer(entry.getKey()), entry.getValue());
    }

    public void add(Customer customer, String data) {
        map.put(new Customer(customer), data);
    }
}
