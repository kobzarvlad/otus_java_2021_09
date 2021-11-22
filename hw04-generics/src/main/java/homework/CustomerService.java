package homework;


import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CustomerService {

    //todo: 3. надо реализовать методы этого класса
    private final TreeMap<Customer, String> customerMap = new TreeMap<>(Comparator.comparing(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        return getEntryWithClonedKey(customerMap.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return getEntryWithClonedKey(customerMap.higherEntry(customer));
    }

    public void add(Customer customer, String data) {
        customerMap.put(customer.clone(), data);
    }

    private static Map.Entry<Customer, String> getEntryWithClonedKey(Map.Entry<Customer, String> entry) {
        return entry != null ? Map.entry(entry.getKey().clone(), entry.getValue()) : null;
    }
}
