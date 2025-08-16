package com.telkom.repository;

import com.telkom.model.FormData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FormDataRepository extends JpaRepository<FormData, Long> {
    List<FormData> findByEmail(String email);
}