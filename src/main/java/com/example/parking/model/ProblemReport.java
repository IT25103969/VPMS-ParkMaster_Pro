package com.example.parking.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ProblemReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String problemType;
    private String securityLevel; // LOW, MED, HIGH
    private String spot;
    private String description;
    private LocalDateTime reportTime;

    public ProblemReport() {}

    public ProblemReport(Long id, String problemType, String securityLevel, String spot, String description, LocalDateTime reportTime) {
        this.id = id;
        this.problemType = problemType;
        this.securityLevel = securityLevel;
        this.spot = spot;
        this.description = description;
        this.reportTime = reportTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProblemType() { return problemType; }
    public void setProblemType(String problemType) { this.problemType = problemType; }

    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }

    public String getSpot() { return spot; }
    public void setSpot(String spot) { this.spot = spot; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getReportTime() { return reportTime; }
    public void setReportTime(LocalDateTime reportTime) { this.reportTime = reportTime; }
}
