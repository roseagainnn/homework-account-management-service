package gigabank.accountmanagement.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * Информация о пользователе
 */
@Data
public class User {
    private final String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private final LocalDate birthDate;
    private final List<BankAccount> bankAccounts;
}
