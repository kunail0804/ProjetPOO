package com.delorent.model.Louable;

import java.util.List;

public record SqlClause(String predicate, List<Object> params) {

    public SqlClause(String predicate, List<Object> params) {
        this.predicate = predicate;
        this.params = params;
    }

    public String getPredicate() {
        return predicate;
    }

    public List<Object> getParams() {
        return params;
    }
}
