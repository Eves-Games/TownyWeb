package net.worldmc.townyweb.utils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PaginationUtil<T> {
    private static final int DEFAULT_PAGE_SIZE = 10;

    public Map<String, Object> paginateList(List<T> items, int page, int pageSize) {
        int totalResults = items.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalResults / pageSize));

        page = Math.max(1, Math.min(page, totalPages));

        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalResults);

        List<T> paginatedItems = items.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", paginatedItems);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        return response;
    }

    public Map<String, Object> paginateList(List<T> items, int page) {
        return paginateList(items, page, DEFAULT_PAGE_SIZE);
    }
}