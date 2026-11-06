package gigabank.accountmanagement.entity;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Builder
public record Transaction (String id, BigDecimal value, TransactionType type,
        String category, BankAccount bankAccount, LocalDateTime createdDate){}
