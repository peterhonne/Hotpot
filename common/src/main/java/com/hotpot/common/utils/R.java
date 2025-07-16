package com.hotpot.common.utils;

import com.hotpot.common.constant.CommonConstant;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Peter
 * @date 2023/3/06
 * @description
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private int code;

	@Getter
	@Setter
	private String msg;


	@Getter
	@Setter
	private T data;

	@Getter
	@Setter
//	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String encryptedData;

	public Boolean isOk() {
		if(code!= 0){
			return false;
		}else{
			return true;
		}
	}

	public static <T> R<T> ok() {
		return restResult(null, CommonConstant.SUCCESS, null);
	}

	public static <T> R<T> ok(T data) {
		return restResult(data, CommonConstant.SUCCESS, null);
	}

	public static <T> R<T> ok(T data, String msg) {
		return restResult(data, CommonConstant.SUCCESS, msg);
	}

	public static <T> R<T> failed() {
		return restResult(null, CommonConstant.FAIL, null);
	}

	public static <T> R<T> failed(String msg) {
		return restResult(null, CommonConstant.FAIL, msg);
	}

	public static <T> R<T> failed(T data) {
		return restResult(data, CommonConstant.FAIL, null);
	}

	public static <T> R<T> failed(T data, String msg) {
		return restResult(data, CommonConstant.FAIL, msg);
	}

	public static <T> R<T> failed(T data, int code, String msg) {
		return restResult(data, code, msg);
	}

	public static <T> R<T> failed(int code, String msg) {
		return restResult(null, code, msg);
	}

	private static <T> R<T> restResult(T data, int code, String msg) {
		R<T> apiResult = new R<>();
		apiResult.setCode(code);
		apiResult.setData(data);
		apiResult.setMsg(msg);
		return apiResult;
	}
}