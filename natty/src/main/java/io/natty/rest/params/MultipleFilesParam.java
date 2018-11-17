package io.natty.rest.params;

import io.natty.rest.URIDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.ArrayList;
import java.util.List;

public class MultipleFilesParam extends Param {
	public MultipleFilesParam(String name, Class<?> type) {
		super(name, type);
	}

	@Override
	public Object get(ChannelHandlerContext ctx, URIDecoder uriDecoder) {
		List<InterfaceHttpData> bodyHttpDatas = uriDecoder.getBodyHttpDatas();
		if (bodyHttpDatas == null || bodyHttpDatas.size() == 0) {
			return null;
		}
		List<FileUpload> files = new ArrayList<>();
		for (InterfaceHttpData data : bodyHttpDatas) {
			if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
				FileUpload fileUpload = (FileUpload) data;
				if (fileUpload.isCompleted()) {
					files.add(fileUpload);
				}
			}
		}
		return files;
	}
}
