package ru.ps.vlcatv.traktoauth.TraktTv.data;

import androidx.annotation.Keep;

import java.util.ArrayList;

import ru.ps.vlcatv.utils.reflect.ArrayReflect;
import ru.ps.vlcatv.utils.reflect.FieldReflect;
import ru.ps.vlcatv.utils.reflect.ObjectReflect;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;

@Keep
public class TestData extends ReflectAttribute {

    @FieldReflect("device_code")
    private String DevCode = "DevCode not null";
    @FieldReflect("user_code")
    private String UserCode = "UserCode not null";
    @FieldReflect("verification_url")
    private String VerificationUrl = "verification_url not null";

    @FieldReflect("expires_in")
    private long ExpiresIn = 1000L;
    @FieldReflect("interval_in")
    private long Interval = 2000L;

    @ObjectReflect("structure_test")
    private TestDataExt structTest = new TestDataExt();
    @ArrayReflect("array_test")
    private ArrayList<String> arrayList = new ArrayList<>();

    public TestData() {
        //structTest.textTxt = "TextTxt not null";
        //structTest.intNumber = 2000;
        //arrayList.add("One");
        //arrayList.add("Two");
        //arrayList.add("Three");
    }

    @Keep
    public static class TestDataExt extends ReflectAttribute {
        @FieldReflect("test_txt")
        String textTxt = null;
        @FieldReflect("int_number")
        int intNumber = -1;

        TestDataExt() {}
    }
}
