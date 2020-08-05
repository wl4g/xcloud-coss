/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.coss.common.utils;

/**
 * {@link RequestParameters}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月29日 v1.0.0
 * @see
 */
public final class RequestParameters {

	public static final String SUBRESOURCE_ACL = "acl";
	public static final String SUBRESOURCE_REFERER = "referer";
	public static final String SUBRESOURCE_LOCATION = "location";
	public static final String SUBRESOURCE_LOGGING = "logging";
	public static final String SUBRESOURCE_WEBSITE = "website";
	public static final String SUBRESOURCE_LIFECYCLE = "lifecycle";
	public static final String SUBRESOURCE_UPLOADS = "uploads";
	public static final String SUBRESOURCE_DELETE = "delete";
	public static final String SUBRESOURCE_CORS = "cors";
	public static final String SUBRESOURCE_APPEND = "append";
	public static final String SUBRESOURCE_TAGGING = "tagging";
	public static final String SUBRESOURCE_IMG = "img";
	public static final String SUBRESOURCE_STYLE = "style";
	public static final String SUBRESOURCE_REPLICATION = "replication";
	public static final String SUBRESOURCE_REPLICATION_PROGRESS = "replicationProgress";
	public static final String SUBRESOURCE_REPLICATION_LOCATION = "replicationLocation";
	public static final String SUBRESOURCE_CNAME = "cname";
	public static final String SUBRESOURCE_BUCKET_INFO = "bucketInfo";
	public static final String SUBRESOURCE_COMP = "comp";
	public static final String SUBRESOURCE_OBJECTMETA = "objectMeta";
	public static final String SUBRESOURCE_QOS = "qos";
	public static final String SUBRESOURCE_LIVE = "live";
	public static final String SUBRESOURCE_STATUS = "status";
	public static final String SUBRESOURCE_VOD = "vod";
	public static final String SUBRESOURCE_START_TIME = "startTime";
	public static final String SUBRESOURCE_END_TIME = "endTime";
	public static final String SUBRESOURCE_PROCESS_CONF = "processConfiguration";
	public static final String SUBRESOURCE_PROCESS = "x-oss-process";
	public static final String SUBRESOURCE_CSV_SELECT = "csv/select";
	public static final String SUBRESOURCE_CSV_META = "csv/meta";
	public static final String SUBRESOURCE_JSON_SELECT = "json/select";
	public static final String SUBRESOURCE_JSON_META = "json/meta";
	public static final String SUBRESOURCE_SQL = "sql";
	public static final String SUBRESOURCE_SYMLINK = "symlink";
	public static final String SUBRESOURCE_STAT = "stat";
	public static final String SUBRESOURCE_RESTORE = "restore";
	public static final String SUBRESOURCE_ENCRYPTION = "encryption";
	public static final String SUBRESOURCE_VRESIONS = "versions";
	public static final String SUBRESOURCE_VRESIONING = "versioning";
	public static final String SUBRESOURCE_VRESION_ID = "versionId";
	public static final String SUBRESOURCE_POLICY = "policy";
	public static final String SUBRESOURCE_REQUEST_PAYMENT = "requestPayment";
	public static final String SUBRESOURCE_QOS_INFO = "qosInfo";
	public static final String SUBRESOURCE_ASYNC_FETCH = "asyncFetch";

	public static final String SUBRESOURCE_UDF = "udf";
	public static final String SUBRESOURCE_UDF_NAME = "udfName";
	public static final String SUBRESOURCE_UDF_IMAGE = "udfImage";
	public static final String SUBRESOURCE_UDF_IMAGE_DESC = "udfImageDesc";
	public static final String SUBRESOURCE_UDF_APPLICATION = "udfApplication";
	public static final String SUBRESOURCE_UDF_LOG = "udfApplicationLog";

	public static final String PREFIX = "prefix";
	public static final String DELIMITER = "delimiter";
	public static final String MARKER = "marker";
	public static final String MAX_KEYS = "max-keys";
	public static final String BID = "bid";
	public static final String ENCODING_TYPE = "encoding-type";
	public static final String VERSION_ID_MARKER = "version-id-marker";
	public static final String TAG_KEY = "tag-key";
	public static final String TAG_VALUE = "tag-value";

	public static final String UPLOAD_ID = "uploadId";
	public static final String PART_NUMBER = "partNumber";
	public static final String MAX_UPLOADS = "max-uploads";
	public static final String UPLOAD_ID_MARKER = "upload-id-marker";
	public static final String KEY_MARKER = "key-marker";
	public static final String MAX_PARTS = "max-parts";
	public static final String PART_NUMBER_MARKER = "part-number-marker";
	public static final String RULE_ID = "rule-id";
	public static final String SEQUENTIAL = "sequential";

	public static final String SECURITY_TOKEN = "security-token";

	public static final String POSITION = "position";
	public static final String STYLE_NAME = "styleName";

	public static final String COMP_ADD = "add";
	public static final String COMP_DELETE = "delete";
	public static final String COMP_CREATE = "create";
	public static final String COMP_UPGRADE = "upgrade";
	public static final String COMP_RESIZE = "resize";

	public static final String STAT = "stat";
	public static final String HISTORY = "history";
	public static final String PLAYLIST_NAME = "playlistName";
	public static final String SINCE = "since";
	public static final String TAIL = "tail";

	/* V1 signature params */
	public static final String SIGNATURE = "Signature";
	public static final String OSS_ACCESS_KEY_ID = "OSSAccessKeyId";

	/* V2 signature params */
	public static final String OSS_SIGNATURE_VERSION = "x-oss-signature-version";
	public static final String OSS_EXPIRES = "x-oss-expires";
	public static final String OSS_ACCESS_KEY_ID_PARAM = "x-oss-access-key-id";
	public static final String OSS_ADDITIONAL_HEADERS = "x-oss-additional-headers";
	public static final String OSS_SIGNATURE = "x-oss-signature";

	public static final String OSS_TRAFFIC_LIMIT = "x-oss-traffic-limit";
	public static final String OSS_REQUEST_PAYER = "x-oss-request-payer";

	public static final String VPCIP = "vpcip";
	public static final String VIP = "vip";
}