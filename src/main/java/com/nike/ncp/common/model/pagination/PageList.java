package com.nike.ncp.common.model.pagination;

import java.util.List;

public interface PageList<T> {
    List<T> getData();
    Page getPage();
}
