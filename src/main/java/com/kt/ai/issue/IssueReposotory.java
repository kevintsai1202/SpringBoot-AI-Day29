package com.kt.ai.issue;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueReposotory extends JpaRepository<Issue, Long> {}
