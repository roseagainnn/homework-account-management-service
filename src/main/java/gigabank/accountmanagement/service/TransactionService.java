package gigabank.accountmanagement.service;

import java.util.HashSet;
import java.util.Set;

/**
 * Сервис отвечает за управление платежами и переводами
 */

public class TransactionService {

    public static Set<String> transactionCategories = Set.of(
            "Health", "Beauty", "Education");

    public Set<String> validateCategories (Set<String> categories) {
    }
}
