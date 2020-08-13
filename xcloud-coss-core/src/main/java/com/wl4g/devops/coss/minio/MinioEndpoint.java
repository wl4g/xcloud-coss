package com.wl4g.devops.coss.minio;

import com.wl4g.devops.coss.ServerCossEndpoint;
import com.wl4g.devops.coss.common.exception.CossException;
import com.wl4g.devops.coss.common.exception.ServerCossException;
import com.wl4g.devops.coss.common.model.*;
import com.wl4g.devops.coss.common.model.bucket.Bucket;
import com.wl4g.devops.coss.common.model.bucket.BucketList;
import com.wl4g.devops.coss.common.model.bucket.BucketMetadata;
import com.wl4g.devops.coss.common.model.metadata.BucketStatusMetaData;
import com.wl4g.devops.coss.config.MinioFsCossProperties;
import io.minio.*;
import io.minio.messages.Item;
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

    private MinioFsCossProperties minioFsCossProperties;

    private MinioClient minioClient;

    public MinioEndpoint(MinioFsCossProperties config) {
        super(config);
        this.minioFsCossProperties = config;
        try {
            minioClient = MinioClient.builder()
                    .endpoint(minioFsCossProperties.getEndpoint())
                    .credentials(minioFsCossProperties.getAccessKey(), minioFsCossProperties.getSecretKey())
                    .build();
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
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();
            minioClient.makeBucket(makeBucketArgs);
            Bucket bucket = new Bucket(bucketName);
            bucket.setCreationDate(new Date());
            log.info("createBucket success [%s]", bucketName);
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
                Date date = b.creationDate() != null ? Date.from(b.creationDate().toInstant()) : null;
                bucket.setCreationDate(date);
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
            RemoveBucketArgs removeBucketArgs = RemoveBucketArgs.builder().bucket(bucketName).build();
            minioClient.removeBucket(removeBucketArgs);
            log.info("deleteBucket success [%s]", bucketName);
        } catch (Exception e) {
            throw new CossException(e);
        }
    }

    @Override
    public BucketMetadata getBucketMetadata(String bucketName) throws CossException, ServerCossException {
        try {
            BucketMetadata bucketMetadata = new BucketMetadata();
            bucketMetadata.setBucketName(bucketName);
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
            prefix = fixKey(prefix, '/');
            ListObjectsArgs listObjectsArgs = ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(false).build();
            Iterable<Result<Item>> results = minioClient.listObjects(listObjectsArgs);
            for (Result<Item> result : results) {
                Item item = result.get();
                ObjectSummary objectSummary = new ObjectSummary();
                objectSummary.setBucketName(bucketName);
                objectSummary.setKey(subDir(item.objectName(), prefix));
                if (item.isDir()) {

                } else {
                    objectSummary.setETag(item.etag());
                    objectSummary.setSize(item.size());
                    objectSummary.setOwner(new Owner(item.owner().id(), item.owner().displayName()));
                    if (item.lastModified() != null) {
                        Date date = Date.from(item.lastModified().toInstant());
                        objectSummary.setMtime(date.getTime());
                    }
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
            StatObjectArgs statObjectArgs = StatObjectArgs.builder().bucket(bucketName).object(key).build();
            ObjectStat objectStat = minioClient.statObject(statObjectArgs);
            objectMetadata.setEtag(objectStat.etag());
            objectMetadata.setContentType(objectStat.contentType());
            if (objectStat.createdTime() != null) {
                Date date = Date.from(objectStat.createdTime().toInstant());
                objectMetadata.setAtime(date.getTime());
                objectMetadata.setEtime(date.getTime());
                objectMetadata.setMtime(date.getTime());
            }
            GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucketName).object(key).build();
            objectValue.setObjectContent(minioClient.getObject(getObjectArgs));
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
            key = fixKey(key, '/');
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucketName).object(key).stream(input, -1, 5 * 1024 * 1024).build();
            minioClient.putObject(putObjectArgs);
            log.info("putObject success [%s %s]", bucketName, key);
        } catch (Exception e) {
            throw new CossException(e);
        }
        return null;
    }

    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) throws CossException, ServerCossException {
        try {
            sourceKey = fixKey(sourceKey, '/');
            CopySource copySource = CopySource.builder().bucket(sourceBucketName).object(sourceKey).build();

            //pre copy
            if (destinationKey.endsWith("/")) {
                destinationKey = destinationKey + getFileNameFromPath(sourceKey);
            }
            log.info("copyObject success from [%s %s] to [%s %s]", sourceBucketName, sourceKey, destinationBucketName, destinationKey);
            CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder().source(copySource).bucket(destinationBucketName).object(destinationKey).build();
            minioClient.copyObject(copyObjectArgs);
        } catch (Exception e) {
            throw new CossException(e);
        }
        return null;
    }

    @Override
    public CopyObjectResult moveObject(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) throws CossException, ServerCossException {
        copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
        deleteObject(sourceBucketName, sourceKey);
        log.info("moveObject from success [%s %s] to [%s %s]", sourceBucketName, sourceKey, destinationBucketName, destinationKey);
        return null;
    }

    @Override
    public void deleteObject(String bucketName, String key) throws CossException, ServerCossException {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucketName).object(key).build();
            minioClient.removeObject(removeObjectArgs);
            log.info("deleteObject success [%s %s]", bucketName, key);
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


    private String subDir(String key, String dir) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(dir)) {
            int i = key.indexOf(dir);
            if (i == 0) {
                key = key.substring(dir.length(), key.length());
            }
        }
        return key;
    }

    private String getFileNameFromPath(String path) {
        int i = path.lastIndexOf("/");
        if (i >= 0) {
            return path.substring(i, path.length());
        }
        return path;
    }


}
