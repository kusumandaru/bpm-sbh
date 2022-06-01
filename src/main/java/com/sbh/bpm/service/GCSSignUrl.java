package com.sbh.bpm.service;

import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GCSSignUrl implements IGCSSignUrl {
  private static final Logger logger = LoggerFactory.getLogger(GCSSignUrl.class);

  @Value("${gcs.client-account}")
  String gcsClientAccount;

  @Value("${gcs.private-key}")
  String gcsPrivateKey;

  @Value("${gcs.url}")
  String gcsUrl;

  @Value("${gcs.bucket}")
  String gcsBucket;

  private static String clientAccount;
  private static String privateKey;
  private static String url;
  private static String bucket;

  void init_static(){
    clientAccount = (String)(gcsClientAccount);
    privateKey = (String)(gcsPrivateKey);
    url = (String)(gcsUrl);
    bucket = (String)(gcsBucket);
  }
  
  private static String bucketName;
  private static String blobName;

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
    return url + objectPath();
  }


  public String GetSignedUrl() {
    init_static();;

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
                         + "?GoogleAccessId=" + clientAccount 
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
      String realPK = privateKey.replaceAll("-----END PRIVATE KEY-----", "")
              .replaceAll("-----BEGIN PRIVATE KEY-----", "").replaceAll("\n", "");
      byte[] b1 = Base64.getDecoder().decode(realPK);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(spec);
  }
}

