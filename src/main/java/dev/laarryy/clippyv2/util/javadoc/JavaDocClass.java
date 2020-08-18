package dev.laarryy.clippyv2.util.javadoc;

import java.util.ArrayList;
import java.util.List;

public class JavaDocClass {
    private List<JavaDocMethod> methods = new ArrayList<>();
    private String packageString = "";
    private String url = "";

    public List<JavaDocMethod> getMethods() {
        return this.methods;
    }
    public void setMethods(List<JavaDocMethod> methods) {
        this.methods = methods;
    }

    public String getPackageString() {
        return this.packageString;
    }
    public void setPackageString(String packageString) {
        this.packageString = packageString;
    }

    public String getUrl() {
        return "https://javadoc.io/doc/net.luckperms/api/latest/" + this.packageString.replaceAll("\\.", "/") + "/" ;
    }
}
