package com.wesleyxbz.helpdesk.api.repository;

import com.wesleyxbz.helpdesk.api.entity.ChangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeStatusRepository extends JpaRepository<ChangeStatus, Long> {

    List<ChangeStatus> findByTicketIdOrderByDateChangeStatusDesc(Long ticketId);

}
