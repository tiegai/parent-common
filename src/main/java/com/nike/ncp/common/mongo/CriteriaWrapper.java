package com.nike.ncp.common.mongo;

import cn.hutool.core.util.StrUtil;
import com.nike.ncp.common.mongo.reflection.ReflectionUtil;
import com.nike.ncp.common.mongo.reflection.SerializableFunction;
import lombok.Getter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Query Builder Parent Class
 */
@Getter
public abstract class CriteriaWrapper {

     private Integer current;
     private Integer size;
     private boolean andLink = true;

     private Criteria criteria;
     private List<Criteria> list = new ArrayList<Criteria>();

     private String[] fields;

     protected CriteriaWrapper(boolean andLink, Integer current, Integer size) {
         this.andLink = andLink;
         this.current = current;
         this.size = size;
     }

    /**
     * Convert Wrapper to Criteria
     *
     * @return Criteria
     */
    public Criteria build() {
        criteria = new Criteria();
        if (this.list.size() > 0) {
            if (andLink) {
                criteria.andOperator(listToArry(this.list));
            } else {
                criteria.orOperator(listToArry(this.list));
            }
        }
        return criteria;
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{} \\需要第一个替换，否则replace方法替换时会有逻辑bug
     */
    public static String replaceRegExp(String str) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }

        return str.replace("\\", "\\\\").replace("*", "\\*")//
                .replace("+", "\\+").replace("|", "\\|")//
                .replace("{", "\\{").replace("}", "\\}")//
                .replace("(", "\\(").replace(")", "\\)")//
                .replace("^", "\\^").replace("$", "\\$")//
                .replace("[", "\\[").replace("]", "\\]")//
                .replace("?", "\\?").replace(",", "\\,")//
                .replace(".", "\\.").replace("&", "\\&");
    }

    private Criteria[] listToArry(List<Criteria> criteriaList) {
        return criteriaList.toArray(new Criteria[criteriaList.size()]);
    }

    /**
     * The value is equal to the provided value. Eg: userName=James
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper eq(SerializableFunction<E, R> column, Object params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).is(params));
        return this;
    }

    /**
     * unequal to
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper ne(SerializableFunction<E, R> column, Object params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).ne(params));
        return this;
    }

    /**
     * less than
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper lt(SerializableFunction<E, R> column, Object params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).lt(params));
        return this;
    }

    /**
     * less than or equal to
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper lte(SerializableFunction<E, R> column, Object params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).lte(params));
        return this;
    }

    /**
     * greater than
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper gt(SerializableFunction<E, R> column, Object params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).gt(params));
        return this;
    }

    /**
     * greater than or equal to
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper gte(SerializableFunction<E, R> column, Object params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).gte(params));
        return this;
    }

    /**
     * contain
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper contain(SerializableFunction<E, R> column, Object params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).all(params));
        return this;
    }

    /**
     * contains, connected with or
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper containOr(SerializableFunction<E, R> column, Collection<?> params) {
        CriteriaOrWrapper criteriaOrWrapper = new CriteriaOrWrapper();
        for (Object object : params) {
            criteriaOrWrapper.contain(column, object);
        }

        list.add(criteriaOrWrapper.build());
        return this;
    }

    /**
     * contains, connected with or
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper containOr(SerializableFunction<E, R> column, Object[] params) {
        return containOr(column, Arrays.asList(params));
    }

    /**
     * contains, connected with and.params is collection
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper containAnd(SerializableFunction<E, R> column, Collection<?> params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).all(params));
        return this;
    }

    /**
     * contains, connected with and.params is arrays
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper containAnd(SerializableFunction<E, R> column, Object[] params) {
        return containAnd(column, Arrays.asList(params));
    }

    /**
     * fuzzy query
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper like(SerializableFunction<E, R> column, String params) {
        Pattern pattern = Pattern.compile("^.*" + replaceRegExp(params) + ".*$", Pattern.CASE_INSENSITIVE);
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).regex(pattern));
        return this;
    }

    /**
     * query in
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper in(SerializableFunction<E, R> column, Collection<?> params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).in(params));
        return this;
    }

    /**
     * query in
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper in(SerializableFunction<E, R> column, Object[] params) {
        return in(column, Arrays.asList(params));
    }

    /**
     * not query in
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper nin(SerializableFunction<E, R> column, Collection<?> params) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).nin(params));
        return this;
    }

    /**
     * not query in
     *
     * @param column
     * @param params
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper nin(SerializableFunction<E, R> column, Object[] params) {
        return nin(column, Arrays.asList(params));
    }

    /**
     * field is null query
     *
     * @param column
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper isNull(SerializableFunction<E, R> column) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).is(null));
        return this;
    }

    /**
     * field is not null query
     *
     * @param column
     * @return CriteriaWrapper
     */
    public <E, R> CriteriaWrapper isNotNull(SerializableFunction<E, R> column) {
        list.add(Criteria.where(ReflectionUtil.getFieldName(column)).ne(null));
        return this;
    }

    /**
     * collection query
     *
     * @param arrName    collection name
     * @param column collection field name
     * @param param  value
     * @return
     */
    public <E, R> CriteriaWrapper findArray(String arrName, SerializableFunction<E, R> column, String param) {
        list.add(Criteria.where(arrName).elemMatch(Criteria.where(ReflectionUtil.getFieldName(column)).is(param)));
        return this;
    }

    public <E, R> CriteriaWrapper findArray(String arrName, String column, String param) {
        list.add(Criteria.where(arrName).elemMatch(Criteria.where(column).is(param)));
        return this;
    }


    /**
     * fuzzy collection query
     *
     * @param arrName    collection name
     * @param column collection field name
     * @param param  value
     * @return
     */
    public <E, R> CriteriaWrapper findArrayLike(String arrName, SerializableFunction<E, R> column, String param) {
        Pattern pattern = Pattern.compile("^.*" + replaceRegExp(param) + ".*$", Pattern.CASE_INSENSITIVE);
        list.add(Criteria.where(arrName).elemMatch(Criteria.where(ReflectionUtil.getFieldName(column)).regex(pattern)));
        return this;
    }


    public <E, R> CriteriaWrapper findArrayLike(String arrName, String column, String param) {
        Pattern pattern = Pattern.compile("^.*" + replaceRegExp(param) + ".*$", Pattern.CASE_INSENSITIVE);
        list.add(Criteria.where(arrName).elemMatch(Criteria.where(column).regex(pattern)));
        return this;
    }
    /**
     * If you don't want some fields to appear in the returned results, you can exclude them.
     * @param columns
     * @return
     * @param <E>
     * @param <R>
     */
    public <E, R> CriteriaWrapper exclude(String... columns) {
        Assert.notNull(columns, "Keys must not be null!");
        fields = columns;
        return this;
    }

}
