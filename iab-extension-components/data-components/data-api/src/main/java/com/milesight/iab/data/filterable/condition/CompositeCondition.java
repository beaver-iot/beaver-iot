package com.milesight.iab.data.filterable.condition;

import com.milesight.iab.data.filterable.enums.BooleanOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leon
 */
public abstract class CompositeCondition implements Condition {

    protected BooleanOperator operator = BooleanOperator.AND;
    /** Query conditions */
    protected List<Condition> searchConditions = new ArrayList<>();

    public CompositeCondition(BooleanOperator operator, List<Condition> searchConditions) {
        this.operator = operator;
        this.searchConditions = searchConditions;
    }

    public BooleanOperator getBooleanOperator() {
        return operator;
    }

    public List<Condition> getConditions() {
        return searchConditions;
    }
}
