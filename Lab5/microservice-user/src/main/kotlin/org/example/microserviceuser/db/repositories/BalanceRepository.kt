package org.example.microserviceuser.db.repositories

import org.example.microserviceuser.db.Balance
import org.springframework.data.jpa.repository.JpaRepository

interface BalanceRepository : JpaRepository<Balance, Long> {
}