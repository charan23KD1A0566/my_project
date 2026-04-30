package com.project.repository;

import com.project.entity.QRSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QRSessionRepository extends JpaRepository<QRSession, Long> {
}
