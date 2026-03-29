package com.newspaper.System.repository;

import com.newspaper.System.model.Newspaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewspaperRepository extends JpaRepository<Newspaper, Integer> {}
