package gigabank.accountmanagement.service;

import gigabank.accountmanagement.Main;
import gigabank.accountmanagement.entity.BankAccount;
import gigabank.accountmanagement.entity.Transaction;
import gigabank.accountmanagement.entity.TransactionType;
import gigabank.accountmanagement.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сервис предоставляет аналитику по операциям пользователей
 */

public class AnalyticsService {

    public BigDecimal getMonthlySpendingByCategory(BankAccount bankAccount, String category){
        BigDecimal result = BigDecimal.ZERO;

        if(bankAccount == null || category == null ) {
            return result;
        }

        LocalDateTime startDate = LocalDateTime.now().minusMonths(1);

        for (Transaction transaction : bankAccount.getTransactions()){
            if(TransactionType.PAYMENT.equals(transaction.type()) && !transaction.createdDate().isBefore(startDate) &&
            transaction.category().equals(category)) {

                result = result.add(transaction.value());
            }
        }

        return result;
    }

        public Map<String, BigDecimal> getMonthlySpendingByCategories(User user, Set<String> categories) {
            Map<String, BigDecimal> result = new HashMap<>();
            Set<String> validateCategory = new TransactionService().validateCategories(categories);

            if (user == null || categories == null || validateCategory.isEmpty()|| categories.isEmpty()) {
                return result;
            }


            LocalDateTime startDate = LocalDateTime.now().minusMonths(1);

            for (BankAccount bankAccount : user.getBankAccounts()) {
                for (Transaction transaction : bankAccount.getTransactions()) {
                    if (TransactionType.PAYMENT.equals(transaction.type()) && !transaction.createdDate().isBefore(startDate)
                            && validateCategory.contains(transaction.category())) {

                        result.merge(transaction.category(), transaction.value(), BigDecimal::add);
                    }
                }
            }
            return result;
        }

    public LinkedHashMap<String, List<Transaction>> getTransactionHistorySortedByAmount(User user){
                LinkedHashMap<String, List<Transaction>> resultMap = new LinkedHashMap<>();
                List<Transaction> transactions = new ArrayList<>();

                if (user == null){
                    return resultMap;
                }

                for(BankAccount bankAccount : user.getBankAccounts()){
                    for(Transaction transaction : bankAccount.getTransactions()){
                        if(TransactionType.PAYMENT.equals(transaction.type())){
                            transactions.add(transaction);
                        }
                    }
                }

                transactions.sort((t1,t2) -> t2.value().compareTo(t1.value()));

                for(Transaction transaction : transactions){
                    resultMap.computeIfAbsent(transaction.category(), k -> new ArrayList<>()).add(transaction);
                }

                return resultMap;
    }

    public List<Transaction> getLastNTransactions(User user, int n){
        List<Transaction> result = new ArrayList<>();
        List<
                Transaction> allTransactions = new ArrayList<>();

        if(user == null || n<=0){
            return result;
        }

        for(BankAccount bankAccount : user.getBankAccounts()){
            allTransactions.addAll(bankAccount.getTransactions());
        }

        allTransactions.sort((t1,t2) -> t2.createdDate().compareTo(t1.createdDate()));
        int limit = Math.min(n,allTransactions.size());

        for(int i = 0; i<limit; i ++ ){
            result.add(allTransactions.get(i));
        }

        return result;
    }

    public PriorityQueue<Transaction> getTopNLargestTransactions(User user, int n){
        PriorityQueue<Transaction> result = new PriorityQueue<>(Comparator.comparing(Transaction::value));


        if(user == null || n<=0){
            return result;
        }


        for(BankAccount bankAccount : user.getBankAccounts()){
            for(Transaction transaction : bankAccount.getTransactions()){
                if(TransactionType.PAYMENT.equals(transaction.type())){
                    if(result.size()<n){
                        result.offer(transaction);
                    } else if (transaction.value().compareTo(result.peek().value()) > 0){
                        result.poll();
                        result.offer(transaction);
                    }
                }
            }
        }

        return result;
    }
}
