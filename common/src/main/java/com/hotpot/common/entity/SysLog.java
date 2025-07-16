package com.hotpot.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Peter
 * @date 2023/3/06
 * @description
 */
@Data
public class SysLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String threadId;

	private String type;

	private String title;

	private Integer createId;

	private String createBy;

	private LocalDateTime createTime;

	private Long startTime;

	private LocalDateTime updateTime;

	private String remoteAddr;

	private String userAgent;

	private String requestUri;

	private String method;

	private String params;

	private Long time;

	private String exception;

	private String serviceId;

	private String delFlag;

	public void setStartTime(Long startTime){
		this.startTime = startTime;
		this.createTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());
	}


}
