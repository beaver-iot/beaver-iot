package com.milesight.iab.data.jpa.support;

import com.milesight.iab.data.filterable.Filterable;
import com.milesight.iab.data.filterable.SearchFilter;
import com.milesight.iab.data.filterable.condition.CompareCondition;
import com.milesight.iab.data.filterable.condition.CompositeCondition;
import com.milesight.iab.data.filterable.condition.Condition;
import com.milesight.iab.data.filterable.enums.BooleanOperator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author leon
 */
public class SpecificationConverter {

    public static <T> Specification<T> toSpecification(Consumer<Filterable> consumer){
        SearchFilter nextQuerySpecs = new SearchFilter(BooleanOperator.AND, new ArrayList<>());
        consumer.accept(nextQuerySpecs);
        return SpecificationConverter.toSpecification(nextQuerySpecs);
    }

    public static <T> Specification<T> toSpecification(SearchFilter searchFilter){
        Specification<T> specification = new Specification<>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = toPredicateList(root, query, criteriaBuilder, searchFilter.getConditions(), new ArrayList<>());
                return query.where(predicates.toArray(new Predicate[0])).getRestriction();
            }
        };
        return specification;
    }

    private static <T> List<Predicate> toPredicateList(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, List<Condition> conditions, List<Predicate> predicates){
        for (Condition condition : conditions) {
            if (condition instanceof CompareCondition){
                CompareCondition compareCondition = (CompareCondition)condition;
                switch (compareCondition.getSearchOperator()){
                    case EQ:
                        predicates.add(criteriaBuilder.equal(root.get(compareCondition.getName()), compareCondition.getValue()));
                        break;
                    case NE:
                        predicates.add(criteriaBuilder.notEqual(root.get(compareCondition.getName()), compareCondition.getValue()));
                        break;
                    case GT:
                        predicates.add(criteriaBuilder.greaterThan(root.get(compareCondition.getName()), (Comparable) compareCondition.getValue()));
                        break;
                    case GE:
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(compareCondition.getName()), (Comparable) compareCondition.getValue()));
                        break;
                    case LT:
                        predicates.add(criteriaBuilder.lessThan(root.get(compareCondition.getName()), (Comparable) compareCondition.getValue()));
                        break;
                    case LE:
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(compareCondition.getName()), (Comparable) compareCondition.getValue()));
                        break;
                    case LIKE:
                        predicates.add(criteriaBuilder.like(root.get(compareCondition.getName()), "%" + compareCondition.getValue() + "%"));
                        break;
                    case NOT_LIKE:
                        predicates.add(criteriaBuilder.notLike(root.get(compareCondition.getName()), "%" + compareCondition.getValue() + "%"));
                        break;
                    case LIKE_LEFT:
                        predicates.add(criteriaBuilder.like(root.get(compareCondition.getName()), "%" + compareCondition.getValue()));
                        break;
                    case LIKE_RIGHT:
                        predicates.add(criteriaBuilder.like(root.get(compareCondition.getName()), compareCondition.getValue() + "%"));
                        break;
                    case BETWEEN:
                        Pair betweenValues = (Pair) compareCondition.getValue();
                        predicates.add(criteriaBuilder.between(root.get(compareCondition.getName()), (Comparable) betweenValues.getFirst(), (Comparable) betweenValues.getSecond()));
                        break;
                    case IS_NULL:
                        predicates.add(criteriaBuilder.isNull(root.get(compareCondition.getName())));
                        break;
                    case IS_NOT_NULL:
                        predicates.add(criteriaBuilder.isNotNull(root.get(compareCondition.getName())));
                        break;
                    case IN:
                        predicates.add(root.get(compareCondition.getName()).in((Object[]) compareCondition.getValue()));
                        break;
                    case NOT_IN:
                        predicates.add(criteriaBuilder.not(root.get(compareCondition.getName()).in((Object[]) compareCondition.getValue())));
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported search operator");
                }
            }else if(condition instanceof CompositeCondition){
                CompositeCondition compositeCondition = (CompositeCondition)condition;
                List<Predicate> compositePredicates = toPredicateList(root, query, criteriaBuilder, compositeCondition.getConditions(), new ArrayList<>());
                if(compositeCondition.getBooleanOperator() == BooleanOperator.AND) {
                    predicates.add(criteriaBuilder.and(compositePredicates.toArray(new Predicate[0])));
                }else{
                    predicates.add(criteriaBuilder.or(compositePredicates.toArray(new Predicate[0])));
                }
            }else{
                throw new RuntimeException("Unsupported condition type");
            }
        }
        return predicates;
    }
}
