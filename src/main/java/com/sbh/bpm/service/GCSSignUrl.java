package com.sbh.bpm.service;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCSSignUrl {
  private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStorage.class);

  // Google Service Account Client ID. Replace with your account.
  static final String CLIENT_ACCOUNT = "bpm-sbh-storage@bpm-sbh.iam.gserviceaccount.com";
 
  // Private key from the private_key field in the download JSON file. Replace
  // this!
  static final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC1DcJFiIwJBpJK\nPWcP9dYwjUENc7qMOzXmTYcfsyV4CJpL245e8c/xvlI5jesPn6QcPZ/efMaUQvwZ\nAb3KPrB4U3BNK1RU6UUC+7YL5d4HrmQaC+lAAGkU3OHuJ2/hRm2SpwFQfgHe1uKd\nID7fmlbkf4oFZ2u9L0u8U38+/jV9jgGHRC6SDOQT6IXW2niDABAX79kgC0Qs+prs\n1nMDMtD+iiurOGbASCo5nVWQmaxX4Bny+FHbl+6s2eDYyhOfY/N4AAJRcUoO54CN\nVxdG5rQ5zmW5qAlPi0XxbmFaeiogPB4mffFFuc9UwhPrKI/cwcFV3AVsdRSrfEPo\nv5E3sYqnAgMBAAECggEAWah0mgZigl20t/rsPKUa6nQo0MaMjMENkfy2e2Y1mdl4\nvzP8rtvEhAwhn8q9qsD9fdaugnj7xZPeT2vjVnHw5cU8CZr5agsSV8Xags3UQ62C\neLuJSF5rqx8u6sawyrgbrtHw/rFHr9ht5C2BtS0y9niKGt76JXd0Y9BdZQ5x4rhG\np/MwOslPY6ioi/drGhQrLS1IoU79b7C02tqCSWu60bG8XkNF0LyF2B8JcAA+W9jT\nxkuc8Yz1d/7G+XSGjoy06SaL70PNh+iRmWFjfqLDLIA96m+EMhdoZ3MUJSaaKCV+\nb/qSVobzbJg24VEr2aE78B6tIEEq4P/+0ne8gIixUQKBgQD2tws3RXV5FeRO9ZTn\nUJ6Lr2SDlRtVhUbLGlHrE3wbWNolxTBUH6Z9QaC5ZFttHna5oSdXIdU2EQcAD7Kp\nlUziLIvk2c7B8iKPzAPN9nxaLJ1kQn5MMqZ1GMFbrCR4fIsgHtxOeR9ztUwdLU37\nhpzo+boaRgBjpuprobku/18C8wKBgQC73htWQZVVh200BQFLlJvxOMlB1WdeJ93u\nDY6icN+FSDGPi7MyQjVeHUJH95m+9pcVM0tUY2MH1EDQiMKZ9X4C6HJS5xh731H/\nxkVxmzVQUszvJNz7jt0xJza4SAErrZHspgw7qWVp1z+KSLso7QzeaEJwb49wXaKV\nssWVUSD+fQKBgEnmtI5e2PxnSVc5itvrM0KmTBZ6IJDydi06ehVPZdqPN/EeawaX\n4Hgeo3VFEXnTKT8bfbxE3yft28nx6dOQbFJHg7QOZjeEP0QRmYYlckRUnuS8YjaS\n6Xej7a93FgmdTNNXSyiacYhoYx9ORdC1FuVWgJ+EHnO7OPb6n4zrY9tvAoGAfCzE\nuPDEejH3dmjQgFqL8JduP3uExXFoeY6wdqJQu9IKIC1glpm4mKqSQutfzh9DoZmz\n8uV2cZhT9wJOTmE7l0R5PF7PiiEEHCG7iOnkBWGVX4RoJEY74d9fhdLiIzKov1jJ\nwS2ogJrrvsVwIA7/cIpNT/ANSWI6suPlQVrMiikCgYAMAkZl4F6jhIa/TQp3vR0G\nLfIzOvZ8AE4SZswsynykKHHZvnYbxOhRU1w7+snN4p9s8UWg3VMJY1UtqtQpQNo3\nhl0TQfqK+Z7QQqgrpaVVXVniHw7i9QMxzh5ajaGWyZyStl61eoNL/owmXgdEehTJ\nPUTEmfL7+SmUzhAP3tIEAQ==\n-----END PRIVATE KEY-----\n";

  // base url of google cloud storage objects
  static final String BASE_GCS_URL = "https://storage.googleapis.com";

  private static String bucketName;
  private static String blobName;

  // bucket and object we are trying to access. Replace this!
  // static final String OBJECT_PATH = "/qptbucket/earth.jpg";
  // full url to the object.
  // static final String FULL_OBJECT_URL = BASE_GCS_URL + OBJECT_PATH;

  // expiry time of the url in Linux epoch form (seconds since january 1970)
  static String expiryTime;

  public void SetBucket(String name) {
    bucketName = name;
  }

  public void SetBlobName(String name) {
    blobName = name;
  }

  private static String objectPath() {
    return "/" + bucketName + "/" + blobName;
  }

  private static String fullObjectUrl() {
    return BASE_GCS_URL + objectPath();
  }


  public String GetSignedUrl() {
    setExpiryTimeInEpoch();
    String stringToSign = getSignInput();
    String signedString = "";
    PrivateKey pk;
    try {
      pk = getPrivateKey();
      signedString = getSignedString(stringToSign, pk);
      // URL encode the signed string so that we can add this URL
      signedString = URLEncoder.encode(signedString, "UTF-8");
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    
    String signedUrl = getSignedUrl(signedString);
    return signedUrl;
  }

  // Set an expiry date for the signed url. Sets it at one minute ahead of
  // current time.
  // Represented as the epoch time (seconds since 1st January 1970)
  private static void setExpiryTimeInEpoch() {
      long now = System.currentTimeMillis();
      // expire in a 10 minute!
      // note the conversion to seconds as needed by GCS.
      long expiredTimeInSeconds = (now + 60 * 1000L * 10) / 1000;
      expiryTime = expiredTimeInSeconds + "";
  }

  // The signed URL format as required by Google.
  private static String getSignedUrl(String signedString) {
      String signedUrl = fullObjectUrl()
                         + "?GoogleAccessId=" + CLIENT_ACCOUNT 
                         + "&Expires=" + expiryTime
                         + "&Signature=" + signedString;
      return signedUrl;
  }

  // We sign the expiry time and bucket object path
  private static String getSignInput() {
      return "GET" + "\n"
                  + "" + "\n"
                  + "" + "\n"
                  + expiryTime + "\n"
                  + objectPath();
  }

  // Use SHA256withRSA to sign the request
  private static String getSignedString(String input, PrivateKey pk) throws Exception {
      Signature privateSignature = Signature.getInstance("SHA256withRSA");
      privateSignature.initSign(pk);
      privateSignature.update(input.getBytes("UTF-8"));
      byte[] s = privateSignature.sign();
      return Base64.getEncoder().encodeToString(s);
  }

  // Get private key object from unencrypted PKCS#8 file content
  private static PrivateKey getPrivateKey() throws Exception {
      // Remove extra characters in private key.
      String realPK = PRIVATE_KEY.replaceAll("-----END PRIVATE KEY-----", "")
              .replaceAll("-----BEGIN PRIVATE KEY-----", "").replaceAll("\n", "");
      byte[] b1 = Base64.getDecoder().decode(realPK);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(spec);
  }
}

