/*
 * Copyright (C) 2014 StarTIC
 */
package com.bq.oss.lib.ws.queries;

import java.lang.IllegalArgumentException;
import java.lang.String;
import java.util.Optional;

import com.bq.oss.lib.queries.exception.MalformedJsonQueryException;
import com.bq.oss.lib.queries.parser.AggregationParser;
import com.bq.oss.lib.queries.parser.QueryParser;
import com.bq.oss.lib.queries.request.*;
import com.bq.oss.lib.ws.api.error.ApiRequestException;
import com.bq.oss.lib.ws.api.error.ErrorMessage;


/**
 * @author Alexander De Leon
 * 
 */
public class QueryParameters {

	private Pagination pagination;
	private Optional<Sort> sort;
	private Optional<ResourceQuery> query;
	private Optional<ResourceSearch> search;
	private Optional<Aggregation> aggregationOperation;

	public QueryParameters(int pageSize, int page, int maxPageSize, Optional<String> sort, Optional<String> query,
			QueryParser queryParser, Optional<String> aggregation, AggregationParser aggregationParser,
			Optional<String> search) {
		this.pagination = buildPagination(page, pageSize, maxPageSize);
		this.sort = buildSort(sort);
		this.query = buildOptionalQuery(query, queryParser);
		this.aggregationOperation = buildOptionalAggregation(aggregation, aggregationParser);
		this.search = buildSearch(search);
	}

	public QueryParameters(QueryParameters other) {
		this.pagination = other.pagination;
		this.sort = other.sort;
		this.query = other.query;
		this.aggregationOperation = other.aggregationOperation;
		this.search = other.search;
	}

	private Optional<ResourceSearch> buildSearch(Optional<String> optionalSearch) {
		return optionalSearch.map(search -> Optional.of(new ResourceSearch(search))).orElse(Optional.empty());
	}

	public Pagination getPagination() {
		return pagination;
	}

	public Optional<ResourceQuery> getQuery() {
		return query;
	}

	public Optional<Sort> getSort() {
		return sort;
	}

	public Optional<Aggregation> getAggregation() {
		return aggregationOperation;
	}

	public Optional<ResourceSearch> getSearch() {
		return search;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	public void setQuery(Optional<ResourceQuery> query) {
		this.query = query;
	}

	public void setSearch(Optional<ResourceSearch> search) {
		this.search = search;
	}

	public void setSort(Optional<Sort> sort) {
		this.sort = sort;
	}

	public void setAggregation(Optional<Aggregation> aggregationOperation) {
		this.aggregationOperation = aggregationOperation;
	}

	private Optional<Aggregation> buildOptionalAggregation(Optional<String> aggregation,
			AggregationParser aggregationParser) {

		if (aggregation.isPresent()) {
			try {
				return Optional.of(aggregationParser.parse(aggregation.get()));
			} catch (MalformedJsonQueryException e) {
				throw new ApiRequestException("invalid_aggregation",
						ErrorMessage.INVALID_AGGREGATION.getMessage(aggregation, e.getMessage()), e);
			}
		}
		return Optional.empty();
	}

	private Optional<Sort> buildSort(Optional<String> sort) {
		try {
			return sort.isPresent() ? Optional.of(Sort.fromString(sort.get())) : Optional.empty();
		} catch (IllegalArgumentException e) {
			throw new ApiRequestException("invalid_sort", ErrorMessage.INVALID_SORT.getMessage(sort,
					e.getMessage()), e);
		}
	}

	private Pagination buildPagination(int page, int pageSize, int macPageSize) {
		return new Pagination(assertValidPage(page), assertValidPageSize(pageSize, macPageSize));
	}

	private Optional<ResourceQuery> buildOptionalQuery(Optional<String> query, QueryParser queryParser) {
		if (query.isPresent()) {
			try {
				return Optional.of(queryParser.parse(query.get()));
			} catch (MalformedJsonQueryException e) {
				throw new ApiRequestException("invalid_query", ErrorMessage.INVALID_QUERY.getMessage(query,
						e.getMessage()), e);
			}
		}
		return Optional.empty();
	}

	private int assertValidPageSize(int pageSize, int maxPageSize) {
		if (!(pageSize > 0 && pageSize <= maxPageSize)) {
			throw new ApiRequestException("invalid_page_size", ErrorMessage.INVALID_PAGE_SIZE.getMessage(
					pageSize, maxPageSize));
		}
		return pageSize;
	}

	private int assertValidPage(int page) {
		if (page < 0) {
			throw new ApiRequestException("invalid_page", ErrorMessage.INVALID_PAGE.getMessage(page));
		}
		return page;
	}
}
