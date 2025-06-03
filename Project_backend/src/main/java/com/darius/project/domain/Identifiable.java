package com.darius.project.domain;

public interface Identifiable<ID>{
    ID getId();
    void setId(ID id);
}
