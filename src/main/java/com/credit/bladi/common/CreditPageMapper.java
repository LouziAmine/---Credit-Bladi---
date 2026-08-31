package com.credit.bladi.common;

import java.util.function.Function;

public final class CreditPageMapper {
    private CreditPageMapper() {
    }

    public static <S, T> CreditPage<T> map(CreditPage<S> source, Function<S, T> itemMapper) {
        if (source == null) {
            return new CreditPage<>();
        }
        CreditPage<T> page = new CreditPage<>();
        page.setItems(source.getItems().stream().map(itemMapper).toList());
        page.setCurrentPage(source.getCurrentPage());
        page.setLastPage(source.getLastPage());
        page.setSize(source.getSize());
        page.setTotal(source.getTotal());
        return page;
    }
}
