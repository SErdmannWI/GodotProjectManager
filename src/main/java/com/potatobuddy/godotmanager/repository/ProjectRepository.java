package com.potatobuddy.godotmanager.repository;

import com.potatobuddy.godotmanager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
}
