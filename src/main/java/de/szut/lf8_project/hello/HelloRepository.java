package de.szut.lf8_project.hello;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface HelloRepository extends JpaRepository<HelloEntity, Long> {
    List<HelloEntity> findByMessage(String message);
}
