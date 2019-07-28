package com.wesleyxbz.helpdesk.api.repository;

import com.wesleyxbz.helpdesk.api.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByUserIdOrderByDate(Pageable pages, Long userId);

    Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDate(String title, String status, String priority, Pageable pages);

    Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDate(String title, String status, String priority, Long userId, Pageable pages);

    Page<Ticket> findByNumber(int number, Pageable pages);

    Page<Ticket> findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDate(String title, String status, String priority, Long assignedUserId, Pageable pages);

    Ticket save(Optional<Ticket> ticketCurrent);
}
