package com.kihomura.screenvault.repository;

import com.kihomura.screenvault.pojo.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Integer> {
}
