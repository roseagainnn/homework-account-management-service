package gigabank.accountmanagement.service;

import gigabank.accountmanagement.entity.BankAccount;
import gigabank.accountmanagement.entity.Transaction;
import gigabank.accountmanagement.entity.TransactionType;
import gigabank.accountmanagement.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сервис предоставляет аналитику по операциям пользователей
 */
public class AnalyticsService {

    public BigDecimal getMonthlySpendingByCategory(BankAccount bankAccount, String category){

    }

    public Map<String, BigDecimal> getMonthlySpendingByCategories(User user, Set<String> categories){
    }

    public LinkedHashMap<String, List<Transaction>> getTransactionHistorySortedByAmount(User user){

    }

    public List<Transaction> getLastNTransaction(User user, int n){

        }
    }
    public PriorityQueue<Transaction> getTopNLargestTransactions(User user, int n){

    }


}
