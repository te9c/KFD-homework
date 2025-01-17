package com.example.lab4.db.repositories

import com.example.lab4.db.Balance
import org.springframework.data.jpa.repository.JpaRepository

interface BalanceRepository : JpaRepository<Balance, Long> {
}