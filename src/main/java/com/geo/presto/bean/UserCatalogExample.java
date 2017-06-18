package com.geo.presto.bean;

import java.util.ArrayList;
import java.util.List;

public class UserCatalogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public UserCatalogExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andCatalogIsNull() {
            addCriterion("catalog_ is null");
            return (Criteria) this;
        }

        public Criteria andCatalogIsNotNull() {
            addCriterion("catalog_ is not null");
            return (Criteria) this;
        }

        public Criteria andCatalogEqualTo(String value) {
            addCriterion("catalog_ =", value, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogNotEqualTo(String value) {
            addCriterion("catalog_ <>", value, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogGreaterThan(String value) {
            addCriterion("catalog_ >", value, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogGreaterThanOrEqualTo(String value) {
            addCriterion("catalog_ >=", value, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogLessThan(String value) {
            addCriterion("catalog_ <", value, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogLessThanOrEqualTo(String value) {
            addCriterion("catalog_ <=", value, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogLike(String value) {
            addCriterion("catalog_ like", value, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogNotLike(String value) {
            addCriterion("catalog_ not like", value, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogIn(List<String> values) {
            addCriterion("catalog_ in", values, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogNotIn(List<String> values) {
            addCriterion("catalog_ not in", values, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogBetween(String value1, String value2) {
            addCriterion("catalog_ between", value1, value2, "catalog");
            return (Criteria) this;
        }

        public Criteria andCatalogNotBetween(String value1, String value2) {
            addCriterion("catalog_ not between", value1, value2, "catalog");
            return (Criteria) this;
        }

        public Criteria andSchemaIsNull() {
            addCriterion("schema_ is null");
            return (Criteria) this;
        }

        public Criteria andSchemaIsNotNull() {
            addCriterion("schema_ is not null");
            return (Criteria) this;
        }

        public Criteria andSchemaEqualTo(String value) {
            addCriterion("schema_ =", value, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaNotEqualTo(String value) {
            addCriterion("schema_ <>", value, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaGreaterThan(String value) {
            addCriterion("schema_ >", value, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaGreaterThanOrEqualTo(String value) {
            addCriterion("schema_ >=", value, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaLessThan(String value) {
            addCriterion("schema_ <", value, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaLessThanOrEqualTo(String value) {
            addCriterion("schema_ <=", value, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaLike(String value) {
            addCriterion("schema_ like", value, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaNotLike(String value) {
            addCriterion("schema_ not like", value, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaIn(List<String> values) {
            addCriterion("schema_ in", values, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaNotIn(List<String> values) {
            addCriterion("schema_ not in", values, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaBetween(String value1, String value2) {
            addCriterion("schema_ between", value1, value2, "schema");
            return (Criteria) this;
        }

        public Criteria andSchemaNotBetween(String value1, String value2) {
            addCriterion("schema_ not between", value1, value2, "schema");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNull() {
            addCriterion("username is null");
            return (Criteria) this;
        }

        public Criteria andUsernameIsNotNull() {
            addCriterion("username is not null");
            return (Criteria) this;
        }

        public Criteria andUsernameEqualTo(String value) {
            addCriterion("username =", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotEqualTo(String value) {
            addCriterion("username <>", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThan(String value) {
            addCriterion("username >", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("username >=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThan(String value) {
            addCriterion("username <", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLessThanOrEqualTo(String value) {
            addCriterion("username <=", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameLike(String value) {
            addCriterion("username like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotLike(String value) {
            addCriterion("username not like", value, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameIn(List<String> values) {
            addCriterion("username in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotIn(List<String> values) {
            addCriterion("username not in", values, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameBetween(String value1, String value2) {
            addCriterion("username between", value1, value2, "username");
            return (Criteria) this;
        }

        public Criteria andUsernameNotBetween(String value1, String value2) {
            addCriterion("username not between", value1, value2, "username");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}