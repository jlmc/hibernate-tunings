package io.costax.concurrency.domain.bank;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "client", schema = "bank")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;
    private String name;

    //@ManyToMany(mappedBy = "clients")
    //private List<BankAccount> accounts = new ArrayList<>();

    public Client() {
    }

    private Client(final String name) {
        this.name = name;
    }

    public static Client createClient(final String name) {
        return new Client(name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        final Client client = (Client) o;
        return Objects.equals(code, client.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Client{" +
                "code=" + code +
                ", name='" + name + '\'' +
                '}';
    }
}
