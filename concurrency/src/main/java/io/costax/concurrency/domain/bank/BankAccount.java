package io.costax.concurrency.domain.bank;


import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "bank_account", schema = "bank")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long code;
    private String number;
    private BigDecimal amount = BigDecimal.ONE;

    @ManyToMany
    @JoinTable(name = "bank_account_client", schema = "bank",
            joinColumns = @JoinColumn(name = "bank_account_code", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "client_code", nullable = false, updatable = false))
    private Set<Client> clients = new HashSet<>();


    public BankAccount() {
    }

    private BankAccount(final String number, final BigDecimal amount) {
        this.number = number;
        this.amount = amount;
    }

    public static BankAccount createBankAccount(final String number, final BigDecimal amount) {
        return new BankAccount(number, amount);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccount)) return false;
        final BankAccount that = (BankAccount) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "code=" + code +
                ", number='" + number + '\'' +
                ", amount=" + amount +
                '}';
    }

    public void addClient(Client client) {
        this.clients.add(client);
    }

    public Long getCode() {
        return code;
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public List<Client> getClients() {
        return List.copyOf(this.clients);
    }

    /**
     * Extract money from the account, basically execute a subtract. we are not take care about any security issue
     * @param valueToExtract value to extract.
     * @return the extracted value.
     */
    public BigDecimal extractMoney(final BigDecimal valueToExtract) {

        if (this.amount.compareTo(valueToExtract) < 0) {
            throw new IllegalStateException(String.format("The current account [%s] contain only [%f] that is less than the required value [%f]",code, amount, valueToExtract));
        }

        this.amount = this.amount.subtract(valueToExtract);
        return valueToExtract;
    }

    /**
     * Add money from the account, basically execute a sum. we are not take care about any security issue
     * @param valueToAdd value to add.
     * @return the total of amount the account after added value.
     */
    public BigDecimal addMoney(BigDecimal valueToAdd) {
        this.amount = this.amount.add(valueToAdd);
        return this.amount;
    }

    public BigDecimal extractAllMoney() {
        return extractMoney(getAmount());
    }
}
