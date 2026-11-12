package com.urlshortener.repository;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public class ClickAnalyticsRepositoryImpl implements ClickAnalyticsRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ClickAnalyticsRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Object[]> countClicksByBrowser(String shortCode) {
        return countByField(shortCode, "browser", null);
    }

    @Override
    public List<Object[]> countClicksByOperatingSystem(String shortCode) {
        return countByField(shortCode, "operatingSystem", null);
    }

    @Override
    public List<Object[]> countClicksByDeviceType(String shortCode) {
        return countByField(shortCode, "deviceType", null);
    }

    @Override
    public List<Object[]> countClicksByReferer(String shortCode, int limit) {
        return countByField(shortCode, "referer", limit);
    }

    private List<Object[]> countByField(String shortCode, String field, Integer limit) {
        Aggregation aggregation = limit == null
            ? Aggregation.newAggregation(
                Aggregation.match(Criteria.where("shortCode").is(shortCode)),
                Aggregation.group(field).count().as("count")
            )
            : Aggregation.newAggregation(
                Aggregation.match(Criteria.where("shortCode").is(shortCode)),
                Aggregation.group(field).count().as("count"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "count")),
                Aggregation.limit(limit)
            );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "click_analytics", Document.class);
        return results.getMappedResults().stream()
            .map(row -> new Object[] { row.get("_id"), row.get("count") })
            .toList();
    }
}

