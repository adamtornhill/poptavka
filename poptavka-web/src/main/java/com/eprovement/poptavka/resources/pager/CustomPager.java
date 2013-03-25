package com.eprovement.poptavka.resources.pager;

import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.Style;
import com.google.gwt.resources.client.ImageResource;

public interface CustomPager extends SimplePager.Resources {
    @Override
    @Source("pager/first.png")
    ImageResource simplePagerFirstPage();

    @Override
    @Source("pager/first.png")
    ImageResource simplePagerFirstPageDisabled();

    @Override
    @Source("pager/last.png")
    ImageResource simplePagerLastPage();

    @Override
    @Source("pager/last.png")
    ImageResource simplePagerLastPageDisabled();

    @Override
    @Source("pager/next.png")
    ImageResource simplePagerNextPage();

    @Override
    @Source("pager/next.png")
    ImageResource simplePagerNextPageDisabled();

    @Override
    @Source("pager/previous.png")
    ImageResource simplePagerPreviousPage();

    @Override
    @Source("pager/previous.png")
    ImageResource simplePagerPreviousPageDisabled();

    /**
     * The styles used in this widget.
     */
    @Override
    @Source("CustomPager.css")
    Style simplePagerStyle();
}