package com.techtest.eaglebank.entities;

import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.baeldung.openapi.model.TransactionResponse.CurrencyEnum;
import com.baeldung.openapi.model.TransactionResponse.TypeEnum;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    public long getId() {
        return id;
    }

    @Column(name = "transactionType")
    @Enumerated(value=EnumType.STRING)
    public TypeEnum transfactionType;

    @Column(name = "amount")
    public double amount;

    @Column(name = "currency")
    @Enumerated(value=EnumType.STRING)
    public CurrencyEnum currency;

    @Column(name = "reference")
    public String reference;

    @Column(name = "accountNumber")
    public String accountNumber;

    @Column(name = "createdAt")
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public OffsetDateTime createdTimestamp;

    @Column(name = "updatedAt")
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public OffsetDateTime updatedTimestamp;
}
