package com.snaps.common.structure;

import android.content.Context;

import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.data.request.GetTemplateLoad;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.StringUtil;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import errorhandle.logger.Logg;

/**
 * Created by ifunbae on 16. 4. 22..
 */
public class SnapsTemplateManager {
    private static final String TAG = SnapsTemplateManager.class.getSimpleName();

    private volatile static SnapsTemplateManager ourInstance = null;
    private SnapsTemplate template = null;
    private AtomicBoolean isEditActivityFinishing = new AtomicBoolean(false); //Activity가 종료될때 데이터를 초기화하는데, onDestroy가 불려지기 전에 템플릿을 만들려고 하면 싱크가 맞지 않는다.
    private Object editActivityFinishingSyncLocker = new Object();

    private int pageWidth = 0, pageHeight = 0;
    private boolean isMultiPageProduct = false;

    public static void createInstance() {
        if (ourInstance == null) {
            synchronized (SnapsTemplateManager.class) {
                if (ourInstance == null) {
                    ourInstance = new SnapsTemplateManager();
                }
            }
        }
    }

    public static void finalizeInstance() {
        if (ourInstance != null) {
            ourInstance.cleanInstance();
            ourInstance = null;
        }
    }

    public static SnapsTemplateManager getInstance() {
        if (ourInstance == null)
            createInstance();
        return ourInstance;
    }

    private SnapsTemplateManager() {
    }

    public File getCalendarTemplateCacheFilePath(Context context) {
        String parentPath = Const_VALUE.PATH_PACKAGE(context, false);
        final String cachePath = "/cache/template/calendar_template.xml";
        return new File(parentPath + cachePath);
    }

    public void exportTemplateCache(final File exportFile, final String url) {
        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {}

            @Override
            public void onBG() {
                String templateStr = GetTemplateLoad.getThemeBookTemplateResultStr(url);
                FileUtil.writeFileWithStr(exportFile, templateStr);
            }

            @Override
            public void onPost() {}
        });
    }

    public void getTemplateFromCache(final File cacheFile, final SnapsCommonResultListener<SnapsTemplate> listener) {
        if (cacheFile == null || !cacheFile.exists() || cacheFile.length() < 1) {
            if (listener != null) {
                listener.onResult(null);
            }
            return;
        }

        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            private SnapsTemplate template = null;
            @Override
            public void onPre() {}

            @Override
            public void onBG() {
                String contents = FileUtil.readFile(cacheFile);
                if (StringUtil.isEmpty(contents)) {
                    return;
                }

                InputStream stringStream = null;
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    factory.setValidating(false);
                    SAXParser parser = factory.newSAXParser();
                    XMLReader reader = parser.getXMLReader();

                    GetTemplateXMLHandler xml = new GetTemplateXMLHandler();

                    reader.setContentHandler(xml);
                    stringStream = new ByteArrayInputStream(contents.getBytes("UTF-8"));
                    reader.parse(new InputSource(stringStream));
                    template = xml.getTemplate();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                } finally {
                    try {
                        if (stringStream != null) {
                            stringStream.close();
                        }
                    } catch (IOException e) {
                        Dlog.e(TAG, e);
                    }
                }
            }

            @Override
            public void onPost() {
                if (listener != null) {
                    listener.onResult(template);
                }
            }
        });
    }

    public void setSnapsTemplate(SnapsTemplate template) {
        this.template = template;
        setProductDimenInfoWithTemplate(template);
    }

    private void setProductDimenInfoWithTemplate(SnapsTemplate template) {
        try {
            if (template == null || template.info == null) return;

            isMultiPageProduct = template.getPages() != null && template.getPages().size() > 2;

            SnapsTemplateInfo templateInfo = template.info;
            String pageMMWidth = templateInfo.F_PAGE_MM_WIDTH;
            String pageMMHeight = templateInfo.F_PAGE_MM_HEIGHT;

            pageWidth = !StringUtil.isEmpty(pageMMWidth) ? (int) Float.parseFloat(pageMMWidth) : 0;
            pageHeight = !StringUtil.isEmpty(pageMMHeight) ? (int) Float.parseFloat(pageMMHeight) : 0;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public SnapsTemplate getSnapsTemplate() {
        return this.template;
    }

    public boolean isActivityFinishing() {
        return isEditActivityFinishing.get();
    }

    public void setActivityFinishing(boolean activityFinishing) {
        isEditActivityFinishing.set(activityFinishing);
    }

    public static void waitIfEditActivityFinishing() {
        SnapsTemplateManager templateManager = getInstance();
        if (templateManager.isActivityFinishing()) {
            synchronized (templateManager.getEditActivityFinishingSyncLocker()) {
                if (templateManager.isActivityFinishing()) {
                    try {
                        templateManager.getEditActivityFinishingSyncLocker().wait();
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }

    public static void notifyEditActivityFinishingSyncLocker() {
        SnapsTemplateManager templateManager = getInstance();
        if (templateManager.isActivityFinishing()) {
            templateManager.setActivityFinishing(false);
            synchronized (templateManager.getEditActivityFinishingSyncLocker()) {
                templateManager.getEditActivityFinishingSyncLocker().notify();
            }
        }
    }

    public Object getEditActivityFinishingSyncLocker() {
        return editActivityFinishingSyncLocker;
    }

    synchronized public void cleanInstance() {
        if (template != null) {

            if (template.priceList != null) {
                template.priceList.clear();
                template.priceList = null;
            }

            if (template.getPages() != null) {
                template.getPages().clear();
//                template.getPages() = null;
                template.setNullPages();
            }
            if (template._backPageList != null) {
                template._backPageList.clear();
                template._backPageList = null;
            }
            if (template._hiddenPageList != null) {
                template._hiddenPageList.clear();
                template._hiddenPageList = null;
            }
            if (template.delimgList != null) {
                template.delimgList.clear();
                template.delimgList = null;
            }
            if (template.clientInfo != null)
                template.clientInfo = null;

            if (template.info != null)
                template.info = null;

            if (template.saveInfo != null)
                template.saveInfo = null;

            if (template.myphotoImageList != null) {
                template.myphotoImageList.clear();
            }

            if (template.clonePageList != null)
                template.clonePageList.clear();

            // 사용되는 폰트 정보를 저장하기 위한 set
            if (template.fonts != null)
                template.fonts.clear();

            if (template.arrFonts != null)
                template.arrFonts.clear();


            template = null;
            Dlog.d("cleanInstance()");
        }
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public boolean isMultiPageProduct() {
        return isMultiPageProduct;
    }
}
