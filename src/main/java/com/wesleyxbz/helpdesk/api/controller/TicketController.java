package com.wesleyxbz.helpdesk.api.controller;

import com.wesleyxbz.helpdesk.api.dto.Summary;
import com.wesleyxbz.helpdesk.api.entity.ChangeStatus;
import com.wesleyxbz.helpdesk.api.entity.Ticket;
import com.wesleyxbz.helpdesk.api.entity.User;
import com.wesleyxbz.helpdesk.api.enums.ProfileEnum;
import com.wesleyxbz.helpdesk.api.enums.StatusEnum;
import com.wesleyxbz.helpdesk.api.response.Response;
import com.wesleyxbz.helpdesk.api.security.jwt.JwtTokenUtil;
import com.wesleyxbz.helpdesk.api.service.TicketService;
import com.wesleyxbz.helpdesk.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> createOrUpdate(HttpServletRequest requet, @RequestBody Ticket ticket, BindingResult result) {

        Response<Ticket> response = new Response<Ticket>();

        try {
            validateCreateTicket(ticket, result);

            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }

            ticket.setStatus(StatusEnum.getStatus("New"));
            ticket.setUser(userFromRequest(requet));
            ticket.setDate(new Date());
            ticket.setNumber(generateNumber());
            Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
            response.setData(Optional.ofNullable(ticketPersisted));

        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    private void validateCreateTicket(Ticket ticket, BindingResult result) {
        if (ticket.getTitle() == null) {
            result.addError(new ObjectError("Ticket", "Titulo não informado"));
            return;
        }
    }

    public User userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUsernameFromToken(token);
        return userService.findByEmail(email);
    }

    private Integer generateNumber() {
        Random random = new Random();
        return random.nextInt(9999);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result) {

        Response<Ticket> response = new Response<Ticket>();

        try {
            validateUpdateTicket(ticket, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }

            Optional<Ticket> ticketCurrent = ticketService.findById(ticket.getId());
            ticket.setStatus(ticketCurrent.get().getStatus());
            ticket.setUser(ticketCurrent.get().getUser());
            ticket.setDate(ticketCurrent.get().getDate());
            ticket.setNumber(ticketCurrent.get().getNumber());

            if (ticketCurrent.get().getAssignedUser() != null) {
                ticket.setAssignedUser(ticketCurrent.get().getAssignedUser());
            }

            Ticket ticketPersisted = ticketService.createOrUpdate(ticket);
            response.setData(Optional.ofNullable(ticketPersisted));

        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    private void validateUpdateTicket(Ticket ticket, BindingResult result) {
        if (ticket.getId() == null) {
            result.addError(new ObjectError("Ticket", "Ticket nao existe"));
            return;
        }
    }

    @GetMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> finById(@PathVariable("id") Long id) {

        Response<Ticket> response = new Response<Ticket>();
        Optional<Ticket> ticket = ticketService.findById(id);

        if (ticket == null) {
            response.getErrors().add("Ticket não Encontrado : " + id);
            return ResponseEntity.badRequest().body(response);
        }

        List<ChangeStatus> changes = new ArrayList<>();
        Iterable<ChangeStatus> changesCurrent = ticketService.listChangeStatus(ticket.get().getId());

        for (Iterator<ChangeStatus> iterator = changesCurrent.iterator(); iterator.hasNext(); ) {
            ChangeStatus changeStatus = iterator.next();
            changeStatus.setTicket(null);
            changes.add(changeStatus);
        }

        ticket.get().setChanges(changes);
        response.setData(ticket);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") Long id) {

        Response<String> response = new Response<>();
        Optional<Ticket> ticket = ticketService.findById(id);

        if (ticket == null) {
            response.getErrors().add("Ticket não Encontrado : " + id);
            return ResponseEntity.badRequest().body(response);
        }

        ticketService.deleteById(id);
        return ResponseEntity.ok(new Response<String>());

    }

    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request, @PathVariable("page") int page, @PathVariable("count") int count) {

        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        User userRequest = userFromRequest(request);

        if (userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
            tickets = ticketService.listTicket(page, count);
        } else if (userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
            tickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
        }

        response.setData(Optional.ofNullable(tickets));
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request,
                                                               @PathVariable int page,
                                                               @PathVariable int count,
                                                               @PathVariable Integer number,
                                                               @PathVariable String title,
                                                               @PathVariable String status,
                                                               @PathVariable String priority,
                                                               @PathVariable boolean assigned) {

        title = title.equals("uninformed") ? "" : title;
        status = status.equals("uninformed") ? "" : status;
        priority = priority.equals("uninformed") ? "" : priority;

        Response<Page<Ticket>> response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;

        if (number > 0) {
            tickets = ticketService.findByNumber(page, count, number);
        } else {
            User userRequest = userFromRequest(request);
            if (userRequest.getProfile().equals(ProfileEnum.ROLE_TECHNICIAN)) {
                if (assigned) {
                    tickets = ticketService.findByParametersAndAssignedUser(page, count, title, status, priority, userRequest.getId());
                } else {
                    tickets = ticketService.findByParameters(page, count, title, status, priority);
                }
            } else if (userRequest.getProfile().equals(ProfileEnum.ROLE_CUSTOMER)) {
                tickets = ticketService.findByParametersAndCurrentUser(page, count, title, status, priority, userRequest.getId());
            }
        }

        response.setData(Optional.ofNullable(tickets));
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "{id}/{status}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> changeStatus(@PathVariable Long id,
                                                         @PathVariable String status,
                                                         HttpServletRequest request,
                                                         @RequestBody Ticket ticket,
                                                         BindingResult result) {

        Response<Ticket> response = new Response<>();

        try {
            validateChangeStatus(id, status, result);

            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }

            Optional<Ticket> ticketCurrent = ticketService.findById(id);
            ticketCurrent.get().setStatus(StatusEnum.getStatus(status));

            if (status.equals("Assigned")) {
                ticket.setAssignedUser(userFromRequest(request));
            }

            Ticket ticketPersisted = ticketService.createOrUpdate(ticketCurrent);
            ChangeStatus changeStatus = new ChangeStatus();
            changeStatus.setUserChange(userFromRequest(request));
            changeStatus.setDateChangeStatus(new Date());
            changeStatus.setStatus(StatusEnum.getStatus(status));
            changeStatus.setTicket(ticketPersisted);
            ticketService.createChangeStatus(changeStatus);
            response.setData(Optional.ofNullable(ticketPersisted));

        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    private void validateChangeStatus(Long id, String status, BindingResult result) {
        if (id == null || id.equals("")) {
            result.addError(new ObjectError("Ticket", "Id no information"));
            return;
        }

        if (status == null || status.equals("")) {
            result.addError(new ObjectError("Ticket", "Status no information"));
            return;
        }
    }

    @GetMapping(value = "/summary")
    public ResponseEntity<Response<Summary>> findSummary() {

        Response<Summary> response = new Response<Summary>();
        Summary chart = new Summary();
        int amountNew = 0;
        int amountResolved = 0;
        int amountApproved = 0;
        int amountDisapproved = 0;
        int amountAssigned = 0;
        int amountClosed = 0;
        Iterable<Ticket> tickets = ticketService.findAll();

        if (tickets != null) {
            for (Iterator<Ticket> iterator = tickets.iterator(); iterator.hasNext(); ) {
                Ticket ticket = iterator.next();

                if (ticket.getStatus().equals(StatusEnum.New)) {
                    amountNew++;
                }
                if (ticket.getStatus().equals(StatusEnum.Resolved)) {
                    amountResolved++;
                }
                if (ticket.getStatus().equals(StatusEnum.Approved)) {
                    amountApproved++;
                }
                if (ticket.getStatus().equals(StatusEnum.Disapproved)) {
                    amountDisapproved++;
                }
                if (ticket.getStatus().equals(StatusEnum.Assigned)) {
                    amountAssigned++;
                }
                if (ticket.getStatus().equals(StatusEnum.Closed)) {
                    amountClosed++;
                }
            }
        }

        chart.setAmountNew(amountNew);
        chart.setAmountResolved(amountResolved);
        chart.setAmountApproved(amountApproved);
        chart.setAmountDisapproved(amountDisapproved);
        chart.setAmountAssigned(amountAssigned);
        chart.setAmountClosed(amountClosed);
        response.setData(Optional.of(chart));

        return ResponseEntity.ok(response);
    }


}
