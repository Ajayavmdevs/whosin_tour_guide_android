package com.whosin.app.service.rest;

public class HttpCommon {

    /*HTTP Methods*/
    public static final String HTTPRequestMethodGET = "GET";
    public static final String HTTPRequestMethodHEAD = "HEAD";
    public static final String HTTPREquestMethodDELETE = "DELETE";
    public static final String HTTPRequestMethodPOST = "POST";
    public static final String HTTPRequestMethodPUT = "PUT";
    public static final String HTTPRequestMethodPATCH = "PATCH";

    /*HTTP Headers*/
    public static final String HTTPRequestHeaderNameContentType = "Content-Type";
    public static final String HTTPRequestHeaderNameContentLength = "Content-Length";
    public static final String HTTPRequestHeaderNameContentEncoding = "Content-Encoding";
    public static final String HTTPRequestHeaderNameAccept = "Accept";
    public static final String HTTPRequestHeaderApplicationType = "application-type";
    public static final String HTTPRequestHeaderAuthorization = "Authorization";
    public static final String HTTPRequestHeaderCacheControl = "Cache-Control";
    public static final String HTTPRequestHeaderCacheControlNoCache = "no-cache";
    public static final String HTTPRequestHeaderNameAcceptEncoding = "Accept-Encoding";


    public static final String HTTPURLRequestContentEncodingGZIP = "application/gzip";
    public static final String HTTPURLRequestContentTypeJSON = "application/json; charset=utf-8";
    public static final String HTTPURLRequestContentTypeXML = "application/xml; charset=utf-8";
    public static final String HTTPURLRequestContentTypeIMAGE = "image/*";
    public static final String HTTPURLRequestApplicationTypeREST = "REST";
    public static final String HTTPURLRequestContentTypeXWFORMURLENCODED = "application/x-www-form-urlencoded";
    public static final String HTTPURLRequestContentTypeBINARYOCTETSTREAM = "binary/octet-stream";
    public static final String HTTPURLRequestContentTypeAPPLICATIONOCTEMSTREAM = "application/octet-stream";
    public static final String HTTPURLRequestContentTypeMULTIPARTFORMDATA = "multipart/form-data";

    /*HTTP Status Codes*/
    public static final int HTTPStatusCodeOK = 200;
    public static final int HTTPStatusCodeCREATED = 201;
    public static final int HTTPStatusCodeACCEPTED = 202;
    public static final int HTTPStatusCodeNOCONTENT = 204;
    public static final int HTTPStatusCodeMULTIPLECHOICES = 300;
    public static final int HTTPStatusCodeBADREQUEST = 400;
    public static final int HTTPStatusCodeUNAUTHORIZED = 401;
    public static final int HTTPStatusCodeFORBIDDEN = 403;
    public static final int HTTPStatusCodeNotFound = 404;
    public static final int HTTPStatusCodeMETHODNOTALLOWED = 405;
    public static final int HTTPSTatusCodeCONFLICT = 409;
    public static final int HTTPStatusCodeINTERNALERROR = 500;

    /*Mime Types*/
    public static final String HTTPMimeTypeTEXTPLAIN = "text/plain";
    public static final String HTTPMimeTypeTEXTHTML = "text/html";
    public static final String HTTPMimeTypeIMAGEJPEG = "image/jpeg";
    public static final String HTTPMimeTypeIMAGEPNG = "image/png";
    public static final String HTTPMimeTypeAUDIOMPEG = "audio/mpeg";
    public static final String HTTPMimeTypeAUDIOOGG = "audio/ogg";

}
