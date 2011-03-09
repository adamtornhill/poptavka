package cz.poptavka.sample.client.home.widget.category.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CategoryItemView extends Composite implements CategoryItemPresenter.CategoryItemInterface {

    private static CategoryItemUiBinder uiBinder = GWT.create(CategoryItemUiBinder.class);

    interface CategoryItemUiBinder extends UiBinder<Widget, CategoryItemView> {
    }



    @UiField
    Label itemCount;
    @UiField
    Anchor itemLink;
    @UiField
    Image itemImage;

    public CategoryItemView() {
        initWidget(uiBinder.createAndBindUi(this));


    }

    @Override
    public Widget getWidgetView() {
        return this;
    }

    @Override
    public Label getItemCount() {
        return itemCount;
    }

    @Override
    public Anchor getItemLink() {
        return itemLink;
    }

    @Override
    public Image getItemImage() {
        return itemImage;
    }

    public void setItemCount(Label itemCount) {
        this.itemCount = itemCount;
    }

    public void setItemLink(Anchor itemLink) {
        this.itemLink = itemLink;
    }

    public void setItemImage(Image itemImage) {
        this.itemImage = itemImage;
    }

}