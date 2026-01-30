package sba301.fe.edu.vn.besba.base;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

@Getter
@Setter
@Builder
public class PageDTO<T> {
    int totalPages;
    long totalElements;
    int currentPage;
    int pageSize;
    List<T> content;
    String sortBy;

    public static <R, T> PageDTO<R> of(
            List<T> list, Function<T, R> mapper, long total, int page, int size) {
        return PageDTO.<R>builder()
                .content(list.stream().map(mapper).toList())
                .totalPages((int) ((total - 1) / size + 1))
                .totalElements(total)
                .currentPage(page)
                .pageSize(size)
                .build();
    }
}
