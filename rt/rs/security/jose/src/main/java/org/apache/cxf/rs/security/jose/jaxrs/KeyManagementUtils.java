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

package org.apache.cxf.rs.security.jose.jaxrs;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.cxf.Bus;
import org.apache.cxf.common.util.PropertyUtils;
import org.apache.cxf.common.util.crypto.CryptoUtils;
import org.apache.cxf.jaxrs.utils.ResourceUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.security.SecurityContext;


/**
 * Encryption helpers
 */
public final class KeyManagementUtils {
    public static final String RSSEC_KEY_STORE_TYPE = "rs.security.keystore.type";
    public static final String RSSEC_KEY_STORE_PSWD = "rs.security.keystore.password";
    public static final String RSSEC_KEY_PSWD = "rs.security.key.password";
    public static final String RSSEC_KEY_STORE_ALIAS = "rs.security.keystore.alias";
    public static final String RSSEC_KEY_STORE_ALIASES = "rs.security.keystore.aliases";
    public static final String RSSEC_KEY_STORE_FILE = "rs.security.keystore.file";
    public static final String RSSEC_PRINCIPAL_NAME = "rs.security.principal.name";
    public static final String RSSEC_KEY_PSWD_PROVIDER = "rs.security.key.password.provider";
    public static final String RSSEC_SIG_KEY_PSWD_PROVIDER = "rs.security.signature.key.password.provider";
    public static final String RSSEC_DECRYPT_KEY_PSWD_PROVIDER = "rs.security.decryption.key.password.provider";
    public static final String RSSEC_DEFAULT_ALGORITHMS = "rs.security.default.algorithms";
    
    private KeyManagementUtils() {
    }
    
    public static PublicKey loadPublicKey(Message m, Properties props) {
        KeyStore keyStore = KeyManagementUtils.loadPersistKeyStore(m, props);
        return CryptoUtils.loadPublicKey(keyStore, props.getProperty(RSSEC_KEY_STORE_ALIAS));
    }
    public static PublicKey loadPublicKey(Message m, String keyStoreLocProp) {
        return loadPublicKey(m, keyStoreLocProp, null);
    }
    public static PublicKey loadPublicKey(Message m, String keyStoreLocPropPreferred, String keyStoreLocPropDefault) {
        String keyStoreLoc = getMessageProperty(m, keyStoreLocPropPreferred, keyStoreLocPropDefault);
        Bus bus = m.getExchange().getBus();
        try {
            Properties props = ResourceUtils.loadProperties(keyStoreLoc, bus);
            return KeyManagementUtils.loadPublicKey(m, props);
        } catch (Exception ex) {
            throw new SecurityException(ex);
        }
    }
    private static String getMessageProperty(Message m, String keyStoreLocPropPreferred, 
                                             String keyStoreLocPropDefault) {
        String propLoc = 
            (String)MessageUtils.getContextualProperty(m, keyStoreLocPropPreferred, keyStoreLocPropDefault);
        if (propLoc == null) {
            throw new SecurityException();
        }
        return propLoc;
    }
    private static PrivateKey loadPrivateKey(KeyStore keyStore, 
                                            Message m,
                                            Properties props, 
                                            Bus bus, 
                                            PrivateKeyPasswordProvider provider,
                                            String keyOper) {
        
        String keyPswd = props.getProperty(RSSEC_KEY_PSWD);
        String alias = getKeyId(m, props, RSSEC_KEY_STORE_ALIAS, keyOper);
        char[] keyPswdChars = provider != null ? provider.getPassword(props) 
            : keyPswd != null ? keyPswd.toCharArray() : null;    
        return CryptoUtils.loadPrivateKey(keyStore, keyPswdChars, alias);
    }
    
    public static PrivateKey loadPrivateKey(Message m, String keyStoreLocProp, String keyOper) {
        return loadPrivateKey(m, keyStoreLocProp, null, keyOper);
    }
    public static PrivateKey loadPrivateKey(Message m, String keyStoreLocPropPreferred,
                                            String keyStoreLocPropDefault, String keyOper) {
        String keyStoreLoc = getMessageProperty(m, keyStoreLocPropPreferred, keyStoreLocPropDefault);
        Bus bus = m.getExchange().getBus();
        try {
            Properties props = ResourceUtils.loadProperties(keyStoreLoc, bus);
            return loadPrivateKey(m, props, keyOper);
        } catch (Exception ex) {
            throw new SecurityException(ex);
        }
    }
    
