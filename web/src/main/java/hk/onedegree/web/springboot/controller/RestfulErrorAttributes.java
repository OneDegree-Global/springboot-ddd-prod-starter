package hk.onedegree.web.springboot.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

public class RestfulErrorAttributes extends DefaultErrorAttributes {


    public Map<String, Object> getErrorAttributes(
            WebRequest webRequest, ErrorAttributeOptions options) {

        Map<String, Object> errorAttributes =
                super.getErrorAttributes(webRequest, options);
        errorAttributes.put("locale", webRequest.getLocale()
                .toString());
        errorAttributes.remove("error");


        return errorAttributes;
    }
}

