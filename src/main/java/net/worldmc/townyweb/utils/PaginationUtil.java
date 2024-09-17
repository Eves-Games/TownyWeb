package net.worldmc.townyweb.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaginationUtil {

    /**
     * Paginates a list of items.
     *
     * @param <T> The type of elements in the list
     * @param items The full list of items to paginate
     * @param page The requested page number (1-based)
     * @param pageSize The number of items per page
     * @return A Map containing the paginated data and pagination information
     */
    public static <T> Map<String, Object> paginateList(List<T> items, int page, int pageSize) {
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
}