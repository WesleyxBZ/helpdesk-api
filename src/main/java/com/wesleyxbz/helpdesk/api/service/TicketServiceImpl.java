package com.wesleyxbz.helpdesk.api.service;

import com.wesleyxbz.helpdesk.api.entity.ChangeStatus;
import com.wesleyxbz.helpdesk.api.entity.Ticket;
import com.wesleyxbz.helpdesk.api.repository.ChangeStatusRepository;
import com.wesleyxbz.helpdesk.api.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ChangeStatusRepository changeStatusRepository;

    @Override
    public Ticket createOrUpdate(Ticket ticket) {
        return this.ticketRepository.save(ticket);
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public Page<Ticket> listTicket(int page, int count) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findAll(pages);
    }

    @Override
    public ChangeStatus createChangeStatus(ChangeStatus changeStatus) {
        return changeStatusRepository.save(changeStatus);
    }

    @Override
    public List<ChangeStatus> listChangeStatus(Long ticketId) {
        return changeStatusRepository.findByTicketIdOrderByDateChangeStatusDesc(ticketId);
    }

    @Override
    public Page<Ticket> findByCurrentUser(int page, int count, Long userId) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByUserIdOrderByDate(pages, userId);
    }

    @Override
    public Page<Ticket> findByParameters(int page, int count, String title, String status, String priority) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDate(title, status, priority, pages);
    }

    @Override
    public Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status, String priority, Long userId) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDate(title, status, priority, userId, pages);
    }

    @Override
    public Page<Ticket> findByNumber(int page, int count, int number) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByNumber(number, pages);
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Page<Ticket> findByParametersAndAssignedUser(int page, int count, String title, String status, String priority, Long assignedUserId) {
        Pageable pages = new PageRequest(page, count);
        return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDate(title, status, priority, assignedUserId, pages);
    }

    @Override
    public Ticket createOrUpdate(Optional<Ticket> ticketCurrent) {
        return ticketRepository.save(ticketCurrent);
    }

}
