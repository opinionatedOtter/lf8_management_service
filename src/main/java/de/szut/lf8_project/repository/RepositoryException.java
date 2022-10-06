package de.szut.lf8_project.repository;

import de.szut.lf8_project.common.Statuscode;
import lombok.Getter;


public class RepositoryException extends Exception {

    @Getter
    private Statuscode statuscode;

    public RepositoryException(String msg) {
        super(msg);
    }

    public RepositoryException(Statuscode code) {
        this.statuscode = code;
    }
}
