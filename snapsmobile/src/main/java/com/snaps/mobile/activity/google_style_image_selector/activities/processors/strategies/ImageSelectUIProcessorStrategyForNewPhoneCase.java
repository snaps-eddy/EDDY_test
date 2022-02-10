//package com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies;
//
//import com.snaps.common.data.parser.GetTemplateXMLHandler;
//import com.snaps.common.data.request.GetTemplateLoad;
//import com.snaps.common.structure.SnapsTemplate;
//import com.snaps.common.structure.SnapsTemplateManager;
//import com.snaps.common.utils.constant.Config;
//import com.snaps.common.utils.log.Dlog;
//import com.snaps.mobile.R;
//import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
//import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
//import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
//import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
//import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayTemplateShapeAdapter;
//
//import org.xml.sax.InputSource;
//import org.xml.sax.XMLReader;
//
//import java.io.InputStream;
//
//import javax.xml.parsers.SAXParser;
//import javax.xml.parsers.SAXParserFactory;
//
//import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
//
///**
// * @Marko 목업 템플릿 교체를 위해 생성함. 원래 폰케이스는 ImageSelectUIProcessorStrategyForTemplateProducts 를 생성하게 되어있음.
// */
//public class ImageSelectUIProcessorStrategyForNewPhoneCase extends ImageSelectUIProcessorStrategyForTemplateBase {
//
//    private static final String TAG = ImageSelectUIProcessorStrategyForNewPhoneCase.class.getSimpleName();
//
//    @Override
//    public void initialize(ImageSelectUIProcessor uiProcessor) {
//        if (uiProcessor == null) return;
//
//        this.uiProcessor = uiProcessor;
//        this.activity = uiProcessor.getActivity();
//
//        uiProcessor.setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.TEMPLATE);
//        pageProgress = new DialogDefaultProgress(uiProcessor.getActivity());
//
//        loadTemplate();
//    }
//
//    @Override
//    public void postInitialized() {
//    }
//
//    @Override
//    public boolean isExistTrayView() {
//        return true;
//    }
//
//    @Override
//    public boolean isExistOnlyTrayAllViewLayout() {
//        return false;
//    }
//
//    @Override
//    public ImageSelectTrayBaseAdapter createTrayAdapter() {
//        if (uiProcessor == null) return null;
//        return new ImageSelectTrayTemplateShapeAdapter(activity, uiProcessor.getIntentData());
//    }
//
//    @Override
//    protected void handleGetTemplateBeforeTask() {
//    }
//
//    @Override
//    protected void handleGetTemplateAfterTask(SnapsTemplate template) {
//    }
//
//    @Override
//    protected void onTemplateLoaded() {
//        setTemplateThumbnails();
//    }
//
//    @Override
//    protected SnapsTemplate loadTemplate(final ImageSelectIntentData intentData) {
//        try {
//            Config.checkServiceThumbnailSimpleFileDir();
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
//
//        SnapsTemplateManager.getInstance().cleanInstance();
//
//        try {
//            SAXParserFactory factory = SAXParserFactory.newInstance();
//            factory.setValidating(false);
////            factory.setNamespaceAware(true); // Unit test 에서는 이 옵션을 켜 줘야 정상적으로 파싱한다.
//
//            SAXParser parser = factory.newSAXParser();
//            XMLReader reader = parser.getXMLReader();
//
//            GetTemplateXMLHandler xml = new GetTemplateXMLHandler();
//            reader.setContentHandler(xml);
//
//            InputStream urlInputStream = activity.getResources().openRawResource(R.raw.mock_new_phonecase_template);
//            reader.parse(new InputSource(urlInputStream));
//            return xml.getTemplate();
//
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
//
//
//        return GetTemplateLoad.getTemplate(SnapsTemplate.getTemplateUrl(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
//    }
//}
