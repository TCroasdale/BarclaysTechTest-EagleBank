package com.techtest.eaglebank.entities;

import java.time.OffsetDateTime;
import java.util.Random;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.baeldung.openapi.model.BankAccountResponse.CurrencyEnum;
import com.baeldung.openapi.model.BankAccountResponse.SortCodeEnum;
import com.baeldung.openapi.model.CreateBankAccountRequest.AccountTypeEnum;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@AllArgsConstructor
@Data
@Getter
@Setter
public class Account {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(name = "accountName")
    public String accountName;

    @Column(name = "accountType")
    @Enumerated(value=EnumType.STRING)
    public AccountTypeEnum accountType;

    @Column(name = "ownerid")
    public long ownerid;

    @Column(name = "accountNumber")
    public String accountNumber;

    @Column(name = "sortCode")
    @Enumerated(value=EnumType.STRING)
    public SortCodeEnum sortCode;

    @Column(name = "balance")
    public double balance;

    @Column(name = "currency")
    @Enumerated(value=EnumType.STRING)
    public CurrencyEnum currency;


    @Column(name = "createdAt")
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public OffsetDateTime createdTimestamp;

    @Column(name = "updatedAt")
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public OffsetDateTime updatedTimestamp;

    private static String randomDigit() {
        return Integer.toString(new Random().nextInt(9));
    }

    public Account() {
        accountNumber = "";
        for (int i = 0; i < 8; i++) {
            accountNumber += randomDigit();
        }

        sortCode = SortCodeEnum._10_10_10;
        balance = 0;
        currency = CurrencyEnum.GBP;
    }
}
