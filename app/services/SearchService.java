package services;

import interpreter.*;
import io.ebean.Query;
import models.entities.BaseModel;
import models.repositories.BaseRepository;
import play.mvc.Http;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Filter, ordering and limit service for controllers
 */
public class SearchService {
    private IParser parser;

    public SearchService(IParser parser) {
        this.parser = parser;
    }

    /**
     * Retrieve query string params and apply requested operations before get operations
     * @param request {@link Http.Request} object with request information
     * @param repository {@link BaseRepository<T>} repository object associated to controller
     * @param <T> Base model type associated to controller
     * @return {@link Query<T>} query with all containing all requested operations
     */
    public <T extends BaseModel> Query<T> EvaluateFilters(Http.Request request, BaseRepository<T> repository) {
        var query = repository.finder.query();

        if (request == null)
            return query;

        var queryParams = request.queryString();
        if (queryParams.isEmpty())
            return query;

        return queryParams.entrySet().stream().reduce(query, (prvQuery, entry) -> {
            if (entry.getValue().length <= 0)
                return prvQuery;

            var queryValue = String.join(" ", entry.getValue());

            switch (entry.getKey().toLowerCase()) {
                case "$filter":
                    return this.ApplyFilter(prvQuery, queryValue);
                case "$orderby":
                    return this.ApplyOrder(prvQuery, queryValue);
                case "$skip":
                    return this.ApplySkip(prvQuery, queryValue);
                case "$top":
                    return this.ApplyTop(prvQuery, queryValue);
                default:
                    return prvQuery;
            }
        }, (prv, result) -> prv = result);
    }

    /**
     * Translate $filter query param into {@link Query<T>} object
     * @param query base {@link Query<T>} object to apply filter
     * @param paramValue $filter query param value
     * @param <T> Base model type associated to controller
     * @return {@link Query<T>} query with filter operation applied
     */
    private <T extends BaseModel> Query<T> ApplyFilter(Query<T> query, String paramValue) {
        var expression = this.parser.parse(paramValue);
        var filter = expression.evaluate();
        return query.where(filter);
    }

    /**
     * Translate $orderby query param into {@link Query<T>} object
     * @param query base {@link Query<T>} object to apply order
     * @param paramValue $orderby query param value
     * @param <T> Base model type associated to controller
     * @return {@link Query<T>} query with order operation applied
     */
    private <T extends BaseModel> Query<T> ApplyOrder(Query<T> query, String paramValue) {
        if (paramValue.isEmpty())
            return query;

        var fields = Arrays.stream(paramValue.split(",")).map(x -> x.trim().split("\\s+")).collect(Collectors.toList());
        if (fields.size() <= 0)
            return query;

        for (String[] field : fields) {
            boolean descending = field.length == 2 && field[1].equalsIgnoreCase("desc");
            if (descending)
                query = query.orderBy().desc(field[0]);
            else
                query = query.orderBy().asc(field[0]);
        }

        return query;
    }

    /**
     * Translate $top query param into {@link Query<T>} object
     * @param query base {@link Query<T>} object to apply limit operation
     * @param paramValue $top query param value
     * @param <T> Base model type associated to controller
     * @return {@link Query<T>} query with limit operation applied
     */
    private <T extends BaseModel> Query<T> ApplyTop(Query<T> query, String paramValue) {
        if (paramValue.isEmpty())
            return query;

        try {
            int d = Integer.parseInt(paramValue);
            return query.setMaxRows(d);
        } catch (NumberFormatException nfe) {
            return query;
        }
    }

    /**
     * Translate $skip query param into {@link Query<T>} object
     * @param query base {@link Query<T>} object to apply skip operation
     * @param paramValue $skip query param value
     * @param <T> Base model type associated to controller
     * @return {@link Query<T>} query with skip operation applied
     */
    private <T extends BaseModel> Query<T> ApplySkip(Query<T> query, String paramValue) {
        if (paramValue.isEmpty())
            return query;

        try {
            int d = Integer.parseInt(paramValue);
            return query.setFirstRow(d);
        } catch (NumberFormatException nfe) {
            return query;
        }
    }
}
