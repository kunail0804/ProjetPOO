package com.delorent.model;

import java.util.List;

public record SqlClause(String predicate, List<Object> params) {}
