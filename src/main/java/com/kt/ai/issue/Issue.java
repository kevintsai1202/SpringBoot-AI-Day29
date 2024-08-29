package com.kt.ai.issue;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Issue {
    @Id
    private Long id;
    private String issue;
    private String solution;
    private String model;
	@Override
	public String toString() {
		return "model: "+model+"\n"
		      +"issue: "+issue+"\n"
			  +"solution: "+solution;
	}
}
