package com.hamza.smartleadgenerator.message.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInboundMessageRepository extends JpaRepository<InboundMessageEntity, Long> {

}