    public static String getKeyId(Message m, Properties props, String preferredPropertyName, String keyOper) {
        String kid = null;
        String altPropertyName = null;
        if (keyOper != null) {
            if (keyOper.equals(JsonWebKey.KEY_OPER_ENCRYPT) || keyOper.equals(JsonWebKey.KEY_OPER_DECRYPT)) {
                altPropertyName = preferredPropertyName + ".jwe";
            } else if (keyOper.equals(JsonWebKey.KEY_OPER_SIGN) || keyOper.equals(JsonWebKey.KEY_OPER_VERIFY)) {
                altPropertyName = preferredPropertyName + ".jws";
            }
            String direction = m.getExchange().getOutMessage() == m ? ".out" : ".in";
            kid = (String)MessageUtils.getContextualProperty(m, preferredPropertyName, altPropertyName + direction);
        }
        
        if (kid == null) {
            kid = props.getProperty(preferredPropertyName);
        }
        if (kid == null && altPropertyName != null) {
            kid = props.getProperty(altPropertyName);
        }
        return kid;
    }
    public static PrivateKeyPasswordProvider loadPasswordProvider(Message m, Properties props, String keyOper) {
        PrivateKeyPasswordProvider cb = 
            (PrivateKeyPasswordProvider)m.getContextualProperty(RSSEC_KEY_PSWD_PROVIDER);
        if (cb == null && keyOper != null) {
            String propName = keyOper.equals(JsonWebKey.KEY_OPER_SIGN) ? RSSEC_SIG_KEY_PSWD_PROVIDER
                : keyOper.equals(JsonWebKey.KEY_OPER_DECRYPT) 
                ? RSSEC_DECRYPT_KEY_PSWD_PROVIDER : null;
            if (propName != null) {
                cb = (PrivateKeyPasswordProvider)m.getContextualProperty(propName);
            }
        }
        return cb;
    }
    
    public static PrivateKey loadPrivateKey(Message m, Properties props, String keyOper) {
        Bus bus = m.getExchange().getBus();
        KeyStore keyStore = loadPersistKeyStore(m, props);
        PrivateKeyPasswordProvider cb = loadPasswordProvider(m, props, keyOper);
        if (cb != null && m.getExchange().getInMessage() != null) {
            SecurityContext sc = m.getExchange().getInMessage().get(SecurityContext.class);
            if (sc != null) {
                Principal p = sc.getUserPrincipal();
                if (p != null) {
                    props.setProperty(RSSEC_PRINCIPAL_NAME, p.getName());
                }
            }
        }
        return loadPrivateKey(keyStore, m, props, bus, cb, keyOper);
    }
    public static KeyStore loadPersistKeyStore(Message m, Properties props) {
        KeyStore keyStore = (KeyStore)m.getExchange().get(props.get(RSSEC_KEY_STORE_FILE));
        if (keyStore == null) {
            keyStore = loadKeyStore(props, m.getExchange().getBus());
            m.getExchange().put((String)props.get(RSSEC_KEY_STORE_FILE), keyStore);
        }
        return keyStore;
    }
    public static KeyStore loadKeyStore(Properties props, Bus bus) {
        String keyStoreType = props.getProperty(RSSEC_KEY_STORE_TYPE);
        String keyStoreLoc = props.getProperty(RSSEC_KEY_STORE_FILE);
        String keyStorePswd = props.getProperty(RSSEC_KEY_STORE_PSWD);
        try {
            InputStream is = ResourceUtils.getResourceStream(keyStoreLoc, bus);
            return CryptoUtils.loadKeyStore(is, keyStorePswd.toCharArray(), keyStoreType);
        } catch (Exception ex) {
            throw new SecurityException(ex);
        }
    }

    public static List<String> encodeX509CertificateChain(List<X509Certificate> chain) {
        List<String> encodedChain = new ArrayList<String>(chain.size());
        for (X509Certificate cert : chain) {
            try {
                encodedChain.add(CryptoUtils.encodeCertificate(cert));
            } catch (Exception ex) {
                throw new SecurityException(ex);
            }    
        }
        return encodedChain;
    }
    public static List<X509Certificate> toX509CertificateChain(List<String> base64EncodedChain) {
        if (base64EncodedChain != null) {
            List<X509Certificate> certs = new ArrayList<X509Certificate>(base64EncodedChain.size());
            for (String encodedCert : base64EncodedChain) {
                try {
                    certs.add((X509Certificate)CryptoUtils.decodeCertificate(encodedCert));
                } catch (Exception ex) {
                    throw new SecurityException(ex);
                }
            }
            //TODO: validate the chain
            return certs;
        } else {
            return null;
        }
    }
    public static String getKeyAlgorithm(Message m, Properties props, String propName, String defaultAlg) {
        String algo = props.getProperty(propName);
        if (algo == null && PropertyUtils.isTrue(m.getContextualProperty(RSSEC_DEFAULT_ALGORITHMS))) {
            algo = defaultAlg;
        }
        return algo;
    }
}
