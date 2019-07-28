package com.wesleyxbz.helpdesk.api.service;

import com.wesleyxbz.helpdesk.api.entity.ChangeStatus;
import com.wesleyxbz.helpdesk.api.entity.Ticket;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface TicketService {

    Ticket createOrUpdate(Ticket ticket);

    Optional<Ticket> findById(Long id);

    void deleteById(Long id);

    Page<Ticket> listTicket(int page, int count);

    ChangeStatus createChangeStatus(ChangeStatus changeStatus);

    List<ChangeStatus> listChangeStatus(Long ticketId);

    Page<Ticket> findByCurrentUser(int page, int count, Long userId);

    Page<Ticket> findByParameters(int page, int count, String title, String status, String priority);

    Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status, String priority, Long userId);

    Page<Ticket> findByNumber(int page, int count, int number);

    List<Ticket> findAll();

    Page<Ticket> findByParametersAndAssignedUser(int page, int count, String title, String status, String priority, Long assignedUserId);

    Ticket createOrUpdate(Optional<Ticket> ticketCurrent);
}