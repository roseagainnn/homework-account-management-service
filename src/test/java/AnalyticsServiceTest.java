import gigabank.accountmanagement.entity.BankAccount;
import gigabank.accountmanagement.entity.Transaction;
import gigabank.accountmanagement.entity.TransactionType;
import gigabank.accountmanagement.entity.User;
import gigabank.accountmanagement.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AnalyticsServiceTest {

    private AnalyticsService analyticsService;

    private BankAccount emptyAccount(){
        return new BankAccount("Mr.Empty",new User("EmptyUser",LocalDate.of(2000,1,1 ),new ArrayList<>()),new ArrayList<>());
    }

    private Transaction testTransaction(){
       return new Transaction("29129XAXD123",new BigDecimal("25.00"),TransactionType.PAYMENT,"Health",emptyAccount(),LocalDateTime.now().minusDays(2));
    }

    private User testUser(){
        return new User("2312X",LocalDate.of(2000,1,1),new ArrayList<BankAccount>());
    }

    @BeforeEach
    void setup(){
        analyticsService = new AnalyticsService();
    }

    @Test
    void getMonthlySpendingByCategorySuccessfulExecution(){
        BankAccount account = emptyAccount();
        Transaction transaction = testTransaction();

        account.getTransactions().add(transaction);

       BigDecimal result = analyticsService.getMonthlySpendingByCategory(account,transaction.category());

        assertEquals(0, new BigDecimal("25.00").compareTo(result));
    }

    @Test
    void getMonthlySpendingByCategoryUserNull(){
        Transaction transaction = testTransaction();

        BigDecimal result = analyticsService.getMonthlySpendingByCategory(null,transaction.category());
        assertEquals(0,BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void getMonthlySpendingByCategoryNullCategory(){
        BankAccount account = emptyAccount();

        Transaction t1 = new Transaction("29129XAXD123",new BigDecimal("25.00"),TransactionType.PAYMENT,null,account,LocalDateTime.now().minusDays(2));
        account.getTransactions().add(t1);
        BigDecimal result = analyticsService.getMonthlySpendingByCategory(account,t1.category());

        assertEquals(0,BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void getMonthlySpendingByCategoryTransactionPaymentNoValid(){
        BankAccount account = emptyAccount();

        Transaction t1 = new Transaction("29129XAXD123",new BigDecimal("25.00"),TransactionType.DEPOSIT,"Health",account,LocalDateTime.now().minusDays(2));
        account.getTransactions().add(t1);
        BigDecimal result = analyticsService.getMonthlySpendingByCategory(account,t1.category());

        assertEquals(0,BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void getMonthlySpendingByCategoryDateNoValid(){
        BankAccount account = emptyAccount();

        Transaction t1 = new Transaction("29129XAXD123",new BigDecimal("25.00"),TransactionType.PAYMENT,"Health",account,LocalDateTime.now().minusDays(33));
        account.getTransactions().add(t1);
        BigDecimal result = analyticsService.getMonthlySpendingByCategory(account,t1.category());

        assertEquals(0,BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void getMonthlySpendingByCategoriesSuccessfulExecution(){

        Set<String> validateCategory = new HashSet<>();
        BankAccount account = emptyAccount();
        User user = testUser();
        user.getBankAccounts().add(account);

        validateCategory.add("Health");
        validateCategory.add("Beauty");
        validateCategory.add("Education");

        Transaction t1 = new Transaction("29129XAXD123",new BigDecimal("28.00"),TransactionType.PAYMENT,"Health",account,LocalDateTime.now().minusDays(2));
        Transaction t2 = new Transaction("312213123213",new BigDecimal("200.00"),TransactionType.PAYMENT,"Health",account,LocalDateTime.now().minusDays(2));
        Transaction t3 = new Transaction("0JSDH2837128",new BigDecimal("133.77"),TransactionType.PAYMENT,"Beauty",account,LocalDateTime.now().minusDays(2));


        account.getTransactions().add(t1);
        account.getTransactions().add(t2);
        account.getTransactions().add(t3);

        Map<String, BigDecimal> result = analyticsService.getMonthlySpendingByCategories(user, validateCategory);

        assertEquals(0,new BigDecimal("228.00").compareTo(result.get("Health")));
        assertEquals(0,new BigDecimal("133.77").compareTo(result.get("Beauty")));
        assertFalse(result.containsKey("Education"));

    }

    @Test
    void getMonthlySpendingByCategoriesEmptySet(){
        Set<String> validateCategory = new HashSet<>();
        User user = testUser();
        Map<String, BigDecimal> result = analyticsService.getMonthlySpendingByCategories(user, validateCategory);

        assertTrue(result.isEmpty());
    }


    @Test
    void getTransactionHistorySortedByAmountSuccessfulExecution(){
        BankAccount account = emptyAccount();
        User user = testUser();
        user.getBankAccounts().add(account);

        Transaction t1 = new Transaction("29129XAXD123",new BigDecimal("28.00"),TransactionType.PAYMENT,"Health",account,LocalDateTime.now().minusDays(2));
        Transaction t2 = new Transaction("312213123213",new BigDecimal("200.00"),TransactionType.PAYMENT,"Education",account,LocalDateTime.now().minusDays(2));
        Transaction t3 = new Transaction("0JSDH2837128",new BigDecimal("133.77"),TransactionType.PAYMENT,"Beauty",account,LocalDateTime.now().minusDays(2));
        Transaction t4 = new Transaction("0124ASD32318",new BigDecimal("500.00"),TransactionType.PAYMENT,"Beauty",account,LocalDateTime.now().minusDays(2));
        Transaction t5 = new Transaction("213784738412",new BigDecimal("1000.00"),TransactionType.DEPOSIT,"Beauty",account,LocalDateTime.now().minusDays(2));


        account.getTransactions().add(t1);
        account.getTransactions().add(t2);
        account.getTransactions().add(t3);
        account.getTransactions().add(t4);
        account.getTransactions().add(t5);

        LinkedHashMap<String, List<Transaction>> resultMap = analyticsService.getTransactionHistorySortedByAmount(user);

        assertTrue(resultMap.get("Health").contains(t1));
        assertTrue(resultMap.get("Education").contains(t2));
        assertTrue(resultMap.get("Beauty").contains(t3));
        assertTrue(resultMap.get("Beauty").contains(t4));

        List<String> keyList = new ArrayList<>(resultMap.keySet());

        assertTrue(keyList.get(0).contains("Beauty"));
        assertTrue(keyList.get(1).contains("Education"));
        assertTrue(keyList.get(2).contains("Health"));

        assertEquals(t4, resultMap.get("Beauty").get(0));
        assertEquals(t3, resultMap.get("Beauty").get(1));

        assertFalse(resultMap.get("Beauty").contains(t5));

    }

    @Test
   void getLastNTransactionsSuccessfulExecution() {

        BankAccount account = emptyAccount();
        BankAccount account2 = emptyAccount();
        User user = testUser();
        user.getBankAccounts().add(account);
        user.getBankAccounts().add(account2);


        int THREE_TRANSACTIONS = 3;
        int TWO_TRANSACTIONS = 2;
        int FIVE_TRANSACTIONS = 5;
        int ZERO_TRANSACTIONS = 0;
        int MINUS_TRANSACTIONS = -1;


        Transaction t1 = new Transaction("29129XAXD123", new BigDecimal("28.00"), TransactionType.PAYMENT, "Health", account, LocalDateTime.now().minusDays(2));
        Transaction t2 = new Transaction("312213123213", new BigDecimal("200.00"), TransactionType.PAYMENT, "Education", account, LocalDateTime.now().minusDays(5));
        Transaction t3 = new Transaction("0JSDH2837128", new BigDecimal("133.77"), TransactionType.PAYMENT, "Beauty", account, LocalDateTime.now().minusDays(1));
        Transaction t4 = new Transaction("0124ASD32318", new BigDecimal("500.00"), TransactionType.PAYMENT, "Beauty", account2, LocalDateTime.now().minusDays(0));
        Transaction t5 = new Transaction("213784738412", new BigDecimal("1000.00"), TransactionType.DEPOSIT, "Beauty", account2, LocalDateTime.now().minusDays(0));

        account.getTransactions().add(t1);
        account.getTransactions().add(t2);
        account.getTransactions().add(t3);
        account2.getTransactions().add(t4);
        account2.getTransactions().add(t5);


        List<Transaction> threeTransactions = analyticsService.getLastNTransactions(user, THREE_TRANSACTIONS);
        List<Transaction> twoTransactions = analyticsService.getLastNTransactions(user, TWO_TRANSACTIONS);
        List<Transaction> fiveTransactions = analyticsService.getLastNTransactions(user, FIVE_TRANSACTIONS);
        List<Transaction> zeroTransactions = analyticsService.getLastNTransactions(user, ZERO_TRANSACTIONS);
        List<Transaction> minusTransactions = analyticsService.getLastNTransactions(user, MINUS_TRANSACTIONS);

        List<Transaction> emptyResult = analyticsService.getLastNTransactions(testUser(), THREE_TRANSACTIONS);

        assertTrue(emptyResult.isEmpty());

        assertEquals(3, threeTransactions.size());
        assertEquals(5, fiveTransactions.size());
        assertEquals(0, zeroTransactions.size());
        assertEquals(0, minusTransactions.size());


        assertEquals(2, twoTransactions.size());

        assertEquals(t4, twoTransactions.get(0));
        assertEquals(t5, twoTransactions.get(1));

        assertEquals(t4, fiveTransactions.get(0));
        assertEquals(t5, fiveTransactions.get(1));
        assertEquals(t3, fiveTransactions.get(2));
        assertEquals(t1, fiveTransactions.get(3));
        assertEquals(t2, fiveTransactions.get(4));

    }

    @Test
   void getTopNLargestTransactionsSuccessfulExecution(){

        BankAccount account = emptyAccount();
        BankAccount account2 = emptyAccount();
        User user = testUser();
        user.getBankAccounts().add(account);
        user.getBankAccounts().add(account2);

        User user2 = testUser();
        BankAccount account3 = emptyAccount();
        user2.getBankAccounts().add(account3);

        int THREE_TRANSACTIONS = 3;
        int FIVE_TRANSACTIONS = 5;


        Transaction t1 = new Transaction("29129XAXD123",new BigDecimal("28.00"),TransactionType.PAYMENT,"Health",account,LocalDateTime.now().minusDays(2));
        Transaction t2 = new Transaction("312213123213",new BigDecimal("200.00"),TransactionType.PAYMENT,"Education",account,LocalDateTime.now().minusDays(5));
        Transaction t3 = new Transaction("0JSDH2837128",new BigDecimal("133.77"),TransactionType.PAYMENT,"Beauty",account,LocalDateTime.now().minusDays(1));
        Transaction t4 = new Transaction("0124ASD32318",new BigDecimal("500.00"),TransactionType.PAYMENT,"Beauty",account2,LocalDateTime.now().minusDays(0));
        Transaction t5 = new Transaction("213784738412",new BigDecimal("1000.00"),TransactionType.DEPOSIT,"Beauty",account2,LocalDateTime.now().minusDays(0));

        Transaction t6 = new Transaction("213784738412",new BigDecimal("1000.00"),TransactionType.DEPOSIT,"Beauty",account3,LocalDateTime.now().minusDays(0));


        account.getTransactions().add(t1);
        account.getTransactions().add(t2);
        account.getTransactions().add(t3);
        account2.getTransactions().add(t4);
        account2.getTransactions().add(t5);

        account3.getTransactions().add(t6);

        PriorityQueue<Transaction> result = analyticsService.getTopNLargestTransactions(user,THREE_TRANSACTIONS);
        PriorityQueue<Transaction> result2 = analyticsService.getTopNLargestTransactions(user,FIVE_TRANSACTIONS);

        PriorityQueue<Transaction> result3 = analyticsService.getTopNLargestTransactions(user2,FIVE_TRANSACTIONS);

        assertEquals(3,result.size());
        assertEquals(4,result2.size());

        assertTrue(result3.isEmpty());

        assertTrue(result.contains(t4));
        assertTrue(result.contains(t2));
        assertTrue(result.contains(t3));
        assertFalse(result.contains(t5));

        assertTrue(result2.contains(t1));
        assertTrue(result2.contains(t2));
        assertTrue(result2.contains(t3));
        assertTrue(result2.contains(t4));

        assertEquals(t3,result.poll());
        assertEquals(t2,result.poll());
        assertEquals(t4,result.poll());

    }
}
