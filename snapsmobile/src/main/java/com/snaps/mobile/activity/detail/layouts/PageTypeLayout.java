package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionBaseCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCommonValue;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class PageTypeLayout extends LinkedLayout {
    public static final String PAGE_TYPE_INDEX = "page_type_index";
    public static final String CARD_TYPE_INDEX = "card_type_index";
    public static final String GLOSSY_TYPE_INDEX = "glossy_type_index";
    public static final String NOTE_TYPE = "note_paperType";
    public static final String SPRING_NOTE_TYPE = "springnote_paperType";
    public static final String ACCORDION_CARD = "accordion_cardType";
    public static final String ID_PHOTO = "id_photoType";
    private ImageView[][] images;
    private SnapsProductOptionCell cellData;
    private int selectedIndex = -2;
    private boolean forceSelect;
    private String comboBoxType = null;
    private PageTypeLayout(Context context) {
        super(context);
    }

    public static PageTypeLayout createInstance(Context context, LayoutRequestReciever reciever, boolean forceSelect,String comboBoxType) {
        PageTypeLayout instance = new PageTypeLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        instance.forceSelect = forceSelect;
        instance.comboBoxType = comboBoxType;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if (!(data instanceof SnapsProductOptionCell)) return;
        cellData = (SnapsProductOptionCell) data;
        ViewGroup container = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_page_type, this);
        ((TextView) container.findViewById(R.id.title)).setText(cellData.getName());
        //TODO   노트류 이미지 나오면 적용한다.

        if (comboBoxType != null) {
            ImageView pageTypeImageView1 = (ImageView) findViewById(R.id.pageTypeImageView1);
            ImageView pageTypeImageView2 = (ImageView) findViewById(R.id.pageTypeImageView2);
            ImageView pageTypeImageView3 = (ImageView) findViewById(R.id.pageTypeImageView3);
            if (comboBoxType.equals(NOTE_TYPE)) {
                pageTypeImageView1.setImageResource(R.drawable.icon_note_konel);
                pageTypeImageView2.setImageResource(R.drawable.icon_note_grid);
                pageTypeImageView3.setImageResource(R.drawable.icon_note_line);

            } else if (comboBoxType.equals(SPRING_NOTE_TYPE)) {
                pageTypeImageView1.setImageResource(R.drawable.icon_springnote_muji);
                pageTypeImageView2.setImageResource(R.drawable.icon_springnote_grid);
                pageTypeImageView3.setImageResource(R.drawable.icon_springnote_line);
            } else if (comboBoxType.equals(ACCORDION_CARD)) {
                pageTypeImageView1.setImageResource(R.drawable.icon_accordioncard_nor);
                pageTypeImageView2.setImageResource(R.drawable.icon_accordioncard_cut);
            } else if (comboBoxType.equals(ID_PHOTO)) {
                pageTypeImageView1.setImageResource(R.drawable.icon_idpicture_glossy);
                pageTypeImageView2.setImageResource(R.drawable.icon_idpicture_matte);
            }
        }

        LinearLayout pageLayout = (LinearLayout) container.findViewById( R.id.pages );
        ViewGroup item;


        if( !StringUtil.isEmpty(cellData.getInnerPaperKind()) && cellData.getInnerPaperKind().contains( "|") ) {
            String[] valueNames = cellData.getInnerPaperKind().split( "\\|" );
            for( int i = 0; i < valueNames.length; ++i ) {
                if( i == 1 && valueNames.length > cellData.getValueList().size() ) {
                    SnapsProductOptionCommonValue temp = cellData.getValueList().get(0).clone();
                    temp.setCmd( temp.getCmd().replace("paperCode=160001", "paperCode=160002") );
                    cellData.getValueList().add( 1, temp );
                }

                if( i > cellData.getValueList().size() - 1 ) break;
                cellData.getValueList().get(i).setName( valueNames[i] );
            }
        }

        images = new ImageView[cellData.getValueList().size()][2];
        int resId, imageResId, textResId, checkResId;
        ImageView image, check;
        for( int i = 0; i < 3; ++i ) {
            resId = getResources().getIdentifier("item" + (i + 1), "id", getContext().getPackageName() );
            item = (RelativeLayout) pageLayout.findViewById( resId );
            if( i > cellData.getValueList().size() - 1 ) {
                if(comboBoxType != null && (comboBoxType.equals(ACCORDION_CARD) || comboBoxType.equals(ID_PHOTO))) {
                    item.setVisibility( View.GONE );
                } else {
                    item.setVisibility( View.INVISIBLE );
                }

                continue;
            }
                item.setVisibility( View.VISIBLE );

            item.setTag( i );

            imageResId = getResources().getIdentifier("page_border" + (i + 1), "id", getContext().getPackageName() );
            textResId = getResources().getIdentifier("page_text" + (i + 1), "id", getContext().getPackageName() );
            checkResId = getResources().getIdentifier("page_check" + (i + 1), "id", getContext().getPackageName() );
            image = (ImageView) item.findViewById( imageResId );
            check = (ImageView) item.findViewById( checkResId );
            images[i] = new ImageView[]{ image, check };

            ( (TextView) item.findViewById(textResId)).setText( cellData.getValueList().get(i).getName() );

            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem( (Integer) v.getTag() );
                }
            });
        }

        parent.addView(this);

        int defaultIndex = StringUtil.isEmpty( cellData.getDefalutIndex() ) ? forceSelect ? 0 : -1 : Integer.parseInt( cellData.getDefalutIndex() );
        if( reciever != null ) {
            String type = null;
            if(comboBoxType != null && comboBoxType.equals(ACCORDION_CARD)) {
                type = CARD_TYPE_INDEX ;
            } else if(comboBoxType != null && comboBoxType.equals(ID_PHOTO)) {
                type = GLOSSY_TYPE_INDEX ;
            } else {
                type = PAGE_TYPE_INDEX ;
            }
            String indexStr = reciever.getSelectedValue( ( type ));;
            if( !StringUtil.isEmpty(indexStr) )
                defaultIndex = Integer.parseInt( indexStr );

            if( defaultIndex > cellData.getValueList().size() - 1  )
                defaultIndex = forceSelect ? 0 : -1;
        }
        selectItem( defaultIndex );
    }

    private void selectItem( int index ) {
        if( index == selectedIndex ) return;
        selectedIndex = index;

        for( int i = 0; i < images.length; ++i ) {
            images[i][0].setVisibility(i == selectedIndex ? View.VISIBLE : View.GONE);
            images[i][1].setVisibility(i == selectedIndex ? View.VISIBLE : View.GONE);
        }

        if( reciever != null ) {
            if( tailViewId != 0 )
                reciever.removeLayout( tailViewId );
            String type = null;
            if(comboBoxType != null && comboBoxType.equals(ACCORDION_CARD)) {
                type = CARD_TYPE_INDEX ;
            } else if(comboBoxType != null && comboBoxType.equals(ID_PHOTO)) {
                type = GLOSSY_TYPE_INDEX ;
            } else {
                type = PAGE_TYPE_INDEX ;
            }
            reciever.itemSelected(type, selectedIndex < 0 ? "" : "" + selectedIndex, true );

            SnapsProductOptionCommonValue value = cellData.getValueList().get( (selectedIndex < 0 ? 0 : selectedIndex) );
            SnapsProductOptionBaseCell cell = value.getChildControl();
            if( cell != null )
                tailViewId = reciever.createNextLayout( (ViewGroup)getParent(), cell.getCellType(), cell, id );
            else {
                if( value.getPrice() != null && value.getPrice().getValues() != null && !StringUtil.isEmpty(value.getPrice().getValues().getPrice()) )
                    reciever.removeLayout( tailViewId );
                else
                    setBottomLineVisibility( false );
                tailViewId = reciever.itemSelected( value, (ViewGroup)getParent(), id );
            }
        }
    }
}
