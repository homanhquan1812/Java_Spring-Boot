package org.homanhquan.productservice.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Slice;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SliceResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return SliceResponse.<T>builder()
                .content(slice.getContent())
                .pageNumber(slice.getNumber())
                .pageSize(slice.getSize())
                .first(slice.isFirst())
                .last(slice.isLast())
                .hasNext(slice.hasNext())
                .hasPrevious(slice.hasPrevious())
                .build();
    }
}
