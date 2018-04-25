package com.betfair.entities;

import com.betfair.enums.types.RunnerStatus;

public class RunnerDetails {

    private Long selectionId;
    private String selectionName;
    private Integer runnerOrder;
    private RunnerStatus runnerStatus;
    private SimpleOdds winRunnerOdds;
    private SimpleOdds eachwayRunnerOdds;
    private Double handicap;

    public Long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(Long selectionId) {
        this.selectionId = selectionId;
    }

    public String getSelectionName() {
        return selectionName;
    }

    public void setSelectionName(String selectionName) {
        this.selectionName = selectionName;
    }

    public Integer getRunnerOrder() {
        return runnerOrder;
    }

    public void setRunnerOrder(Integer runnerOrder) {
        this.runnerOrder = runnerOrder;
    }

    public RunnerStatus getRunnerStatus() {
        return runnerStatus;
    }

    public void setRunnerStatus(RunnerStatus runnerStatus) {
        this.runnerStatus = runnerStatus;
    }

    public SimpleOdds getWinRunnerOdds() {
        return winRunnerOdds;
    }

    public void setWinRunnerOdds(SimpleOdds winRunnerOdds) {
        this.winRunnerOdds = winRunnerOdds;
    }

    public SimpleOdds getEachwayRunnerOdds() {
        return eachwayRunnerOdds;
    }

    public void setEachwayRunnerOdds(SimpleOdds eachwayRunnerOdds) {
        this.eachwayRunnerOdds = eachwayRunnerOdds;
    }

    public Double getHandicap() {
        return handicap;
    }

    public void setHandicap(Double handicap) {
        this.handicap = handicap;
    }
}
