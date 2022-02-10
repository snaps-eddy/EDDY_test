package com.snaps.common.data.request;

import android.content.res.AssetManager;

import com.snaps.common.data.parser.GetSaveXMLHandler;
import com.snaps.common.data.parser.GetSaveXMLHandlerSupportIos;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.data.parser.GetThemeBookTemplateXMLHandler;
import com.snaps.common.data.parser.SnapsXMLPullParser;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.ISnapsConfigConstants;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.log.SnapsInterfaceLogListener;
import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class GetTemplateLoad {
    private static final String TAG = GetTemplateLoad.class.getSimpleName();

    public static SnapsTemplate getTemplateByXmlPullParser(String urlString, boolean isSaveXML, SnapsInterfaceLogListener interfaceLogListener) {
        URL url = null;
        SnapsTemplate template = null;

        try {
            url = new URL(urlString);

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfacePreRequest(urlString);
        } catch (MalformedURLException error) {
            Dlog.e(TAG, error);
        }

        if (url == null) return null;

        InputStream urlInputStream = null;
        try {
            if (isSaveXML) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                GetTemplateXMLHandler xml = new GetSaveXMLHandler();
                reader.setContentHandler(xml);

                urlInputStream = url.openStream();
                reader.parse(new InputSource(urlInputStream));
                template = xml.getTemplate();
            } else {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                urlInputStream = url.openStream();
                parser.setInput(urlInputStream, null);
                SnapsXMLPullParser p = new SnapsXMLPullParser(parser);
                template = p.getTemplate();
            }

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceResult(200, "success get template.");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
        } finally {
            try {
                if (urlInputStream != null) {
                    urlInputStream.close();
                    urlInputStream = null;
                }
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }

        return template;
    }

    public static String getThemeBookTemplateResultStr(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Dlog.e(TAG, e);
        }

        InputStream inputStream = null;
        try {
            if (url != null)
                inputStream = url.openStream();
            return FileUtil.convertStreamToString(inputStream);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }
            }
        }
    }

    public static SnapsTemplate getThemeBookTemplate(String urlString, boolean isSaveXML, SnapsInterfaceLogListener interfaceLogListener) {
        Dlog.d(Dlog.PRE_FIX_CS + "getThemeBookTemplate() Template Load URL:" + urlString);

        URL url = null;
        SnapsTemplate template = null;

        try {
            url = new URL(urlString);

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfacePreRequest(urlString);
        } catch (MalformedURLException e) {
            Dlog.e(TAG, e);
        }

        InputStream inputStream = null;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            GetTemplateXMLHandler xml = null;

            if (isSaveXML) {
                if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
                    xml = SnapsDiaryConstants.IS_SUPPORT_IOS_VERSION ? new GetSaveXMLHandlerSupportIos() : new GetSaveXMLHandler();
                } else {
                    xml = new GetSaveXMLHandler();
                }
            } else
                xml = new GetThemeBookTemplateXMLHandler();

            reader.setContentHandler(xml);

            if (url != null)
                inputStream = url.openStream();

            reader.parse(new InputSource(inputStream));
            template = xml.getTemplate();

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceResult(200, "success get template.");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }
            }
        }
        return template;
    }

    public static SnapsTemplate getNewYearsCardTemplate(String urlString, boolean isSaveXML, SnapsInterfaceLogListener interfaceLogListener) {
        Dlog.d(Dlog.PRE_FIX_CS + "getNewYearsCardTemplate() Template Load URL:" + urlString);

        URL url = null;
        SnapsTemplate template = null;

        try {
            url = new URL(urlString);

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfacePreRequest(urlString);
        } catch (MalformedURLException e) {
            Dlog.e(TAG, e);
        }

        InputStream inputStream = null;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            GetTemplateXMLHandler xml = null;

            if (isSaveXML) {
                xml = new GetSaveXMLHandler();
            } else
                xml = new GetThemeBookTemplateXMLHandler();

            reader.setContentHandler(xml);

            if (url != null)
                inputStream = url.openStream();

            reader.parse(new InputSource(inputStream));
            template = getParserTemplate(xml.getTemplate());

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceResult(200, "success get template.");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }
            }
        }
        return template;
    }

    private static SnapsTemplate getParserTemplate(SnapsTemplate snapsTemplate) {
        for (int i = 0; i < snapsTemplate.getPages().size(); i++) {
            if (i > 1) {
                String type = snapsTemplate.getPages().get(i).type;
                Dlog.d("getParserTemplate() type:" + i);
                if (snapsTemplate.getPages().get(i).type.equals("hidden")) {
                    SnapsPage snapsPage = snapsTemplate.getPages().get(i);
                    snapsTemplate.getPages().remove(snapsPage);
                    i--;
                }
                ;

            }

            if (i > 0 && i < snapsTemplate.priceList.size()) {
                snapsTemplate.priceList.remove(i);
            }
        }
        return snapsTemplate;
    }

    public static SnapsTemplate getTemplate(String urlString, SnapsInterfaceLogListener interfaceLogListener) {
        Dlog.d(Dlog.PRE_FIX_CS + "load template URL : " + urlString);

        URL url = null;
        SnapsTemplate template = null;

        try {
            url = new URL(urlString);
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfacePreRequest(urlString);
        } catch (MalformedURLException e) {
            Dlog.e(TAG, e);
        }

        if (url == null) return null;

        InputStream urlInputStream = null;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            GetTemplateXMLHandler xml = new GetTemplateXMLHandler();

            reader.setContentHandler(xml);
            urlInputStream = url.openStream();

            reader.parse(new InputSource(urlInputStream));
            template = xml.getTemplate();

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceResult(200, "success get template.");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
            return null;
        }

        return template;
    }

    public static String getFileContentWithStream(FileInputStream fis) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append('\n');
        }
        return sb.toString();
    }

    public static SnapsTemplate getFileTemplate(String urlString, SnapsInterfaceLogListener interfaceLogListener) {
        Dlog.d(Dlog.PRE_FIX_CS + "getFileTemplate() Template Load URL:" + urlString);

        if (interfaceLogListener != null)
            interfaceLogListener.onSnapsInterfacePreRequest(urlString);

        FileInputStream fstream = null;
        SnapsTemplate template = null;

        String fileContents = "";

        try {
            fstream = new FileInputStream(urlString);
            fileContents = getFileContentWithStream(fstream);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            GetTemplateXMLHandler xml = new GetTemplateXMLHandler();

            reader.setContentHandler(xml);

            InputSource inputSource = new InputSource();
            inputSource.setEncoding("UTF-8");
            inputSource.setCharacterStream(new StringReader(fileContents));
            reader.parse(inputSource);

            template = xml.getTemplate();

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceResult(200, "success get template.");
        } catch (Exception e) {
            Dlog.e(TAG, e);

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
            return null;
        } finally {
            try {
                if (fstream != null)
                    fstream.close();
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }

        return template;
    }

    public static SnapsTemplate getDefaultTemplate(String urlString, boolean isSaveXML, SnapsInterfaceLogDefaultHandler interfaceLogListener, AssetManager assetManager) {
        URL url = null;

        SnapsTemplate template;

        try {
            url = new URL(urlString);
            if (interfaceLogListener != null) {
                interfaceLogListener.onSnapsInterfacePreRequest(urlString);
            }
        } catch (MalformedURLException e) {
            Dlog.e(TAG, e);
        }

        InputStream inputStream = null;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            GetTemplateXMLHandler xml;

            if (isSaveXML) {
                xml = new GetSaveXMLHandler();

            } else {
                xml = new GetThemeBookTemplateXMLHandler();
            }

            reader.setContentHandler(xml);

            if (url != null && isSaveXML) {
                inputStream = url.openStream();
                Dlog.d(Dlog.PRE_FIX_CS + "getDefaultTemplate() Template Load URL:" + urlString);

            } else {
                Dlog.d(Dlog.PRE_FIX_CS + "getDefaultTemplate() Template Load Local file:" + ISnapsConfigConstants.SNAPS_BASE_TEMPLATE_FILE_NAME);
                inputStream = assetManager.open(ISnapsConfigConstants.SNAPS_BASE_TEMPLATE_FILE_NAME);
            }

            reader.parse(new InputSource(inputStream));
            template = xml.getTemplate();

            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceResult(200, "success get template.");
        } catch (Exception e) {
            Dlog.e(TAG, e);
            if (interfaceLogListener != null)
                interfaceLogListener.onSnapsInterfaceException(e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }
            }
        }
        return template;
    }
}
