package services;

import interpreter.*;
import io.ebean.Query;
import models.entities.BaseModel;
import models.repositories.BaseRepository;
import play.mvc.Http;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SearchService {
    private IParser parser;

    public SearchService(IParser parser) {
        this.parser = parser;
    }

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

    private <T extends BaseModel> Query<T> ApplyFilter(Query<T> query, String paramValue) {
        var expression = this.parser.parse(paramValue);
        var filter = expression.evaluate();
        return query.where(filter);
    }

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
