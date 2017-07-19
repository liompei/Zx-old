package com.liompei.zxlog;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by Liompei
 * Time 2017/7/19 0:24
 * 1137694912@qq.com
 * remark:日志打印和toast输出
 */

public class Zx {
    private static boolean IS_SHOW_LOG = true;
    private static boolean IS_SHOW_TOAST = true;
    private static String TAG;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final int V = 0x1;
    public static final int D = 0x2;
    public static final int I = 0x3;
    public static final int W = 0x4;
    public static final int E = 0x5;

    public static final int WTF = 0x6;
    public static final int JSON = 0x7;
    public static final int XML = 0x8;

    public static final int JSON_INDENT = 4;

    public static final String DEFAULT_MESSAGE = "execute";
    public static final String TAG_DEFAULT = "Zx";
    public static final String NULL_TIPS = "Log`s object is null";


    private static final int STACK_TRACE_INDEX = 5;
    private static final String SUFFIX = ".java";

    private static boolean mIsGlobalTagEmpty = true;

    private static Context CONTEXT;

    public static void initLog(String tag, boolean isShowlog) {
        IS_SHOW_LOG = isShowlog;
        TAG = tag;
        mIsGlobalTagEmpty = TextUtils.isEmpty(TAG);
    }

    public static void initToast(Context context, boolean isShowToast) {
        IS_SHOW_TOAST = isShowToast;
        CONTEXT = context;
    }

    /**
     * V verbose
     */
    public static void v() {
        printLog(V, null, DEFAULT_MESSAGE);
    }

    public static void v(Object verbose) {
        printLog(V, null, verbose);
    }

    public static void v(String tag, Object verbose) {
        printLog(V, tag, verbose);
    }

    /**
     * D debug
     */
    public static void d() {
        printLog(D, null, DEFAULT_MESSAGE);
    }

    public static void d(Object debug) {
        printLog(D, null, debug);
    }

    public static void d(String tag, Object debug) {
        printLog(D, tag, debug);
    }

    /**
     * I information
     */

    public static void i() {
        printLog(I, null, DEFAULT_MESSAGE);
    }

    public static void i(Object information) {
        printLog(I, null, information);
    }

    public static void i(String tag, Object information) {
        printLog(I, tag, information);
    }

    /**
     * W warning
     */

    public static void w() {
        printLog(W, null, DEFAULT_MESSAGE);
    }

    public static void w(Object warning) {
        printLog(W, null, warning);
    }

    public static void w(String tag, Object warning) {
        printLog(W, tag, warning);
    }

    /**
     * e error
     */

    public static void e() {
        printLog(E, null, DEFAULT_MESSAGE);
    }

    public static void e(Object error) {
        printLog(E, null, error);
    }

    public static void e(String tag, Object error) {
        printLog(E, tag, error);
    }

    /**
     * wtf
     */
    public static void wtf() {
        printLog(WTF, null, DEFAULT_MESSAGE);
    }

    public static void wtf(Object wtf) {
        printLog(WTF, null, wtf);
    }

    public static void wtf(String tag, Object wtf) {
        printLog(WTF, tag, wtf);
    }

    /**
     * json
     *
     * @param jsonData
     */
    public static void json(String jsonData) {
        printLog(JSON, null, jsonData);
    }

    public static void json(String tag, String jsonData) {
        printLog(JSON, tag, jsonData);
    }

    /**
     * xml
     *
     * @param xml
     */

    public static void xml(String xml) {
        printLog(XML, null, xml);
    }

    public static void xml(String tag, String xml) {
        printLog(XML, tag, xml);
    }

    public static void show(String toast) {
        if (!IS_SHOW_TOAST) {
            return;
        }

        try {
            Toast.makeText(CONTEXT, toast, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Log.e("Zx--", "---------------------------------------------");
            Log.e("Zx--error", e.getMessage());
            Log.e("Zx--suggest", "please add Context with Zx.initToast() ");
            Log.e("Zx--", "---------------------------------------------");
        }
    }

    private static void printLog(int type, String tagStr, Object object) {
        if (!IS_SHOW_LOG) {
            return;
        }

        String[] contents = wrapperContent(tagStr, object);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];

        switch (type) {
            case V:
            case D:
            case I:
            case W:
            case E:
            case WTF:
                toPrint(type, tag, headString + msg);
                break;
            case JSON:
                printJson(tag, msg, headString);
                break;
            case XML:
                printXml(tag, msg, headString);
                break;
        }
    }

    private static String[] wrapperContent(String tagStr, Object object) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement = stackTrace[STACK_TRACE_INDEX];
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {

            className = classNameInfo[classNameInfo.length - 1] + SUFFIX;
        }
        if (className.contains("$")) {
            className = className.split("\\$")[0] + SUFFIX;
        }

        String methodName = targetElement.getMethodName();
        int lineNumber = targetElement.getLineNumber();
        if (lineNumber < 0) {
            lineNumber = 0;
        }

        String tag = (tagStr == null ? className : tagStr);

        if (mIsGlobalTagEmpty && TextUtils.isEmpty(tagStr)) {
            tag = TAG_DEFAULT;
        } else if (!TextUtils.isEmpty(tagStr)) {

            tag = tagStr;
        } else if (!mIsGlobalTagEmpty && TextUtils.isEmpty(tagStr)) {
            tag = TAG;
        }

        String msg = (object == null) ? NULL_TIPS : getObjectsString(object);

        String headString = "[ (" + className + ":" + lineNumber + ")#" + methodName + " ] ";

        return new String[]{tag, msg, headString};
    }

    private static String getObjectsString(Object object) {

        return object.toString();

    }

    public static void toPrint(int type, String tag, String msg) {

        int index = 0;
        int maxLength = 4000;
        int countOfSub = msg.length() / maxLength;

        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + maxLength);
                printSub(type, tag, sub);
                index += maxLength;
            }
            printSub(type, tag, msg.substring(index, msg.length()));
        } else {
            printSub(type, tag, msg);
        }
    }

    private static void printSub(int type, String tag, String sub) {

        switch (type) {
            case Zx.V:
                Log.v(tag, sub);
                break;
            case Zx.D:
                Log.d(tag, sub);
                break;
            case Zx.I:
                Log.i(tag, sub);
                break;
            case Zx.W:
                Log.w(tag, sub);
                break;
            case Zx.E:
                Log.e(tag, sub);
                break;
            case Zx.WTF:
                Log.wtf(tag, sub);
                break;
        }
    }

    public static void printJson(String tag, String msg, String headString) {

        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(Zx.JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(Zx.JSON_INDENT);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        message = headString + Zx.LINE_SEPARATOR + message;
        String[] lines = message.split(Zx.LINE_SEPARATOR);
        for (String line : lines) {
            Log.d(tag, "|| " + line);
        }
        printLine(tag, false);
    }

    public static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || line.equals("\n") || line.equals("\t") || TextUtils.isEmpty(line.trim());
    }

    public static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }

    public static void printXml(String tag, String xml, String headString) {

        if (xml != null) {
            xml = formatXML(xml);
            xml = headString + "\n" + xml;
        } else {
            xml = headString + Zx.NULL_TIPS;
        }

        printLine(tag, true);
        String[] lines = xml.split(Zx.LINE_SEPARATOR);
        for (String line : lines) {
            if (!isEmpty(line)) {
                Log.d(tag, "|| " + line);
            }
        }
        printLine(tag, false);
    }

    public static String formatXML(String inputXML) {
        try {
            Source xmlInput = new StreamSource(new StringReader(inputXML));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (Exception e) {
            e.printStackTrace();
            return inputXML;
        }
    }
}
