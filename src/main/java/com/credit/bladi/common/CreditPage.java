package com.credit.bladi.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CreditPage<T> implements Serializable {
    private List<T> items;

    private int currentPage;

    private int lastPage;

    private int size;

    private int total;

    public List<T> getItems() {
        return items == null ? Collections.emptyList() : Collections.unmodifiableList(items);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public static <T> CreditPage<T> empty(int currentPage, int size) {
        return of(new ArrayList<>(), currentPage, size, 0);
    }

    public static <T> CreditPage<T> of(List<T> items, int currentPage, int size, long total) {
        CreditPage<T> page = new CreditPage<>();
        page.setItems(items);
        page.setCurrentPage(currentPage);
        page.setLastPage(size > 0 ? (int) Math.ceil((double) total / size) : 0);
        page.setSize(size);
        page.setTotal((int) total);
        return page;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CreditPage && EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format("CreditPage [items=%s, currentPage=%s, lastPage=%s, size=%s, total=%s]",
            items, currentPage, lastPage, size, total);
    }
}
