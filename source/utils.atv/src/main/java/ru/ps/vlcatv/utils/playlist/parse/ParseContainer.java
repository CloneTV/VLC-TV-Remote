package ru.ps.vlcatv.utils.playlist.parse;

import java.util.Locale;

import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;

public class ParseContainer extends ReflectAttribute {

    @IFieldReflect("season")
    public int intId = -1;

    @IFieldReflect("type")
    public String typeId = null;

    @IFieldReflect("val")
    public String valId = null;

    @Override
    public String toString() {
        return String.format(
               Locale.getDefault(),
               "\n\t{ type=%s, id=%d, value=\"%s\" }",
                (Text.isempty(typeId)) ? "-" : typeId,
                intId,
                (Text.isempty(valId)) ? "-" : valId

        );
    }
}
