package com.wl4g.devops.coss.minio;

import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.log.SmartLoggerFactory;
import com.wl4g.devops.coss.ServerCossEndpoint;
import com.wl4g.devops.coss.common.exception.CossException;
import com.wl4g.devops.coss.common.exception.ServerCossException;
import com.wl4g.devops.coss.common.model.*;
import com.wl4g.devops.coss.common.model.bucket.Bucket;
import com.wl4g.devops.coss.common.model.bucket.BucketList;
import com.wl4g.devops.coss.common.model.bucket.BucketMetadata;
import com.wl4g.devops.coss.common.model.metadata.BucketStatusMetaData;
import com.wl4g.devops.coss.config.MinioFsCossProperties;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.messages.Item;
import io.minio.messages.NotificationConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author vjay
 * @date 2020-08-11 15:39:00
 */
public class MinioEndpoint extends ServerCossEndpoint<MinioFsCossProperties> {

    SmartLogger log = SmartLoggerFactory.getLogger(getClass());

    private MinioFsCossProperties minioFsCossProperties;

    private MinioClient minioClient;

    public MinioEndpoint(MinioFsCossProperties config) {
        super(config);
        this.minioFsCossProperties = config;
        try {
            minioClient = new MinioClient(minioFsCossProperties.getEndpoint(), minioFsCossProperties.getAccessKey(), minioFsCossProperties.getSecretKey());
        } catch (Exception e) {
            log.error("Create Minio Client error", e);
        }
    }

    @Override
    public CossProvider kind() {
        return CossProvider.Minio;
    }

    @Override
    public Bucket createBucket(String bucketName) throws CossException, ServerCossException {
        try {
            minioClient.makeBucket(bucketName);
            Bucket bucket = new Bucket(bucketName);
            bucket.setCreationDate(new Date());
            return bucket;
        } catch (Exception e) {
            throw new CossException(e);
        }
    }

    @Override
    public BucketList<Bucket> listBuckets(String prefix, String marker, Integer maxKeys) throws CossException, ServerCossException {
        try {
            List<io.minio.messages.Bucket> listBuckets = minioClient.listBuckets();
            BucketList<Bucket> bucketList = new BucketList<>();
            List<Bucket> buckets = new ArrayList<>();
            for (io.minio.messages.Bucket b : listBuckets) {
                Bucket bucket = new Bucket();
                bucket.setName(b.name());
                bucket.setCreationDate(b.creationDate());
                buckets.add(bucket);
            }
            bucketList.getBucketList().addAll(buckets);
            return bucketList;
        } catch (Exception e) {
            throw new CossException(e);
        }
    }

    @Override
    public void deleteBucket(String bucketName) throws CossException, ServerCossException {
        try {
            minioClient.removeBucket(bucketName);
        } catch (Exception e) {
            throw new CossException(e);
        }
    }

    @Override
    public BucketMetadata getBucketMetadata(String bucketName) throws CossException, ServerCossException {
        try {
            NotificationConfiguration bucketNotification = minioClient.getBucketNotification(bucketName);
            BucketMetadata bucketMetadata = new BucketMetadata();
            bucketMetadata.setBucketName(bucketNotification.name);
        } catch (Exception e) {
            throw new CossException(e);
        }
        return null;
    }

    @Override
    public AccessControlList getBucketAcl(String bucketName) throws CossException, ServerCossException {
        return null;
    }

    @Override
    public void setBucketAcl(String bucketName, ACL acl) throws CossException, ServerCossException {

    }

    @Override
    public BucketStatusMetaData getBucketIndex(String bucketName) throws Exception {
        return null;
    }

    @Override
    public ObjectListing<ObjectSummary> listObjects(String bucketName, String prefix) throws CossException, ServerCossException {
        try {
            ObjectListing<ObjectSummary> objectListing = new ObjectListing<>();
            objectListing.setBucketName(bucketName);
            objectListing.setPrefix(prefix);
            prefix = fixKey(prefix,'/');
            Iterable<Result<Item>> results = minioClient.listObjects(bucketName, prefix,false);
            for (Result<Item> result : results) {
                Item item = result.get();
                ObjectSummary objectSummary = new ObjectSummary();
                objectSummary.setBucketName(bucketName);
                objectSummary.setKey(subDir(item.objectName(),prefix));
                if(item.isDir()){

                }else{
                    objectSummary.setETag(item.etag());
                    objectSummary.setSize(item.objectSize());
                    objectSummary.setOwner(new Owner(item.owner().id(), item.owner().displayName()));
                    objectSummary.setMtime(item.lastModified().getTime());
                    objectSummary.setStorageType(kind().getValue());

                }
                objectListing.getObjectSummaries().add(objectSummary);
            }
            return objectListing;
        } catch (Exception e) {
            throw new CossException(e);
        }
    }

    @Override
    public ObjectValue getObject(String bucketName, String key) throws CossException, ServerCossException {
        ObjectValue objectValue = new ObjectValue();
        objectValue.setBucketName(bucketName);
        objectValue.setKey(key);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        try {
            ObjectStat objectStat = minioClient.statObject(bucketName, key);
            objectMetadata.setEtag(objectStat.etag());
            objectMetadata.setContentType(objectStat.contentType());
            objectMetadata.setAtime(objectStat.createdTime().getTime());
            objectMetadata.setEtime(objectStat.createdTime().getTime());
            objectMetadata.setMtime(objectStat.createdTime().getTime());
            objectMetadata.setContentDisposition(objectStat.matDesc());

            objectValue.setObjectContent(minioClient.getObject(bucketName, key));
        } catch (Exception e) {
            throw new CossException(e);
        }
        objectValue.setMetadata(objectMetadata);
        return objectValue;
    }

    @Override
    public CossPutObjectResult putObjectMetaData(String bucketName, String key, ObjectMetadata metadata) {
        return null;
    }

    @Override
    public CossPutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata) throws CossException, ServerCossException {
        try {
            key = fixKey(key,'/');
            minioClient.putObject(bucketName, key, input, null);
        } catch (Exception e) {
            throw new CossException(e);
        }
        return null;
    }

    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) throws CossException, ServerCossException {
        return null;
    }

    @Override
    public void deleteObject(String bucketName, String key) throws CossException, ServerCossException {
        try {
            minioClient.removeObject(bucketName, key);
        } catch (Exception e) {
            throw new CossException(e);
        }
    }

    @Override
    public void deleteVersion(String bucketName, String key, String versionId) throws CossException, ServerCossException {

    }

    @Override
    public CossRestoreObjectResult restoreObject(CossRestoreObjectRequest request) throws CossException, ServerCossException {
        return null;
    }

    @Override
    public ObjectAcl getObjectAcl(String bucketName, String key) throws CossException, ServerCossException {
        return null;
    }

    @Override
    public void setObjectAcl(String bucketName, String key, ACL acl) throws CossException, ServerCossException {

    }

    @Override
    public boolean doesObjectExist(String bucketName, String key) throws CossException, ServerCossException {
        return false;
    }

    private void init() throws InvalidPortException, InvalidEndpointException {
        MinioClient minioClient = new MinioClient("https://play.min.io", "Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");
    }

    private String fixKey(String str, char beTrim) {
        //str.replaceAll("//","/");

        int st = 0;
        int len = str.length();
        char[] val = str.toCharArray();
        while ((st < len) && (val[st] <= beTrim)) {
            st++;
        }
        return st > 0 ? str.substring(st, len) : str;
    }


    private String subDir(String key,String dir){
        if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(dir)){
            int i = key.indexOf(dir);
            if(i==0){
                key = key.substring(dir.length(),key.length());
            }
        }
        return key;
    }


}
