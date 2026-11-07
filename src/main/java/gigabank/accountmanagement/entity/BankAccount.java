package gigabank.accountmanagement.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Информация о банковском счете пользователя
 */

import lombok.ToString;
@Data
public class BankAccount {
    private final String id;
    private BigDecimal balance;
    @ToString.Exclude
    private final User owner;
    @ToString.Exclude
    private final List<Transaction> transactions;

    public BankAccount(String id, User owner, List<Transaction> transactions) {
        this.id = id;
        this.owner = owner;
        this.transactions = transactions;
    }
}
