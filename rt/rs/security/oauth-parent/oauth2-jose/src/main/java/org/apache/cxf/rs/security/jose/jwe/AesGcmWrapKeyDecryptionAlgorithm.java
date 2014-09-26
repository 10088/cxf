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
package org.apache.cxf.rs.security.jose.jwe;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.SecretKey;

import org.apache.cxf.common.util.Base64UrlUtility;
import org.apache.cxf.rs.security.jose.jwa.Algorithm;
import org.apache.cxf.rs.security.oauth2.utils.crypto.CryptoUtils;

public class AesGcmWrapKeyDecryptionAlgorithm extends WrappedKeyDecryptionAlgorithm {
    public AesGcmWrapKeyDecryptionAlgorithm(String encodedKey) {    
        this(encodedKey, null);
    }
    public AesGcmWrapKeyDecryptionAlgorithm(String encodedKey, String supportedAlgo) {    
        this(CryptoUtils.decodeSequence(encodedKey), supportedAlgo);
    }
    public AesGcmWrapKeyDecryptionAlgorithm(byte[] secretKey) {    
        this(secretKey, null);
    }
    public AesGcmWrapKeyDecryptionAlgorithm(byte[] secretKey, String supportedAlgo) {    
        this(CryptoUtils.createSecretKeySpec(secretKey, Algorithm.AES_ALGO_JAVA), supportedAlgo);
    }
    public AesGcmWrapKeyDecryptionAlgorithm(SecretKey secretKey) {    
        this(secretKey, null);
    }
    public AesGcmWrapKeyDecryptionAlgorithm(SecretKey secretKey, String supportedAlgo) {    
        super(secretKey, supportedAlgo);
    }
    @Override
    protected byte[] getEncryptedContentEncryptionKey(JweCompactConsumer consumer) {
        byte[] encryptedCekKey = super.getEncryptedContentEncryptionKey(consumer);
        byte[] tag = getDecodedBytes(consumer, "tag");
        return JweCompactConsumer.getCipherWithAuthTag(encryptedCekKey, tag);
    }
    protected AlgorithmParameterSpec getAlgorithmParameterSpec(JweCompactConsumer consumer) {
        byte[] iv = getDecodedBytes(consumer, "iv");
        return CryptoUtils.getContentEncryptionCipherSpec(128, iv);
    }
    private byte[] getDecodedBytes(JweCompactConsumer consumer, String headerName) {
        try {
            Object ivHeader = consumer.getJweHeaders().getHeader(headerName);
            return Base64UrlUtility.decode(ivHeader.toString());
        } catch (Exception ex) {
            throw new SecurityException(ex);
        }
    }
    protected void validateKeyEncryptionAlgorithm(String keyAlgo) {
        super.validateKeyEncryptionAlgorithm(keyAlgo);
        if (!Algorithm.isAesGcmKeyWrap(keyAlgo)) {
            throw new SecurityException();
        }
    }
}
