package io.natty.rest.params;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.natty.rest.ReflectionUtil;
import io.natty.rest.URIDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FormParam extends Param {

    private static final Path tempDir;

    static {
        try {
            tempDir = Files.createTempDirectory("netty");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(FormParam.class);

    public FormParam(String name, Class<?> type) {
        super(name, type);
    }

    @Override
    public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {

        List<InterfaceHttpData> bodyHttpDatas = uriDecoder.getBodyHttpDatas();
        if (bodyHttpDatas == null || bodyHttpDatas.size() == 0) {
            return null;
        }

        for (InterfaceHttpData data : bodyHttpDatas) {
            if (name.equals(data.getName())) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    Attribute attribute = (Attribute) data;
                    try {
                        return ReflectionUtil.castTo(type, attribute.getValue());
                    } catch (IOException e) {
                        log.error("Error getting form params. Reason : {}", e.getMessage(), e);
                    }
                }
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) data;
                    if (fileUpload.isCompleted()) {
                        return fileUpload;
//                        try {
//
//                        } finally {
//                            uriDecoder.removeHttpDataFromClean(fileUpload); //remove
//                        }
                        // the File of to delete file
                    } else {
//					responseContent.append("\tFile to be continued but should not!\r\n");
                    }
                }
            }
        }

        return null;
    }

}
