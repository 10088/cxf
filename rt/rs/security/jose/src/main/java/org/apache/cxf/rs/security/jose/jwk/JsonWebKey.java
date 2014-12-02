/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.rs.security.jose.jwk;

import java.util.List;
import java.util.Map;

import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxrs.provider.json.JsonMapObject;
import org.apache.cxf.rs.security.jose.JoseConstants;


public class JsonWebKey extends JsonMapObject {
    
    public static final String KEY_TYPE = "kty";
    public static final String PUBLIC_KEY_USE = "use";
    public static final String KEY_OPERATIONS = "key_ops";
    public static final String KEY_ALGO = JoseConstants.HEADER_ALGORITHM;
    public static final String KEY_ID = JoseConstants.HEADER_KEY_ID;
    public static final String X509_URL = JoseConstants.HEADER_X509_URL;
    public static final String X509_CHAIN = JoseConstants.HEADER_X509_CHAIN;
    public static final String X509_THUMBPRINT = JoseConstants.HEADER_X509_THUMBPRINT;
    public static final String X509_THUMBPRINT_SHA256 = JoseConstants.HEADER_X509_THUMBPRINT_SHA256;
    
    public static final String KEY_TYPE_RSA = "RSA";
    public static final String RSA_MODULUS = "n";
    public static final String RSA_PUBLIC_EXP = "e";
    public static final String RSA_PRIVATE_EXP = "d";
    public static final String RSA_FIRST_PRIME_FACTOR = "p";
    public static final String RSA_SECOND_PRIME_FACTOR = "q";
    public static final String RSA_FIRST_PRIME_CRT = "dp";
    public static final String RSA_SECOND_PRIME_CRT = "dq";
    public static final String RSA_FIRST_CRT_COEFFICIENT = "qi";
        
    public static final String KEY_TYPE_OCTET = "oct";
    public static final String OCTET_KEY_VALUE = "k";
    
    public static final String KEY_TYPE_ELLIPTIC = "EC";
    public static final String EC_CURVE = "crv";
    public static final String EC_CURVE_P256 = "P-256";
    public static final String EC_CURVE_P384 = "P-384";
    public static final String EC_CURVE_P512 = "P-512";
    public static final String EC_X_COORDINATE = "x";
    public static final String EC_Y_COORDINATE = "y";
    public static final String EC_PRIVATE_KEY = "d";
    
    public static final String PUBLIC_KEY_USE_SIGN = "sig";
    public static final String PUBLIC_KEY_USE_ENCRYPT = "enc";
    
    public static final String KEY_OPER_SIGN = "sign";
    public static final String KEY_OPER_VERIFY = "verify";
    public static final String KEY_OPER_ENCRYPT = "encrypt";
    public static final String KEY_OPER_DECRYPT = "decrypt";
    
    public JsonWebKey() {
        
    }
    
    public JsonWebKey(Map<String, Object> values) {
        super(values);
    }
    
    public void setKeyType(String keyType) {
        setProperty(KEY_TYPE, keyType);
    }

    public String getKeyType() {
        return (String)getProperty(KEY_TYPE);
    }

    public void setPublicKeyUse(String use) {
        setProperty(PUBLIC_KEY_USE, use);
    }
    
    public String getPublicKeyUse() {
        return (String)getProperty(PUBLIC_KEY_USE);
    }

    public void setKeyOperation(List<String> keyOperation) {
        setProperty(KEY_OPERATIONS, keyOperation);
    }

    public List<String> getKeyOperation() {
        return CastUtils.cast((List<?>)getProperty(KEY_OPERATIONS));
    }
    
    public void setAlgorithm(String algorithm) {
        setProperty(KEY_ALGO, algorithm);
    }

    public String getAlgorithm() {
        return (String)getProperty(KEY_ALGO);
    }
    
    public void setKid(String kid) {
        setProperty(KEY_ID, kid);
    }

    public String getKid() {
        return (String)getProperty(KEY_ID);
    }
    
    public void setX509Url(String x509Url) {
        setProperty(X509_URL, x509Url);
    }
    
    public String getX509Url() {
        return (String)getProperty(X509_URL);
    }

    public void setX509Chain(List<String> x509Chain) {
        setProperty(X509_CHAIN, x509Chain);
    }

    public List<String> getX509Chain() {
        return CastUtils.cast((List<?>)getProperty(X509_CHAIN));
    }
    
    public void setX509Thumbprint(String x509Thumbprint) {
        setProperty(X509_THUMBPRINT, x509Thumbprint);
    }
    
    public String getX509Thumbprint() {
        return (String)getProperty(X509_THUMBPRINT);
    }
    
    public void setX509ThumbprintSHA256(String x509Thumbprint) {
        setProperty(X509_THUMBPRINT_SHA256, x509Thumbprint);
    }
    
    public String getX509ThumbprintSHA256() {
        return (String)getProperty(X509_THUMBPRINT_SHA256);
    }
    
    public JsonWebKey setKeyProperty(String name, Object value) {
        setProperty(name, value);
        return this;
    }
    public Object getKeyProperty(String name) {
        return getProperty(name);
    }
    
    
    
}
